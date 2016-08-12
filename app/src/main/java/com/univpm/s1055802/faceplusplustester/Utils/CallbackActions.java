package com.univpm.s1055802.faceplusplustester.Utils;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kekko on 16/05/16.
 */
public class CallbackActions {

    protected Callback callback = null;
    protected Context context = null;
    protected AppCompatActivity activity = null;

    public CallbackActions() {
    }

    public CallbackActions(AppCompatActivity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }
}
