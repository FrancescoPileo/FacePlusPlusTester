package com.univpm.s1055802.faceplusplustester.Gallery;

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

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Adapters.ImageAdapter;
import com.univpm.s1055802.faceplusplustester.Utils.Directories;
import com.univpm.s1055802.faceplusplustester.Utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by kekko on 26/07/16.
 */
public class GalleryVideosFragment extends Fragment implements GalleryFragment {


    static final String AQUIRED_PHOTO = "Photo";
    static final int REQUEST_DETECT = 0;
    GridView imagegrid = null;
    String dir = null;
    private ImageAdapter imageAdapter;
    ArrayList<File> videoFiles;
    File framesDir;
    ArrayList<String> filePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dir = Directories.VIDEOS;
        getFromDir(dir);
        imageAdapter = new ImageAdapter(getActivity(), filePath, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiseDialog((int) v.getTag(R.string.position), (String)v.getTag(R.string.uri));
            }
        }, ImageAdapter.MediaType.video);
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
     * Ottiene la lista di tutti gli Uri dei video contenuti in una cartella
     * @param dir la cartella specificata
     */
    public void getFromDir(String dir)
    {
        File file= new File(dir);
        videoFiles = new ArrayList<>();

        if (file.isDirectory())
        {
            File[] listDir = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });


            filePath = new ArrayList<>();
            for (int i = 0; i < listDir.length; i++)
            {

                videoFiles.add(listDir[i].listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        File file = new File(dir.getPath(), name);
                        return (name.toLowerCase().endsWith(".mp4") && file.length() > 10);
                    }
                })[0]);
                framesDir = new File(listDir[i],"Frames");
                if (framesDir.isDirectory()){
                    File[] listFrames = framesDir.listFiles();
                    filePath.add(i, Uri.fromFile(listFrames[0]).toString());
                }
            }
        }
    }

    /**
     * Elimina i video selezionate e tutti i file ad essi associati, chiedendo conferma con una finestra di dialogo
     */
    public void deleteChecked(){
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm")
                .setMessage("Confirm delete?")
                .setIcon(R.drawable.fpp_delete_black)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        ArrayList<String> checked = imageAdapter.getCheckedItems();
                        for (String uri : checked) {
                            File framesDir = new File(FileUtils.getPath(getContext(), Uri.parse(uri)));
                            File dir = new File(framesDir.getParent());
                            FileUtils.deleteRecursive(dir);
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

    /**
     *  abilita tutte le CheckBox per permettere l'eliminazione di più video
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

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * visualizza una finestra di dialogo che permette di scegliere se riprodurre il video o selezionare uno
     * dei suoi frame
     *
     * @param position indice del video
     * @param uri Uri del video
     */
    private void choiseDialog(final int position,final String uri){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Video")
                .setItems(R.array.video_choise, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which){
                            case 0:
                                intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(videoFiles.get(position)), "video/*");
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(getActivity().getApplicationContext(), GalleryMain.class);
                                File frame = new File(FileUtils.getPath(getActivity().getApplicationContext(), Uri.parse(uri)));
                                String dir = frame.getParent();
                                intent.putExtra("dir", dir);
                                intent.putExtra("video", false);
                                startActivityForResult(intent, REQUEST_DETECT);
                                break;
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
