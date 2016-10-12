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

import com.univpm.s1055802.faceplusplustester.Face.Face;
import com.univpm.s1055802.faceplusplustester.Group.Group;
import com.univpm.s1055802.faceplusplustester.Group.GroupActions;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomEditFaceList;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomEditList;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 09/05/16.
 */
public class PersonEdit extends AppCompatActivity{

    private EditText txteId = null;
    private EditText txteName = null;
    private EditText txteTag = null;
    private ListView lstvFaces = null;
    private ListView lstvGroups = null;
    private Toolbar toolbar = null;
    protected ArrayList<Face> faceList;
    protected ArrayList<Group> groupList;
    private CustomEditFaceList dataAdapterFace;
    private CustomEditList dataAdapterGroup;

    private String personString = null;
    private JSONObject personJSON = null;
    private Person person = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_edit);

        txteId = (EditText) findViewById(R.id.txteId);
        txteId.setKeyListener(null);
        txteName = (EditText) findViewById(R.id.txteName);
        txteTag = (EditText) findViewById(R.id.txteTag);
        lstvFaces = (ListView) findViewById(R.id.lstvFaces);
        lstvGroups = (ListView) findViewById(R.id.lstvGroups);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        personString = getIntent().getStringExtra("person_string");
        try {
            personJSON = new JSONObject(personString);
            person = new Person(personJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        txteId.setText(person.getId());
        txteName.setText(person.getName());
        txteTag.setText(person.getTag());

        setListViewFaces();
        setListViewGroups();

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
            PersonActions.EditPerson editPerson = new PersonActions.EditPerson(PersonEdit.this);
            editPerson.setCallback(new Callback() {
                @Override
                public void processResult(JSONObject rst) {
                    PersonEdit.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PersonEdit.this.getApplicationContext(), "Edited", Toast.LENGTH_SHORT).show();
                        }
                    });
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
            editPerson.editPerson(person.getId(), txteName.getText().toString(), txteTag.getText().toString() );
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * imposta la list view che contiene le varie face assegnate alla persona
     */
    private void setListViewFaces(){
        faceList = faceArrayFromJson(personJSON);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonActions.RemoveFace removeFace = new PersonActions.RemoveFace(PersonEdit.this);
                removeFace.setCallback(new Callback() {
                    @Override
                    public void processResult(final JSONObject rst) {
                        PersonEdit.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ((CustomEditFaceList) lstvFaces.getAdapter()).removeFaceById(rst.getString("face_id"));
                                    Toast toast = Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT);
                                    toast.show();
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                removeFace.removeFace(person.getId(), ((Face) v.getTag()).getFaceId());
            }
        };

        dataAdapterFace = new CustomEditFaceList(this, R.layout.list_face_text_del, faceList, listener);

        lstvFaces.setAdapter(dataAdapterFace);
        ImageUtils.setListViewHeightBasedOnItems(lstvFaces);
    }

    /**
     * crea un array ad hoc per la list view
     * @param allGroups JSON contenente tutti i gruppi
     * @param personGroups JSON contenente i gruppi ai quali appartiene la persona
     * @return array ad hoc
     */
    private ArrayList<Info> createGroupArray(JSONObject allGroups, JSONObject personGroups){
        ArrayList<Info> groups = Info.infoArrayFromJson(allGroups, Info.GROUP);
        ArrayList<Info> myGroups = Info.infoArrayFromJson(personGroups, Info.GROUP);

        for (int i=0; i<groups.size(); i++) {
            for (int j=0; j<myGroups.size(); j++){
                if (groups.get(i).getId().equals(myGroups.get(j).getId())){
                    groups.get(i).setChecked(true);
                }
            }
        }
        return groups;
    }

    /**
     * imposta la list view che contiene i gruppi ai quali appartiene la persona
     */
    private void setListViewGroups(){
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info group = ((Info) v.getTag());
                if (((CheckBox) v).isChecked()) {
                    //aggiungi gruppo
                    GroupActions.AddPerson addPerson = new GroupActions.AddPerson(PersonEdit.this);
                    addPerson.setCallback(new Callback() {
                        @Override
                        public void processResult(JSONObject rst) {
                            PersonEdit.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PersonEdit.this, "added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    addPerson.addPerson(group.getId(), person.getId());
                } else {
                    //togli gruppo
                    GroupActions.RemovePerson removePerson = new GroupActions.RemovePerson(PersonEdit.this);
                    removePerson.setCallback(new Callback() {
                        @Override
                        public void processResult(JSONObject rst) {
                            PersonEdit.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PersonEdit.this, "removed", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    removePerson.removePerson(group.getId(), person.getId());
                }

            }
        };
        GroupActions.GetGroupsList getGroupsList = new GroupActions.GetGroupsList(PersonEdit.this);
        getGroupsList.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                PersonEdit.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Info> groupArray = createGroupArray(rst, personJSON);
                        dataAdapterGroup = new CustomEditList(PersonEdit.this , R.layout.list_check_text_text, groupArray, listener);
                        lstvGroups.setAdapter(dataAdapterGroup);
                        ImageUtils.setListViewHeightBasedOnItems(lstvGroups);
                    }
                });

            }
        });
        getGroupsList.getGroupsList();
    }


    /**
     * converte il JSON contente la lista delle facce in un ArrayList<Face>
     * @param jsonObject JSON conetenente la lista delle facce
     * @return l'ArrayList
     */
    private ArrayList<Face> faceArrayFromJson(JSONObject jsonObject){
        ArrayList<Face> array = new ArrayList<Face>();
        try {
            int personNumber = jsonObject.getJSONArray("face").length();
            for (int i=0; i<personNumber; i++) {
                Face face = new Face(jsonObject.getJSONArray("face").getJSONObject(i));
                array.add(face);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;
    }

}
