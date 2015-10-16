package com.example.ift604.hockeyapp.models;

/**
 * Created by arsp2901 on 2015-10-01.
 */
public class Penalty {
    public String player;
    public String reason;
    public int duration;
    private int time;

    public Penalty(String player, String reason, int duration, int time)
    {
        this.player = player;
        this.reason = reason;
        this.duration = duration;
        this.time = time;
    }

    public String getTime() {
        return Integer.toString(MatchDetails.getPeriod(time)) + " | " + MatchDetails.getTime(time);
    }
}