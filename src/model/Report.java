package model;

public class Report {
    private static int nextId = 1;       // shared counter for report ids
    private int reportId;
    private String reportType;
    private String generatedAt;
    private String content;

    public Report(String reportType) {
        this(nextId++, reportType);
    }

    public Report(int reportId, String reportType) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.generatedAt = java.time.LocalDateTime.now().withNano(0).toString().replace('T', ' ');
        this.content = "";
    }

    // Arrays are passed to these methods
    public boolean compileEvents(Event[] events, int count) {
        if (events == null || count <= 0) {
            content = "No events recorded.";
            return false;
        }
        String text = "EVENT REPORT\n";
        for (int i = 0; i < count; i++) {
            text += "  " + events[i] + "\n";
        }
        content = text + "Total events: " + count;
        return true;
    }

    public boolean compileBudgets(Event[] events, int count) {
        if (events == null || count <= 0) {
            content = "No budgets recorded.";
            return false;
        }
        double totalAllocated = 0;
        double totalActual = 0;
        String text = "BUDGET vs ACTUAL REPORT\n";
        for (int i = 0; i < count; i++) {
            Budget b = events[i].getBudget();
            text += "  " + events[i].getTitle() + ": " + b + "\n";
            totalAllocated += b.getAllocated();
            totalActual += b.getActual();
        }
        content = text + "TOTAL allocated " + totalAllocated + " | TOTAL actual " + totalActual
                + " | Difference " + (totalAllocated - totalActual);
        return true;
    }

    public boolean compileParticipation(Registration[] regs, int count) {
        int confirmed = 0;
        int cancelled = 0;
        for (int i = 0; i < count; i++) {
            if (regs[i].getStatus().equals("CONFIRMED")) {
                confirmed++;
            } else if (regs[i].isCancelled()) {
                cancelled++;
            }
        }
        content = "PARTICIPATION REPORT\n  Total registrations: " + count + "\n  Confirmed: "
                + confirmed + "\n  Cancelled: " + cancelled;
        return true;
    }

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
