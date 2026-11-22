package model;

/** Event implements the Cancellable interface and OWNS a Budget (composition). */
public class Event implements Cancellable {
    private int eventId;
    private String title;
    private String schedule;
    private String status;           // DRAFT, CREATED, PUBLISHED, CANCELLED
    private Venue venue;             // association: assigned venue
    private Budget budget;           // COMPOSITION: Budget is created inside Event
    private int registeredCount;
    private String cancelReason = "";

    // Constructor 1: creates its own Budget object (composition)
    public Event(int eventId, String title, String schedule, double allocated, double planned) {
        this.eventId = eventId;
        this.title = title;
        this.schedule = schedule;
        this.status = "DRAFT";
        this.budget = new Budget(eventId, allocated, planned);
        this.registeredCount = 0;
    }

    // Constructor 2 (overloading): event without budget figures yet
    public Event(int eventId, String title, String schedule) {
        this(eventId, title, schedule, 0, 0);
    }

    public boolean save() {
        boolean complete = title != null && !title.isEmpty() && schedule != null
                && !schedule.isEmpty() && venue != null;
        if (complete) {
            status = "CREATED";
        }
        return complete;
    }

    public boolean publish() {
        if (status.equals("CREATED")) {
            status = "PUBLISHED";
            return true;
        }
        return false;
    }

    // Interface method
    @Override
    public boolean cancel(String reason) {
        if (status.equals("CANCELLED")) {
            return false;
        }
        status = "CANCELLED";
        cancelReason = reason;
        if (venue != null) {
            venue.release(this);       // venue becomes free again
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return status.equals("CANCELLED");
    }

    // Method overloading: update(title)  and  update(title, schedule)
    public boolean update(String newTitle) {
        if (isCancelled() || newTitle.isEmpty()) {
            return false;
        }
        title = newTitle;
        return true;
    }

    public boolean update(String newTitle, String newSchedule) {
        if (isCancelled() || newTitle.isEmpty() || newSchedule.isEmpty()) {
            return false;
        }
        if (venue != null && !newSchedule.equals(schedule)) {
            venue.release(this);
            String oldSchedule = schedule;
            schedule = newSchedule;
            if (!venue.reserve(this)) {          // new slot is busy: go back to the old one
                schedule = oldSchedule;
                venue.reserve(this);
                return false;
            }
        }
        title = newTitle;
        return true;
    }

    // ---- seat handling ----
    public int getSeatsLeft() {
        if (venue == null) {
            return 0;
        }
        return venue.getCapacity() - registeredCount;
    }

    public boolean hasSeat() {
        return getSeatsLeft() > 0;
    }

    public boolean addRegistered() {
        if (hasSeat()) {
            registeredCount++;
            return true;
        }
        return false;
    }

    public void removeRegistered() {
        if (registeredCount > 0) {
            registeredCount--;
        }
    }

    // Type casting: int -> double -> int
    public int getOccupancyPercent() {
        if (venue == null || venue.getCapacity() == 0) {
            return 0;
        }
        return (int) ((double) registeredCount / venue.getCapacity() * 100);
    }

    public boolean isOpenForRegistration() {
        return status.equals("PUBLISHED") && hasSeat();
    }

    @Override
    public String toString() {
        String venueName = (venue == null) ? "not assigned" : venue.getName();
        return "[Event #" + eventId + "] " + title + " | " + schedule + " | " + status
                + " | Venue: " + venueName + " | Registered: " + registeredCount
                + " (" + getOccupancyPercent() + "% full)";
    }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) { this.venue = venue; }
    public Budget getBudget() { return budget; }
    public int getRegisteredCount() { return registeredCount; }
    public String getCancelReason() { return cancelReason; }
}
