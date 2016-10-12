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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomList;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupInfo extends AppCompatActivity {

    private ListView lstvInfo = null;
    private ListView lstvPersons = null;
    private TextView txtvPersons = null;
    private TextView txtvInfo = null;
    private Toolbar toolbar;

    private final int REQUEST_GROUP_EDIT = 2;
    private String groupId = null;
    private String groupString = null;
    private JSONObject groupJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info);
        groupId = getIntent().getStringExtra("group_id");


        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        txtvInfo = (TextView) findViewById(R.id.txtvInfo);
        txtvInfo.setVisibility(View.INVISIBLE);
        lstvInfo = (ListView) findViewById(R.id.lstvInfo);
        txtvPersons = (TextView) findViewById(R.id.txtvPersons);
        txtvPersons.setVisibility(View.INVISIBLE);
        lstvPersons = (ListView) findViewById(R.id.lstvPersons);
        setSupportActionBar(toolbar);

        retrieveGroupInfo(groupId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GROUP_EDIT:
                retrieveGroupInfo(groupId);
                break;
        }
    }


    /**
     * Popola la ListView con le informazioni del Group
     * @param group
     */
    private void setLstvInfo(Group group){
        ArrayList<String> groupInfo = new ArrayList<String>();
        groupInfo.add("Id: " + group.getId());
        groupInfo.add("Name: " + group.getName());
        groupInfo.add("Tag: " + group.getTag());


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupInfo);
        lstvInfo.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvInfo);
    }

    /**
     * Popola la ListView con la lista delle persone assegnate al Group
     */
    private void setLstvPersons(){
        ArrayList<Info> personsArray = Info.infoArrayFromJson(groupJSON, Info.PERSON);
        CustomList adapter = new
                CustomList(GroupInfo.this, R.layout.list_text_text, personsArray);

        lstvPersons.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvPersons);
    }


    /**
     * recupera le informazioni del gruppo specificato, dal server fpp
     * @param groupId id del gruppo del quale recuperare le info
     */
    private void retrieveGroupInfo(String groupId){
        GroupActions.GetGroupInfo getGroupInfo = new GroupActions.GetGroupInfo(GroupInfo.this);
        getGroupInfo.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                GroupInfo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        groupJSON = rst;
                        groupString = rst.toString();
                        Group group = null;
                        try {
                            group = new Group(rst);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        txtvPersons.setVisibility(View.VISIBLE);
                        txtvInfo.setVisibility(View.VISIBLE);
                        setLstvInfo(group);
                        setLstvPersons();
                    }
                });

            }
        });
        GroupInfo.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(GroupInfo.this.getApplicationContext(), "Retrieving data", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        getGroupInfo.getGroupInfo(groupId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info_toolbar, menu);
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
            editGroup();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * visualizza la finestra di dialogo per confermare l'eliminazione del gruppo
     * in caso di risposta positiva avvia l'activity per l'eliminazione
     */
    private void confirmDeleteDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Confirm delete?")
                .setIcon(R.drawable.fpp_delete_black)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        GroupActions.DeleteGroup deleteGroup = new GroupActions.DeleteGroup(GroupInfo.this);
                        deleteGroup.setCallback(new Callback() {
                            @Override
                            public void processResult(JSONObject rst) {
                                GroupInfo.this.runOnUiThread(new Runnable() {
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
                        deleteGroup.deleteGroup(groupId);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * avvia l'activity per la modifica del gruppo
     */
    private void editGroup(){
        Intent editIntent = new Intent(getApplicationContext(), GroupEdit.class);
        editIntent.putExtra("group_string", groupString);
        startActivityForResult(editIntent, REQUEST_GROUP_EDIT);
    }

}
