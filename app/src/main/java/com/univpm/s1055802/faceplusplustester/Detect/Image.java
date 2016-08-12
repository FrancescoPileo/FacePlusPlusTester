package com.univpm.s1055802.faceplusplustester.Detect;

import com.univpm.s1055802.faceplusplustester.Face.Face;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by kekko on 30/04/16.
 */

/**
 * Classe che contiene tutte le informazioni di una immagine sulla quale è stata svolta una Detect
 */
public class Image {
    private String imgId;
    private int facesNumber;
    private Face[] faces;
    private int imgHeight;
    private int imgWidth;
    private String sessionId;
    private String Url;

    public Image(JSONObject rst) throws JSONException{
        this.setImgId(rst.getString("img_id"));
        this.setImgHeight(rst.getInt("img_height"));
        this.setImgWidth(rst.getInt("img_width"));
        this.setSessionId(rst.getString("session_id"));
        this.setUrl(rst.getString("url"));
        this.setFacesNumber(rst.getJSONArray("face").length());
        this.setFaces(new Face[this.getFacesNumber()]);
        for (int i=0; i < getFacesNumber(); i++){
            this.setFace(i, new Face(rst.getJSONArray("face").getJSONObject(i)));
        }
    }

    @Override
    public String toString() {
        String out = "Image{" +
                "imgId='" + imgId + '\'' +
                ", facesNumber=" + facesNumber +
                ", faces=" + Arrays.toString(faces) +
                ", imgHeight=" + imgHeight +
                ", imgWidth=" + imgWidth +
                ", sessionId='" + sessionId + '\'' +
                ", Url='" + Url + '\'' +
                '}';

        return out;
    }

    public Image(String imgId) {
        this.imgId = imgId;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public int getFacesNumber() {
        return facesNumber;
    }

    public void setFacesNumber(int facesNumber) {
        this.facesNumber = facesNumber;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Face[] getFaces() {
        return faces;
    }

    public void setFaces(Face[] faces) {
        this.faces = faces;
    }

    public Face getFace(int faceIndex){
        return this.faces[faceIndex];
    }

    public void setFace(int faceIndex, Face face){
        this.faces[faceIndex] = face;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidht) {
        this.imgWidth = imgWidht;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
