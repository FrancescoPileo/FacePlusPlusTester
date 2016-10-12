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

package com.univpm.s1055802.faceplusplustester.Training;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kekko on 14/06/16.
 */

/**
 * Classe che contiene tutte le informazioni di un Training
 */
public class Training {

    public class Target {
        public static final String PERSON = "Person";
        public static final String GROUP = "Group";
        public static final String FACESET = "Faceset";
    }

    public class Status {
        public static final String NEW = "NEW";
        public static final String INQUEUE = "INQUEUE";
        public static final String SUCC = "SUCC";
        public static final String FAILED = "FAILED";
        public static final String MODIFIED = "MODIFIED";
    }

    private static final String SAVED_TRAININGS = "saved_trainings";


    String target = null;
    String targetId = null;
    String targetName = null;
    String sessionId = null;
    String status = null;
    String date = null;

    public Training(String target, Info targetInfo, String sessionId){
        this.target = target;
        this.targetId = targetInfo.getId();
        this.targetName = targetInfo.getName();
        this.sessionId = sessionId;
        this.status = Status.NEW;
        this.date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    }

    public Training(JSONObject trainer) throws JSONException {
        this.setTarget(trainer.getString("target"));
        this.setStatus(trainer.getString("status"));
        this.setTargetId(trainer.getString("target_id"));
        this.setTargetName(trainer.getString("target_name"));
        this.setSessionId(trainer.getString("session_id"));
        this.setDate(trainer.getString("date"));
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("date", this.getDate());
        result.put("target", this.getTarget());
        result.put("target_id", this.getTargetId());
        result.put("target_name", this.getTargetName());
        result.put("session_id", this.getSessionId());
        result.put("status", this.getStatus());
        return result;
    }

    public static ArrayList<Training> trainingArrayFromJson(JSONObject jsonObject) {
        ArrayList<Training> array = new ArrayList<Training>();
        try {
            int trainingNumber = jsonObject.getJSONArray("training").length();
            for (int i = 0; i < trainingNumber; i++) {
                Training training = new Training(jsonObject.getJSONArray("training").getJSONObject(i));
                array.add(training);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;
    }

    public static JSONObject trainingJsonFromArray (ArrayList<Training> arrayList) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++) {
            Log.v("list", String.valueOf(i));
            try {
                jsonArray.put(arrayList.get(i).toJSONObject());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonObject.put("training", (Object) jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("json", jsonObject.toString());
        return jsonObject;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
