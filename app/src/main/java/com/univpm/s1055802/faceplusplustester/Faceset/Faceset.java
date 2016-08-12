package com.univpm.s1055802.faceplusplustester.Faceset;

import com.univpm.s1055802.faceplusplustester.Face.Face;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kekko on 19/05/16.
 */

/**
 * Classe che contiene tutte le informazioni di un Faceset
 */
public class Faceset extends Info {

    protected int faceNumber;
    protected Face[] faces;


    public Faceset(JSONObject jsonObject, String infoType) throws JSONException{
        super(jsonObject, infoType);

        if (!jsonObject.isNull("face")){
            this.setFaceNumber(jsonObject.getJSONArray("face").length());
            this.setFaces(new Face[getFaceNumber()]);
            for (int i = 0; i < getFaceNumber(); i++){
                this.setFace(i, new Face(jsonObject.getJSONArray("face").getJSONObject(i)));
            }
        }
    }

    public Faceset(JSONObject jsonObject) throws JSONException{
        super(jsonObject, Info.FACESET);

        if (!jsonObject.isNull("face")){
            this.setFaceNumber(jsonObject.getJSONArray("face").length());
            this.setFaces(new Face[getFaceNumber()]);
            for (int i = 0; i < getFaceNumber(); i++){
                this.setFace(i, new Face(jsonObject.getJSONArray("face").getJSONObject(i)));
            }
        }
    }

    public Face[] getFaces() {
        return faces;
    }

    public void setFaces(Face[] faces) {
        this.faces = faces;
    }

    public Face getFace(int index){
        return  faces[index];
    }

    public void setFace(int index, Face face){
        this.faces[index] = face;
    }

    public int getFaceNumber() {
        return faceNumber;
    }

    public void setFaceNumber(int faceNumber) {
        this.faceNumber = faceNumber;
    }
}
