package model;

public class UniversityAdministrator extends Staff {
    private int adminId;
    private double approvalLimit;
    private String office;
    private String councilRole;

    public UniversityAdministrator(int userId, String name, String email, String password,
                                   String employeeNo, String department, String designation,
                                   int adminId, double approvalLimit, String office, String councilRole) {
        super(userId, name, email, password, employeeNo, department, designation);
        this.adminId = adminId;
        this.approvalLimit = approvalLimit;
        this.office = office;
        this.councilRole = councilRole;
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "viewEvents", "approveBudget", "generateReport", "oversight" };
    }

    public boolean approveBudget(Budget budget) {
        return budget.validate(approvalLimit);
    }

    // From class diagram: generateReport(type): Report
    public Report generateReport(String type, Event[] events, int count) {
        Report report = new Report(type);
        switch (type) {
            case "events":
                report.compileEvents(events, count);
                break;
            case "budgets":
                report.compileBudgets(events, count);
                break;
            default:
                report.setContent("Unknown report type: " + type);
        }
        return report;
    }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public double getApprovalLimit() { return approvalLimit; }
    public void setApprovalLimit(double approvalLimit) { this.approvalLimit = approvalLimit; }
    public String getOffice() { return office; }
    public void setOffice(String office) { this.office = office; }
    public String getCouncilRole() { return councilRole; }
    public void setCouncilRole(String councilRole) { this.councilRole = councilRole; }
}
