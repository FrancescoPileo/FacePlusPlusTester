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

package com.univpm.s1055802.faceplusplustester.Detect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.univpm.s1055802.faceplusplustester.Gallery.GalleryMain;
import com.univpm.s1055802.faceplusplustester.Utils.Directories;
import com.univpm.s1055802.faceplusplustester.Utils.FileUtils;
import com.univpm.s1055802.faceplusplustester.Utils.Permissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


//todo aggiungere toolbar back (anche ad acquire)
public class AcquireVideo extends AppCompatActivity {

    static final int REQUEST_TAKE_VIDEO = 3;
    static final int GALLERY_INTENT_CALLED = 5;
    static final int CAMERA_AND_WRITE_PERMISSION = 1;


    private File videoFile;
    private File videoDir;
    private File framesDir;
    private Intent galleryIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Permissions.checkPermissions(AcquireVideo.this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_AND_WRITE_PERMISSION, new Runnable() {
                @Override
                public void run() {
                    Acquire();
                }
            });
        } else {
            Acquire();
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
            case CAMERA_AND_WRITE_PERMISSION: {
                if (grantResults.length > 0){
                    boolean allPermission = true;
                    for (int i = 0; i < grantResults.length && allPermission; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            allPermission = false;
                        }
                    }
                    if (allPermission){
                        Acquire();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                return;
            }
        }
    }

    /**
     * Richiama la fotocamera, acquisisce il video e lo salva in un file
     * @return il file salvato
     */
    private void Acquire(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        videoFile = null;
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.v("alert", "file error");
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
            }
        }
    }

    /**
     * Crea il file destinato al video acquisito
     * @return il File destinato a contenere il video acquisito
     * @throws IOException
     */
    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "FPP_" + timeStamp + "_";
        String videoFileDir = "FPP_" + timeStamp;
        videoDir = new File(Directories.VIDEOS, videoFileDir);
        videoDir.mkdirs();
        framesDir = new File(videoDir, "Frames");
        framesDir.mkdirs();
        createGalleryIntent(framesDir);

        File video = File.createTempFile(
                videoFileName,  /* prefix */
                ".mp4",         /* suffix */
                videoDir      /* directory */
        );

        return video;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_VIDEO){
            if (resultCode == RESULT_CANCELED) {
                FileUtils.deleteRecursive(videoDir);
                setResult(Activity.RESULT_CANCELED);
            } else {
                captureFrames(Uri.fromFile(videoFile));
                //captureFrames(data.getData());
                startActivityForResult(galleryIntent, GALLERY_INTENT_CALLED);
            }
            finish();
        }
    }

    /**
     * Crea l'intent per la cattura del video
     * @param Dir la directory dove sono salvati i frames del video
     */
    private void createGalleryIntent(File Dir)
    {
        galleryIntent = new Intent(getApplicationContext(), GalleryMain.class);
        galleryIntent.putExtra("dir", Dir.getAbsolutePath());
        galleryIntent.putExtra("video", false);
    }

    /**
     * Estrae un frame per ogni secondo del video e li salva sul dispositivo
     * @param uri uri della cartella dove salvare i frames
     */
    private void captureFrames(Uri uri){
        String filePath = FileUtils.getPath(getApplicationContext(), uri);
        File video = new  File(filePath);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            double duration = Double.parseDouble(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            Log.v("duration", String.valueOf(duration));

            int timeSkip = 1000000;
            for (int i = 0; i < duration*1000; i = i + timeSkip) {
                File img = null;
                try {
                    img = new File(framesDir, "Frame_" + String.valueOf(i/timeSkip) + ".jpg");
                    OutputStream fOut = null;
                    fOut = new FileOutputStream(img);
                    Bitmap imgBmp = retriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_NEXT_SYNC);
                    imgBmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }

    }
}
