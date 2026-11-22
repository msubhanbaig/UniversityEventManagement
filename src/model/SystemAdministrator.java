package model;

public class SystemAdministrator extends Staff {
    private int adminId;
    private int accessLevel;
    private String lastAudit;
    private String accountScope;

    public SystemAdministrator(int userId, String name, String email, String password,
                               String employeeNo, String department, String designation,
                               int adminId, int accessLevel, String lastAudit, String accountScope) {
        super(userId, name, email, password, employeeNo, department, designation);
        this.adminId = adminId;
        this.accessLevel = accessLevel;
        this.lastAudit = lastAudit;
        this.accountScope = accountScope;
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "manageAccounts", "assignRoles" };
    }

    // From class diagram: createAccount(data): User  (UserData replaced by plain parameters)
    public User createAccount(int userId, String name, String email, String password, String role) {
        switch (role) {
            case "student":
                return new Student(userId, name, email, password, "NEW-" + userId);
            case "organizer":
                return new EventOrganizer(userId, name, email, password, "EMP-" + userId, userId, 1);
            default:
                return null;
        }
    }

    public boolean assignRole(User user, String role) {
        boolean validRole = role.equals("student") || role.equals("organizer") || role.equals("admin");
        return user != null && validRole && accessLevel >= 2;
    }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }
    public String getLastAudit() { return lastAudit; }
    public void setLastAudit(String lastAudit) { this.lastAudit = lastAudit; }
    public String getAccountScope() { return accountScope; }
    public void setAccountScope(String accountScope) { this.accountScope = accountScope; }
}
