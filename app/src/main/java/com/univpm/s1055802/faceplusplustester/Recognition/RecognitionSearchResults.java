/*
 *     FacePlusPlusTester - Android application to test the FacePlusPlus' APIs
 *     Copyright (C) 2016-2020  Francesco Antonio Pileo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.univpm.s1055802.faceplusplustester.Recognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.univpm.s1055802.faceplusplustester.Face.FaceInfo;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomSearchCandidateList;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecognitionSearchResults extends AppCompatActivity {

    private String facesetId;
    private String faceId;
    private ListView lstvInfo = null;
    private ListView lstvCandidates = null;
    private Toolbar toolbar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_identify_results);
        lstvInfo = (ListView) findViewById(R.id.lstvInfo);
        lstvCandidates = (ListView) findViewById(R.id.lstvCandidates);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        facesetId = getIntent().getStringExtra("faceset_id");
        faceId = getIntent().getStringExtra("face_id");

        getResults();
    }

    /**
     * Acquisisce il risultato della Search
     */
    private void getResults(){

        RecognitionActions.Search search = new RecognitionActions.Search(RecognitionSearchResults.this);
        search.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst){
                RecognitionSearchResults.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Recognition.SearchResults result = null;
                        try {
                            result = new Recognition.SearchResults(rst);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setLstvInfo(result);
                        setLstvCandidates(result);
                    }
                });

            }
        });
        search.search(faceId, facesetId);
    }

    /**
     * Inserisce le info dei risultati nella ListView appropriata
     * @param results informazioni da inserire
     */
    public void setLstvInfo(Recognition.SearchResults results) {

        ArrayList<String> recognitionInfo = new ArrayList<String>();
        recognitionInfo.add("Session id: " + results.getSessionId());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recognitionInfo);
        lstvInfo.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvInfo);

    }

    /**
     * Inserisce nella ListView i candidati risultanti dall'operazione di Search
     * @param results informazioni da inserire
     */
    public void setLstvCandidates(Recognition.SearchResults results){
        ArrayList<Recognition.SearchResults.Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < results.candidatesNumber; i++){
            candidates.add(results.getCandidate(i));
        }
        CustomSearchCandidateList adapter = new CustomSearchCandidateList(this, R.layout.list_2text, candidates);
        lstvCandidates.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvCandidates);

        lstvCandidates.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent faceInfoIntent = new Intent(getApplicationContext(), FaceInfo.class);
                faceInfoIntent.putExtra("face_id", ((Recognition.SearchResults.Candidate)(((CustomSearchCandidateList.ViewHolder)view.getTag()).getTag())).getFaceId());
                startActivity(faceInfoIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ok_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ok) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
