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

package com.univpm.s1055802.faceplusplustester.Utils.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.univpm.s1055802.faceplusplustester.Face.Face;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Directories;

import java.io.File;
import java.util.ArrayList;

public class CustomEditFaceList extends ArrayAdapter<Face>{

    private final Activity context;
    private ArrayList<Face> faceList;
    private Face face = null;
    private View.OnClickListener listener = null;
    ViewHolder holder;

    public CustomEditFaceList(Activity context, int textViewResourceId,
                              ArrayList<Face> faceList, View.OnClickListener listener) {
        super(context, textViewResourceId, faceList);
        this.context = context;
        this.faceList = faceList;
        this.listener = listener;

    }

    private class ViewHolder {

        ImageView img;
        TextView id;
        TextView del;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_face_text_del, null);

            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.id = (TextView) convertView.findViewById(R.id.faceId);
            holder.del = (TextView) convertView.findViewById(R.id.del);
            convertView.setTag(holder);


        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        face = faceList.get(position);

        File imgFile = new File(Directories.FACES + File.separator + face.getFaceId() + ".png" );
        if (imgFile.exists()){
            Bitmap myBit = BitmapFactory.decodeFile(Directories.FACES + File.separator + face.getFaceId() + ".png");
            holder.img.setImageBitmap(myBit);
        } else {
            holder.img.setImageResource(R.drawable.fpp_no_face);
        }
        holder.id.setText(face.getFaceId());
        holder.del.setTag(face);
        holder.del.setOnClickListener(listener);

        return convertView;

    }

    public void removeFaceById(String faceId){
        for (int i = 0; i < faceList.size(); i++){
            if (faceId.equals(faceList.get(i).getFaceId())){
                faceList.remove(i);
            }
        }
        notifyDataSetChanged();
    }
}
