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

import android.os.Environment;

import java.io.File;

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
