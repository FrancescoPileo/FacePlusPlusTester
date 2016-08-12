package com.univpm.s1055802.faceplusplustester.Person;

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


import com.univpm.s1055802.faceplusplustester.Face.FaceInfo;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomFaceList;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomList;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 09/05/16.
 */
public class PersonInfo extends AppCompatActivity {

    private ListView lstvInfo = null;
    private ListView lstvFaces = null;
    private ListView lstvGroups = null;
    private TextView txtvInfo = null;
    private TextView txtvFaces = null;
    private TextView txtvGroups = null;
    private Toolbar toolbar;

    private final int REQUESt_PERSON_EDIT = 2;
    private String personId = null;
    private String personString = null;
    private JSONObject personJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);
        personId = getIntent().getStringExtra("person_id");


        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        txtvInfo = (TextView) findViewById(R.id.txtvInfo);
        lstvInfo = (ListView) findViewById(R.id.lstvInfo);
        txtvFaces = (TextView) findViewById(R.id.txtvFaces);
        lstvFaces = (ListView) findViewById(R.id.lstvFaces);
        txtvGroups = (TextView) findViewById(R.id.txtvGroups);
        lstvGroups = (ListView) findViewById(R.id.lstvGroups);
        txtvInfo.setVisibility(View.INVISIBLE);
        txtvGroups.setVisibility(View.INVISIBLE);
        txtvFaces.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);

        retrievePersonInfo(personId);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESt_PERSON_EDIT:
                retrievePersonInfo(personId);
                break;
        }
    }


    private void setLstvInfo(Person person){
        ArrayList<String> personInfo = new ArrayList<String>();
        personInfo.add("Id: " + person.getId());
        personInfo.add("Name: " + person.getName());
        personInfo.add("Tag: " + person.getTag());


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, personInfo);
        lstvInfo.setAdapter(adapter);
        lstvFaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent faceInfoIntent = new Intent(getApplicationContext(), FaceInfo.class);
                faceInfoIntent.putExtra("face_id", ((CustomFaceList)parent.getAdapter()).getImgId(position));
                startActivity(faceInfoIntent);
            }
        });
        ImageUtils.setListViewHeightBasedOnItems(lstvInfo);
    }

    private void setLstvFaces(Person person){
        String faceIDs[] = new String[person.getFaceNumber()];
        for (int i=0; i<person.getFaceNumber(); i++) {
            faceIDs[i] = person.getFace(i).getFaceId();
        }
        CustomFaceList adapter = new
                CustomFaceList(PersonInfo.this, faceIDs, faceIDs);

        lstvFaces.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvFaces);

    }


    private void setLstvGroups(){
        ArrayList<Info> groupArray = Info.infoArrayFromJson(personJSON, Info.GROUP);
        CustomList adapter = new
                CustomList(PersonInfo.this, R.layout.list_text_text, groupArray);

        lstvGroups.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvGroups);
    }


    /**
     * recupera le informazioni della persona specificata dal server fpp
     * @param personId id della persona della quale recuperare le info
     */
    private void retrievePersonInfo(String personId){
        PersonActions.GetPersonInfo getPersonInfo = new PersonActions.GetPersonInfo(PersonInfo.this);
        getPersonInfo.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                PersonInfo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        personJSON = rst;
                        personString = rst.toString();
                        Person person = null;
                        try {
                            person = new Person(rst);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        txtvInfo.setVisibility(View.VISIBLE);
                        txtvFaces.setVisibility(View.VISIBLE);
                        txtvGroups.setVisibility(View.VISIBLE);
                        setLstvInfo(person);
                        setLstvFaces(person);
                        setLstvGroups();
                    }
                });

            }
        });
        PersonInfo.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(PersonInfo.this.getApplicationContext(), "Retrieving data", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        getPersonInfo.getPersonInfo(personId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        String locked = getIntent().getStringExtra("locked");
        if (locked == null){
            getMenuInflater().inflate(R.menu.info_toolbar, menu);
        } else {
            getMenuInflater().inflate(R.menu.ok_toolbar, menu);
        }
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
        if (id == R.id.action_edit){
            editPerson();
        }
        if (id == R.id.action_ok) {
            setResult(RESULT_OK);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * visualizza la finestra di dialogo per confermare l'eliminazione della persona
     * in caso di risposta positiva avvia l'activity per l'eliminazione
     */
    private void confirmDeleteDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Confirm delete?")
                .setIcon(R.drawable.fpp_delete_black)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        PersonActions.DeletePerson deletePerson = new PersonActions.DeletePerson(PersonInfo.this);
                        deletePerson.setCallback(new Callback() {
                            @Override
                            public void processResult(JSONObject rst) {
                                PersonInfo.this.runOnUiThread(new Runnable() {
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
                        deletePerson.deletePerson(personId);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * avvia l'activity per la modifica della persona
     */
    private void editPerson(){
        Intent editIntent = new Intent(getApplicationContext(), PersonEdit.class);
        editIntent.putExtra("person_string", personString);
        startActivityForResult(editIntent, REQUESt_PERSON_EDIT);
    }

}
