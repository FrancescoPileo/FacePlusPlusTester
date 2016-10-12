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

package com.univpm.s1055802.faceplusplustester.Person;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Training.Training;
import com.univpm.s1055802.faceplusplustester.Training.TrainingActions;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomCheckList;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PersonMain extends AppCompatActivity {

    private Toolbar toolbar;
    private Toolbar deleteToolbar;
    FloatingActionButton fab;
    private final String SAVED_PERSONS = "saved_persons";
    protected final int REQUEST_PERSON_CREATE = 1;
    private final int REQUEST_PERSON_INFO = 3;
    protected ArrayList<Info> personList;
    private CustomCheckList dataAdapter;

    protected ListView listView = null;

    public enum OnClickIntent {getInfo, assignFace, doTraining}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);

        listView = (ListView) findViewById(R.id.list);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Persons");
        deleteToolbar = (Toolbar) findViewById(R.id.deleteToolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPerson(v);
            }
        });

        setSupportActionBar(toolbar);

        deleteToolbar.inflateMenu(R.menu.delete_toolbar);
        setDeleteToolbar();

        listView.setEnabled(false);
        retrievePersonsList();

    }

    /**
     * metodo usato per aggiungere un persona, richiamato dal floatingActionButton
     * @param v
     */
    public void createPerson(View v){
        Intent intent = new Intent(
                            getApplicationContext(),
                            PersonCreate.class
                        );
        startActivityForResult(intent, REQUEST_PERSON_CREATE);
    }

    /**
     * inserisce le icone nella toolbar principale
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainlist_toolbar, menu);
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
        if (id == R.id.action_delete) {
            enableCheckbox(true);
            showDeleteToolbar(true);
        }
        if (id == R.id.action_refresh){
            getPersonListFromFpp();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * gestisce la toolbar di eliminazione
     */
    private void setDeleteToolbar(){
        deleteToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_cancel:
                        selectAll(false);
                        enableCheckbox(false);
                        showDeleteToolbar(false);
                        break;
                    case R.id.action_all:
                        selectAll(true);
                        break;
                    case R.id.action_ok:
                        PersonActions.DeletePersons deletePersons = new PersonActions.DeletePersons(PersonMain.this);
                        deletePersons.setCallback(new Callback() {
                            @Override
                            public void processResult(JSONObject rst) {
                                PersonMain.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(PersonMain.this.getApplicationContext(), "Deleted", Toast.LENGTH_SHORT);
                                        toast.show();
                                        getPersonListFromFpp();
                                        selectAll(false);
                                        enableCheckbox(false);
                                        showDeleteToolbar(false);
                                    }
                                });
                            }
                        });
                        deletePersons.deletePersons(deletingPersons());
                        break;
                }
                return true;
            }
        });
    }

    JSONObject deletingPersons(){
        JSONObject deletingListObj = new JSONObject();
        JSONArray deletingListArray = new JSONArray();
        for (int i = 0; i<personList.size(); i++ ){
            Info person = personList.get(i);
            if (person.isChecked() == true){
                deletingListArray.put(person.getName());
            }
        }
        try {
            deletingListObj.put("person_delete", deletingListArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deletingListObj;
    }

    /**
     * visualizza la toolbar di eliminazione
     */
    private void showDeleteToolbar(boolean flag){
        if (flag) {
            toolbar.setVisibility(View.GONE);
            deleteToolbar.setVisibility(View.VISIBLE);
        } else {
            deleteToolbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        }
    }



    /**
     *  abilita tutte le CheckBox per permettere l'eliminazione di più persone
     */
    private void enableCheckbox(boolean flag){
        if (dataAdapter!=null) {
            dataAdapter.setEnableAll(flag);
            dataAdapter.notifyDataSetChanged();
        }

    }

    /**
     *  seleziona tutte le checkbox
     */
    private void selectAll(boolean flag){
        if (dataAdapter!=null) {
            dataAdapter.setCheckAll(flag);
            dataAdapter.notifyDataSetChanged();
        }
    }

    /**
     * ottiene la lista di persone gestendo i diversi casi
     */
    private void retrievePersonsList(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String jsonPersonsToString = sharedPref.getString(SAVED_PERSONS, null);
        JSONObject jsonObject = null;

        if (jsonPersonsToString == null){
            getPersonListFromFpp();
        } else {
            try {
                jsonObject = new JSONObject(jsonPersonsToString);
            } catch (JSONException e){
                e.printStackTrace();
            }
            checkAndSetListView(jsonObject);
        }
        listView.setEnabled(true);
    }


    /**
     * recupera la lista delle persone dal server di fpp
     */
    private void getPersonListFromFpp(){
        listView.setVisibility(View.INVISIBLE);
        PersonActions.GetPersonsList personList = new PersonActions.GetPersonsList(PersonMain.this);
        personList.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                PersonMain.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setListView(rst);
                        setSharedPreferences(rst);
                    }
                });

            }
        });
        PersonMain.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(PersonMain.this.getApplicationContext(), "Updating", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        personList.getPersonsList();

    }

    /**
     * controlla la data in cui è stata creata la copia della lista delle persone e nel caso la aggiorna e setta
     * la list view
     * @param jsonObject JSON contentente le informazioni sulla lista delle persone
     */
    private void checkAndSetListView(JSONObject jsonObject){
        try {
            String dateString = jsonObject.getString("date");
            DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date date = format.parse(dateString);
            Date actualDate = new Date();
            long millisDiff = actualDate.getTime() - date.getTime();

            int seconds = (int) (millisDiff / 1000 % 60);
            int minutes = (int) (millisDiff / 60000 % 60);
            int hours = (int) (millisDiff / 3600000 % 24);
            int days = (int) (millisDiff / 86400000);

            if (days > 0 || hours > 2) {
                getPersonListFromFpp();
            } else {
                setListView(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_PERSON_CREATE:
                if (resultCode == Activity.RESULT_OK) {
                    getPersonListFromFpp();
                }
                break;
            case REQUEST_PERSON_INFO:
                if (resultCode == RESULT_OK){
                    getPersonListFromFpp();
                }
                selectAll(false);
                enableCheckbox(false);
                showDeleteToolbar(false);
                break;
        }
        listView.setEnabled(true);

    }

    /**
     * salva la stringa contenente la lista delle persone nelle risorse locali
     * @param jsonObject è il JSON contentente la lista delle persone
     */
    private void setSharedPreferences(JSONObject jsonObject){
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            jsonObject.put("date", timeStamp);
            String resultString = jsonObject.toString();
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SAVED_PERSONS, resultString);
            editor.commit();
        } catch (JSONException e){
            e.printStackTrace();
        }


    }

    /**
     * inserisce la lista delle persone nella list view
     * @param jsonObject è il JSON contentente la lista delle persone
     */
    protected void setListView(JSONObject jsonObject){
        personList = Info.infoArrayFromJson(jsonObject, Info.PERSON);

        dataAdapter = new CustomCheckList(this, R.layout.list_check_text_text, personList);
        dataAdapter.setEnableAll(false);
        listView.setAdapter(dataAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                startIntent((OnClickIntent)getIntent().getSerializableExtra("onClickIntent"), ((Info)(((CustomCheckList.ViewHolder)view.getTag()).getCheck().getTag())));
            }
        });

        listView.setVisibility(View.VISIBLE);
    }

    /**
     * Metodo che permette di diversificare l'azione del click in base al motivo per il quale è stata richiamata
     * la lista di persone
     *
     * @param intent enum che specifica il motivo
     * @param personInfo Informazioni sulla persona selezionata
     */
    private void startIntent(OnClickIntent intent, final Info personInfo){
        switch (intent){
            case getInfo:
                Intent personInfoIntent = new Intent (getApplicationContext(), PersonInfo.class);
                personInfoIntent.putExtra("person_id", personInfo.getId());
                startActivityForResult(personInfoIntent, REQUEST_PERSON_INFO);
                break;
            case doTraining:
                TrainingActions.TrainVerify verify = new TrainingActions.TrainVerify(PersonMain.this);
                verify.setCallback(new Callback() {
                    @Override
                    public void processResult(JSONObject rst) {
                        try {
                            String sessionId = rst.getString("session_id");
                            Training t = new Training(Training.Target.PERSON, personInfo, sessionId);
                            TrainingActions.SaveTraining saveTraining = new TrainingActions.SaveTraining(PersonMain.this);
                            saveTraining.saveTraining(t);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        PersonMain.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "trained", Toast.LENGTH_SHORT);
                                toast.show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                });
                verify.verify(personInfo.getId());

                break;
            case assignFace:
                String faceId = getIntent().getStringExtra("face_id");
                PersonActions.AddFace addFace = new PersonActions.AddFace(PersonMain.this);
                addFace.setCallback(new Callback() {
                    @Override
                    public void processResult(JSONObject rst) {
                        PersonMain.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "added", Toast.LENGTH_SHORT);
                                toast.show();
                                finish();
                            }
                        });
                    }
                });
                addFace.addFace(personInfo.getId(), faceId);

                break;
            default:
                break;
        }
    }


}
