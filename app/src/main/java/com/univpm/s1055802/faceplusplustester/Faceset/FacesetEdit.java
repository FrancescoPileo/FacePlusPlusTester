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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.Face.Face;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomEditFaceList;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 09/05/16.
 */
public class FacesetEdit extends AppCompatActivity{

    private EditText txteId = null;
    private EditText txteName = null;
    private EditText txteTag = null;
    private ListView lstvFaces = null;
    private Toolbar toolbar = null;
    protected ArrayList<Face> faceList;
    private CustomEditFaceList dataAdapterFace;

    private String facesetString = null;
    private JSONObject facesetJSON = null;
    private Faceset faceset = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faceset_edit);

        txteId = (EditText) findViewById(R.id.txteId);
        txteId.setKeyListener(null);
        txteName = (EditText) findViewById(R.id.txteName);
        txteTag = (EditText) findViewById(R.id.txteTag);
        lstvFaces = (ListView) findViewById(R.id.lstvFaces);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        facesetString = getIntent().getStringExtra("faceset_string");
        try {
            facesetJSON = new JSONObject(facesetString);
            faceset = new Faceset(facesetJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        txteId.setText(faceset.getId());
        txteName.setText(faceset.getName());
        txteTag.setText(faceset.getTag());

        setListViewFaces();

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
            FacesetActions.EditFaceset editFaceset = new FacesetActions.EditFaceset(FacesetEdit.this);
            editFaceset.setCallback(new Callback() {
                @Override
                public void processResult(JSONObject rst) {
                    FacesetEdit.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FacesetEdit.this.getApplicationContext(), "Edited", Toast.LENGTH_SHORT).show();
                        }
                    });
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
            editFaceset.editFaceset(faceset.getId(), txteName.getText().toString(), txteTag.getText().toString() );
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * imposta la list view che contiene le varie face assegnate al faceset
     */
    private void setListViewFaces(){
        faceList = faceArrayFromJson(facesetJSON);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacesetActions.RemoveFace removeFace = new FacesetActions.RemoveFace(FacesetEdit.this);
                removeFace.setCallback(new Callback() {
                    @Override
                    public void processResult(final JSONObject rst) {
                        FacesetEdit.this.runOnUiThread(new Runnable() {
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
                removeFace.removeFace(faceset.getId(), ((Face) v.getTag()).getFaceId());
            }
        };

        dataAdapterFace = new CustomEditFaceList(this, R.layout.list_face_text_del, faceList, listener);

        lstvFaces.setAdapter(dataAdapterFace);
        ImageUtils.setListViewHeightBasedOnItems(lstvFaces);
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
