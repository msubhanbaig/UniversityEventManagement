package model;

public class Guild {
    private int guildId;
    private String name;
    private int presidentId;
    private String status;
    private Student[] members = new Student[30];
    private int memberCount = 0;
    private String[] activities = new String[10];
    private int activityCount = 0;

    public Guild(int guildId, String name, int presidentId, String status) {
        this.guildId = guildId;
        this.name = name;
        this.presidentId = presidentId;
        this.status = status;
    }

    public Guild(int guildId, String name, int presidentId) {
        this(guildId, name, presidentId, "ACTIVE");
    }

    public boolean updateDetails(String newName) {
        if (newName == null || newName.isEmpty()) {
            return false;
        }
        name = newName;
        return true;
    }

    public boolean addMember(Student student) {
        if (memberCount >= members.length) {
            return false;
        }
        members[memberCount++] = student;
        return true;
    }

    public boolean addActivity(String activity) {
        if (activityCount >= activities.length) {
            return false;
        }
        activities[activityCount++] = activity;
        return true;
    }

    public String listActivities() {
        String text = "";
        for (int i = 0; i < activityCount; i++) {
            text += (i + 1) + ". " + activities[i] + "  ";
        }
        return text.isEmpty() ? "No activities" : text;
    }

    public String summary() {
        return "Guild #" + guildId + " " + name + " | President id " + presidentId + " | "
                + status + " | Members: " + memberCount + " | Activities: " + listActivities();
    }

    public int getGuildId() { return guildId; }
    public void setGuildId(int guildId) { this.guildId = guildId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPresidentId() { return presidentId; }
    public void setPresidentId(int presidentId) { this.presidentId = presidentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
