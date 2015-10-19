package com.example.ift604.hockeyapp.models;

/**
 * Created by P-O on 2015-10-18.
 */
public class ParisMessage {
    public String matchId;
    public String team;
    public int amount;

    public ParisMessage(String matchId, String team, int amount) {
        this.matchId = matchId;
        this.team = team;
        this.amount = amount;
    }
}
