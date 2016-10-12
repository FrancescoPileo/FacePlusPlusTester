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

package com.univpm.s1055802.faceplusplustester.Face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomList;
import com.univpm.s1055802.faceplusplustester.Utils.Directories;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class FaceInfo extends AppCompatActivity{

    private ImageView imgvFace;
    private TextView txtvInfo;
    private ListView lstvInfo;
    private TextView txtvPersons;
    private ListView lstvPersons;
    private TextView txtvFacesets;
    private ListView lstvFacesets;

    private Face face;
    private JSONObject faceJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_info);
        imgvFace = (ImageView) findViewById(R.id.imgvFace);
        txtvInfo = (TextView) findViewById(R.id.txtvInfo);
        lstvInfo = (ListView) findViewById(R.id.lstvInfo);
        txtvPersons = (TextView) findViewById(R.id.txtvPersons);
        lstvPersons = (ListView) findViewById(R.id.lstvPersons);
        txtvFacesets = (TextView) findViewById(R.id.txtvFacesets);
        lstvFacesets = (ListView) findViewById(R.id.lstvFacesets);

        txtvInfo.setVisibility(View.INVISIBLE);
        txtvPersons.setVisibility(View.INVISIBLE);
        txtvFacesets.setVisibility(View.INVISIBLE);

        retrieveFaceInfo(this.getIntent().getStringExtra("face_id"));
    }

    /**
     * Recupera le informazioni della face dal server fpp
     * @param faceId id della faccia della quale recuperare le informazioni
     */
    private void retrieveFaceInfo(String faceId){
        FaceAction.GetFaceInfo getFaceInfo = new FaceAction.GetFaceInfo(this);
        getFaceInfo.setCallback(new Callback() {
            @Override
            public void processResult(JSONObject rst) {


                try {
                    faceJson = rst.getJSONArray("face_info").getJSONObject(0);
                    face = new Face(faceJson);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FaceInfo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setImgvFace();
                        setLstvInfo();
                        setLstvPersons();
                        setLstvFacesets();
                    }
                });

            }
        });
        FaceInfo.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FaceInfo.this, "Getting info", Toast.LENGTH_SHORT).show();
            }
        });
        getFaceInfo.getFaceInfo(faceId);
    }

    /**
     * Imposta l'Image View che contiene l'immagine della faccia
     */
    private void setImgvFace(){
        File imgFile = new File(Directories.FACES + File.separator + face.getFaceId() + ".png" );
        if (imgFile.exists()){
            Bitmap myBit = BitmapFactory.decodeFile(Directories.FACES + File.separator + face.getFaceId() + ".png");
            imgvFace.setImageBitmap(myBit);
        } else {
            imgvFace.setImageResource(R.drawable.fpp_no_face);
        }

    }

    /**
     * Imposta la list view che contiente le informazioni della faccia
     */
    private void setLstvInfo(){
        ArrayList<String> faceInfo = new ArrayList<String>();
        faceInfo.add("Id: " + face.getFaceId());
        faceInfo.add("Age: " + face.getFaceAttrAgeValue() + " +/- " + face.getFaceAttrAgeRange());
        faceInfo.add("Gender: " + face.getFaceAttrGenderValue() + " (" + face.getFaceAttrGenderConfidence() + "%)");
        faceInfo.add("Race: " + face.getFaceAttrRaceValue() + " (" + face.getFaceAttrRaceConfidence() + "%)");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, faceInfo);
        lstvInfo.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvInfo);
        txtvInfo.setVisibility(View.VISIBLE);
    }

    /**
     * Imposta la list view che contiene le persone alla quale è assegnata la faccia
     */
    private void setLstvPersons(){
        ArrayList<Info> personList = Info.infoArrayFromJson(faceJson, Info.PERSON);
        CustomList adapter = new CustomList(this, R.layout.list_text_text, personList);
        lstvPersons.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvPersons);
        txtvPersons.setVisibility(View.VISIBLE);
    }


    /**
     * Imposta la list view che contiene i facesets ai quali è assegnata la faccia
     */
    private void setLstvFacesets(){
        ArrayList<Info> facesetList = Info.infoArrayFromJson(faceJson, Info.FACESET);
        CustomList adapter = new CustomList(this, R.layout.list_text_text, facesetList);
        lstvFacesets.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvFacesets);
        txtvFacesets.setVisibility(View.VISIBLE);

    }
}
