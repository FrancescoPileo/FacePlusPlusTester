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

import com.univpm.s1055802.faceplusplustester.Person.PersonInfo;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomIdentifyCandidateList;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 14/07/16.
 */
public class RecognitionIdentifyResults extends AppCompatActivity {

    private String groupId;
    private String faceId;
    private ListView lstvInfo = null;
    private ListView lstvCandidates = null;
    private Toolbar toolbar = null;

    private final int REQUEST_PERSON_INFO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_identify_results);
        lstvInfo = (ListView) findViewById(R.id.lstvInfo);
        lstvCandidates = (ListView) findViewById(R.id.lstvCandidates);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        groupId = getIntent().getStringExtra("group_id");
        faceId = getIntent().getStringExtra("face_id");

        getResults();
    }

    /**
     * Acquisisce il risultato della Identify
     */
    private void getResults(){

        RecognitionActions.Identify identify = new RecognitionActions.Identify(RecognitionIdentifyResults.this);
        identify.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst){
                RecognitionIdentifyResults.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Recognition.IdentifyResults result = null;
                        try {
                            result = new Recognition.IdentifyResults(rst);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setLstvInfo(result);
                        setLstvCandidates(result);
                    }
                });

            }
        });
        identify.identify(faceId, groupId);
    }

    /**
     * Inserisce le info dei risultati nella ListView appropriata
     * @param results informazioni da inserire
     */
    public void setLstvInfo(Recognition.IdentifyResults results) {

        ArrayList<String> recognitionInfo = new ArrayList<String>();
        recognitionInfo.add("Face id: " + results.getFaceId());
        recognitionInfo.add("Session id: " + results.getSessionId());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recognitionInfo);
        lstvInfo.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvInfo);

    }

    /**
     * Inserisce nella ListView i candidati risultanti dall'operazione di Identify
     * @param results informazioni da inserire
     */
    public void setLstvCandidates(Recognition.IdentifyResults results){
        ArrayList<Recognition.IdentifyResults.Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < results.candidatesNumber; i++){
            candidates.add(results.getCandidate(i));
        }
        CustomIdentifyCandidateList adapter = new CustomIdentifyCandidateList(this, R.layout.list_2text, candidates);
        lstvCandidates.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvCandidates);

        lstvCandidates.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent personInfoIntent = new Intent (getApplicationContext(), PersonInfo.class);
                personInfoIntent.putExtra("person_id", ((Recognition.IdentifyResults.Candidate)(((CustomIdentifyCandidateList.ViewHolder)view.getTag()).getTag())).getPersonId());
                personInfoIntent.putExtra("locked", "true");
                startActivityForResult(personInfoIntent, REQUEST_PERSON_INFO);
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
