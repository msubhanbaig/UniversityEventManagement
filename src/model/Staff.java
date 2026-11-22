package model;

/** Abstract child of User and parent of the three staff roles (multilevel inheritance). */
public abstract class Staff extends User {
    private String employeeNo;
    private String department;
    private String designation;
    private boolean active;

    public Staff(int userId, String name, String email, String password,
                 String employeeNo, String department, String designation) {
        super(userId, name, email, password);
        this.employeeNo = employeeNo;
        this.department = department;
        this.designation = designation;
        this.active = true;
    }

    @Override
    public abstract String[] getPermissions();   // still abstract: each staff role must implement it

    public boolean reviewEvent(Event event) {
        return active && !event.isCancelled();
    }

    public String viewReport(Report report) {
        if (active) {
            return report.getContent();
        }
        return "Access denied: inactive staff account.";
    }

    @Override
    public String describe() {
        return super.describe() + " | " + employeeNo + " | " + department + " | " + designation;
    }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
