package com.univpm.s1055802.faceplusplustester.Session;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.univpm.s1055802.faceplusplustester.FppTester;
import com.univpm.s1055802.faceplusplustester.Utils.CallbackActions;

import org.json.JSONObject;

/**
 * Created by kekko on 16/06/16.
 */
public class SessionActions {


    public static class GetSession extends CallbackActions {

        public GetSession(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Acquisisce i risultati di una Session
         * @param sessionId Id della Session
         */
        public void getSession(final String sessionId){
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            PostParameters postParameters = new PostParameters();
                            postParameters.setSessionId(sessionId);
                            FppTester fppTester = (FppTester)activity.getApplication();
                            JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).infoGetSession(postParameters);
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
}
