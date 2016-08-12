package com.univpm.s1055802.faceplusplustester.Faceset;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Training.Training;
import com.univpm.s1055802.faceplusplustester.Training.TrainingActions;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomCheckList;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kekko on 07/05/16.
 */
public class FacesetMain extends AppCompatActivity {

    private Toolbar toolbar;
    private Toolbar deleteToolbar;
    private FloatingActionButton fab;
    private final String SAVED_FACESETS = "saved_facesets";
    protected final int REQUEST_FACESET_CREATE = 1;
    private final int REQUEST_FACESET_INFO = 3;
    protected ArrayList<Info> facesetList;
    private CustomCheckList dataAdapter;

    public enum OnClickIntent {getInfo, assignFace, doTraining}

    protected ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);

        listView = (ListView) findViewById(R.id.list);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Facesets");
        deleteToolbar = (Toolbar) findViewById(R.id.deleteToolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFaceset(v);
            }
        });

        setSupportActionBar(toolbar);

        deleteToolbar.inflateMenu(R.menu.delete_toolbar);
        setDeleteToolbar();

        listView.setEnabled(false);
        retrieveFacesetsList();

    }

    /**
     * metodo usato per aggiungere un faceset, richiamato dal floatingActionButton
     * @param v
     */
    public void createFaceset(View v){
        Intent intent = new Intent(
                            getApplicationContext(),
                            FacesetCreate.class
                        );
        startActivityForResult(intent, REQUEST_FACESET_CREATE);
    }

    /**
     * inserisce le icone nella toolbar principale
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainlist_toolbar, menu);
        return true;
    }

    /**
     * gestisce la toolbar principale
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            enableCheckbox(true);
            showDeleteToolbar(true);
        }
        if (id == R.id.action_refresh){
            getFacesetListFromFpp();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * gestisce la toolbar di eliminazione
     */
    private void setDeleteToolbar(){
        deleteToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_cancel:
                        selectAll(false);
                        enableCheckbox(false);
                        showDeleteToolbar(false);
                        break;
                    case R.id.action_all:
                        selectAll(true);
                        break;
                    case R.id.action_ok:
                        FacesetActions.DeleteFacesets deleteFacesets = new FacesetActions.DeleteFacesets(FacesetMain.this);
                        deleteFacesets.setCallback(new Callback() {
                            @Override
                            public void processResult(JSONObject rst) {
                                FacesetMain.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(FacesetMain.this.getApplicationContext(), "Deleted", Toast.LENGTH_SHORT);
                                        toast.show();
                                        getFacesetListFromFpp();
                                        selectAll(false);
                                        enableCheckbox(false);
                                        showDeleteToolbar(false);
                                    }
                                });
                            }
                        });
                        deleteFacesets.deleteFacesets(deletingFacesets());
                        break;
                }
                return true;
            }
        });
    }

    /**
     * crea un JSONObject ad hoc per gestire l'eliminazione multipla di faceset
     * @return Il JSONOblject
     */
    JSONObject deletingFacesets(){
        JSONObject deletingListObj = new JSONObject();
        JSONArray deletingListArray = new JSONArray();
        for (int i = 0; i< facesetList.size(); i++ ){
            Info faceset = facesetList.get(i);
            if (faceset.isChecked() == true){
                deletingListArray.put(faceset.getName());
            }
        }
        try {
            deletingListObj.put("faceset_delete", deletingListArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deletingListObj;
    }

    /**
     * visualizza la toolbar di eliminazione
     */
    private void showDeleteToolbar(boolean flag){
        if (flag) {
            toolbar.setVisibility(View.GONE);
            deleteToolbar.setVisibility(View.VISIBLE);
        } else {
            deleteToolbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        }
    }



    /**
     *  abilita tutte le CheckBox per permettere l'eliminazione di più facesets
     */
    private void enableCheckbox(boolean flag){
        if (dataAdapter!=null) {
            dataAdapter.setEnableAll(flag);
            dataAdapter.notifyDataSetChanged();
        }

    }

    /**
     *  seleziona tutte le checkbox
     */
    private void selectAll(boolean flag){
        if (dataAdapter!=null) {
            dataAdapter.setCheckAll(flag);
            dataAdapter.notifyDataSetChanged();
        }
    }

    /**
     * ottiene la lista di faceset gestendo i diversi casi
     */
    private void retrieveFacesetsList(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String jsonFacesetsToString = sharedPref.getString(SAVED_FACESETS, null);
        JSONObject jsonObject = null;

        if (jsonFacesetsToString == null){
            getFacesetListFromFpp();
        } else {
            try {
                jsonObject = new JSONObject(jsonFacesetsToString);
            } catch (JSONException e){
                e.printStackTrace();
            }
            checkAndSetListView(jsonObject);
        }
        listView.setEnabled(true);
    }


    /**
     * recupera la lista dei facesets dal server di fpp
     */
    private void getFacesetListFromFpp(){
        listView.setVisibility(View.INVISIBLE);
        FacesetActions.GetFacesetsList facesetList = new FacesetActions.GetFacesetsList(FacesetMain.this);
        facesetList.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                FacesetMain.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setListView(rst);
                        setSharedPreferences(rst);
                    }
                });

            }
        });
        FacesetMain.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(FacesetMain.this.getApplicationContext(), "Updating", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        facesetList.getFacesetsList();

    }

    /**
     * controlla la data in cui è stata creata la copia della lista dei faceset e nel caso la aggiorna e setta
     * la list view
     * @param jsonObject JSON contentente le informazioni sulla lista dei facesets
     */
    private void checkAndSetListView(JSONObject jsonObject){
        try {
            String dateString = jsonObject.getString("date");
            DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date date = format.parse(dateString);
            Date actualDate = new Date();
            long millisDiff = actualDate.getTime() - date.getTime();

            int seconds = (int) (millisDiff / 1000 % 60);
            int minutes = (int) (millisDiff / 60000 % 60);
            int hours = (int) (millisDiff / 3600000 % 24);
            int days = (int) (millisDiff / 86400000);

            if (days > 0 || hours > 2) {
                getFacesetListFromFpp();
            } else {
                setListView(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_FACESET_CREATE:
                if (resultCode == Activity.RESULT_OK) {
                    getFacesetListFromFpp();
                }
                break;
            case REQUEST_FACESET_INFO:
                if (resultCode == RESULT_OK){
                    getFacesetListFromFpp();
                }
                selectAll(false);
                enableCheckbox(false);
                showDeleteToolbar(false);
                break;
        }
        listView.setEnabled(true);

    }

    /**
     * salva la stringa contenente la lista dei faceset nelle risorse locali
     * @param jsonObject è il JSON contentente la lista dei facesets
     */
    private void setSharedPreferences(JSONObject jsonObject){
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            jsonObject.put("date", timeStamp);
            String resultString = jsonObject.toString();
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SAVED_FACESETS, resultString);
            editor.commit();
        } catch (JSONException e){
            e.printStackTrace();
        }


    }

    /**
     * inserisce la lista dei facesets nella list view
     * @param jsonObject è il JSON contentente la lista dei facesets
     */
    protected void setListView(JSONObject jsonObject){
        facesetList = Info.infoArrayFromJson(jsonObject, Info.FACESET);

        dataAdapter = new CustomCheckList(this, R.layout.list_check_text_text, facesetList);
        dataAdapter.setEnableAll(false);
        listView.setAdapter(dataAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                startIntent((OnClickIntent)getIntent().getSerializableExtra("onClickIntent"), ((Info)(((CustomCheckList.ViewHolder)view.getTag()).getCheck().getTag())));
            }
        });

        listView.setVisibility(View.VISIBLE);
    }

    /**
     * Metodo che permette di diversificare l'azione del click in base al motivo per il quale è stata richiamata
     * la lista di Faceset
     *
     * @param intent enum che specifica il motivo
     * @param facesetInfo Informazioni sul Faceset selezionato
     */
    private void startIntent(OnClickIntent intent, final Info facesetInfo){
        switch (intent){
            case getInfo:
                Intent facesetInfoIntent = new Intent (getApplicationContext(), FacesetInfo.class);
                facesetInfoIntent.putExtra("faceset_id", facesetInfo.getId());
                startActivityForResult(facesetInfoIntent, REQUEST_FACESET_INFO);
                break;
            case doTraining:
                TrainingActions.TrainSearch search = new TrainingActions.TrainSearch(FacesetMain.this);
                search.setCallback(new Callback() {
                    @Override
                    public void processResult(JSONObject rst) {
                        try {
                            String sessionId = rst.getString("session_id");
                            Training t = new Training(Training.Target.FACESET, facesetInfo, sessionId);
                            TrainingActions.SaveTraining saveTraining = new TrainingActions.SaveTraining(FacesetMain.this);
                            saveTraining.saveTraining(t);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FacesetMain.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "trained", Toast.LENGTH_SHORT);
                                toast.show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                });
                search.search(facesetInfo.getId());
                break;
            case assignFace:
                String faceId = getIntent().getStringExtra("face_id");
                FacesetActions.AddFace addFace = new FacesetActions.AddFace(FacesetMain.this);
                addFace.setCallback(new Callback() {
                    @Override
                    public void processResult(JSONObject rst) {
                        FacesetMain.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "added", Toast.LENGTH_SHORT);
                                toast.show();
                                finish();
                            }
                        });
                    }
                });
                addFace.addFace(facesetInfo.getId(), faceId);

                break;
            default:
                break;
        }
    }


    /**
     * crea l'array list che serve per popolare la list view
     * @param jsonObject json contenente le informazioni sulla lista di facesets
     * @return l'array list opportunamente settata
     */
    private ArrayList<Faceset> arrayFromJson(JSONObject jsonObject){
        ArrayList<Faceset> array = new ArrayList<Faceset>();
        try {
            int facesetNumber = jsonObject.getJSONArray("faceset").length();
            for (int i=0; i < facesetNumber; i++) {
                Faceset faceset = new Faceset(jsonObject.getJSONArray("faceset").getJSONObject(i));
                array.add(faceset);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;
    }

}
