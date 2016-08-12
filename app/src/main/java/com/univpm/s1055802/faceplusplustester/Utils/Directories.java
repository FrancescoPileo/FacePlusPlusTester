package com.univpm.s1055802.faceplusplustester.Utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by kekko on 28/04/16.
 */

/**
 * Classe che definisce le diverse directories dell'applicazione
 */
public class Directories {

    public final static String MAIN = Environment.getExternalStorageDirectory() + File.separator + "FppTester";

    public final static String NOMEDIA  = MAIN + File.separator + ".nomedia";

    public final static String TEMP = MAIN + File.separator + ".temp";

    public final static String IMAGES = MAIN + File.separator + "images";

    public final static String TESTS = MAIN + File.separator + "tests";

    public final static String THUMB = IMAGES + File.separator + ".thumbnails";

    public final static String FACES = IMAGES + File.separator + ".faces";

    public final static String VIDEOS = MAIN + File.separator + "videos";

}
