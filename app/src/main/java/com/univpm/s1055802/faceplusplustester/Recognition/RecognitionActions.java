package com.univpm.s1055802.faceplusplustester.Recognition;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.univpm.s1055802.faceplusplustester.FppTester;
import com.univpm.s1055802.faceplusplustester.Utils.CallbackActions;

import org.json.JSONObject;

/**
 * Created by kekko on 14/07/16.
 */
public class RecognitionActions {

    public static class Verify extends CallbackActions {

        public Verify(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Verifica che la faccia selezionata appartenga ad una persona specificata
         * @param faceId Id della faccia
         * @param personId Id della persona
         */
        public void verify(final String faceId, final String personId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setPersonId(personId);
                        postParameters.setFaceId(faceId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).recognitionVerify(postParameters);
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

    public static class Identify extends CallbackActions {

        public Identify(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Restituisce una lista di persone che appartengono al Group selezionato, che hanno maggior somiglianza con la
         * faccia specificata
         *
         * @param faceId Id della faccia
         * @param groupId Id del Group
         */
        public void identify(final String faceId, final String groupId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setGroupId(groupId);
                        postParameters.setKeyFaceId(faceId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).recognitionIdentify(postParameters);
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

    public static class Search extends CallbackActions {

        public Search(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Restituisce una lista di face, appartenenti al Faceset selezionato, che più assomigliano alla faccia specificata
         *
         * @param faceId Id della faccia
         * @param facesetId Id del Faceset
         */
        public void search(final String faceId, final String facesetId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setFacesetId(facesetId);
                        postParameters.setKeyFaceId(faceId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).recognitionSearch(postParameters);
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

}
