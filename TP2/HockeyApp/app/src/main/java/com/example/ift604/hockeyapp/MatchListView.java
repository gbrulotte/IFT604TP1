package com.example.ift604.hockeyapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.ift604.hockeyapp.models.JSONTags;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchListView extends Activity {

    public static final String json_data = "[{\"id\":\"195a2b3b-edab-4e72-a6a7-6d47d6611d41\",\"teamA\":\"Canadiens de Montréal\",\"teamB\":\"Maple Leafs de Toronto\",\"scoreA\":2,\"scoreB\":1}," +
                                            "{\"id\":\"3cb75f1c-893f-4a59-9792-8091c582755a\",\"teamA\":\"Canucks de Vancouver\",\"teamB\":\"Senateurs d'Ottawa\",\"scoreA\":3,\"scoreB\":5}," +
                                            "{\"id\":\"ea8a87c1-22b0-45a1-986e-43a9dfda50ec\",\"teamA\":\"Sharks de San José\",\"teamB\":\"Pitbull de Hamilton\",\"scoreA\":2,\"scoreB\":2}]";


    public static final String EXTRAS_KEY_ID = "id";
    private ArrayList<HashMap<String, String>> _matches;

    private Handler _handler = new Handler();
    private RefresherThread _refresher;
    private int _refreshInterval = 10 * 1000; // 2 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list_view);
        loadMatchesAsync();
        startRefresher();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_refresher.isPaused()) {
            Log.i("MatchListView", "Resuming the refresher thread");
            _refresher.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MatchListView", "Pausing the refresher thread");
        _refresher.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MatchListView", "Stopping the refresher thread");
        _refresher.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match_list_view, menu);
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
            loadMatchesAsync();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startRefresher() {
        _refresher = new RefresherThread(_refreshInterval, _handler, new Runnable() {
            @Override
            public void run() {
                loadMatchesAsync();
            }
        });
        Log.i("MatchListView", "Starting the refresher thread");
        _refresher.start();
    }

    private void loadMatchesAsync() {
        Log.i("MatchListView", "Refreshing the data");
        new GetMatchesTask().execute();
    }

    private class GetMatchesTask extends AsyncTask<Void, Void, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MatchListView.this);
            pDialog.setMessage("Getting the matches...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONArray jArray = new JSONParser().getJSONArrayFromString(json_data);
            return jArray;
        }

        @Override
        protected void onPostExecute(JSONArray jArray) {
            super.onPostExecute(jArray);
            pDialog.dismiss();
            try {
                _matches = new ArrayList<>();
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObj = jArray.getJSONObject(i);

                    HashMap<String, String> map = new HashMap<>();
                    map.put(JSONTags.ID, jObj.getString(JSONTags.ID));
                    map.put(JSONTags.TEAM_A, jObj.getString(JSONTags.TEAM_A));
                    map.put(JSONTags.TEAM_B, jObj.getString(JSONTags.TEAM_B));
                    map.put(JSONTags.SCORE_A, Integer.toString(jObj.getInt(JSONTags.SCORE_A)));
                    map.put(JSONTags.SCORE_B, Integer.toString(jObj.getInt(JSONTags.SCORE_B)));

                    _matches.add(map);
                }

                // Met à jour le UI
                ListAdapter adapter = new SimpleAdapter(MatchListView.this, _matches,
                        R.layout.listview_item_match,
                        new String[] { JSONTags.TEAM_A, JSONTags.TEAM_B, JSONTags.SCORE_A, JSONTags.SCORE_B },
                        new int[] { R.id.listview_txtTeamA, R.id.listview_txtTeamB, R.id.listview_txtScoreA, R.id.listview_txtScoreB });
                ListView list = (ListView)findViewById(R.id.list_matches);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MatchListView.this, MatchView.class);
                        intent.putExtra(EXTRAS_KEY_ID, _matches.get(position).get(JSONTags.ID));
                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}