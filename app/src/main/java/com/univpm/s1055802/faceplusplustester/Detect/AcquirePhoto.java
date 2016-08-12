package com.univpm.s1055802.faceplusplustester.Detect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.univpm.s1055802.faceplusplustester.Utils.Directories;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kekko on 29/04/16.
 */
public class AcquirePhoto extends Activity {

    static final int REQUEST_TAKE_PHOTO = 3;
    static final int REQUEST_DETECT_PHOTO = 4;
    static final String AQUIRED_PHOTO = "Photo";

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Acquire();

    }

    /**
     * Richiama la fotocamera, acquisisce la foto e la salva in un file
     * @return il file salvato
     */
    private void Acquire(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        photoFile = null;
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.v("alert", "file error");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    /**
     * Crea il file destinato alla foto acquisita
     * @return il File destinato a contenere la foto acquisita
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "FPP_" + timeStamp + "_";
        File storageDir = new File(Directories.IMAGES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO){
            if (resultCode == RESULT_CANCELED) {
                photoFile.delete();
                setResult(Activity.RESULT_CANCELED);
            }
            else {
                Intent detectIntent = new Intent(this, DetectImage.class);
                detectIntent.putExtra(AQUIRED_PHOTO, photoFile.getAbsolutePath());
                startActivityForResult(detectIntent, REQUEST_DETECT_PHOTO);
            }
            finish();
        } else if (requestCode == REQUEST_DETECT_PHOTO){
            finish();
        }
    }
}
