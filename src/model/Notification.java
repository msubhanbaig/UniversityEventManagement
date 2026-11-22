package model;

public class Notification {
    private int notificationId;
    private int recipientId;
    private String message;
    private String deliveryStatus;

    // Constructor 1
    public Notification(int notificationId, int recipientId, String message) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.message = message;
        this.deliveryStatus = "PENDING";
    }

    // Constructor 2 (overloading): message prepared later
    public Notification(int notificationId, int recipientId) {
        this(notificationId, recipientId, "");
    }

    public boolean prepare(Event event) {
        message = "Event '" + event.getTitle() + "' on " + event.getSchedule()
                + " is now " + event.getStatus() + ".";
        return true;
    }

    // Method overloading: prepare(event) and prepare(event, note)
    public boolean prepare(Event event, String note) {
        message = note + " Event '" + event.getTitle() + "' on " + event.getSchedule() + ".";
        return true;
    }

    public boolean send() {
        if (message == null || message.isEmpty()) {
            return logFailure("empty message");
        }
        deliveryStatus = "DELIVERED";
        System.out.println("   [Notification -> user " + recipientId + "] " + message);
        return true;
    }

    public boolean logFailure(String reason) {
        deliveryStatus = "FAILED (" + reason + ")";
        return false;       // delivery failed; registration itself stays valid
    }

    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }
    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
}
