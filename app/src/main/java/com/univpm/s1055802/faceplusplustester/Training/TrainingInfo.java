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

package com.univpm.s1055802.faceplusplustester.Training;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Session.Session;
import com.univpm.s1055802.faceplusplustester.Session.SessionActions;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 17/06/16.
 */
public class TrainingInfo extends AppCompatActivity{

    private ListView lstvInfo = null;
    private TextView txtvInfo = null;
    private Toolbar toolbar;

    private String sessionId = null;
    private String sessionString = null;
    private JSONObject sessionJSON = null;

    private Training training = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_info);
        sessionId = getIntent().getStringExtra("session_id");


        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        lstvInfo = (ListView) findViewById(R.id.lstvInfo);
        txtvInfo = (TextView) findViewById(R.id.txtvInfo);
        txtvInfo.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);

        retrieveTrainingInfo(sessionId);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Inserisce nell ListView le informazioni riguardanti il Training e la sua Session
     *
     * @param session Session del training
     * @param training Informazioni del Training
     */
    private void setLstvInfo(Session session, Training training){
        ArrayList<String> trainingInfo = new ArrayList<String>();
        trainingInfo.add("Session Id: " + session.getSessionId());
        trainingInfo.add("Target: " + training.getTarget());
        trainingInfo.add("Target Id: " + training.getTargetId());
        trainingInfo.add("Target Name: " + training.getTargetName());
        trainingInfo.add("Date: " + training.getDate());
        trainingInfo.add("Timestamp Create: " + session.getCreateTime());
        trainingInfo.add("Timestamp Finish: " + session.getFinishTime());
        int elapsed =  (session.getFinishTime() - session.getCreateTime());
        trainingInfo.add("Elapsed time (s): " + elapsed);
        trainingInfo.add("Status: " + session.getStatus());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trainingInfo);
        lstvInfo.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvInfo);
    }

    /**
     * recupera le informazioni del training locale
     * @param sessionId id della sessione della quale recuperare le info
     */
    private void retrieveTrainingInfo(final String sessionId){
        //prende informazioni dal training locale
        TrainingActions.SearchTraining searchTraining = new TrainingActions.SearchTraining(TrainingInfo.this);
        searchTraining.setCallback(new Callback() {
            @Override
            public void processResult(JSONObject rst) {
                if (rst != null){
                    try {
                        training = new Training(rst);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        searchTraining.searchTraining(sessionId);
        final Training finalTraining = training;
        //prende informazioni dalla session remota
        SessionActions.GetSession getSessionInfo = new SessionActions.GetSession(TrainingInfo.this);
        getSessionInfo.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                TrainingInfo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sessionJSON = rst;
                        sessionString = rst.toString();
                        Session session = null;
                        try {
                            session = new Session(rst);
                            TrainingActions.UpdateTraining updateTraining = new TrainingActions.UpdateTraining(TrainingInfo.this);
                            updateTraining.updateTraining(sessionId, rst.getString("status"));
                            setLstvInfo(session, finalTraining);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        txtvInfo.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
        TrainingInfo.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(TrainingInfo.this.getApplicationContext(), "Retrieving data", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        getSessionInfo.getSession(sessionId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainlist_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            confirmDeleteDialog();
        }
        if (id == R.id.action_refresh){
            retrieveTrainingInfo(sessionId);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * visualizza la finestra di dialogo per confermare l'eliminazione del Training
     * in caso di risposta positiva effettua l'eliminazione
     */
    private void confirmDeleteDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Confirm delete?")
                .setIcon(R.drawable.fpp_delete_black)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        TrainingActions.DeleteTraining deleteTraining = new TrainingActions.DeleteTraining(TrainingInfo.this);
                        deleteTraining.setCallback(new Callback() {
                            @Override
                            public void processResult(JSONObject rst) {
                                TrainingInfo.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast toast = Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                        });
                        deleteTraining.deleteTraining(sessionId);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

}
