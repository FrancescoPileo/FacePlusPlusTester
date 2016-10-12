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

package com.univpm.s1055802.faceplusplustester.Session;

import com.univpm.s1055802.faceplusplustester.Training.Training;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe che contiene tutte le informazioni della Session
 */
public class Session {

    private String sessionId = null;
    private int createTime;
    private int finishTime;
    private Object result;
    private String status;

    public Session(String sessionId, int createTime, int finishTime, Object result, String status) {
        this.sessionId = sessionId;
        this.createTime = createTime;
        this.finishTime = finishTime;
        this.result = result;
        this.status = status;
    }

    public Session(JSONObject session) throws JSONException {
        this.setSessionId(session.getString("session_id"));
        this.setCreateTime(session.getInt("create_time"));
        this.setFinishTime(session.getInt("finish_time"));
        this.setResult(session.get("result"));
        this.setStatus(session.getString("status"));
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
