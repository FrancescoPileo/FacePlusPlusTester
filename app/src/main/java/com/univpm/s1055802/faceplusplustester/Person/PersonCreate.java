package com.univpm.s1055802.faceplusplustester.Person;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.Group.GroupActions;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.CustomCheckList;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 05/05/16.
 */
public class PersonCreate extends AppCompatActivity {

    private EditText txteName = null;
    private EditText txteTag = null;
    private ListView lstvGroups = null;
    private Toolbar toolbar = null;

    protected ArrayList<Info> groupList;
    private CustomCheckList dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_create);
        txteName = (EditText) findViewById(R.id.txteName);
        txteTag = (EditText) findViewById(R.id.txteTag);
        lstvGroups = (ListView) findViewById(R.id.lstvGroups);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getGroupList();
    }

    /**
     * Crea un'Array List contenente tutti gli id delle persone da aggiungere al gruppo
     * @return l'array list
     */
    private ArrayList <String> getSelectedGroups(){
        ArrayList<String> selectedGroups = new ArrayList<String>();
        for (int i=0; i < groupList.size(); i++){
            if (groupList.get(i).isChecked())
                selectedGroups.add(groupList.get(i).getId());
        }
        return selectedGroups;
    }

    /**
     * ottiene la lista delle persone e la inserisce nella list view
     *
     */
    private void getGroupList(){
        GroupActions.GetGroupsList getGroupsList = new GroupActions.GetGroupsList(PersonCreate.this);
        getGroupsList.setCallback(new Callback() {
            @Override
            public void processResult(final JSONObject rst) {
                PersonCreate.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setGroupsListView(rst);
                    }
                });

            }
        });
        PersonCreate.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(PersonCreate.this.getApplicationContext(), "Getting info", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        getGroupsList.getGroupsList();
    }

    /**
     * inserisce la lista di persone nella list view
     * @param jsonObject json contenente la lista delle persone
     */
    private void setGroupsListView(JSONObject jsonObject){
        groupList = Info.infoArrayFromJson(jsonObject, Info.GROUP);

        dataAdapter = new CustomCheckList(this, R.layout.list_check_text_text, groupList);
        lstvGroups.setAdapter(dataAdapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvGroups);
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
            ArrayList <String> selectedGroups = getSelectedGroups();
            PersonActions.SendCreate sendCreate = new PersonActions.SendCreate(PersonCreate.this);
            sendCreate.setCallback(new Callback() {
                @Override
                public void processResult(JSONObject rst) {
                    PersonCreate.this.runOnUiThread(new Runnable() {
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
            sendCreate.sendCreation(personName,personTag,selectedGroups);
        }

        return super.onOptionsItemSelected(item);
    }
}
