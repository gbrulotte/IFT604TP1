package com.example.ift604.hockeyapp.models;

/**
 * Created by veip1702 on 2015-10-02.
 */
public final class JSONTags {
    public static final String ID = "id";
    public static final String TEAM_A = "teamA";
    public static final String TEAM_B = "teamB";
    public static final String SCORE_A = "scoreA";
    public static final String SCORE_B = "scoreB";
    public static final String CHRONO = "chrono";
    public static final String GOALS = "goals";
    public static final String PENALTIES = "penalties";

    public final class Goals {
        public static final String TEAM = "team";
        public static final String PLAYER = "player";
        public static final String ASSISTS = "assists";
        public static final String TIME = "time";
    }

    public final class Penalties {
        public static final String TEAM = "team";
        public static final String PLAYER = "player";
        public static final String INFRINGEMENT = "infringement";
        public static final String DURATION = "duration";
        public static final String TIME = "time";
    }
}
