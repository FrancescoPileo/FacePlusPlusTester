package com.univpm.s1055802.faceplusplustester.Recognition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 14/07/16.
 */
public class RecognitionVerifyResults extends AppCompatActivity {

    private String personId;
    private String faceId;
    private ListView lstvResults = null;
    private Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_verify_results);
        lstvResults = (ListView) findViewById(R.id.lstvResults);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        personId = getIntent().getStringExtra("person_id");
        faceId = getIntent().getStringExtra("face_id");

        getResults();
    }


    /**
     * Acquisisce il risultato della Identify
     */
    private void getResults(){

        RecognitionActions.Verify verify = new RecognitionActions.Verify(RecognitionVerifyResults.this);
        verify.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst){
                RecognitionVerifyResults.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Recognition.VerifyResult result = null;
                        try {
                            result = new Recognition.VerifyResult(rst);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setLstvResults(result);
                    }
                });

            }
        });
        verify.verify(faceId, personId);
    }


    /**
     * Inserisce le info dei risultati nella ListView appropriata
     * @param result informazioni da inserire
     */
    public void setLstvResults(Recognition.VerifyResult result) {
        ArrayList<String> recognitionResults = new ArrayList<String>();
        recognitionResults.add("Confidence: " + result.getConfidence());
        recognitionResults.add("Is same person: " + result.getSamePerson().toString());
        recognitionResults.add("Session Id: " + result.getSessionId());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recognitionResults);
        lstvResults.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvResults);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ok_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ok) {
           setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
