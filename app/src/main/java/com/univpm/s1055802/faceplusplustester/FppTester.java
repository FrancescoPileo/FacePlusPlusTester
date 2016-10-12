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
