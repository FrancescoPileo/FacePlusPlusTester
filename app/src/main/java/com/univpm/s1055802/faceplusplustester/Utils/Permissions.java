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

package com.univpm.s1055802.faceplusplustester.Utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

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
