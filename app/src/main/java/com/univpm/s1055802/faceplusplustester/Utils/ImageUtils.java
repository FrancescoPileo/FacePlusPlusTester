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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.util.Log;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    /**
     * Raddrizza l'immagine
     * @param imgPath percorso dell'immagine da analizzare
     * @param bitmap bitmap dell'immagine da analizzare
     * @return bitmat rotato dell'immagine
     */
    public static Bitmap rectifyRotation(String imgPath, Bitmap bitmap){
        try {
            ExifInterface ei = new ExifInterface(imgPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        }catch(IOException ex){
            Log.v("Alert", "No bitmap to rotate");
        }
        return bitmap;
    }

    /**
     * Ruota un'immagine
     * @param source bitmap dell'immagine originale
     * @param angle angolo di rotazione
     * @return bitmap dell'immagine rotata
     */
    private static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    /**
     * Ridimensiona un'immagine, evitando di superare la dimensione massima delle texture del dispositivo
     * @param source bitmap dell'immagine originale
     * @return bitmap dell'immagine ridimensionata
     */
    public static Bitmap rectifySize(Bitmap source){

        if (source != null) {

            int maxTextureSize = getMaxTextureSize();

            int height = source.getHeight();
            int width = source.getWidth();
            int newHeight = source.getHeight();
            int newWidth = source.getWidth();
            boolean toScale = false;

            if (height > width) {
                if (height > maxTextureSize) {
                    toScale = true;
                    newHeight = maxTextureSize;
                    newWidth = (int) ((double) maxTextureSize / height * width);
                }
            } else {
                if (width > maxTextureSize) {
                    toScale = true;
                    newWidth = maxTextureSize;
                    newHeight = (int) ((double) maxTextureSize / width * height);
                }
            }
            //Log.v("newWidth:", String.valueOf(newWidth));
            //Log.v("newHeight:", String.valueOf(newHeight));
            if (toScale)
                return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
            else
                return source;
        } else {
            return null;
        }
    }

    /**
     * Ridimensiona un'immagine ad una grandezza di anteprima
     * @param source bitmap dell'immagine originale
     * @return bitmap dell'immagine ridimensionata
     */
    public static Bitmap getThumbnail(Bitmap source, Context context){

        int thumbHeight = dpToPx(context, 100);
        int thumbWidth = dpToPx(context, 100);

        if (source != null) {

            int height = source.getHeight();
            int width = source.getWidth();
            int newHeight = source.getHeight();
            int newWidth = source.getWidth();
            boolean toScale = false;

            if (height > width) {
                if (height > thumbHeight) {
                    toScale = true;
                    newHeight = thumbHeight;
                    newWidth = (int) ((double) thumbHeight / height * width);
                }
            } else {
                if (width > thumbWidth) {
                    toScale = true;
                    newWidth = thumbWidth;
                    newHeight = (int) ((double) thumbWidth / width * height);
                }
            }
            //Log.v("newWidth:", String.valueOf(newWidth));
            //Log.v("newHeight:", String.valueOf(newHeight));
            if (toScale)
                return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
            else
                return source;
        } else {
            return null;
        }
    }

    /**
     * Restituisce la grandezza massima di una texture del dispositivo in uso
     * @return la grandezza max della texture
     */
    private static int getMaxTextureSize(){

        EGLDisplay dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        EGL14.eglInitialize(dpy, vers, 0, vers, 1);

        int[] configAttr = {
                EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
                EGL14.EGL_LEVEL, 0,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        EGL14.eglChooseConfig(dpy, configAttr, 0,
                configs, 0, 1, numConfig, 0);
        if (numConfig[0] == 0) {
            // TROUBLE! No config found.
        }
        EGLConfig config = configs[0];

        int[] surfAttr = {
                EGL14.EGL_WIDTH, 64,
                EGL14.EGL_HEIGHT, 64,
                EGL14.EGL_NONE
        };
        EGLSurface surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);

        int[] ctxAttrib = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        EGLContext ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);

        EGL14.eglMakeCurrent(dpy, surf, surf, ctx);

        int[] maxTextureSize = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);

        EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT);
        EGL14.eglDestroySurface(dpy, surf);
        EGL14.eglDestroyContext(dpy, ctx);
        EGL14.eglTerminate(dpy);

        return maxTextureSize[0];
    }

    /**
     * salva un jpeg della face passata come armgomento
     * @param faceBmp bitmap dell' immagine da salvare
     * @param imgName nome del file da salvare
     */
    public static void saveScaledFaceBitmap(Bitmap faceBmp, String imgName){

        final int FACE_BMP_MAX_SIZE = 64;
        FileOutputStream out = null;
        try {
            String filePath =  Directories.FACES + File.separator + imgName + ".png";
            out = new FileOutputStream(filePath);
            int bmpHeight = faceBmp.getHeight();
            int bmpWidth = faceBmp.getWidth();
            int newHeight = 0, newWidth = 0;
            boolean toScale = false;
            if (bmpHeight > bmpWidth){
                if (bmpHeight > FACE_BMP_MAX_SIZE){
                    toScale = true;
                    newHeight = FACE_BMP_MAX_SIZE;
                    newWidth = (int)((double) newHeight / bmpHeight * bmpWidth);
                }
            } else {
                if (bmpWidth > FACE_BMP_MAX_SIZE){
                    toScale = true;
                    newWidth = FACE_BMP_MAX_SIZE;
                    newHeight = (int)((double) newWidth / bmpWidth * bmpHeight);
                }
            }
            if (toScale) {
                Bitmap scaledFaceBmp = Bitmap.createScaledBitmap(faceBmp, newWidth, newHeight, true);
                scaledFaceBmp.compress(Bitmap.CompressFormat.PNG, 80, out); // bmp is your Bitmap instance
            } else
            {
                faceBmp.compress(Bitmap.CompressFormat.PNG, 80, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();
            Log.v("size", String.valueOf(numberOfItems));

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
