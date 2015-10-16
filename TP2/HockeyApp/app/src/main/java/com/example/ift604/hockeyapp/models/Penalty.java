package com.example.ift604.hockeyapp.models;

/**
 * Created by arsp2901 on 2015-10-01.
 */
public class Penalty {
    public String team;
    public String player;
    public String infringement;
    public int duration;
    private int time;

    public Penalty(String team, String player, String infringement, int duration, int time)
    {
        this.team = team;
        this.player = player;
        this.infringement = infringement;
        this.duration = duration;
        this.time = time;
    }

    public String getTime() {
        return Integer.toString(MatchDetails.getPeriod(time)) + " | " + MatchDetails.getTime(time);
    }
}