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
import android.widget.EditText;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;

import org.json.JSONObject;

/**
 * Created by kekko on 05/05/16.
 */
public class FacesetCreate extends AppCompatActivity {

    private EditText txteName = null;
    private EditText txteTag = null;
    private Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faceset_create);
        txteName = (EditText) findViewById(R.id.txteName);
        txteTag = (EditText) findViewById(R.id.txteTag);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            String personName = txteName.getText().toString();
            String personTag = txteTag.getText().toString();
            FacesetActions.SendCreate sendCreate = new FacesetActions.SendCreate(FacesetCreate.this);
            sendCreate.setCallback(new Callback() {
                @Override
                public void processResult(JSONObject rst) {
                    FacesetCreate.this.runOnUiThread(new Runnable() {
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
            sendCreate.sendCreation(personName,personTag);
        }

        return super.onOptionsItemSelected(item);
    }
}
