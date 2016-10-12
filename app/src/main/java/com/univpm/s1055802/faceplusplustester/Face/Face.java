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

package com.univpm.s1055802.faceplusplustester.Face;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kekko on 30/04/16.
 */

/**
 * Classe che contiene tutte le informazioni di una Face sulla quale è stata svolta una Detect
 */
public class Face {
    private String faceId;
    private int faceAttrAgeRange;
    private int faceAttrAgeValue;
    private double faceAttrGenderConfidence;
    private String faceAttrGenderValue;
    /* Analizzando il JSON non ci sono questi dati
    private double faceAttrGlassConfidence;
    private String faceAttrGlassValue;
    private double faceAttrPosePitchAngleValue;
    private double faceAttrPoseRollAngleValue;
    private double faceAttrPoseYawAngleValue;
    */
    private double faceAttrRaceConfidence;
    private String faceAttrRaceValue;
    private double faceAttrSmilingValue;
    private double facePositionCenterX;
    private double facePositionCenterY;
    private double facePositionEyeLeftX;
    private double facePositionEyeLeftY;
    private double facePositionEyeRightX;
    private double facePositionEyeRightY;
    private double facePositionHeight;
    private double facePositionWidth;
    private double facePositionMouthLeftX;
    private double facePositionMouthLeftY;
    private double facePositionMouthRightX;
    private double facePositionMouthRightY;
    private double facePositionNoseX;
    private double facePositionNoseY;
    private String faceTag;

    private boolean toDelete;


    public Face(JSONObject face) throws JSONException {
        this.setFaceId(face.getString("face_id"));
        this.setFaceTag(face.getString("tag"));
        if (!face.isNull("attribute")) {
            this.setFaceAttrAgeRange(face.getJSONObject("attribute").getJSONObject("age").getInt("range"));
            this.setFaceAttrAgeValue(face.getJSONObject("attribute").getJSONObject("age").getInt("value"));
            this.setFaceAttrGenderConfidence(face.getJSONObject("attribute").getJSONObject("gender").getDouble("confidence"));
            this.setFaceAttrGenderValue(face.getJSONObject("attribute").getJSONObject("gender").getString("value"));
            /* Analizzando il JSON non ci sono questi dati
            this.setFaceAttrGlassConfidence(face.getJSONObject("attribute").getJSONObject("glass").getDouble("confidence"));
            this.setFaceAttrGlassValue(face.getJSONObject("attribute").getJSONObject("glass").getString("value"));
            this.setFaceAttrPosePitchAngleValue(face.getJSONObject("attribute").getJSONObject("pose").getJSONObject("pitch_angle").getDouble("value"));
            this.setFaceAttrPoseRollAngleValue(face.getJSONObject("attribute").getJSONObject("pose").getJSONObject("roll_angle").getDouble("value"));
            this.setFaceAttrPoseYawAngleValue(face.getJSONObject("attribute").getJSONObject("pose").getJSONObject("yaw_angle").getDouble("value"));
            */
            this.setFaceAttrRaceConfidence(face.getJSONObject("attribute").getJSONObject("race").getDouble("confidence"));
            this.setFaceAttrRaceValue(face.getJSONObject("attribute").getJSONObject("race").getString("value"));
            this.setFaceAttrSmilingValue(face.getJSONObject("attribute").getJSONObject("smiling").getDouble("value"));
            this.setFacePositionCenterX(face.getJSONObject("position").getJSONObject("center").getDouble("x"));
            this.setFacePositionCenterY(face.getJSONObject("position").getJSONObject("center").getDouble("y"));
            this.setFacePositionEyeLeftX(face.getJSONObject("position").getJSONObject("eye_left").getDouble("x"));
            this.setFacePositionEyeLeftY(face.getJSONObject("position").getJSONObject("eye_left").getDouble("y"));
            this.setFacePositionEyeRightX(face.getJSONObject("position").getJSONObject("eye_right").getDouble("x"));
            this.setFacePositionEyeRightY(face.getJSONObject("position").getJSONObject("eye_right").getDouble("y"));
            this.setFacePositionHeight(face.getJSONObject("position").getDouble("height"));
            this.setFacePositionWidth(face.getJSONObject("position").getDouble("width"));
            this.setFacePositionMouthLeftX(face.getJSONObject("position").getJSONObject("mouth_left").getDouble("x"));
            this.setFacePositionMouthLeftY(face.getJSONObject("position").getJSONObject("mouth_left").getDouble("y"));
            this.setFacePositionMouthRightX(face.getJSONObject("position").getJSONObject("mouth_right").getDouble("x"));
            this.setFacePositionMouthRightY(face.getJSONObject("position").getJSONObject("mouth_right").getDouble("y"));
            this.setFacePositionNoseX(face.getJSONObject("position").getJSONObject("nose").getDouble("x"));
            this.setFacePositionNoseY(face.getJSONObject("position").getJSONObject("nose").getDouble("y"));
        }
        toDelete = false;
    }

    @Override
    public String toString() {
        return "Face{" +
                "faceId='" + faceId + '\'' +
                ", faceAttrAgeRange=" + faceAttrAgeRange +
                ", faceAttrAgeValue=" + faceAttrAgeValue +
                ", faceAttrGenderConfidence=" + faceAttrGenderConfidence +
                ", faceAttrGenderValue='" + faceAttrGenderValue + '\'' +
                ", faceAttrRaceConfidence=" + faceAttrRaceConfidence +
                ", faceAttrRaceValue='" + faceAttrRaceValue + '\'' +
                ", faceAttrSmilingValue=" + faceAttrSmilingValue +
                ", facePositionCenterX=" + facePositionCenterX +
                ", facePositionCenterY=" + facePositionCenterY +
                ", facePositionEyeLeftX=" + facePositionEyeLeftX +
                ", facePositionEyeLeftY=" + facePositionEyeLeftY +
                ", facePositionEyeRightX=" + facePositionEyeRightX +
                ", facePositionEyeRightY=" + facePositionEyeRightY +
                ", facePositionHeight=" + facePositionHeight +
                ", facePositionWidth=" + facePositionWidth +
                ", facePositionMouthLeftX=" + facePositionMouthLeftX +
                ", facePositionMouthLeftY=" + facePositionMouthLeftY +
                ", facePositionMouthRightX=" + facePositionMouthRightX +
                ", facePositionMouthRightY=" + facePositionMouthRightY +
                ", facePositionNoseX=" + facePositionNoseX +
                ", facePositionNoseY=" + facePositionNoseY +
                ", faceTag='" + faceTag + '\'' +
                '}';
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public int getFaceAttrAgeRange() {
        return faceAttrAgeRange;
    }

    public void setFaceAttrAgeRange(int faceAttrAgeRange) {
        this.faceAttrAgeRange = faceAttrAgeRange;
    }

    public int getFaceAttrAgeValue() {
        return faceAttrAgeValue;
    }

    public void setFaceAttrAgeValue(int faceAttrAgeValue) {
        this.faceAttrAgeValue = faceAttrAgeValue;
    }

    public double getFaceAttrGenderConfidence() {
        return faceAttrGenderConfidence;
    }

    public void setFaceAttrGenderConfidence(double faceAttrGenderConfidence) {
        this.faceAttrGenderConfidence = faceAttrGenderConfidence;
    }

    public String getFaceAttrGenderValue() {
        return faceAttrGenderValue;
    }

    public void setFaceAttrGenderValue(String faceAttrGenderValue) {
        this.faceAttrGenderValue = faceAttrGenderValue;
    }

    /*Analizzando il JSON non ci sono questi dati

    public double getFaceAttrGlassConfidence() {
        return faceAttrGlassConfidence;
    }

    public void setFaceAttrGlassConfidence(double faceAttrGlassConfidence) {
        this.faceAttrGlassConfidence = faceAttrGlassConfidence;
    }

    public String getFaceAttrGlassValue() {
        return faceAttrGlassValue;
    }

    public void setFaceAttrGlassValue(String faceAttrGlassValue) {
        this.faceAttrGlassValue = faceAttrGlassValue;
    }

    public double getFaceAttrPosePitchAngleValue() {
        return faceAttrPosePitchAngleValue;
    }

    public void setFaceAttrPosePitchAngleValue(double faceAttrPosePitchAngleValue) {
        this.faceAttrPosePitchAngleValue = faceAttrPosePitchAngleValue;
    }

    public double getFaceAttrPoseRollAngleValue() {
        return faceAttrPoseRollAngleValue;
    }

    public void setFaceAttrPoseRollAngleValue(double faceAttrPoseRollAngleValue) {
        this.faceAttrPoseRollAngleValue = faceAttrPoseRollAngleValue;
    }

    public double getFaceAttrPoseYawAngleValue() {
        return faceAttrPoseYawAngleValue;
    }

    public void setFaceAttrPoseYawAngleValue(double faceAttrPoseYawAngleValue) {
        this.faceAttrPoseYawAngleValue = faceAttrPoseYawAngleValue;
    }
    */

    public double getFaceAttrRaceConfidence() {
        return faceAttrRaceConfidence;
    }

    public void setFaceAttrRaceConfidence(double faceAttrRaceConfidence) {
        this.faceAttrRaceConfidence = faceAttrRaceConfidence;
    }

    public String getFaceAttrRaceValue() {
        return faceAttrRaceValue;
    }

    public void setFaceAttrRaceValue(String faceAttrRaceValue) {
        this.faceAttrRaceValue = faceAttrRaceValue;
    }

    public double getFaceAttrSmilingValue() {
        return faceAttrSmilingValue;
    }

    public void setFaceAttrSmilingValue(double faceAttrSmilingValue) {
        this.faceAttrSmilingValue = faceAttrSmilingValue;
    }

    public double getFacePositionCenterX() {
        return facePositionCenterX;
    }

    public void setFacePositionCenterX(double facePositionCenterX) {
        this.facePositionCenterX = facePositionCenterX;
    }

    public double getFacePositionCenterY() {
        return facePositionCenterY;
    }

    public void setFacePositionCenterY(double facePositionCenterY) {
        this.facePositionCenterY = facePositionCenterY;
    }

    public double getFacePositionEyeLeftX() {
        return facePositionEyeLeftX;
    }

    public void setFacePositionEyeLeftX(double facePositionEyeLeftX) {
        this.facePositionEyeLeftX = facePositionEyeLeftX;
    }

    public double getFacePositionEyeLeftY() {
        return facePositionEyeLeftY;
    }

    public void setFacePositionEyeLeftY(double facePositionEyeLeftY) {
        this.facePositionEyeLeftY = facePositionEyeLeftY;
    }

    public double getFacePositionEyeRightX() {
        return facePositionEyeRightX;
    }

    public void setFacePositionEyeRightX(double facePositionEyeRightX) {
        this.facePositionEyeRightX = facePositionEyeRightX;
    }

    public double getFacePositionEyeRightY() {
        return facePositionEyeRightY;
    }

    public void setFacePositionEyeRightY(double facePositionEyeRightY) {
        this.facePositionEyeRightY = facePositionEyeRightY;
    }

    public double getFacePositionHeight() {
        return facePositionHeight;
    }

    public void setFacePositionHeight(double facePositionHeight) {
        this.facePositionHeight = facePositionHeight;
    }

    public double getFacePositionWidth() {
        return facePositionWidth;
    }

    public void setFacePositionWidth(double facePositionWidth) {
        this.facePositionWidth = facePositionWidth;
    }

    public double getFacePositionMouthLeftX() {
        return facePositionMouthLeftX;
    }

    public void setFacePositionMouthLeftX(double facePositionMouthLeftX) {
        this.facePositionMouthLeftX = facePositionMouthLeftX;
    }

    public double getFacePositionMouthLeftY() {
        return facePositionMouthLeftY;
    }

    public void setFacePositionMouthLeftY(double facePositionMouthLeftY) {
        this.facePositionMouthLeftY = facePositionMouthLeftY;
    }

    public double getFacePositionMouthRightX() {
        return facePositionMouthRightX;
    }

    public void setFacePositionMouthRightX(double facePositionMouthRightX) {
        this.facePositionMouthRightX = facePositionMouthRightX;
    }

    public double getFacePositionMouthRightY() {
        return facePositionMouthRightY;
    }

    public void setFacePositionMouthRightY(double facePositionMouthRightY) {
        this.facePositionMouthRightY = facePositionMouthRightY;
    }

    public double getFacePositionNoseX() {
        return facePositionNoseX;
    }

    public void setFacePositionNoseX(double facePositionNoseX) {
        this.facePositionNoseX = facePositionNoseX;
    }

    public double getFacePositionNoseY() {
        return facePositionNoseY;
    }

    public void setFacePositionNoseY(double facePositionNoseY) {
        this.facePositionNoseY = facePositionNoseY;
    }

    public String getFaceTag() {
        return faceTag;
    }

    public void setFaceTag(String faceTag) {
        this.faceTag = faceTag;
    }
}
