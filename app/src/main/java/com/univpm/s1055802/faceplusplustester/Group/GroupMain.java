package com.univpm.s1055802.faceplusplustester.Group;

/**
 * Created by kekko on 16/05/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
public class GroupMain extends AppCompatActivity {

    private Toolbar toolbar;
    private Toolbar deleteToolbar;
    private FloatingActionButton fab;
    private final String SAVED_GROUPS = "saved_groups";
    protected final int REQUEST_GROUP_CREATE = 1;
    private final int REQUEST_GROUP_INFO = 3;
    protected ArrayList<Info> groupList;
    private CustomCheckList dataAdapter;

    protected ListView listView = null;

    public enum OnClickIntent {getInfo, doTraining}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);

        listView = (ListView) findViewById(R.id.list);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Groups");
        deleteToolbar = (Toolbar) findViewById(R.id.deleteToolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup(v);
            }
        });

        setSupportActionBar(toolbar);

        deleteToolbar.inflateMenu(R.menu.delete_toolbar);
        setDeleteToolbar();

        listView.setEnabled(false);
        retrieveGroupsList();

    }

    /**
     * metodo usato per aggiungere un gruppo, richiamato dal floatingActionButton
     * @param v
     */
    public void createGroup(View v){
        Intent intent = new Intent(
                getApplicationContext(),
                GroupCreate.class
        );
        startActivityForResult(intent, REQUEST_GROUP_CREATE);
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
            getGroupListFromFpp();
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
                        GroupActions.DeleteGroups deleteGroups = new GroupActions.DeleteGroups(GroupMain.this);
                        deleteGroups.setCallback(new Callback() {
                            @Override
                            public void processResult(JSONObject rst) {
                                GroupMain.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(GroupMain.this.getApplicationContext(), "Deleted", Toast.LENGTH_SHORT);
                                        toast.show();
                                        getGroupListFromFpp();
                                        selectAll(false);
                                        enableCheckbox(false);
                                        showDeleteToolbar(false);
                                    }
                                });
                            }
                        });
                        deleteGroups.deleteGroups(deletingGroups());
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Crea il JSON contente i gruppi da eliminare
     * @return il JSON dei gruppi da eliminare
     */
    JSONObject deletingGroups(){
        JSONObject deletingListObj = new JSONObject();
        JSONArray deletingListArray = new JSONArray();
        for (int i = 0; i< groupList.size(); i++ ){
            Info group = groupList.get(i);
            if (group.isChecked() == true){
                deletingListArray.put(group.getName());
            }
        }
        try {
            deletingListObj.put("group_delete", deletingListArray);
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
     *  abilita tutte le CheckBox per permettere l'eliminazione di più gruppi
     */
    private void enableCheckbox(boolean flag){
        if (dataAdapter != null) {
            dataAdapter.setEnableAll(flag);
            dataAdapter.notifyDataSetChanged();
        }

    }

    /**
     *  seleziona tutte le checkbox
     */
    private void selectAll(boolean flag){
        if (dataAdapter != null) {
            dataAdapter.setCheckAll(flag);
            dataAdapter.notifyDataSetChanged();
        }
    }

    /**
     * ottiene la lista di gruppi gestendo i diversi casi
     */
    private void retrieveGroupsList(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String jsonGroupsToString = sharedPref.getString(SAVED_GROUPS, null);
        JSONObject jsonObject = null;
        if (jsonGroupsToString == null){
            getGroupListFromFpp();
        } else {
            try {
                jsonObject = new JSONObject(jsonGroupsToString);
            } catch (JSONException e){
                e.printStackTrace();
            }
            checkAndSetListView(jsonObject);
        }
        listView.setEnabled(true);
    }


    /**
     * recupera la lista delle gruppi dal server di fpp
     */
    private void getGroupListFromFpp(){
        listView.setVisibility(View.INVISIBLE);
        GroupActions.GetGroupsList groupList = new GroupActions.GetGroupsList(this);
        groupList.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                GroupMain.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setListView(rst);
                        setSharedPreferences(rst);
                    }
                });

            }
        });
        GroupMain.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(GroupMain.this.getApplicationContext(), "Updating", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        groupList.getGroupsList();

    }

    /**
     * controlla la data in cui è stata creata la copia della lista dei gruppi e nel caso la aggiorna e setta
     * la list view
     * @param jsonObject JSON contentente le informazioni sulla lista dei gruppi
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
                getGroupListFromFpp();
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
            case REQUEST_GROUP_CREATE:
                if (resultCode == Activity.RESULT_OK) {
                    getGroupListFromFpp();
                }
                break;
            case REQUEST_GROUP_INFO:
                if (resultCode == RESULT_OK){
                    getGroupListFromFpp();
                }
                selectAll(false);
                enableCheckbox(false);
                showDeleteToolbar(false);
                break;
        }
        listView.setEnabled(true);

    }

    /**
     * salva la stringa contenente la lista dei gruppi nelle risorse locali
     * @param jsonObject è il JSON contentente la lista dei gruppi
     */
    private void setSharedPreferences(JSONObject jsonObject){
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            jsonObject.put("date", timeStamp);
            String resultString = jsonObject.toString();
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SAVED_GROUPS, resultString);
            editor.commit();
        } catch (JSONException e){
            e.printStackTrace();
        }


    }

    /**
     * inserisce la lista dei gruppi nella list view
     * @param jsonObject è il JSON contentente la lista dei gruppi
     */
    protected void setListView(JSONObject jsonObject){
        groupList = Info.infoArrayFromJson(jsonObject, Info.GROUP);

        dataAdapter = new CustomCheckList(this, R.layout.list_check_text_text, groupList);
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
     * la lista di Group
     *
     * @param intent enum che specifica il motivo
     * @param groupInfo Informazioni sul Group selezionato
     */
    private void startIntent(OnClickIntent intent, final Info groupInfo){
        switch (intent){
            case getInfo:
                Intent groupInfoIntent = new Intent (getApplicationContext(), GroupInfo.class);
                groupInfoIntent.putExtra("group_id", groupInfo.getId());
                startActivityForResult(groupInfoIntent, REQUEST_GROUP_INFO);
                break;
            case doTraining:
                TrainingActions.TrainIdentify identify = new TrainingActions.TrainIdentify(GroupMain.this);
                identify.setCallback(new Callback() {
                    @Override
                    public void processResult(JSONObject rst) {
                        try {
                            String sessionId = rst.getString("session_id");
                            Training t = new Training(Training.Target.GROUP, groupInfo, sessionId);
                            TrainingActions.SaveTraining saveTraining = new TrainingActions.SaveTraining(GroupMain.this);
                            saveTraining.saveTraining(t);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        GroupMain.this.runOnUiThread(new Runnable() {
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
                identify.identify(groupInfo.getId());

                break;
            default:
                break;
        }
    }

}
