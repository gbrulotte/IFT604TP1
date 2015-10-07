package com.example.ift604.hockeyapp.models;

import java.util.ArrayList;

/**
 * Created by veip1702 on 2015-10-02.
 */
public class MatchDetails {
    private static final int _periodLength = 1200;

    public String teamA;
    public String teamB;
    public int scoreA;
    public int scoreB;
    // MaxValue: 3600 seconds
    public int chrono;
    public ArrayList<Goal> goals;
    public ArrayList<Penalty> penalties;

    public MatchDetails() {
        goals = new ArrayList<>();
        penalties = new ArrayList<>();
    }

    public int getPeriod() {
        int tempChrono = ((chrono % _periodLength) == 0 ? chrono - 1 : chrono);
        return (3 - (tempChrono / _periodLength));
    }

    // TODO: 2015-10-02 Finir le code qui extrait le temps du nombre de secondes
    public String getTime() {
        int minutes = 20;
        int seconds = 0;
        if ((chrono % _periodLength) != 0){
            minutes = (chrono % _periodLength) / 60;
            seconds = (chrono % _periodLength) % 60;
        }
        return String.format("%02d:%02d", minutes, seconds);
    }
}
