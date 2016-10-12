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
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.univpm.s1055802.faceplusplustester.R;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    public enum MediaType {photo, video};

    private Activity activity;
    private ArrayList<String> files;
    private LayoutInflater mInflater;
    DisplayImageOptions options;
    private MediaType mediaType;

    private boolean enableAll = false;
    private int visibilityAll = View.INVISIBLE;
    private View.OnClickListener onClickListener = null;

    private SparseBooleanArray checked;

    class ViewHolder {
        ImageView imageview;
        ProgressBar pb;
        ImageView overlay;
        CheckBox delete;
    }

    public ImageAdapter(Activity activity, ArrayList<String> files, View.OnClickListener onClickListener, MediaType mediaType) {
        this.activity = activity;
        this.files = files;
        this.onClickListener = onClickListener;
        this.mediaType = mediaType;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        checked = new SparseBooleanArray();


        options = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    public int getCount() {
        return files.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.gallery_item, null);

            holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
            holder.pb = (ProgressBar) convertView.findViewById(R.id.pb);
            holder.overlay = (ImageView) convertView.findViewById(R.id.overlay);
            holder.delete = (CheckBox) convertView.findViewById(R.id.check);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageview.setTag(R.string.uri, files.get(position));
        holder.imageview.setTag(R.string.position, position);
        holder.imageview.setOnClickListener(onClickListener);


        if (mediaType == MediaType.photo) {
            holder.overlay.setImageResource(R.drawable.fpp_camera);
        } else if (mediaType == MediaType.video){
            holder.overlay.setImageResource(R.drawable.fpp_video);
        }


        holder.delete.setEnabled(enableAll);
        holder.delete.setVisibility(visibilityAll);

        holder.delete.setTag(position);
        holder.delete.setChecked(checked.get(position));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked.put(position, ((CheckBox) v).isChecked());
            }
        });
            /*
            holder.delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checked.put(position, isChecked);
                    Log.v("list", checked.toString());
                }
            });*/

        display(holder.imageview, files.get(position), holder.pb);

        return convertView;
    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i = 0;i < files.size(); i++) {
            if(checked.get(i)) {
                mTempArry.add(files.get(i));
            }
        }

        return mTempArry;
    }

    public void deleteCheckedItems(){
        for (int i=0; i<files.size(); i++) {
            if (checked.get(i)){
                files.set(i, null);
            }
        }
        for (int i = 0; i < files.size(); i++){
            if (files.get(i) == null){
                files.remove(i);
                i--;
            }
        }
        this.notifyDataSetChanged();
    }


    public void resetChecked(){
        checked.clear();
    }

    public void display(ImageView img, String url, final ProgressBar spinner)
    {
        ImageLoader.getInstance().displayImage(url, img, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                spinner.setVisibility(View.VISIBLE);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                spinner.setVisibility(View.GONE);


            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                spinner.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }

        });
    }

    public void checkAll(boolean flag){
        for (int i=0; i<getCount(); i++){
            checked.put(i, flag);
        }
    }

    public boolean isEnableAll() {
        return enableAll;
    }

    public void setEnableAll(boolean enableAll) {
        this.enableAll = enableAll;
    }

    public void setVisibilityAll(int visibilityAll) {this.visibilityAll = visibilityAll; }

}