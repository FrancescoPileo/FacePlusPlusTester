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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.univpm.s1055802.faceplusplustester.Faceset.FacesetMain;
import com.univpm.s1055802.faceplusplustester.Group.GroupMain;
import com.univpm.s1055802.faceplusplustester.Person.PersonMain;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Recognition.RecognitionIdentifyResults;
import com.univpm.s1055802.faceplusplustester.Recognition.RecognitionSearchResults;
import com.univpm.s1055802.faceplusplustester.Recognition.RecognitionVerifyResults;
import com.univpm.s1055802.faceplusplustester.Session.SessionActions;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomTrainingList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TrainingMain extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionsMenu fabMenu;
    private final String SAVED_TRAININGS = "saved_trainings";
    protected final int REQUEST_TRAINING_CREATE = 1;
    private final int REQUEST_TRAINING_INFO = 3;
    private final int REQUEST_VERIFY = 4;
    private final int REQUEST_IDENTIFY = 5;
    private final int REQUEST_SEARCH = 6;
    protected ArrayList<Training> trainingList;
    private CustomTrainingList dataAdapter;

    protected ListView listView = null;

    public enum OnClickIntent {getInfo, search, verify, identify};

    OnClickIntent action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        action = (OnClickIntent) getIntent().getSerializableExtra("onClickIntent");
        setContentView(R.layout.training_main);

        listView = (ListView) findViewById(R.id.list);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Trainings");

        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_group);
        setFabMenu(action);
        setSupportActionBar(toolbar);
        listView.setEnabled(false);
        retrieveTrainingsList();
    }



    /**
     * metodo usato per aggiungere un training di tipo Identify, richiamato dal floatingActionButton
     * @param v
     */
    public void IdentifyTraining(View v){
        fabMenu.collapse();
        Intent intent = new Intent(
                getApplicationContext(),
                GroupMain.class
        );
        intent.putExtra("onClickIntent", GroupMain.OnClickIntent.doTraining);
        startActivityForResult(intent, REQUEST_TRAINING_CREATE);

    }

    /**
     * metodo usato per aggiungere un training di tipo Verify, richiamato dal floatingActionButton
     * @param v
     */
    public void VerifyTraining(View v){
        fabMenu.collapse();
        Intent intent = new Intent(
                getApplicationContext(),
               PersonMain.class
        );
        intent.putExtra("onClickIntent", PersonMain.OnClickIntent.doTraining);
        startActivityForResult(intent, REQUEST_TRAINING_CREATE);

    }

    /**
     * metodo usato per aggiungere un training di tipo Search, richiamato dal floatingActionButton
     * @param v
     */
    public void SearchTraining(View v){
        Intent intent = new Intent(
                getApplicationContext(),
                FacesetMain.class
        );
        intent.putExtra("onClickIntent", FacesetMain.OnClickIntent.doTraining);
        startActivityForResult(intent, REQUEST_TRAINING_CREATE);

    }



    /**
     * inserisce le icone nella toolbar principale
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.training_toolbar, menu);
        return true;
    }

    /**
     * gestisce la toolbar principale
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh){
            checkStatusAll();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Controlla lo stato di tutti i Trainigs e aggiorna la schermata
     */
    private void checkStatusAll(){
        Toast.makeText(this, "Checking", Toast.LENGTH_SHORT).show();

        for (int i=0; i < trainingList.size(); i++){
            trainingList.get(i).setStatus(Training.Status.FAILED);
            SessionActions.GetSession getSession = new SessionActions.GetSession(this);
            final int finalI = i;
            boolean f = false;
            if (i == (trainingList.size()-1)){
                f = true;
            }
            final boolean finalF = f;
            getSession.setCallback(new Callback() {
                @Override
                public void processResult(final JSONObject rst) {
                        //la trainig list risulta vuota
                        TrainingMain.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    trainingList.get(finalI).setStatus(rst.getString("status"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (finalF) {
                                    TrainingActions.UpdateTrainings updateTrainings = new TrainingActions.UpdateTrainings(TrainingMain.this);
                                    updateTrainings.updateTrainings(trainingList);
                                    CustomTrainingList ctl = new CustomTrainingList(TrainingMain.this, 0, trainingList);
                                    listView.setAdapter(ctl);
                                    //ImageUtils.setListViewHeightBasedOnItems(listView);
                                }
                            }
                        });
                }
            });
            getSession.getSession(trainingList.get(i).getSessionId());
        }
    }


    /**
     * ottiene la lista dei Trainings
     */
    private void retrieveTrainingsList(){
        TrainingActions.getTrainings getTrainings = new TrainingActions.getTrainings(TrainingMain.this);
        getTrainings.setCallback(new Callback() {
            @Override
            public void processResult(JSONObject rst) {
                if (rst != null){
                setListView(rst);
                listView.setEnabled(true);
                }
            }
        });
        getTrainings.getTrainings(action);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fabMenu.collapse();
        switch (requestCode){
            case REQUEST_TRAINING_CREATE:
                    retrieveTrainingsList();
                break;
            case REQUEST_TRAINING_INFO:
                if (resultCode == RESULT_OK){
                    retrieveTrainingsList();
                }
                break;
            case REQUEST_IDENTIFY:
            case REQUEST_VERIFY:
            case REQUEST_SEARCH:
                setResult(RESULT_OK);
                finish();
                break;
        }
        listView.setEnabled(true);

    }

    /**
     * inserisce la lista dei Trainings nella list view
     * @param jsonObject è il JSON contentente la lista dei Trainings
     */
    protected void setListView(JSONObject jsonObject){
        trainingList = Training.trainingArrayFromJson(jsonObject);
        CustomTrainingList ctl = new CustomTrainingList(this, R.layout.list_text_2text, trainingList);
        listView.setAdapter(ctl);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                startIntent(action, ((((CustomTrainingList.ViewHolder)view.getTag()).getTraining())));
            }
        });

        listView.setVisibility(View.VISIBLE);
    }

    /**
     * Imposta il Floating Action Button Menu a seconda del motivo per il quale è richiamata la lista di Trainings
     * @param intent enum che gestisce i vari casi
     */
    private void setFabMenu(OnClickIntent intent){

        FloatingActionButton identify = (FloatingActionButton) findViewById(R.id.fab_identify);
        FloatingActionButton verify = (FloatingActionButton) findViewById(R.id.fab_verify);
        FloatingActionButton search = (FloatingActionButton) findViewById(R.id.fab_search);
        switch (intent){
            case identify:
                fabMenu.removeButton(verify);
                fabMenu.removeButton(search);
                break;
            case verify:
                fabMenu.removeButton(identify);
                fabMenu.removeButton(search);
                break;
            case search:
                fabMenu.removeButton(identify);
                fabMenu.removeButton(verify);
                break;
        }

    }

    /*private void defineActions(){
        View.OnClickListener deleteActionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTrainingFromJson(((Training)v.getTag()).getSessionId());
                ((CustomTrainingList) listView.getAdapter()).removeTrainingBySessionId(((Training)v.getTag()).getSessionId());
            }
        };

        View.OnClickListener checkActionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionActions.GetSession getSession = new SessionActions.GetSession(TrainingMain.this);
                getSession.setCallback(new Callback() {
                    @Override
                    public void processResult(JSONObject rst) {
                        TrainingMain.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //todo: creare l'oggetto di classe session (creare anche la classe), prendere il valore di status e aggiornare il json e ricaricare la lista
                                Toast toast = Toast.makeText(TrainingMain.this.getApplicationContext(), "Updated", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                });
                getSession.getSession(((Training)v.getTag()).getSessionId());
            }
        };
    }*/

    /*private void deleteTrainingFromJson(String sessionId){
        SharedPreferences sharedPref = getSharedPreferences("trainings", Context.MODE_PRIVATE);
        String jsonTrainingsString = sharedPref.getString(SAVED_TRAININGS, null);
        JSONObject jsonObject = null;

        if (jsonTrainingsString != null) {
            try {
                jsonObject = new JSONObject(jsonTrainingsString);
                for (int i=0; i< jsonObject.getJSONArray("training").length(); i++){
                    if (sessionId.equals(jsonObject.getJSONArray("training").getJSONObject(i).getString("session_id")))
                        jsonObject.getJSONArray("training").remove(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SAVED_TRAININGS, jsonObject.toString());
            editor.commit();
        }

    }*/


    /**
     * Imposta l'evento da lanciare al click a seconda del motivo per il quale è stata richiamata la
     * lista di Trainings
     *
     * @param intent enum che gestisce i vari casi
     * @param trainingInfo informazioni del training selezionato
     */
    private void startIntent(OnClickIntent intent, final Training trainingInfo){
        //todo controllo che il training sia andato a buon fine
        String faceId = getIntent().getStringExtra("face_id");
        switch (intent){
            case getInfo:
                Intent trainingInfoIntent = new Intent (getApplicationContext(), TrainingInfo.class);
                trainingInfoIntent.putExtra("session_id", trainingInfo.getSessionId());
                startActivityForResult(trainingInfoIntent, REQUEST_TRAINING_INFO);
                break;
            case verify:
                Intent verifyIntent = new Intent (getApplicationContext(), RecognitionVerifyResults.class);
                verifyIntent.putExtra("person_id", trainingInfo.getTargetId());
                verifyIntent.putExtra("face_id", faceId);
                startActivityForResult(verifyIntent, REQUEST_VERIFY);
                break;
            case identify:
                Intent identifyIntent = new Intent (getApplicationContext(), RecognitionIdentifyResults.class);
                identifyIntent.putExtra("group_id", trainingInfo.getTargetId());
                identifyIntent.putExtra("face_id", faceId);
                startActivityForResult(identifyIntent, REQUEST_IDENTIFY);
                break;
            case search:
                Intent searchIntent = new Intent (getApplicationContext(), RecognitionSearchResults.class);
                searchIntent.putExtra("faceset_id", trainingInfo.getTargetId());
                searchIntent.putExtra("face_id", faceId);
                startActivityForResult(searchIntent, REQUEST_SEARCH);
                break;
        }
    }




}
