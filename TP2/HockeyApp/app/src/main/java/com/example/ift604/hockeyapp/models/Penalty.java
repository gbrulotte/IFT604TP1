package com.example.ift604.hockeyapp.models;

/**
 * Created by arsp2901 on 2015-10-01.
 */
public class Penalty {
    public String player;
    public String reason;
    public String duration;
    public String time;

    public Penalty(String player, String reason, String duration, String time)
    {
        this.player = player;
        this.reason = reason;
        this.duration = duration;
        this.time = time;
    }
}