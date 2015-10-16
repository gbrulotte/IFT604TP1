package com.example.ift604.hockeyapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ift604.hockeyapp.models.Penalty;
import com.example.ift604.hockeyapp.models.JSONTags;
import com.example.ift604.hockeyapp.models.MatchDetails;
import com.example.ift604.hockeyapp.models.Goal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

public class MatchView extends Activity {

	//TODO Mettre l'adresse de l'API
    //URL to get JSON Array private static String url = "http://api.learn2crack.com/android/jsonos/";

    private String strJSON =
            "{\"" + JSONTags.TEAM_A + "\":\"Montréal\"," +
            "\"" + JSONTags.TEAM_B + "\":\"Toronto\"," +
            "\"" + JSONTags.SCORE_A + "\":5," +
            "\"" + JSONTags.SCORE_B + "\":0," +
            "\"" + JSONTags.CHRONO + "\":{\"value\":5}," +
            "\"" + JSONTags.GOALS + "\":[{\"" + JSONTags.Goals.TEAM + "\":\"Montréal\"," +
                                         "\"" + JSONTags.Goals.SCORER + "\":\"PK\"," +
                                         "\"" + JSONTags.Goals.ASSISTS + "\":\"Xxx xxx, Yyy yyy\"," +
                                         "\"" + JSONTags.Goals.TIME + "\":\"1ere | 04:12\"}," +
                    "{\"" + JSONTags.Goals.TEAM + "\":\"Montréal\"," +
                            "\"" + JSONTags.Goals.SCORER + "\":\"PK\"," +
                            "\"" + JSONTags.Goals.ASSISTS + "\":\"Xxx xxx, Yyy yyy\"," +
                            "\"" + JSONTags.Goals.TIME + "\":\"1ere | 04:12\"}," +
                    "{\"" + JSONTags.Goals.TEAM + "\":\"Montréal\"," +
                    "\"" + JSONTags.Goals.SCORER + "\":\"PK\"," +
                    "\"" + JSONTags.Goals.ASSISTS + "\":\"Xxx xxx, Yyy yyy\"," +
                    "\"" + JSONTags.Goals.TIME + "\":\"1ere | 04:12\"}," +
                    "{\"" + JSONTags.Goals.TEAM + "\":\"Montréal\"," +
                    "\"" + JSONTags.Goals.SCORER + "\":\"PK\"," +
                    "\"" + JSONTags.Goals.ASSISTS + "\":\"Xxx xxx, Yyy yyy\"," +
                    "\"" + JSONTags.Goals.TIME + "\":\"1ere | 04:19\"}," +
                                        "{\"" + JSONTags.Goals.TEAM + "\":\"Montréal\"," +
                                         "\"" + JSONTags.Goals.SCORER + "\":\"Pacio\"," +
                                         "\"" + JSONTags.Goals.ASSISTS + "\":\"Xxx xxx, Yyy yyy\"," +
                                         "\"" + JSONTags.Goals.TIME + "\":\"1ere | 09:12\"}" +
            "]," +
            "\"" + JSONTags.PENALTIES + "\":[{\"" + JSONTags.Penalties.PLAYER + "\":\"subby\"," +
                                             "\"" + JSONTags.Penalties.REASON + "\":\"High Stick\"," +
                                             "\"" + JSONTags.Penalties.DURATION + "\":\"2:00\"," +
                                             "\"" + JSONTags.Penalties.TIME + "\":\"3e | 16:24\"}," +
                                        "{\"" + JSONTags.Penalties.PLAYER + "\":\"subby\"," +
                                            "\"" + JSONTags.Penalties.REASON + "\":\"High Stick\"," +
                                            "\"" + JSONTags.Penalties.DURATION + "\":\"2:00\"," +
                                            "\"" + JSONTags.Penalties.TIME + "\":\"3e | 12:24\"}" +
            "]" +
            "}";

    private String _id = new UUID(0, 0).toString();
    private MatchDetails _match = null;

    private Handler _handler = new Handler();
    private RefresherThread _refresher;
    private int _refreshInterval = 2 * 60 * 1000; // 2 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            _id = bundle.getString(MatchListView.EXTRAS_KEY_ID);
        }
        //Toast.makeText(MatchView.this, _id.toString(), Toast.LENGTH_SHORT).show();
        loadMatchAsync();
        startRefresher();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_refresher.isPaused()) {
            Log.i("MatchView", "Resuming the refresher thread");
            _refresher.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MatchView", "Pausing the refresher thread");
        _refresher.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MatchView", "Stopping the refresher thread");
        _refresher.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_refresh) {
            loadMatchAsync();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMatchAsync() {
        Log.i("MatchView", "Refreshing the data");
        new JSONParse().execute();
    }

    private void startRefresher() {
        _refresher = new RefresherThread(_refreshInterval, _handler, new Runnable() {
            @Override
            public void run() {
                loadMatchAsync();
            }
        });
        Log.i("MatchView", "Starting the refresher thread");
        _refresher.start();
    }
	
	private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MatchView.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            String json = null;
            JSONObject jsonObject = null;

            try {
                UDPHelper udp = new UDPHelper("10.0.2.2", 8080);
                json = udp.sendAndReceive("MiseAJour~"+_id);
                // Getting JSON from URL
                //TODO change for that: JSONObject json = jParser.getJSONFromUrl(url);
                jsonObject = new JSONObject(json);
            }  catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject c) {
            pDialog.dismiss();
            try {
                if (c != null) {
                    _match = new MatchDetails();
                    _match.teamA = c.getString(JSONTags.TEAM_A);
                    _match.teamB = c.getString(JSONTags.TEAM_B);
                    _match.scoreA = c.getInt(JSONTags.SCORE_A);
                    _match.scoreB = c.getInt(JSONTags.SCORE_B);
                    _match.chrono = c.getJSONObject(JSONTags.CHRONO).getInt("value");

                    JSONArray goalsJSON = c.getJSONArray(JSONTags.GOALS);
                    JSONArray penaltiesJSON = c.getJSONArray(JSONTags.PENALTIES);

                    for (int j = 0; j < goalsJSON.length(); ++j){
                        JSONObject goal = goalsJSON.getJSONObject(j);

                        String team = goal.getString(JSONTags.Goals.TEAM);
                        String scorer = goal.getString(JSONTags.Goals.SCORER);
                        String assists = goal.getString(JSONTags.Goals.ASSISTS);
                        String time = goal.getString(JSONTags.Goals.TIME);
                        _match.goals.add(new Goal(team, scorer, assists, time));
                    }

                    for (int j = 0; j < penaltiesJSON.length(); ++j){
                        JSONObject penalty = penaltiesJSON.getJSONObject(j);

                        String player = penalty.getString(JSONTags.Penalties.PLAYER);
                        String reason = penalty.getString(JSONTags.Penalties.REASON);
                        String duration = penalty.getString(JSONTags.Penalties.DURATION);
                        String time = penalty.getString(JSONTags.Penalties.TIME);
                        _match.penalties.add(new Penalty(player, reason, duration, time));
                    }

                    // Mettre à jour le UI
                    TextView textView = (TextView)findViewById(R.id.match_teamA);
                    textView.setText(_match.teamA);
                    textView = (TextView)findViewById(R.id.match_teamB);
                    textView.setText(_match.teamB);

                    textView = (TextView)findViewById(R.id.match_scoreA);
                    textView.setText(Integer.toString(_match.scoreA));
                    textView = (TextView)findViewById(R.id.match_scoreB);
                    textView.setText(Integer.toString(_match.scoreB));

                    textView = (TextView)findViewById(R.id.match_period);
                    textView.setText(Integer.toString(_match.getPeriod()));
                    textView = (TextView)findViewById(R.id.match_time);
                    textView.setText(_match.getTime());

                    // Affichage des buts
                    displayGoals();

                    // Affichage des pénalités
                    displayPenalties();

                } else {
                    Toast.makeText(MatchView.this, "An error happened getting the data of the match.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void displayGoals() {
            LayoutInflater inflater = (LayoutInflater) MatchView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TableLayout table = (TableLayout)findViewById(R.id.match_view_goals_table);

            for (int i = 2; i < table.getChildCount();) {
                table.removeViewAt(2);
            }

            for (int i = 0; i < _match.goals.size(); i++) {
                Goal goal = _match.goals.get(i);
                View tr = inflater.inflate(R.layout.match_view_goal_row, null);

                TextView textView = (TextView)tr.findViewById(R.id.match_goal_team);
                textView.setText(goal.team);
                textView = (TextView)tr.findViewById(R.id.match_goal_player);
                textView.setText(goal.scorer);
                textView = (TextView)tr.findViewById(R.id.match_goal_assists);
                textView.setText(goal.assists);
                textView = (TextView)tr.findViewById(R.id.match_goal_time);
                textView.setText(goal.time);

                table.addView(tr);
            }
        }

        private void displayPenalties() {
            LayoutInflater inflater = (LayoutInflater) MatchView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TableLayout table = (TableLayout)findViewById(R.id.match_view_penalties_table);

            for (int i = 2; i < table.getChildCount();) {
                table.removeViewAt(2);
            }

            for (int i = 0; i < _match.penalties.size(); i++) {
                Penalty penalty = _match.penalties.get(i);
                View tr = inflater.inflate(R.layout.match_view_penalty_row, null);

                TextView textView = (TextView)tr.findViewById(R.id.match_penalty_player);
                textView.setText(penalty.player);
                textView = (TextView)tr.findViewById(R.id.match_penalty_fault);
                textView.setText(penalty.reason);
                textView = (TextView)tr.findViewById(R.id.match_penalty_length);
                textView.setText(penalty.duration);
                textView = (TextView)tr.findViewById(R.id.match_penalty_time);
                textView.setText(penalty.time);

                table.addView(tr);
            }
        }
    }
}
