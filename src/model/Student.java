package model;

/** Child of User (hierarchical inheritance). */
public class Student extends User {
    private String studentNo;
    private String program;
    private int semester;
    private boolean eligible;

    // Constructor 1 (full)
    public Student(int userId, String name, String email, String password,
                   String studentNo, String program, int semester) {
        super(userId, name, email, password);          // super in constructor
        this.studentNo = studentNo;
        this.program = program;
        this.semester = semester;
        this.eligible = true;
    }

    // Constructor 2 (overloading): defaults for program and semester
    public Student(int userId, String name, String email, String password, String studentNo) {
        this(userId, name, email, password, studentNo, "BS(CS)", 1);
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "viewEvents", "registerForEvent", "cancelRegistration" };
    }

    // From class diagram: register(event): bool
    public boolean register(Event event) {
        return eligible && event.addRegistered();
    }

    // From class diagram: cancelRegistration(reg): bool
    public boolean cancelRegistration(Registration reg) {
        return reg.getStudentId() == getUserId() && reg.cancel();
    }

    @Override
    public String describe() {
        return super.describe() + " | " + studentNo + " | " + program + " sem " + semester;   // super in method
    }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
}
