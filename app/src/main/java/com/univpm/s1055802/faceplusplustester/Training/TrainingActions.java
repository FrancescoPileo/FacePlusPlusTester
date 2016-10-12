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
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.univpm.s1055802.faceplusplustester.FppTester;
import com.univpm.s1055802.faceplusplustester.Utils.CallbackActions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 15/06/16.
 */
public class TrainingActions {

    private static final String SAVED_TRAININGS = "saved_trainings";


    /**
     * Avvia una procedura di Training su una Person per poi effettuare una Verify
     */
    public static class TrainVerify extends CallbackActions {

        public TrainVerify(AppCompatActivity activity){
            super(activity);
        }

        public void verify(final String personId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setPersonId(personId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).trainVerify(postParameters);
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    /**
     * Avvia una procedura di Training su un Group per poi effettuare un Identify
     */
    public static class TrainIdentify extends CallbackActions {

        public TrainIdentify(AppCompatActivity activity){
            super(activity);
        }

        public void identify(final String groupId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setGroupId(groupId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).trainIdentify(postParameters);
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    /**
     * Avvia una procedura di Training su un Faceset per poi effettuare una Search
     */
    public static class TrainSearch extends CallbackActions {

        public TrainSearch(AppCompatActivity activity){
            super(activity);
        }

        public void search(final String facesetId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setFacesetId(facesetId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).trainSearch(postParameters);
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    /**
     * Elimina un Training dal JSON salvato in locale
     */
    public static class DeleteTraining extends CallbackActions {

        public DeleteTraining(AppCompatActivity activity){
            super(activity);
        }

        public void deleteTraining(final String sessionId) {
            SharedPreferences sharedPref = activity.getSharedPreferences("trainings", Context.MODE_PRIVATE);
            String jsonTrainingsString = sharedPref.getString(SAVED_TRAININGS, null);
            JSONObject trainings = null;
            if (jsonTrainingsString != null) {
                try {
                    trainings = new JSONObject(jsonTrainingsString);
                    for (int i = 0; i < trainings.getJSONArray("training").length(); i++) {
                        if (sessionId.equals(trainings.getJSONArray("training").getJSONObject(i).getString("session_id"))) {
                            trainings.getJSONArray("training").remove(i);
                            Log.v("deleted", "ok");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SAVED_TRAININGS, trainings.toString());
            editor.commit();
            if (callback != null){
                callback.processResult(trainings);
            }

        }
    }

    /**
     * Cerca un Training nel JSON salvato in locale
     */
    public static class SearchTraining extends CallbackActions {

        public SearchTraining(AppCompatActivity activity) {
            super(activity);
        }

        public void searchTraining(final String sessionId) {
            SharedPreferences sharedPref = activity.getSharedPreferences("trainings", Context.MODE_PRIVATE);
            String jsonTrainingsString = sharedPref.getString(SAVED_TRAININGS, null);
            JSONObject trainings = null;
            JSONObject found = null;
            if (jsonTrainingsString != null) {
                try {
                    trainings = new JSONObject(jsonTrainingsString);
                    for (int i = 0; i < trainings.getJSONArray("training").length(); i++) {
                        if (sessionId.equals(trainings.getJSONArray("training").getJSONObject(i).getString("session_id"))) {
                            found = trainings.getJSONArray("training").getJSONObject(i);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (callback != null) {
                callback.processResult(found);
            }

        }
    }

    /**
     * Estrae tutti i Trainings salvati nel JSON locale
     */
    public static class getTrainings extends CallbackActions {

        public getTrainings(AppCompatActivity activity) {
            super(activity);
        }

        public void getTrainings(TrainingMain.OnClickIntent action) {
            SharedPreferences sharedPref = activity.getSharedPreferences("trainings", Context.MODE_PRIVATE);
            String jsonTrainingsString = sharedPref.getString(SAVED_TRAININGS, null);
            JSONObject jsonObject = null;

            if (jsonTrainingsString != null) {
                try {
                    jsonObject = new JSONObject(jsonTrainingsString);
                    if (action != TrainingMain.OnClickIntent.getInfo){
                        String show = null;
                        switch (action){
                            case search:
                                show = Training.Target.FACESET;
                                break;
                            case identify:
                                show = Training.Target.GROUP;
                                break;
                            case verify:
                                show = Training.Target.PERSON;
                                break;
                        }
                        JSONObject newJsonObject = jsonObject;
                        for (int i=0; i<jsonObject.getJSONArray("training").length(); i++){
                            if (!jsonObject.getJSONArray("training").getJSONObject(i).getString("target").equals(show)){
                                newJsonObject.getJSONArray("training").remove(i);
                                i--;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            if (callback != null) {

                callback.processResult(jsonObject);
            }

        }
    }

    /**
     * Aggiorna un Training
     */
    public static class UpdateTraining extends CallbackActions {

        public UpdateTraining(AppCompatActivity activity) {
            super(activity);
        }

        public void updateTraining(String sessionId, String status) {
            SharedPreferences sharedPref = activity.getSharedPreferences("trainings", Context.MODE_PRIVATE);
            String jsonTrainingsString = sharedPref.getString(SAVED_TRAININGS, null);
            JSONObject jsonObject = null;

            if (jsonTrainingsString != null) {
                try {
                    jsonObject = new JSONObject(jsonTrainingsString);
                    for (int i=0; i<jsonObject.getJSONArray("training").length(); i++){
                        if (jsonObject.getJSONArray("training").getJSONObject(i).getString("session_id").equals(sessionId)){
                            jsonObject.getJSONArray("training").getJSONObject(i).remove("status");
                            jsonObject.getJSONArray("training").getJSONObject(i).put("status", status);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SAVED_TRAININGS,jsonObject.toString());
                editor.commit();
            }
            if (callback != null) {
                callback.processResult(jsonObject);
            }
        }
    }

    /**
     * Salva un nuovo Training nel JSON locale
     */
    public static class SaveTraining extends CallbackActions{

        public SaveTraining(AppCompatActivity activity){
            super(activity);
        }

        public void saveTraining(Training training){
            SharedPreferences sharedPref = activity.getSharedPreferences("trainings", Context.MODE_PRIVATE);
            String jsonTrainingsString = sharedPref.getString(SAVED_TRAININGS, null);
            JSONObject trainings = null;
            if (jsonTrainingsString == null) {
                trainings = new JSONObject();
                JSONArray array = new JSONArray();
                try {
                    trainings.put("training", (Object) array);
                    array.put((Object) training.toJSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    trainings = new JSONObject(jsonTrainingsString);
                    for (int i = 0; i < trainings.getJSONArray("training").length(); i++)
                        if (training.targetId.equals(trainings.getJSONArray("training").getJSONObject(i).getString("target_id"))
                                && training.target.equals(trainings.getJSONArray("training").getJSONObject(i).getString("target")))
                            trainings.getJSONArray("training").remove(i);
                    trainings.getJSONArray("training").put((Object) training.toJSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SAVED_TRAININGS, trainings.toString());
            editor.commit();
        }
    }

    /**
     * Aggiorna in blocco il JSON locale dei Trainigs
     */
    public static class UpdateTrainings extends CallbackActions{

        public UpdateTrainings(AppCompatActivity activity){
            super(activity);
        }

        public void updateTrainings(ArrayList<Training> arrayList){
            SharedPreferences sharedPref = activity.getSharedPreferences("trainings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SAVED_TRAININGS, Training.trainingJsonFromArray(arrayList).toString());
            editor.commit();
        }
    }






}
