package com.univpm.s1055802.faceplusplustester.Utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by kekko on 12/10/16.
 */

/**
 *  Nelle versioni di android 6.0+ controlla i permessi per accedere alla fotocamera, memoria esterna, etc.
 */
public class Permissions {

    public static void checkPermissions(Activity activity, String[] permissions, final int requestCode, Runnable action){
        boolean hasPermission = true;
        for (int i = 0; i < permissions.length && hasPermission; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
            }
        }
        if (!hasPermission){
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            action.run();
        }
    }
}
