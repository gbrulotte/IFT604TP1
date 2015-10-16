package com.example.ift604.hockeyapp.models;

/**
 * Created by arsp2901 on 2015-10-01.
 */
public class Goal {
    public String team;
    public String player;
    public String assists;
    private int time;

    public Goal(String team, String player, String assists, int time)
    {
        this.team = team;
        this.player = player;
        this.assists = assists;
        this.time = time;
    }

    public String getTime() {
        return Integer.toString(MatchDetails.getPeriod(time)) + " | " + MatchDetails.getTime(time);
    }
}
