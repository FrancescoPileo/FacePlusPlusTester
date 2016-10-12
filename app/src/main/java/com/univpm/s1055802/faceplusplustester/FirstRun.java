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

package com.univpm.s1055802.faceplusplustester;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.CallbackActions;

import org.json.JSONObject;

/**
 * Created by kekko on 08/08/16.
 */
public class FirstRun extends AppCompatActivity {

    private Toolbar toolbar = null;
    private EditText txteApiKey = null;
    private EditText txteApiSecret = null;
    private Switch swtIsCH = null;
    private Switch swtIsDebug = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.first_run);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txteApiKey = (EditText) findViewById(R.id.txteApiKey);
        txteApiSecret = (EditText) findViewById(R.id.txteApiSecret);
        swtIsCH = (Switch) findViewById(R.id.swtIsCH);
        swtIsDebug = (Switch) findViewById(R.id.swtIsDebug);

        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info_back_ok_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            showGuide();
        }
        if (id == R.id.action_back){
            setResult(RESULT_CANCELED);
            finish();
        }
        if (id == R.id.action_ok) {
            checkApplication();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Visualizza una piccola guida su come ottenere i dati per avviare una nuova app
     */
    private void showGuide(){
        new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage("Sign in to www.faceplusplus.com ->" +
                        " My App -> " +
                        "Create App -> " +
                        "Insert all the info -> " +
                        "get the ApiKey, the ApiSecret and the server selected")
                .setIcon(R.drawable.fpp_info_black)
                .setPositiveButton(android.R.string.yes, null).show();

    }

    /**
     *  Controlla che i dati inseriti siano validi, richiedendo al server le informazioni riguardanti
     *  l'applicazione identificata dai dati
     */
    private void checkApplication(){
        GetApp getApp = new GetApp(this);
        getApp.setCallback(new Callback() {
            @Override
            public void processResult(JSONObject rst) {
                SharedPreferences preferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("key", txteApiKey.getText().toString());
                editor.putString("secret", txteApiSecret.getText().toString());
                editor.putBoolean("is_cn", swtIsCH.isChecked());
                editor.putBoolean("is_debug", swtIsDebug.isChecked());
                editor.commit();
                setResult(RESULT_OK);
                finish();
                }
            });

        getApp.getApp(txteApiKey.getText().toString(), txteApiSecret.getText().toString(), swtIsCH.isChecked(), swtIsDebug.isChecked());

    }

    public static class GetApp extends CallbackActions {

        public GetApp(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Ottiene le informazioni dell'applicazione
         * @param apiKey api_key dell'applicazione
         * @param apiSecret api_secret dell'applicazione
         * @param isCH boolean che specifica se stiamo usando il server cinese
         * @param isDebug boolean che specifica se stiamo usando un'applicazione di Debug
         */
        public void getApp(final String apiKey, final String apiSecret, final boolean isCH, final boolean isDebug) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        JSONObject result = new HttpRequests( apiKey, apiSecret, isCH, isDebug).infoGetApp();
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed - check info", Toast.LENGTH_SHORT);
                                Log.v("key", apiKey);
                                Log.v("secret", apiSecret);
                                Log.v("is_ch", String.valueOf(isCH));
                                Log.v("is_debug", String.valueOf(isDebug));
                                toast.show();
                            }
                        });

                    }
                }
            }).start();
        }
    }


}
