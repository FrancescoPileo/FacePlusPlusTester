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

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.Face.Face;
import com.univpm.s1055802.faceplusplustester.Face.FaceAction;
import com.univpm.s1055802.faceplusplustester.Faceset.FacesetMain;
import com.univpm.s1055802.faceplusplustester.Person.PersonMain;
import com.univpm.s1055802.faceplusplustester.Recognition.RecognitionSearchResults;
import com.univpm.s1055802.faceplusplustester.Training.TrainingMain;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.univpm.s1055802.faceplusplustester.Utils.ImageUtils.*;

public class DetectImage extends AppCompatActivity {

    private ImageView imvImage = null;
    private TextView txtvImage = null;
    private TextView txtvFace = null;
    private ListView lstvFace = null;
    private ListView lstvImage = null;
    private Button btnAssignPerson = null;
    private Button btnAssignFaceset = null;
    private Button btnIdentify = null;
    private Button btnVerify = null;
    private Button btnSearch = null;
    private String imgPath = null;
    private Bitmap imgBitmap = null;
    private Image img;
    private Face face;

    static final String ACQUIRED_PHOTO = "Photo";
    static final int REQUEST_PERSON_ASSIGN = 0;
    static final int REQUEST_FACESET_ASSIGN = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acquire_main);

        setImgPath(getIntent().getStringExtra(ACQUIRED_PHOTO));
        imvImage = (ImageView) findViewById(R.id.imvAcquired);
        txtvImage = (TextView) findViewById(R.id.txtvImage);
        txtvFace = (TextView) findViewById(R.id.txtvFace);
        lstvImage = (ListView) findViewById(R.id.lstvImage);
        lstvFace = (ListView) findViewById(R.id.lstvFace);
        btnAssignPerson = (Button) findViewById(R.id.btnAssignPerson);
        btnAssignFaceset = (Button) findViewById(R.id.btnAssignFaceset);
        btnIdentify = (Button) findViewById(R.id.btnIdentify);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnSearch = (Button) findViewById(R.id.btnSearch);


        txtvFace.setVisibility(View.INVISIBLE);
        lstvImage.setVisibility(View.INVISIBLE);
        lstvFace.setVisibility(View.INVISIBLE);
        btnAssignPerson.setVisibility(View.INVISIBLE);
        btnAssignFaceset.setVisibility(View.INVISIBLE);
        btnIdentify.setVisibility(View.INVISIBLE);
        btnVerify.setVisibility(View.INVISIBLE);
        btnSearch.setVisibility(View.INVISIBLE);



        Bitmap myBit = BitmapFactory.decodeFile(imgPath);
        myBit = rectifyRotation(getImgPath(),myBit);
        myBit = rectifySize(myBit);
        imgBitmap = myBit;
        imvImage.setImageBitmap(myBit);
        setImgBitmap(myBit);

        txtvImage.setText("Waiting ...");

        DetectActions.Detect faceppDetect = new DetectActions.Detect(this);
        faceppDetect.setCallback(new Callback() {

            public void processResult(JSONObject rst) {
                //use the red paint
                Paint paint = new Paint();
                paint.setColor(Color.CYAN);
                paint.setStrokeWidth(Math.max(imgBitmap.getWidth(), imgBitmap.getHeight()) / 100f);

                //create a new canvas
                Bitmap bitmap = Bitmap.createBitmap(imgBitmap.getWidth(), imgBitmap.getHeight(), imgBitmap.getConfig());
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(imgBitmap, new Matrix(), null);


                try {
                    img = new Image(rst);
                    //Log.v("ToString", img.toString());

                    //find out all faces
                    for (int i = 0; i < img.getFacesNumber(); ++i) {
                        float x, y, w, h;
                        //get the center point
                        x = (float) img.getFace(i).getFacePositionCenterX();
                        y = (float) img.getFace(i).getFacePositionCenterY();
                        //get face size
                        w = (float)img.getFace(i).getFacePositionWidth();
                        h = (float)img.getFace(i).getFacePositionHeight();

                        //change percent value to the real size
                        x = x / 100 * imgBitmap.getWidth();
                        w = w / 100 * imgBitmap.getWidth() * 0.7f;
                        y = y / 100 * imgBitmap.getHeight();
                        h = h / 100 * imgBitmap.getHeight() * 0.7f;

                        //draw the box to mark it out
                        canvas.drawLine(x - w, y - h, x - w, y + h, paint);
                        canvas.drawLine(x - w, y - h, x + w, y - h, paint);
                        canvas.drawLine(x + w, y + h, x - w, y + h, paint);
                        canvas.drawLine(x + w, y + h, x + w, y - h, paint);


                        //save the face in faces directory
                        //todo da provare
                        int faceX = Math.max(0,(int) (x - w));
                        int faceY = Math.max(0, (int) (y - h));
                        int faceW = Math.min((int) (2 * w), imgBitmap.getWidth() - faceX);
                        int faceH = Math.min( (int) (2 * h), imgBitmap.getHeight() - faceY);
                        Bitmap faceBmp = Bitmap.createBitmap(imgBitmap, faceX , faceY , faceW, faceH);
                        saveScaledFaceBitmap(faceBmp ,img.getFace(i).getFaceId());
                    }



                    //save new image
                    imgBitmap = bitmap;

                    DetectImage.this.runOnUiThread(new Runnable() {

                        public void run() {
                            //show the image
                            imvImage.setImageBitmap(imgBitmap);
                            txtvImage.setText("Image info: ");
                            updateImageInfo();
                            updateFaceInfo();
                            Toast toast = Toast.makeText(DetectImage.this.getApplicationContext(), "Done", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        faceppDetect.detect(imgBitmap);



    }

    /**
     * assegna la faccia selezionata ad una persona da scegliere tra quelle disponibili
     * @param v
     */
    public void assignFaceToPerson(View v){
        Intent assignIntent = new Intent(this, PersonMain.class);
        assignIntent.putExtra("face_id", face.getFaceId());
        assignIntent.putExtra("onClickIntent", PersonMain.OnClickIntent.assignFace);
        startActivityForResult(assignIntent, REQUEST_PERSON_ASSIGN);

    }

    /**
     * assegna la faccia selezionata ad un faceset da scegliere tra quelli disponibili
     * @param v
     */
    public void assignFaceToFaceset(View v){
       Intent assignIntent = new Intent(this, FacesetMain.class);
        assignIntent.putExtra("face_id", face.getFaceId());
        assignIntent.putExtra("onClickIntent", FacesetMain.OnClickIntent.assignFace);
        startActivityForResult(assignIntent, REQUEST_FACESET_ASSIGN);
    }

    /**
     * evita che l'activity si riavvii quando cambia la configurazione del cell
     * (cambio di orientamento, cambio dimensione schermo, comparsa/scomparsa tastiera)
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Imposta il percorso dell'immagine da analizzare
     * @param imgPath il percorso dell'immagine da analizzare
     */
    private void setImgPath(String imgPath){
        this.imgPath = imgPath;
    }

    /**, Bitmap source
     * Ottiene il percorso dell'immagine da analizzare
     * @return il percorso dell'immagine da analizzare
     */
    private String getImgPath(){
        return this.imgPath;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    /**
     * Ottiene (calcola) la dimensione dell' ImageView passata come parametro
     * @param imageView
     * @return
     */
    private int[] getImageViewDim(ImageView imageView){
        int[] dim = new int[2];
        dim[0] = imageView.getLayoutParams().height;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        dim[1] = size.x;

        return dim;
    }

    /**
     * restituisce un array di int con informazioni utili alla gestione dei click sulle faccie
     * @param imageView
     * @return  0 posizione x del bitmap nell'ImageView
     *          1 posizione y del bitmap nell'ImageView
     *          2 larghezza scalata del bitmap
     *          3 altezza scalata del bitmap
     */
    private int[] getBitmapInfoInsideImageView(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        int bmpHeight = bitmap.getHeight();
        int bmpWidth = bitmap.getWidth();
        //Log.v("bmpHeight", String.valueOf(bmpHeight) );
        //Log.v("bmpWidth", String.valueOf(bmpWidth));
        int[] imvDim = getImageViewDim(imageView);
        int imvHeight = imvDim[0];
        int imvWidth = imvDim[1];
        //Log.v("imvHeight", String.valueOf(imvHeight));
        //Log.v("imvWidth", String.valueOf(imvWidth));
        double xScale = 1, yScale = 1;
        int bmpScaledHeight = bmpHeight;
        int bmpScaledWidth = bmpWidth;
        int xBmpPosition = 0, yBmpPosition = 0;

        //TODO controllare se copre tutti i casi possibili
        if (bmpHeight>=bmpWidth){
            if (bmpHeight > imvHeight){
                yScale = (double) imvHeight / (double) bmpHeight;
                //Log.v("yScale", String.valueOf(yScale));
                yBmpPosition = 0;
                bmpScaledHeight = imvHeight;
                bmpScaledWidth =(int) (bmpWidth * yScale);
                //Log.v("bmpScaledWidth", String.valueOf(bmpScaledWidth));
                xBmpPosition = (imvWidth - bmpScaledWidth)/2;
            } else {
                xBmpPosition = (imvWidth - bmpWidth)/2;
                yBmpPosition = (imvHeight - bmpHeight)/2;
            }
        } else {
            if (bmpWidth > imvWidth){
                xScale = (double) imvWidth/ (double) bmpWidth;
                //Log.v("xScale", String.valueOf(xScale));
                xBmpPosition = 0;
                bmpScaledWidth = imvWidth;
                bmpScaledHeight =(int) (bmpHeight*xScale);
                //Log.v("bmpScaledHeight", String.valueOf(bmpScaledHeight));
                yBmpPosition = (imvHeight - bmpScaledHeight)/2;
            } else {
                xBmpPosition = (imvWidth - bmpWidth)/2;
                yBmpPosition = (imvHeight - bmpHeight)/2;
            }
        }

        int[] ret =  {xBmpPosition, yBmpPosition, bmpScaledWidth, bmpScaledHeight};

        return ret;

    }


    /**
     * imposta il listener che gestisce i touch sull'imageView
     */
    private void updateFaceInfo() {

        final int[] p = getBitmapInfoInsideImageView(imvImage);
        /*Log.v("xPos:", String.valueOf(p[0]));
        Log.v("yPos:", String.valueOf(p[1]));*/

        imvImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*Log.v("X >>>", event.getX() + "");
                Log.v("Y >>>", event.getY() + "");*/

                int realX = (int) (event.getX() - p[0]);
                int realY = (int) (event.getY() - p[1]);



                for (int i=0; i<img.getFacesNumber(); i++){
                    float x = (float) img.getFace(i).getFacePositionCenterX() / 100 * p[2];
                    float w = (float) img.getFace(i).getFacePositionWidth() / 100 * p[2] * 0.7f;
                    float y = (float) img.getFace(i).getFacePositionCenterY() / 100 * p[3];
                    float h = (float) img.getFace(i).getFacePositionHeight() / 100 * p[3] * 0.7f;
                    if (realX > (x - w) && realX < (x + w) && realY > (y - h) && realY < (y + h)){
                        updateFaceListView(i);
                        face = img.getFace(i);
                        btnAssignPerson.setVisibility(View.VISIBLE);
                        btnAssignFaceset.setVisibility(View.VISIBLE);
                        btnIdentify.setVisibility(View.VISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        btnSearch.setVisibility(View.VISIBLE);

                    }
                }

                /*Log.v("realX >>>", String.valueOf(realX));
                Log.v("realY >>>", String.valueOf(realY));*/

                return false;
            }
        });
    }

    /**
     * aggiorna le informazioni sull'immagine
     */
    private void updateImageInfo(){
        ArrayList<String> imgInfo = new ArrayList<String>();
        imgInfo.add("Id: " + img.getImgId());
        imgInfo.add("Faces detected: " + img.getFacesNumber());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, imgInfo);
        lstvImage.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvImage);
        lstvImage.setVisibility(View.VISIBLE);
    }

    /**
     * aggiorna le informazioni sulla faccia selezionata
     * @param faceIndex
     */
    private void updateFaceListView(int faceIndex){
        ArrayList<String> faceInfo = new ArrayList<String>();
        faceInfo.add("Id: " + img.getFace(faceIndex).getFaceId());
        faceInfo.add("Age: " + img.getFace(faceIndex).getFaceAttrAgeValue() + " +/- " + img.getFace(faceIndex).getFaceAttrAgeRange());
        faceInfo.add("Gender: " + img.getFace(faceIndex).getFaceAttrGenderValue() + " (" + img.getFace(faceIndex).getFaceAttrGenderConfidence() + "%)");
        faceInfo.add("Race: " + img.getFace(faceIndex).getFaceAttrRaceValue() + " (" + img.getFace(faceIndex).getFaceAttrRaceConfidence() + "%)");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, faceInfo);
        lstvFace.setAdapter(adapter);
        ImageUtils.setListViewHeightBasedOnItems(lstvFace);
        txtvFace.setVisibility(View.VISIBLE);
        lstvFace.setVisibility(View.VISIBLE);

    }

    /**
     * Avvia l'intent che permette il Search sulla face selezionata
     * @param v
     */
    public void searchFace(View v){
        Intent searchIntent = new Intent(this, TrainingMain.class);
        searchIntent.putExtra("face_id", face.getFaceId());
        searchIntent.putExtra("onClickIntent", TrainingMain.OnClickIntent.search);
        startActivity(searchIntent);

    }

    /**
     * Avvia l'intent che permette l'Identify sulla face selezionata
     * @param v
     */
    public void identifyFace(View v){
        Intent identifyIntent = new Intent(this, TrainingMain.class);
        identifyIntent.putExtra("face_id", face.getFaceId());
        identifyIntent.putExtra("onClickIntent", TrainingMain.OnClickIntent.identify);
        startActivity(identifyIntent);

    }

    /**
     * Avvia l'intent che permette il Verify sulla face selezionata
     * @param v
     */
    public void verifyFace(View v){
        Intent verifyIntent = new Intent(this, TrainingMain.class);
        verifyIntent.putExtra("face_id", face.getFaceId());
        verifyIntent.putExtra("onClickIntent", TrainingMain.OnClickIntent.verify);
        startActivity(verifyIntent);
    }

}