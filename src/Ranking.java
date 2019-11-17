import java.io.Serializable;

public class Ranking implements Serializable {

    private String teamName;
    private int rank;
    private int matchesWon;
    private int matchesDrawn;
    private int matchesLost;
    private int goalsFor;
    private int goalsAgainst;
    private int points;
    private int goalDiff;
    private String medal;

    public Ranking() {
    }

    public Ranking(String teamName, int rank, int matchesWon, int matchesDrawn, int matchesLost, int goalsFor, int goalsAgainst, int points, int goalDiff, String medal) {
        this.teamName = teamName;
        this.rank = rank;
        this.matchesWon = matchesWon;
        this.matchesDrawn = matchesDrawn;
        this.matchesLost = matchesLost;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.points = points;
        this.goalDiff = goalDiff;
        this.medal = medal;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon = matchesWon;
    }

    public int getMatchesDrawn() {
        return matchesDrawn;
    }

    public void setMatchesDrawn(int matchesDrawn) {
        this.matchesDrawn = matchesDrawn;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public void setMatchesLost(int matchesLost) {
        this.matchesLost = matchesLost;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalDiff() {
        return goalDiff;
    }

    public void setGoalDiff(int goalDiff) {
        this.goalDiff = goalDiff;
    }

    public String getMedal() {
        return medal;
    }

    public void setMedal(String medal) {
        this.medal = medal;
    }

    public void addWin() {
        points = points + 3;
    }

    public void addDraw() {
        points = points + 1;
    }

    public void addGoalsFor(int goals) {
        goalsFor = goalsFor + goals;
    }

    public void addGoalsAgainst(int goals) {
        goalsAgainst = goalsAgainst + goals;
    }

    public void calculateGoalDifference() {
        setGoalDiff(getGoalsFor() - getGoalsAgainst());
    }

    public String createStringLineforTable() {
        String format = "%-15s%-10d%-10d%-10d%-10d%-10d%-10d%-10d%-10d%-10s%n";
        return String.format(format, teamName, rank, matchesWon, matchesDrawn, matchesLost, goalsFor, goalsAgainst, points, goalDiff, medal);
    }
}
