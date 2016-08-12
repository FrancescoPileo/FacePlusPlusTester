package com.univpm.s1055802.faceplusplustester.Utils.Adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.univpm.s1055802.faceplusplustester.Group.Group;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import java.util.ArrayList;

/**
 * Created by kekko on 03/05/16.
 */

public class CustomEditList extends ArrayAdapter<Info>{

    private final Activity context;
    private ArrayList<Info> infoList;
    private Info info = null;
    private View.OnClickListener listener = null;
    ViewHolder holder;

    public CustomEditList(Activity context, int textViewResourceId,
                          ArrayList<Info> infoList, View.OnClickListener listener) {
        super(context, textViewResourceId, infoList);
        this.context = context;
        this.infoList = infoList;
        this.listener = listener;

    }

    private class ViewHolder {

        CheckBox check;
        TextView name;
        TextView tag;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_check_text_text, null);

            holder = new ViewHolder();
            holder.check = (CheckBox) convertView.findViewById(R.id.check);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.tag = (TextView) convertView.findViewById(R.id.tag);
            convertView.setTag(holder);


        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        info = infoList.get(position);
        holder.check.setTag(info);
        holder.name.setText(info.getName());
        holder.tag.setText(info.getTag());
        holder.check.setOnClickListener(listener);
        holder.check.setChecked(info.isChecked());

        return convertView;

    }
}
