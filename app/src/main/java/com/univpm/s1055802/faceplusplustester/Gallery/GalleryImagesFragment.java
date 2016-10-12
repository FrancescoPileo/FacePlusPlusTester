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

package com.univpm.s1055802.faceplusplustester.Gallery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.univpm.s1055802.faceplusplustester.Detect.DetectImage;
import com.univpm.s1055802.faceplusplustester.Person.PersonActions;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.ImageAdapter;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;
import com.univpm.s1055802.faceplusplustester.Utils.FileUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by kekko on 26/07/16.
 */
public class GalleryImagesFragment extends Fragment implements GalleryFragment {


    static final String AQUIRED_PHOTO = "Photo";
    static final int REQUEST_DETECT = 0;
    GridView imagegrid = null;
    String dir = null;
    private ImageAdapter imageAdapter;
    File[] listFile;
    ArrayList<String> filePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dir = getActivity().getIntent().getStringExtra("dir");
        Log.v("dir", dir);
        getFromDir(dir);

        imageAdapter = new ImageAdapter(getActivity(), filePath, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), DetectImage.class);
                intent.putExtra(AQUIRED_PHOTO, FileUtils.getPath(getActivity().getApplicationContext(), Uri.parse((String) v.getTag(R.string.uri))));
                startActivityForResult(intent, REQUEST_DETECT);
            }
        }, ImageAdapter.MediaType.photo);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.gallery_fragment, container, false);

        imagegrid = (GridView) rootView.findViewById(R.id.PhoneImageGrid);
        imagegrid.setAdapter(imageAdapter);


        return rootView;
    }

    /**
     * Ottiene la lista di tutti gli Uri delle immagini contenute in una cartella
     * @param dir la cartella specificata
     */
    public void getFromDir(String dir)
    {
        File file= new File(dir);

        if (file.isDirectory())
        {
            listFile = file.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    File file = new File(dir.getPath(), name);
                    return (name.toLowerCase().endsWith(".jpg") && file.length() > 10);
                }
            });

            filePath = new ArrayList<String>();
            for (int i = 0; i < listFile.length; i++)
            {
                filePath.add(i, Uri.fromFile(listFile[i]).toString());
            }
        }
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     *  abilita tutte le CheckBox per permettere l'eliminazione di più immagini
     */
    public void enableCheckbox(boolean flag){
        if (imageAdapter!=null) {
            imageAdapter.setEnableAll(flag);
            if (flag){
                imageAdapter.setVisibilityAll(View.VISIBLE);
            } else {
                imageAdapter.setVisibilityAll(View.INVISIBLE);
                imageAdapter.resetChecked();
            }
            imageAdapter.notifyDataSetChanged();
        }

    }

    /**
     *  seleziona tutte le checkbox
     */
    public void selectAll(boolean flag){
        if (imageAdapter!=null) {
            imageAdapter.checkAll(flag);
            imageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Elimina le immagini selezionate, chiedendo conferma con una finestra di dialogo
     */
    public void deleteChecked() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm")
                .setMessage("Confirm delete?")
                .setIcon(R.drawable.fpp_delete_black)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        ArrayList<String> checked = imageAdapter.getCheckedItems();
                        for (String uri : checked) {
                            File file = new File(FileUtils.getPath(getContext(), Uri.parse(uri)));
                            file.delete();
                        }
                        imageAdapter.deleteCheckedItems();
                        enableCheckbox(false);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enableCheckbox(false);
                    }
                }).show();
    }
}
