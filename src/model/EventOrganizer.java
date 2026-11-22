package model;

public class EventOrganizer extends Staff {
    private int organizerId;
    private int guildId;
    private double authorizationLimit;
    private int assignedEvents;

    // Constructor 1 (full)
    public EventOrganizer(int userId, String name, String email, String password,
                          String employeeNo, String department, String designation,
                          int organizerId, int guildId, double authorizationLimit) {
        super(userId, name, email, password, employeeNo, department, designation);
        this.organizerId = organizerId;
        this.guildId = guildId;
        this.authorizationLimit = authorizationLimit;
        this.assignedEvents = 0;
    }

    // Constructor 2 (overloading): default department, designation and limit
    public EventOrganizer(int userId, String name, String email, String password,
                          String employeeNo, int organizerId, int guildId) {
        this(userId, name, email, password, employeeNo, "Student Affairs", "Event Organizer",
             organizerId, guildId, 100000.0);
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "viewEvents", "createEvent", "updateEvent", "recordBudget",
                              "recordAttendance", "generateReport" };
    }

    // From class diagram: createEvent(data): Event  (EventData replaced by plain parameters)
    public Event createEvent(int eventId, String title, String schedule, Venue venue,
                             double allocated, double planned) {
        Event event = new Event(eventId, title, schedule, allocated, planned);
        event.setVenue(venue);
        if (!event.getBudget().validate(authorizationLimit)) {
            return null;                       // budget invalid or outside this organizer's authority
        }
        assignedEvents++;
        return event;
    }

    // From class diagram: recordAttendance(reg): bool
    public boolean recordAttendance(Registration reg) {
        return isActive() && reg.getStatus().equals("CONFIRMED");
    }

    public int getOrganizerId() { return organizerId; }
    public void setOrganizerId(int organizerId) { this.organizerId = organizerId; }
    public int getGuildId() { return guildId; }
    public void setGuildId(int guildId) { this.guildId = guildId; }
    public double getAuthorizationLimit() { return authorizationLimit; }
    public void setAuthorizationLimit(double authorizationLimit) { this.authorizationLimit = authorizationLimit; }
    public int getAssignedEvents() { return assignedEvents; }
    public void setAssignedEvents(int assignedEvents) { this.assignedEvents = assignedEvents; }
}
