package com.univpm.s1055802.faceplusplustester.Face;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.univpm.s1055802.faceplusplustester.Faceset.FacesetActions;
import com.univpm.s1055802.faceplusplustester.FppTester;
import com.univpm.s1055802.faceplusplustester.Utils.CallbackActions;

import org.json.JSONObject;

/**
 * Created by kekko on 19/05/16.
 */

/**
 * Classe che contiene tutte le azioni sulle Face
 */
public class FaceAction {

    public static class GetFaceInfo extends CallbackActions {

        public GetFaceInfo(AppCompatActivity activity) {
            super(activity);
        }

        /**
         * Ottiene le informazioni sulla Face specificata
         * @param faceId Id della Face
         */
        public void getFaceInfo(final String faceId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setFaceId(faceId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).infoGetFace(postParameters);
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
