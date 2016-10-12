package com.univpm.s1055802.faceplusplustester;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.univpm.s1055802.faceplusplustester.Detect.AcquirePhoto;
import com.univpm.s1055802.faceplusplustester.Detect.AcquireVideo;
import com.univpm.s1055802.faceplusplustester.Gallery.GalleryMain;
import com.univpm.s1055802.faceplusplustester.Faceset.FacesetActions;
import com.univpm.s1055802.faceplusplustester.Faceset.FacesetMain;
import com.univpm.s1055802.faceplusplustester.Group.GroupMain;
import com.univpm.s1055802.faceplusplustester.Person.PersonActions;
import com.univpm.s1055802.faceplusplustester.Person.PersonMain;
import com.univpm.s1055802.faceplusplustester.Training.TrainingMain;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.Directories;
import com.univpm.s1055802.faceplusplustester.Utils.FileUtils;
import com.univpm.s1055802.faceplusplustester.Utils.Permissions;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends AppCompatActivity {

    private Button btnAcquire;
    private Button btnTest;
    private Button btnGallery;

    private static final int FIST_RUN = 0;
    private static final int WRITE_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        checkAuth();
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Permissions.checkPermissions(Main.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION, new Runnable() {
                @Override
                public void run() {
                    checkDirectories();
                }
            });
        } else {
            checkDirectories();
        }
        initUIL();
        setSupportActionBar(toolbar);
    }

    /**
     * crea le directory utilizzate dall'app se non esistenti
     *
     *  Directory Three:
     *  /FppTester
     *  /FppTester/.temp
     *  /FppTester/images
     *  /FppTester/images/.thumbnails
     *  /FppTester/images/.faces
     *  /FppTester/videos
     *  /FppTester/tests
     */
    private void checkDirectories(){
        ArrayList<String> dirArray = new ArrayList<>();
        dirArray.add(Directories.MAIN);
        dirArray.add(Directories.FACES);
        dirArray.add(Directories.IMAGES);
        dirArray.add(Directories.TEMP);
        dirArray.add(Directories.TESTS);
        dirArray.add(Directories.THUMB);
        dirArray.add(Directories.VIDEOS);
        for (String dir: dirArray) {
            File directory = new File(dir);
            if (!directory.exists()){
                directory.mkdir();
            }
        }

        File noMedia = new File (Directories.NOMEDIA);
        if (!noMedia.exists()){
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Controlla che siano state inserite le informazioni per accedere al server FacePlusPlus
     * in caso negativo avvia una procedura in inserimento dati
     */
    private void checkAuth(){
        SharedPreferences auth = getSharedPreferences("auth", Context.MODE_PRIVATE);
        if (auth.getString("key", null) == null){
            Log.v("auth", "no");
            Intent intent = new Intent(getApplicationContext(), FirstRun.class);
            startActivityForResult(intent, FIST_RUN);
        } else {
            Log.v("auth", "ok");
            ((FppTester) getApplication()).setApiKey(auth.getString("key", null));
            ((FppTester) getApplication()).setApiSecret(auth.getString("secret", null));
            ((FppTester) getApplication()).setCN(auth.getBoolean("is_cn", false));
            ((FppTester) getApplication()).setDebug(auth.getBoolean("is_debug", false));
        }
    }

    /**
     * Nelle versioni android 6.0+ avvia l'evento di cattura dopo l'acquisizione dei permessi<
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_PERMISSION: {
                if (grantResults.length > 0){
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        checkDirectories();
                    }
                } else {
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == FIST_RUN){
                SharedPreferences auth = getSharedPreferences("auth", Context.MODE_PRIVATE);
                ((FppTester) getApplication()).setApiKey(auth.getString("key", null));
                ((FppTester) getApplication()).setApiSecret(auth.getString("secret", null));
                ((FppTester) getApplication()).setCN(auth.getBoolean("is_cn", false));
                ((FppTester) getApplication()).setDebug(auth.getBoolean("is_debug", false));


            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            resetApplication();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Permette di acquisire un soggetto sul quale effettuare tutte le operazioni
     * Visualizza una finestra di dialogo che permette di scegliere la modalità di acquisizione
     *
     * @param v
     */
    public void acquireSubject(View v){
        choiseDialog();
    }

    /**
     * Visualizza la lista delle Person
     * @param v
     */
    public void personsList(View v){
        Intent intent = new Intent(getApplicationContext(), PersonMain.class);
        intent.putExtra("onClickIntent", PersonMain.OnClickIntent.getInfo);
        startActivity(intent);
    }

    /**
     * Visualizza la lista dei Group
     * @param v
     */
    public void groupsList(View v){
        Intent intent = new Intent(getApplicationContext(), GroupMain.class);
        intent.putExtra("onClickIntent", GroupMain.OnClickIntent.getInfo);
        startActivity(intent);
    }

    /**
     * Visualizza la lista dei Faceset
     * @param v
     */
    public void facesetsList(View v){
        Intent intent = new Intent(getApplicationContext(), FacesetMain.class);
        intent.putExtra("onClickIntent", FacesetMain.OnClickIntent.getInfo);
        startActivity(intent);

    }

    /**
     * Visualizza la lista dei Training
     * @param v
     */
    public void trainingsList(View v){
        Intent intent = new Intent(getApplicationContext(), TrainingMain.class);
        intent.putExtra("onClickIntent", TrainingMain.OnClickIntent.getInfo);
        startActivity(intent);

    }

    /**
     * Visualizza una finestra di dialogo che permette di scegliere la modalità di acquisizione
     */
    private void choiseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acquire")
                .setItems(R.array.acquire_choise, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                       switch (which){
                           case 0:
                               intent = new Intent(getApplicationContext(),AcquirePhoto.class);
                               startActivity(intent);
                               break;
                           case 1:
                               intent = new Intent(getApplicationContext(), AcquireVideo.class);
                               startActivity(intent);
                               break;
                           case 2:
                               intent = new Intent(getApplicationContext(), GalleryMain.class);
                               intent.putExtra("dir", Directories.IMAGES);
                               startActivity(intent);
                               break;

                       }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Inizializza la libreria Universal Image Loadeer
     */
    private void initUIL(){

        // Create configuration for ImageLoader (all options are optional)
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(1500000) // 1.5 Mb
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .build();

        // Get singletone instance of ImageLoader
        // Initialize ImageLoader with created configuration. Do it once.
        ImageLoader.getInstance().init(config);
    }

    /**
     * Resetta l'applicazione eliminado tutti i dati ad essa collegati
     */
    private void resetApplication(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("You are sure to erase all settings and files ?")
                .setIcon(R.drawable.fpp_delete_black)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        //elimina tutte le sharedPreferences
                        File sharedPreferenceFile = new File("/data/data/"+ getPackageName()+ "/shared_prefs/");
                        FileUtils.deleteRecursive(sharedPreferenceFile);

                        //elimina tutti i files
                        File mainDir = new File(Directories.MAIN);
                        FileUtils.deleteRecursive(mainDir);

                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

}
