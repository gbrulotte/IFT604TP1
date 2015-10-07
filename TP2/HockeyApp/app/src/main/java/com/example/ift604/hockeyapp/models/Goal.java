package com.example.ift604.hockeyapp.models;

/**
 * Created by arsp2901 on 2015-10-01.
 */
public class Goal {
    public String team;
    public String scorer;
    public String assists;
    public String time;

    public Goal(String team, String scorer, String assists, String time)
    {
        this.team = team;
        this.scorer = scorer;
        this.assists = assists;
        this.time = time;
    }
}
