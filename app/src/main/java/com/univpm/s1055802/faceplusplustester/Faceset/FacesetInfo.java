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

package com.univpm.s1055802.faceplusplustester.Faceset;

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
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FacesetInfo extends AppCompatActivity {

    private ListView lstvInfo = null;
    private ListView lstvFaces = null;
    private TextView txtvInfo = null;
    private TextView txtvFaces = null;
    private Toolbar toolbar;

    private final int REQUESt_FACESET_EDIT = 2;
    private String facesetId = null;
    private String facesetString = null;
    private JSONObject facesetJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faceset_info);
        facesetId = getIntent().getStringExtra("faceset_id");


        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        txtvInfo = (TextView) findViewById(R.id.txtvInfo);
        lstvInfo = (ListView) findViewById(R.id.lstvInfo);
        txtvFaces = (TextView) findViewById(R.id.txtvFaces);
        lstvFaces = (ListView) findViewById(R.id.lstvFaces);
        txtvInfo.setVisibility(View.INVISIBLE);
        txtvFaces.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);

        retrieveFacesetInfo(facesetId);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESt_FACESET_EDIT:
                retrieveFacesetInfo(facesetId);
                break;
        }
    }

    /**
     * Imposta la list view che contiene le informazioni sul faceset
     * @param faceset oggetto che contiene le informazioni sul faceset
     */
    private void setLstvInfo(Faceset faceset){
        ArrayList<String> facesetInfo = new ArrayList<String>();
        facesetInfo.add("Id: " + faceset.getId());
        facesetInfo.add("Name: " + faceset.getName());
        facesetInfo.add("Tag: " + faceset.getTag());


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, facesetInfo);
        lstvInfo.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvInfo);
    }

    /**
     * Imposta la list view che contiene le facce del faceset
     * @param faceset oggetto che contiene le informazioni sul faceset
     */
    private void setLstvFaces(Faceset faceset){
        String faceIDs[] = new String[faceset.getFaceNumber()];
        for (int i=0; i<faceset.getFaceNumber(); i++) {
            faceIDs[i] = faceset.getFace(i).getFaceId();
        }
        CustomFaceList adapter = new
                CustomFaceList(FacesetInfo.this, faceIDs, faceIDs);

        lstvFaces.setAdapter(adapter);
        lstvFaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent faceInfoIntent = new Intent(getApplicationContext(), FaceInfo.class);
                faceInfoIntent.putExtra("face_id", ((CustomFaceList)parent.getAdapter()).getImgId(position));
                startActivity(faceInfoIntent);
            }
        });

        ImageUtils.setListViewHeightBasedOnItems(lstvFaces);

    }

    /**
     * recupera le informazioni del faceset specificata dal server fpp
     * @param facesetId id del faceset della quale recuperare le info
     */
    private void retrieveFacesetInfo(String facesetId){
        FacesetActions.GetFacesetInfo getFacesetInfo = new FacesetActions.GetFacesetInfo(FacesetInfo.this);
        getFacesetInfo.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                FacesetInfo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        facesetJSON = rst;
                        facesetString = rst.toString();
                        Faceset faceset = null;
                        try {
                            faceset = new Faceset(rst);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        txtvInfo.setVisibility(View.VISIBLE);
                        txtvFaces.setVisibility(View.VISIBLE);
                        setLstvInfo(faceset);
                        setLstvFaces(faceset);
                    }
                });

            }
        });
        FacesetInfo.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(FacesetInfo.this.getApplicationContext(), "Retrieving data", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        getFacesetInfo.getFacesetInfo(facesetId);
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
            editFaceset();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * visualizza la finestra di dialogo per confermare l'eliminazione del faceset
     * in caso di risposta positiva avvia l'activity per l'eliminazione
     */
    private void confirmDeleteDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Confirm delete?")
                .setIcon(R.drawable.fpp_delete_black)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        FacesetActions.DeleteFaceset deleteFaceset = new FacesetActions.DeleteFaceset(FacesetInfo.this);
                        deleteFaceset.setCallback(new Callback() {
                            @Override
                            public void processResult(JSONObject rst) {
                                FacesetInfo.this.runOnUiThread(new Runnable() {
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
                        deleteFaceset.deleteFaceset(facesetId);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * avvia l'activity per la modifica del faceset
     */
    private void editFaceset(){
        Intent editIntent = new Intent(getApplicationContext(), FacesetEdit.class);
        editIntent.putExtra("faceset_string", facesetString);
        startActivityForResult(editIntent, REQUESt_FACESET_EDIT);
    }

}
