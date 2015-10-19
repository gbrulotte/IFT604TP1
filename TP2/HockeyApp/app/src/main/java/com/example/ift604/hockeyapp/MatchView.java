package com.example.ift604.hockeyapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ift604.hockeyapp.models.ParisMessage;
import com.example.ift604.hockeyapp.models.Penalty;
import com.example.ift604.hockeyapp.models.JSONTags;
import com.example.ift604.hockeyapp.models.MatchDetails;
import com.example.ift604.hockeyapp.models.Goal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MatchView extends Activity {

    private static final String BET_KEY = "betsData";
    public static final String SERVER_IP = "10.0.2.2";

    private String _id = new UUID(0, 0).toString();
    private MatchDetails _match = null;

    private Handler _handler = new Handler();
    private RefresherThread _refresher;
    private int _refreshInterval = 2 * 60 * 1000; // 2 minutes

    private boolean _hasBet = false;
    private int _betAmount = 0;
    private String _betTeam = null;

    // Pour sauvegarder le data
    private SharedPreferences _betsData;

    public static Handler _uiHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            _id = bundle.getString(MatchListView.EXTRAS_KEY_ID);
        } else {
            Toast.makeText(MatchView.this, "L'identifiant du match n'a pas pu être chargé. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
            finish();
        }

        loadMatchAsync();
        startRefresher();

        refreshSpinnerTeam(Arrays.asList(""));

        // Ajouter l'event sur le clic si on a pas bet
        Button btnBet = (Button)findViewById(R.id.match_btnBetAmount);
        btnBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textView = (EditText) findViewById(R.id.match_txtBetAmount);
                Spinner spinner = (Spinner) findViewById(R.id.match_spBetTeam);

                int amount = textView.getText().toString().equals("") ? 0 : Integer.parseInt(textView.getText().toString());
                String team = spinner.getSelectedItem().toString();
                if (amount > 0 && !team.equals("")) {
                    saveBetDataAndSendToServer(amount, team);
                    hideBetSectionAndShowAmount(amount, team);
                } else if (amount <= 0) {
                    Toast.makeText(MatchView.this, "Vous devez entrer un montant plus grand que 0 $.", Toast.LENGTH_SHORT).show();
                } else if (team.equals("")) {
                    Toast.makeText(MatchView.this, "Vous devez choisir une équipe sur laquelle parier.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Va chercher les données sauvegardées sur les paris
        _betsData = getSharedPreferences(BET_KEY, MODE_PRIVATE);

        int amount = _betsData.getInt(_id + "_amount", 0);
        String team = _betsData.getString(_id + "_team", null);

        if (amount > 0 && team != null){
            hideBetSectionAndShowAmount(amount, team);
        }

        _uiHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 1:
                        Toast.makeText(MatchView.this, "Une erreur est survenue lors de l'envoi du pari au serveur.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        startService(new Intent(MatchView.this, ParisService.class));
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
        new GetMatchDetailsTask().execute();
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

    private void refreshSpinnerTeam(List<String> values) {
        Spinner spinner = (Spinner)findViewById(R.id.match_spBetTeam);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MatchView.this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void hideBetSectionAndShowAmount(int amount, String team) {
        _hasBet = true;
        _betAmount = amount;
        _betTeam = team;
        hideBetSectionAndShowMessage(String.format("Vous avez parié %d $ sur %s pour ce match.", _betAmount, _betTeam));
    }

    private void hideBetSectionAndShowMessage(String message) {
        View section = findViewById(R.id.match_section_bet);
        section.setVisibility(View.GONE);

        TextView textView = (TextView)findViewById(R.id.match_lblBet);
        textView.setVisibility(View.VISIBLE);
        textView.setText(message);
    }

    private void saveBetDataAndSendToServer(int amount, String team) {
        SharedPreferences.Editor edit = _betsData.edit();
        edit.putInt(_id + "_amount", amount);
        edit.putString(_id + "_team", team);
        edit.apply();
        Log.i("MatchView", "Saved bet");

        if (ParisService._serviceHandler != null) {
            Message msg = new Message();
            msg.obj = new ParisMessage(_id, team, amount);
            ParisService._serviceHandler.sendMessage(msg);
        }
    }
	
	private class GetMatchDetailsTask extends AsyncTask<String, String, MatchDetails> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MatchView.this);
            pDialog.setMessage("Getting the data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values.length > 0) {
                pDialog.setMessage(values[0]);
            }
        }

        @Override
        protected MatchDetails doInBackground(String... args) {
            MatchDetails match = null;
            try {
                UDPHelper udp = new UDPHelper(SERVER_IP, 8080);
                String json = udp.sendAndReceive("MiseAJour~" + _id);
                JSONObject jsonObject = new JSONObject(json);

                publishProgress("Parsing the data...");

                if (jsonObject != null) {
                    match = new MatchDetails();
                    match.teamA = jsonObject.getString(JSONTags.TEAM_A);
                    match.teamB = jsonObject.getString(JSONTags.TEAM_B);
                    match.scoreA = jsonObject.getInt(JSONTags.SCORE_A);
                    match.scoreB = jsonObject.getInt(JSONTags.SCORE_B);
                    match.chrono = jsonObject.getJSONObject(JSONTags.CHRONO).getInt("value");

                    JSONArray goalsJSON = jsonObject.getJSONArray(JSONTags.GOALS);
                    JSONArray penaltiesJSON = jsonObject.getJSONArray(JSONTags.PENALTIES);

                    for (int j = 0; j < goalsJSON.length(); ++j){
                        JSONObject goal = goalsJSON.getJSONObject(j);

                        String team = goal.getString(JSONTags.Goals.TEAM);
                        String scorer = goal.getString(JSONTags.Goals.PLAYER);
                        int time = goal.getInt(JSONTags.Goals.TIME);

                        JSONArray assistsJSON = goal.getJSONArray(JSONTags.Goals.ASSISTS);
                        String assists = "";
                        for (int i = 0; i < assistsJSON.length(); ++i) {
                            if (assists.equals("")) {
                                assists = assistsJSON.get(i).toString();
                            } else {
                                assists += "\n " + assistsJSON.get(i).toString();
                            }
                        }
                        match.goals.add(new Goal(team, scorer, assists, time));
                    }

                    for (int j = 0; j < penaltiesJSON.length(); ++j){
                        JSONObject penalty = penaltiesJSON.getJSONObject(j);

                        String team = penalty.getString(JSONTags.Penalties.TEAM);
                        String player = penalty.getString(JSONTags.Penalties.PLAYER);
                        String reason = penalty.getString(JSONTags.Penalties.INFRINGEMENT);
                        int duration = penalty.getInt(JSONTags.Penalties.DURATION);
                        int time = penalty.getInt(JSONTags.Penalties.TIME);
                        match.penalties.add(new Penalty(team, player, reason, duration, time));
                    }
                }

            }  catch (Exception e) {
                e.printStackTrace();
                match = null;
            }
            return match;
        }

        @Override
        protected void onPostExecute(MatchDetails newMatchDetails) {
            pDialog.dismiss();

            if (newMatchDetails != null) {
                // Si ce n'est pas la première fois qu'on fait le getData, on regarde s'il y a des nouveaux buts ou pénalités.
                if (_match != null) {
                    // Regarder s'il y a des nouveaux buts
                    for (int i = _match.goals.size(); i < newMatchDetails.goals.size(); i++) {
                        Goal goal = newMatchDetails.goals.get(i);
                        String message = String.format("Nouveau but pour %s (%s) !", goal.team, goal.player);
                        Toast.makeText(MatchView.this, message, Toast.LENGTH_LONG).show();
                    }

                    // Regarder s'il y a des nouvelles pénalités
                    for (int i = _match.penalties.size(); i < newMatchDetails.penalties.size(); i++) {
                        Penalty penalty = newMatchDetails.penalties.get(i);
                        String message = String.format("Penalité pour %s (%s). %d minutes pour %s.", penalty.player, penalty.team, penalty.duration, penalty.infringement);
                        Toast.makeText(MatchView.this, message, Toast.LENGTH_LONG).show();
                    }
                }
                _match = newMatchDetails;

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

                MatchView.this.refreshSpinnerTeam(Arrays.asList("", _match.teamA, _match.teamB));

                if (!_hasBet && _match.getPeriod() > 2) {
                    hideBetSectionAndShowMessage("Vous ne pouvez plus parier sur ce match.");
                }

                // Affichage des buts
                displayGoals();

                // Affichage des pénalités
                displayPenalties();

            } else {
                Toast.makeText(MatchView.this, "An error happened getting the data of the match.", Toast.LENGTH_SHORT).show();
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
                textView.setText(goal.player);
                textView = (TextView)tr.findViewById(R.id.match_goal_assists);
                textView.setText(goal.assists);
                textView = (TextView)tr.findViewById(R.id.match_goal_time);
                textView.setText(goal.getTime());

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

                TextView textView = (TextView)tr.findViewById(R.id.match_penalty_team);
                textView.setText(penalty.team);
                textView = (TextView)tr.findViewById(R.id.match_penalty_player);
                textView.setText(penalty.player);
                textView = (TextView)tr.findViewById(R.id.match_penalty_fault);
                textView.setText(penalty.infringement);
                textView = (TextView)tr.findViewById(R.id.match_penalty_length);
                textView.setText(Integer.toString(penalty.duration));
                textView = (TextView)tr.findViewById(R.id.match_penalty_time);
                textView.setText(penalty.getTime());

                table.addView(tr);
            }
        }
    }
}
