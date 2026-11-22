package model;

public class Budget {
    private int budgetId;
    private double allocated;
    private double planned;
    private double actual;

    // Constructor 1
    public Budget(int budgetId, double allocated, double planned) {
        this.budgetId = budgetId;
        this.allocated = allocated;
        this.planned = planned;
        this.actual = 0;
    }

    // Constructor 2 (overloading): no planned expenses yet
    public Budget(int budgetId, double allocated) {
        this(budgetId, allocated, 0);
    }

    // Relational + logical operators in one condition
    public boolean validate(double limit) {
        return allocated > 0 && allocated <= limit && planned <= allocated;
    }

    public boolean recordPlanned(double amount) {
        if (amount > 0 && planned + amount <= allocated) {
            planned = planned + amount;
            return true;
        }
        return false;
    }

    public boolean recordActual(double amount) {
        if (amount > 0) {
            actual = actual + amount;
            return true;
        }
        return false;
    }

    // Arithmetic operator
    public double getRemaining() {
        return allocated - actual;
    }

    // Type casting: double -> int
    public int getUsedPercent() {
        if (allocated <= 0) {
            return 0;
        }
        return (int) (actual / allocated * 100);
    }

    @Override
    public String toString() {
        return "Allocated " + allocated + ", Planned " + planned + ", Actual " + actual
                + ", Remaining " + getRemaining() + " (" + getUsedPercent() + "% used)";
    }

    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }
    public double getAllocated() { return allocated; }
    public void setAllocated(double allocated) { this.allocated = allocated; }
    public double getPlanned() { return planned; }
    public void setPlanned(double planned) { this.planned = planned; }
    public double getActual() { return actual; }
    public void setActual(double actual) { this.actual = actual; }
}
