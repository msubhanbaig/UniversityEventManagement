package model;

/** Abstract parent of every person in the system (Assignment 3: User). */
public abstract class User {
    private int userId;
    private String name;
    private String email;
    private String passwordHash;

    public User(int userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = hash(password);
    }

    // Very simple hashing (no outside libraries allowed): password is never stored as plain text.
    protected static String hash(String text) {
        return String.valueOf(text.hashCode());
    }

    // ---- operations from the class diagram ----
    public boolean login(String password) {
        return passwordHash.equals(hash(password));
    }

    // Abstract method: every role must define its own permissions (overridden in child classes).
    public abstract String[] getPermissions();

    public boolean hasPermission(String permission) {
        String[] perms = getPermissions();          // runtime polymorphism: child version runs
        for (int i = 0; i < perms.length; i++) {
            if (perms[i].equals(permission)) {
                return true;
            }
        }
        return false;
    }

    // Takes an ARRAY as a parameter and returns the matching events.
    public Event[] viewEvents(Event[] events, int count, String query) {
        String q = query.trim().toLowerCase();
        int matches = 0;
        for (int i = 0; i < count; i++) {
            if (isVisible(events[i], q)) {
                matches++;
            }
        }
        Event[] result = new Event[matches];
        int k = 0;
        for (int i = 0; i < count; i++) {
            if (isVisible(events[i], q)) {
                result[k++] = events[i];
            }
        }
        return result;
    }

    private boolean isVisible(Event e, String q) {
        boolean notDraft = !e.getStatus().equals("DRAFT");
        boolean titleMatches = q.isEmpty() || e.getTitle().toLowerCase().contains(q);
        return notDraft && titleMatches;
    }

    public String describe() {
        return getClass().getSimpleName() + " #" + userId + ": " + name + " <" + email + ">";
    }

    // ---- getters / setters ----
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPassword(String newPassword) { this.passwordHash = hash(newPassword); }
}
