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
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.Person.Person;
import com.univpm.s1055802.faceplusplustester.Person.PersonActions;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomEditList;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupEdit extends AppCompatActivity {

    private EditText txteId = null;
    private EditText txteName = null;
    private EditText txteTag = null;
    private ListView lstvPersons = null;
    private Toolbar toolbar = null;
    protected ArrayList<Person> personList;
    private CustomEditList dataAdapterPerson;

    private String groupString = null;
    private JSONObject groupJSON = null;
    private Group group = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_edit);

        txteId = (EditText) findViewById(R.id.txteId);
        txteId.setKeyListener(null);
        txteName = (EditText) findViewById(R.id.txteName);
        txteTag = (EditText) findViewById(R.id.txteTag);
        lstvPersons = (ListView) findViewById(R.id.lstvPersons);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        groupString = getIntent().getStringExtra("group_string");
        try {
            groupJSON = new JSONObject(groupString);
            group = new Group(groupJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        txteId.setText(group.getId());
        txteName.setText(group.getName());
        txteTag.setText(group.getTag());

        setListViewPersons();

        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
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
            GroupActions.EditGroup editGroup = new GroupActions.EditGroup(GroupEdit.this);
            editGroup.setCallback(new Callback() {
                @Override
                public void processResult(JSONObject rst) {
                    GroupEdit.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupEdit.this.getApplicationContext(), "Edited", Toast.LENGTH_SHORT).show();
                        }
                    });
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
            editGroup.editGroup(group.getId(), txteName.getText().toString(), txteTag.getText().toString() );
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * crea un array ad hoc per la list view
     * @param allPersons JSON contenente tutte le persone
     * @param groupPersons JSON contenente le persone che appartengono al gruppo
     * @return array ad hoc
     */
    private ArrayList<Info> createPersonArray(JSONObject allPersons, JSONObject groupPersons){
        ArrayList<Info> persons = Info.infoArrayFromJson(allPersons, Info.PERSON);
        ArrayList<Info> myPersons = Info.infoArrayFromJson(groupPersons, Info.PERSON);

        for (int i=0; i<persons.size(); i++) {
            for (int j=0; j<myPersons.size(); j++){
                if (persons.get(i).getId().equals(myPersons.get(j).getId())){
                    persons.get(i).setChecked(true);
                }
            }
        }
        return persons;
    }

    /**
     * imposta la list view che contiene le persone che appartengono al gruppo
     */
    private void setListViewPersons(){
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info person = ((Info) v.getTag());
                if (((CheckBox) v).isChecked()) {
                    //aggiungi gruppo
                    GroupActions.AddPerson addPerson = new GroupActions.AddPerson(GroupEdit.this);
                    addPerson.setCallback(new Callback() {
                        @Override
                        public void processResult(JSONObject rst) {
                            GroupEdit.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GroupEdit.this, "added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    addPerson.addPerson(group.getId(), person.getId());
                } else {
                    //togli gruppo
                    GroupActions.RemovePerson removePerson = new GroupActions.RemovePerson(GroupEdit.this);
                    removePerson.setCallback(new Callback() {
                        @Override
                        public void processResult(JSONObject rst) {
                            GroupEdit.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GroupEdit.this, "removed", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    removePerson.removePerson(group.getId(), person.getId());
                }

            }
        };

        PersonActions.GetPersonsList getPersonsList = new PersonActions.GetPersonsList(GroupEdit.this);
        getPersonsList.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                GroupEdit.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Info> personArray = createPersonArray(rst, groupJSON);
                        dataAdapterPerson = new CustomEditList(GroupEdit.this , R.layout.list_check_text_text, personArray, listener);
                        lstvPersons.setAdapter(dataAdapterPerson);
                        ImageUtils.setListViewHeightBasedOnItems(lstvPersons);
                    }
                });

            }
        });
        getPersonsList.getPersonsList();
    }
}

