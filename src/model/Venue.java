package model;

public class Venue {
    private int venueId;
    private String name;
    private int capacity;
    private String location;
    private String[] bookedSlots = new String[20];   // array of booked date/time slots
    private int bookedCount = 0;

    // Constructor 1
    public Venue(int venueId, String name, int capacity, String location) {
        this.venueId = venueId;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
    }

    // Constructor 2 (overloading): default location
    public Venue(int venueId, String name, int capacity) {
        this(venueId, name, capacity, "Main Campus");
    }

    public boolean isAvailable(String slot) {
        for (int i = 0; i < bookedCount; i++) {
            if (bookedSlots[i].equals(slot)) {
                return false;
            }
        }
        return true;
    }

    // Method overloading: same name, extra parameter
    public boolean isAvailable(String slot, int minCapacity) {
        return capacity >= minCapacity && isAvailable(slot);
    }

    public boolean reserve(Event event) {
        String slot = event.getSchedule();
        if (!isAvailable(slot) || bookedCount >= bookedSlots.length) {
            return false;                       // prevents double booking
        }
        bookedSlots[bookedCount] = slot;
        bookedCount++;
        return true;
    }

    public boolean release(Event event) {
        for (int i = 0; i < bookedCount; i++) {
            if (bookedSlots[i].equals(event.getSchedule())) {
                for (int j = i; j < bookedCount - 1; j++) {
                    bookedSlots[j] = bookedSlots[j + 1];
                }
                bookedCount--;
                return true;
            }
        }
        return false;
    }

    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
