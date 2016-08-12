package com.univpm.s1055802.faceplusplustester.Person;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.univpm.s1055802.faceplusplustester.FppTester;
import com.univpm.s1055802.faceplusplustester.Group.GroupActions;
import com.univpm.s1055802.faceplusplustester.Utils.CallbackActions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 17/05/16.
 */
public class PersonActions {

    public static class GetPersonsList extends CallbackActions{

        public GetPersonsList(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Ottiene la lista delle persone
         */
        public void getPersonsList() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).infoGetPersonList();
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
         * Permette la creazione di una nuova persona
         * @param personName Nome della persona
         * @param personTag Tag della persona
         * @param selectedGroups Groups ai quali assegnare la persona creata
         */
        public void sendCreation(final String personName, final String personTag, final ArrayList<String> selectedGroups){
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setPersonName(personName);
                        postParameters.setTag(personTag);
                        if (selectedGroups.size() != 0) {
                            postParameters.setGroupId(selectedGroups);
                        }
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).personCreate(postParameters);
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

    public static class GetPersonInfo extends CallbackActions {

        public GetPersonInfo(AppCompatActivity activity) {
            super(activity);
        }

        /**
         * Ottiene le informazioni riguardanti una persona
         * @param personId Id della persona
         */
        public void getPersonInfo(final String personId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setPersonId(personId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).personGetInfo(postParameters);
                        if (callback != null) {
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

    public static class EditPerson extends CallbackActions{

        public EditPerson(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette la modifica di una persona esistente
         * @param personId Id della persona
         * @param newName Nuovo nome della persona
         * @param newTag Nuovo tag della persona
         */
        public void editPerson(final String personId, final String newName, final String newTag) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters parameters = new PostParameters();
                        parameters.setPersonId(personId);
                        parameters.setName(newName);
                        parameters.setTag(newTag);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).personSetInfo(parameters);
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

    public static class DeletePersons extends CallbackActions{

        public DeletePersons(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette l'eliminazione di più persone
         * @param deleteList lista delle persone da eliminare
         */
        public void deletePersons(final JSONObject deleteList) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ArrayList<String> deleteArray = new ArrayList<String>();
                        if (deleteList.getJSONArray("person_delete").length()>0) {
                            for (int i = 0; i < deleteList.getJSONArray("person_delete").length(); i++) {
                                deleteArray.add(deleteList.getJSONArray("person_delete").getString(i));
                            }
                            PostParameters params = new PostParameters();
                            params.setPersonName(deleteArray);
                            FppTester fppTester = (FppTester)activity.getApplication();
                            JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).personDelete(params);
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

    public static class DeletePerson extends CallbackActions{

        public DeletePerson(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette l'eliminazione di una persona
         * @param personId Id della persona da eliminare
         */
        public void deletePerson(final String personId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setPersonId(personId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).personDelete(postParameters);
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
         * Rimuove una Face assegnata ad una persona
         * @param personId Id della persona
         * @param faceId Id della Face
         */
        public void removeFace(final String personId, final String faceId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setPersonId(personId);
                        postParameters.setFaceId(faceId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).personRemoveFace(postParameters);
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
         * Assegna una Face ad una persona
         * @param personId Id della persona
         * @param faceId Id della Face
         */
        public void addFace(final String personId, final String faceId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setPersonId(personId);
                        postParameters.setFaceId(faceId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).personAddFace(postParameters);
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
