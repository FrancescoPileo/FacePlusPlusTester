package com.univpm.s1055802.faceplusplustester.Utils.Adapters;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Directories;

import java.io.File;

/**
 * Created by kekko on 03/05/16.
 */

public class CustomFaceList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] web;
    private final String[] imgId;

    public CustomFaceList(Activity context,
                          String[] web, String[] imgId) {
        super(context, R.layout.list_face_text, web);
        this.context = context;
        this.web = web;
        this.imgId = imgId;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_face_text, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        txtTitle.setText(web[position]);

        File imgFile = new File(Directories.FACES + File.separator + imgId[position] + ".png" );
        if (imgFile.exists()){
            Bitmap myBit = BitmapFactory.decodeFile(Directories.FACES + File.separator + imgId[position] + ".png");
            imageView.setImageBitmap(myBit);
        } else {
            imageView.setImageResource(R.drawable.fpp_no_face);
        }

        return rowView;
    }

    public String getImgId(int index){
        return imgId[index];
    }
}