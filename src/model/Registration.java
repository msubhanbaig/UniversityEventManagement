package model;

public class Registration implements Cancellable {
    private int registrationId;
    private int studentId;
    private int eventId;
    private String status;          // PENDING, CONFIRMED, CANCELLED
    private String failReason = "";

    public Registration(int registrationId, int studentId, int eventId) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.status = "PENDING";
    }

    public boolean validateEligibility(Student student, Event event) {
        if (!student.isEligible()) {
            failReason = "Student is not eligible to register.";
            return false;
        }
        if (!event.getStatus().equals("PUBLISHED")) {
            failReason = "Registration is closed: event is " + event.getStatus() + ".";
            return false;
        }
        if (!event.hasSeat()) {
            failReason = "Event is full.";
            return false;
        }
        return true;
    }

    public boolean save() {
        status = "CONFIRMED";
        return true;
    }

    // Overloading: cancel()  and  cancel(String reason)
    public boolean cancel() {
        return cancel("Cancelled by student");
    }

    @Override
    public boolean cancel(String reason) {
        if (status.equals("CANCELLED")) {
            return false;
        }
        status = "CANCELLED";
        failReason = reason;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return status.equals("CANCELLED");
    }

    @Override
    public String toString() {
        return "[Registration #" + registrationId + "] student " + studentId + " -> event "
                + eventId + " | " + status;
    }

    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFailReason() { return failReason; }
}
