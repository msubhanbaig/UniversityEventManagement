package model;

/** Coordinates the use cases (UC-03 register, UC-06 create event, etc.) - design-added controller. */
public class EventController {
    private int controllerId;
    private String activeRequestId;
    private String lastError;
    private boolean transactionOpen;

    // Arrays that store many objects of one class
    private Event[] events = new Event[50];
    private int eventCount = 0;
    private Venue[] venues = new Venue[10];
    private int venueCount = 0;
    private Registration[] registrations = new Registration[200];
    private int regCount = 0;
    private Attendance[] attendances = new Attendance[200];
    private int attCount = 0;

    private int nextEventId = 1;
    private int nextRegId = 1;
    private int nextNotificationId = 1;
    private int nextAttendanceId = 1;
    private int lastRegistrationId = 0;

    public EventController(int controllerId) {
        this.controllerId = controllerId;
        this.activeRequestId = "NONE";
        this.lastError = "";
        this.transactionOpen = false;
    }

    public EventController() {
        this(1);
    }

    // ------------------------------------------------------------------ venues
    public boolean addVenue(Venue venue) {
        if (venueCount >= venues.length) {
            return false;
        }
        venues[venueCount++] = venue;
        return true;
    }

    // UC-08: list the venues free at a time slot
    public Venue[] listFreeVenues(String slot) {
        return listFreeVenues(slot, 0);
    }

    // Overloaded: also filter by minimum capacity
    public Venue[] listFreeVenues(String slot, int minCapacity) {
        int n = 0;
        for (int i = 0; i < venueCount; i++) {
            if (venues[i].isAvailable(slot, minCapacity)) {
                n++;
            }
        }
        Venue[] free = new Venue[n];
        int k = 0;
        for (int i = 0; i < venueCount; i++) {
            if (venues[i].isAvailable(slot, minCapacity)) {
                free[k++] = venues[i];
            }
        }
        return free;
    }

    // Array passed to a method: search the given array for a venue id
    public Venue findVenueInList(Venue[] list, int id) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].getVenueId() == id) {
                return list[i];
            }
        }
        return null;
    }

    // Array passed to a method: count events with a given status
    public int countByStatus(Event[] list, int count, String status) {
        int total = 0;
        for (int i = 0; i < count; i++) {
            if (list[i].getStatus().equals(status)) {
                total++;
            }
        }
        return total;
    }

    // ------------------------------------------------------------------ events
    public Event findEventById(Event[] list, int count, int id) {
        for (int i = 0; i < count; i++) {
            if (list[i].getEventId() == id) {
                return list[i];
            }
        }
        return null;
    }

    public Event findEvent(int id) {
        return findEventById(events, eventCount, id);
    }

    public Event[] searchEvents(User user, String query) {
        return user.viewEvents(events, eventCount, query);
    }

    // UC-06 Create Event
    public Event createEvent(EventOrganizer organizer, String title, String schedule, Venue selected,
                             double allocated, double planned) {
        lastError = "";
        activeRequestId = "CREATE-" + nextEventId;
        if (!organizer.hasPermission("createEvent")) {
            lastError = "Unauthorized: you cannot create events.";
            return null;
        }
        if (title.trim().isEmpty() || schedule.trim().isEmpty() || selected == null) {
            lastError = "Required fields are missing.";
            return null;
        }
        if (eventCount >= events.length) {
            lastError = "Event storage is full.";
            return null;
        }
        Event event = organizer.createEvent(nextEventId, title, schedule, selected, allocated, planned);
        if (event == null) {
            lastError = "Budget is invalid or outside your authority limit ("
                    + organizer.getAuthorizationLimit() + ").";
            return null;
        }
        transactionOpen = true;                              // begin "transaction"
        if (!selected.isAvailable(schedule)) {
            transactionOpen = false;
            lastError = "Venue was booked in the meantime.";
            return null;
        }
        if (!selected.reserve(event) || !event.save()) {
            selected.release(event);
            transactionOpen = false;                         // rollback
            lastError = "Save failed.";
            return null;
        }
        events[eventCount++] = event;
        nextEventId++;
        transactionOpen = false;                             // commit
        return event;
    }

    // UC-07 Cancel Event (works through the Cancellable interface)
    public boolean cancelEvent(EventOrganizer organizer, int eventId, String reason) {
        lastError = "";
        if (!organizer.hasPermission("updateEvent")) {
            lastError = "Unauthorized.";
            return false;
        }
        Event event = findEvent(eventId);
        if (event == null) {
            lastError = "Event not found.";
            return false;
        }
        if (!cancelItem(event, reason)) {
            lastError = "Event is already cancelled.";
            return false;
        }
        for (int i = 0; i < regCount; i++) {                 // cancel and notify registered students
            if (registrations[i].getEventId() == eventId && !registrations[i].isCancelled()) {
                cancelItem(registrations[i], "Event cancelled: " + reason);
                event.removeRegistered();
                Notification n = new Notification(nextNotificationId++, registrations[i].getStudentId());
                n.prepare(event);
                n.send();
            }
        }
        return true;
    }

    // Interface polymorphism: item can be an Event OR a Registration
    private boolean cancelItem(Cancellable item, String reason) {
        return item.cancel(reason);
    }

    public boolean recordExpense(EventOrganizer organizer, int eventId, double amount) {
        lastError = "";
        Event event = findEvent(eventId);
        if (!organizer.hasPermission("recordBudget") || event == null) {
            lastError = "Not allowed or event not found.";
            return false;
        }
        if (!event.getBudget().recordActual(amount)) {
            lastError = "Amount must be greater than zero.";
            return false;
        }
        return true;
    }

    // --------------------------------------------------------------- registration
    // UC-03 Register for Event (includes UC-04 validate eligibility)
    public boolean registerStudent(Student student, Event event) {
        lastError = "";
        if (event == null) {
            lastError = "Event not found.";
            return false;
        }
        if (!student.hasPermission("registerForEvent")) {
            lastError = "Unauthorized.";
            return false;
        }
        for (int i = 0; i < regCount; i++) {                 // duplicate check
            boolean sameStudent = registrations[i].getStudentId() == student.getUserId();
            boolean sameEvent = registrations[i].getEventId() == event.getEventId();
            if (sameStudent && sameEvent && !registrations[i].isCancelled()) {
                lastError = "You are already registered for this event.";
                return false;
            }
        }
        if (regCount >= registrations.length) {
            lastError = "Registration storage is full.";
            return false;
        }
        Registration reg = new Registration(nextRegId, student.getUserId(), event.getEventId());
        if (!reg.validateEligibility(student, event)) {
            lastError = reg.getFailReason();
            return false;
        }
        if (!student.register(event) || !reg.save()) {
            lastError = "Could not save the registration.";
            return false;
        }
        registrations[regCount++] = reg;
        lastRegistrationId = nextRegId;
        nextRegId++;
        Notification n = new Notification(nextNotificationId++, student.getUserId());   // UC-16 (optional)
        n.prepare(event, "Registration confirmed for");
        n.send();
        return true;
    }

    // UC-05 Cancel Registration
    public boolean cancelRegistration(Student student, int regId) {
        lastError = "";
        Registration reg = findRegistration(regId);
        if (reg == null) {
            lastError = "Registration not found.";
            return false;
        }
        if (!student.cancelRegistration(reg)) {
            lastError = "You can only cancel your own active registration.";
            return false;
        }
        Event event = findEvent(reg.getEventId());
        if (event != null) {
            event.removeRegistered();
        }
        return true;
    }

    public Registration findRegistration(int id) {
        for (int i = 0; i < regCount; i++) {
            if (registrations[i].getRegistrationId() == id) {
                return registrations[i];
            }
        }
        return null;
    }

    // Registrations that are still CONFIRMED for one student (studentId > 0) or one event
    public Registration[] listConfirmedForStudent(int studentId) {
        int n = 0;
        for (int i = 0; i < regCount; i++) {
            if (registrations[i].getStudentId() == studentId && registrations[i].getStatus().equals("CONFIRMED")) {
                n++;
            }
        }
        Registration[] result = new Registration[n];
        int k = 0;
        for (int i = 0; i < regCount; i++) {
            if (registrations[i].getStudentId() == studentId && registrations[i].getStatus().equals("CONFIRMED")) {
                result[k++] = registrations[i];
            }
        }
        return result;
    }

    public Registration[] listConfirmedForEvent(int eventId) {
        int n = 0;
        for (int i = 0; i < regCount; i++) {
            if (registrations[i].getEventId() == eventId && registrations[i].getStatus().equals("CONFIRMED")) {
                n++;
            }
        }
        Registration[] result = new Registration[n];
        int k = 0;
        for (int i = 0; i < regCount; i++) {
            if (registrations[i].getEventId() == eventId && registrations[i].getStatus().equals("CONFIRMED")) {
                result[k++] = registrations[i];
            }
        }
        return result;
    }

    // ----------------------------------------------------------------- attendance
    // UC-13 Record Attendance
    public boolean recordAttendance(EventOrganizer organizer, int regId, boolean present) {
        lastError = "";
        if (!organizer.hasPermission("recordAttendance")) {
            lastError = "Unauthorized.";
            return false;
        }
        Registration reg = findRegistration(regId);
        if (reg == null || !organizer.recordAttendance(reg)) {
            lastError = "Registration not found or not confirmed.";
            return false;
        }
        Attendance a = new Attendance(nextAttendanceId++, regId);
        if (!a.verifyRegistration(reg) || attCount >= attendances.length) {
            lastError = "Could not verify registration.";
            return false;
        }
        if (present) {
            a.markPresent();
        } else {
            a.markAbsent();
        }
        attendances[attCount++] = a;
        return true;
    }

    public int countPresent() {
        int total = 0;
        for (int i = 0; i < attCount; i++) {
            if (attendances[i].isPresent()) {
                total++;
            }
        }
        return total;
    }

    // -------------------------------------------------------------------- reports
    // UC-14 Generate Report
    public Report generateReport(User user, String type) {
        lastError = "";
        if (!user.hasPermission("generateReport")) {
            lastError = "Unauthorized: you cannot generate reports.";
            return null;
        }
        Report report = new Report(type);
        switch (type) {
            case "events":
                report.compileEvents(events, eventCount);
                break;
            case "budgets":
                report.compileBudgets(events, eventCount);
                break;
            case "participation":
                report.compileParticipation(registrations, regCount);
                report.setContent(report.getContent() + "\n  Attendance marked present: " + countPresent());
                break;
            default:
                lastError = "Unknown report type.";
                return null;
        }
        return report;
    }

    // --------------------------------------------------------------------- output
    public String showEventDetails(Event event) {
        return event + "\n      Seats left: " + event.getSeatsLeft() + " | Budget: " + event.getBudget();
    }

    public boolean showConfirmation(String message) {
        System.out.println("[OK] " + message);
        return true;
    }

    public boolean showError(String message) {
        System.out.println("[ERROR] " + message);
        return true;
    }

    // ------------------------------------------------------------ getters/setters
    public Event[] getEvents() { return events; }
    public int getEventCount() { return eventCount; }
    public int getControllerId() { return controllerId; }
    public void setControllerId(int controllerId) { this.controllerId = controllerId; }
    public String getActiveRequestId() { return activeRequestId; }
    public void setActiveRequestId(String activeRequestId) { this.activeRequestId = activeRequestId; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
    public boolean isTransactionOpen() { return transactionOpen; }
    public int getLastRegistrationId() { return lastRegistrationId; }
}
