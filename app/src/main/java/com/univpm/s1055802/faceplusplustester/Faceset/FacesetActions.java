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

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.univpm.s1055802.faceplusplustester.FppTester;
import com.univpm.s1055802.faceplusplustester.Utils.CallbackActions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FacesetActions {


    public static String FACESET_ALL = "All";

    public static class GetFacesetsList extends CallbackActions {

        public GetFacesetsList(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Ottiene il JSON conentente la lista dei Facesets
         */
        public void getFacesetsList() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).infoGetFacesetList();
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

    public static class SendCreate extends CallbackActions{

        public SendCreate(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette la creazione di un nuovo Faceset
         * @param facesetName nome del Faceset
         * @param facesetTag tag del Faceset
         */
        public void sendCreation(final String facesetName, final String facesetTag){
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setFacesetName(facesetName);
                        postParameters.setTag(facesetTag);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).facesetCreate(postParameters);
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e ) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static class GetFacesetInfo extends CallbackActions {

        public GetFacesetInfo(AppCompatActivity activity) {
            super(activity);
        }

        /**
         * Ottiene le informazioni del Faceset specificato
         * @param facesetId Id del Faceset
         */
        public void getFacesetInfo(final String facesetId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setFacesetId(facesetId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).facesetGetInfo(postParameters);
                        if (callback != null) {
                            callback.processResult(result);
                        }

                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        if (e.getErrorCode() == 1005){

                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                    }
                }
            }).start();
        }
    }

    public static class EditFaceset extends CallbackActions{

        public EditFaceset(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette la modifica di un Faceset esistente
         * @param facesetId Id del Faceset da modificare
         * @param newName nuovo nome del Faceset
         * @param newTag nuovo tag del Faceset
         */
        public void editFaceset(final String facesetId, final String newName, final String newTag) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters parameters = new PostParameters();
                        parameters.setFacesetId(facesetId);
                        parameters.setName(newName);
                        parameters.setTag(newTag);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).facesetSetInfo(parameters);
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

    public static class DeleteFacesets extends CallbackActions{

        public DeleteFacesets(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette l'eliminzione di più Faceset
         * @param deleteList lista dei Faceset da eliminare
         */
        public void deleteFacesets(final JSONObject deleteList) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ArrayList<String> deleteArray = new ArrayList<String>();
                        if (deleteList.getJSONArray("faceset_delete").length()>0) {
                            for (int i = 0; i < deleteList.getJSONArray("faceset_delete").length(); i++) {
                                deleteArray.add(deleteList.getJSONArray("faceset_delete").getString(i));
                            }
                            PostParameters params = new PostParameters();
                            params.setFacesetName(deleteArray);
                            FppTester fppTester = (FppTester)activity.getApplication();
                            JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).facesetDelete(params);
                            if (callback != null){
                                callback.processResult(result);
                            }
                        }
                    } catch (FaceppParseException | JSONException e ) {
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

    public static class DeleteFaceset extends CallbackActions{

        public DeleteFaceset(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette l'eliminazione di un Faceset
         * @param facesetId Id del Faceset da eliminare
         */
        public void deleteFaceset(final String facesetId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setFacesetId(facesetId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).facesetDelete(postParameters);
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


    public static class RemoveFace extends CallbackActions{

        public RemoveFace(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Rimuove una Face assegnata ad un Faceset
         * @param facesetId Id del Faceset
         * @param faceId Id della Face
         */
        public void removeFace(final String facesetId, final String faceId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setFacesetId(facesetId);
                        postParameters.setFaceId(faceId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).facesetRemoveFace(postParameters);
                        if (callback != null){
                            result.put("face_id", faceId);
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException | JSONException e) {
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

    public static class AddFace extends CallbackActions{

        public AddFace(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Assegna una Face ad un Faceset
         * @param facesetId Id del Faceset
         * @param faceId Id della Face
         */
        public void addFace(final String facesetId, final String faceId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setFacesetId(facesetId);
                        postParameters.setFaceId(faceId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).facesetAddFace(postParameters);
                        if (callback != null){
                            result.put("face_id", faceId);
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException | JSONException e) {
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
}

