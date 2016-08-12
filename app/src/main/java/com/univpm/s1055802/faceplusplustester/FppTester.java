package com.univpm.s1055802.faceplusplustester;

import android.app.Application;

/**
 * Created by kekko on 03/08/16.
 */

/**
 * Classe che identifica l'intera applicazione, e permette di definire variabili globali
 */
public class FppTester extends Application {

    private String apiKey;
    private String apiSecret;
    private boolean isCN;
    private boolean isDebug;

    public FppTester(){

    }

    public FppTester(String apiKey, String apiSecret, boolean isCN, boolean isDebug){
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.isCN = isCN;
        this.isDebug = isDebug;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public boolean isCN() {
        return isCN;
    }

    public void setCN(boolean CN) {
        this.isCN = CN;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
}
