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
