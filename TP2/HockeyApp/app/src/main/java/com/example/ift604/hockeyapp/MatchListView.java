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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class MatchListView extends Activity {
    public static final String EXTRAS_KEY_ID = "id";
    private ArrayList<HashMap<String, String>> _matches;

    private Handler _handler = new Handler();
    private RefresherThread _refresher;
    private int _refreshInterval = 2 * 60 * 1000; // 2 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list_view);
        loadMatchesAsync();
        startRefresher();
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
            String json = null;
            try {
                UDPHelper udp = new UDPHelper("10.0.2.2", 8080);
                json = udp.sendAndReceive("ListerMatch");
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONArray jsonArray = new JSONParser().getJSONArrayFromString(json);
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jArray) {
            super.onPostExecute(jArray);
            pDialog.dismiss();
            if(jArray == null)
                return;
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
