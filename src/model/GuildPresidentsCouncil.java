package model;

public class GuildPresidentsCouncil {
    private int councilId;
    private String term;
    private int chairId;
    private int meetingCount;
    private User[] presidents = new User[20];
    private int presidentCount = 0;
    private String[] decisions = new String[20];
    private int decisionCount = 0;

    public GuildPresidentsCouncil(int councilId, String term, int chairId) {
        this.councilId = councilId;
        this.term = term;
        this.chairId = chairId;
        this.meetingCount = 0;
    }

    public boolean addPresident(User user) {
        if (user == null || presidentCount >= presidents.length) {
            return false;
        }
        presidents[presidentCount++] = user;
        return true;
    }

    public boolean recordMeeting(String date) {
        if (date == null || date.isEmpty()) {
            return false;
        }
        meetingCount++;
        return true;
    }

    public boolean recordDecision(String text) {
        if (text == null || text.isEmpty() || decisionCount >= decisions.length) {
            return false;
        }
        decisions[decisionCount++] = text;
        return true;
    }

    public String summary() {
        return "Council #" + councilId + " (" + term + ") | Chair id " + chairId + " | Presidents: "
                + presidentCount + " | Meetings: " + meetingCount + " | Decisions: " + decisionCount;
    }

    public int getCouncilId() { return councilId; }
    public void setCouncilId(int councilId) { this.councilId = councilId; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public int getChairId() { return chairId; }
    public void setChairId(int chairId) { this.chairId = chairId; }
    public int getMeetingCount() { return meetingCount; }
    public void setMeetingCount(int meetingCount) { this.meetingCount = meetingCount; }
}
