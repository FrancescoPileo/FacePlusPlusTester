package com.univpm.s1055802.faceplusplustester.Session;

import com.univpm.s1055802.faceplusplustester.Training.Training;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kekko on 14/07/16.
 */

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
