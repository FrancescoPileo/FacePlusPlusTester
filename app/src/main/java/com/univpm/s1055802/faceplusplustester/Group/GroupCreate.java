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

package com.univpm.s1055802.faceplusplustester.Group;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.Person.Person;
import com.univpm.s1055802.faceplusplustester.Person.PersonActions;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomCheckList;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 16/05/16.
 */
public class GroupCreate extends AppCompatActivity {

    private EditText txteName = null;
    private EditText txteTag = null;
    private ListView lstvPersons = null;
    private Toolbar toolbar = null;

    protected ArrayList<Info> personList;
    private CustomCheckList dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_create);
        txteName = (EditText) findViewById(R.id.txteName);
        txteTag = (EditText) findViewById(R.id.txteTag);
        lstvPersons = (ListView) findViewById(R.id.lstvPersons);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getPersonList();

    }

    /**
     * Crea un'Array List contenente tutti gli id delle persone da aggiungere al gruppo
     * @return l'array list
     */
    private ArrayList <String> getSelectedPersons(){
        ArrayList<String> selectedPersons = new ArrayList<String>();
        for (int i=0; i < personList.size(); i++){
            if (personList.get(i).isChecked())
                selectedPersons.add(personList.get(i).getId());
        }
        return selectedPersons;
    }

    /**
     * ottiene la lista delle persone e la inserisce nella list view
     *
     */
    private void getPersonList(){
        PersonActions.GetPersonsList getPersonList = new PersonActions.GetPersonsList(GroupCreate.this);
        getPersonList.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                GroupCreate.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPersonsListView(rst);
                    }
                });

            }
        });
        GroupCreate.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(GroupCreate.this.getApplicationContext(), "Getting info", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        getPersonList.getPersonsList();
    }

    /**
     * inserisce la lista di persone nella list view
     * @param jsonObject json contenente la lista delle persone
     */
    private void setPersonsListView(JSONObject jsonObject){
        personList = Info.infoArrayFromJson(jsonObject, Info.PERSON);

        dataAdapter = new CustomCheckList(this, R.layout.list_check_text_text, personList);
        lstvPersons.setAdapter(dataAdapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvPersons);
    }

    /**
     * crea l'array list che serve per popolare la list view
     * @param jsonObject json contenente le informazioni sulla lista di persone
     * @return l'array list opportunamente settata
     */
    private ArrayList<Person> arrayFromJson(JSONObject jsonObject){
        ArrayList<Person> array = new ArrayList<Person>();
        try {
            int personNumber = jsonObject.getJSONArray("person").length();
            for (int i=0; i<personNumber; i++) {
                Person person = new Person(jsonObject.getJSONArray("person").getJSONObject(i));
                array.add(person);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatemente
        if (id == R.id.action_back) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        if (id == R.id.action_ok){
            String groupName = txteName.getText().toString();
            String groupTag = txteTag.getText().toString();
            ArrayList <String> selectedPersons = getSelectedPersons();
            GroupActions.SendCreate sendCreate = new GroupActions.SendCreate(this);
            sendCreate.setCallback(new Callback() {
                @Override
                public void processResult(JSONObject rst) {
                    GroupCreate.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResult(Activity.RESULT_OK);
                            Toast toast = Toast.makeText(getApplicationContext(), "created", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                    });
                }
            });
            sendCreate.sendCreation(groupName,groupTag, selectedPersons);
        }

        return super.onOptionsItemSelected(item);
    }

}
