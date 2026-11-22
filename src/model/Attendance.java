package model;

public class Attendance {
    private int attendanceId;
    private int registrationId;
    private boolean present;
    private String recordedAt;

    public Attendance(int attendanceId, int registrationId, boolean present) {
        this.attendanceId = attendanceId;
        this.registrationId = registrationId;
        this.present = present;
        this.recordedAt = "";
    }

    public Attendance(int attendanceId, int registrationId) {
        this(attendanceId, registrationId, false);
    }

    private String now() {
        return java.time.LocalDateTime.now().withNano(0).toString().replace('T', ' ');
    }

    public boolean markPresent() {
        present = true;
        recordedAt = now();
        return true;
    }

    public boolean markAbsent() {
        present = false;
        recordedAt = now();
        return true;
    }

    public boolean verifyRegistration(Registration reg) {
        return reg.getRegistrationId() == registrationId && reg.getStatus().equals("CONFIRMED");
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }
    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }
    public String getRecordedAt() { return recordedAt; }
    public void setRecordedAt(String recordedAt) { this.recordedAt = recordedAt; }
}
