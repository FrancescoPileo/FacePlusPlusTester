package com.univpm.s1055802.faceplusplustester.Utils.Adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import java.util.ArrayList;

/**
 * Created by kekko on 03/05/16.
 */

public class CustomList extends ArrayAdapter<Info>{

    private final Activity context;
    private ArrayList<Info> infoList;

    public CustomList(Activity context, int textViewResourceId,
                      ArrayList<Info> infoList) {
        super(context, textViewResourceId, infoList);
        this.context = context;
        this.infoList = infoList;

    }

    private class ViewHolder {

        TextView name;
        TextView tag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_text_text, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.tag = (TextView) convertView.findViewById(R.id.tag);

            convertView.setTag(holder);


        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Info info = infoList.get(position);
        holder.tag.setText(info.getTag());
        holder.name.setText(info.getName());

        return convertView;

    }
}
