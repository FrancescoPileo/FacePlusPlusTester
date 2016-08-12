package com.univpm.s1055802.faceplusplustester.Utils.Adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.univpm.s1055802.faceplusplustester.Person.Person;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import java.util.ArrayList;

/**
 * Created by kekko on 03/05/16.
 */

public class CustomCheckList extends ArrayAdapter<Info>{

    private final Activity context;
    private ArrayList<Info> infoList;

    private boolean checkAll = false; //true: tutte le check sono selezionate false: tutte le check sono deselezionate
    private boolean enableAll = true; //true: tutte le check sono abilitate false: tutte le check sono disabilitate

    public CustomCheckList(Activity context, int textViewResourceId,
                           ArrayList<Info> infoList) {
        super(context, textViewResourceId, infoList);
        this.context = context;
        this.infoList = infoList;

    }

    public class ViewHolder {

        private TextView name;
        private TextView tag;
        private CheckBox check;

        public TextView getName() {
            return name;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public TextView getTag() {
            return tag;
        }

        public void setTag(TextView tag) {
            this.tag = tag;
        }

        public CheckBox getCheck() {
            return check;
        }

        public void setCheck(CheckBox check) {
            this.check = check;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_check_text_text, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.tag = (TextView) convertView.findViewById(R.id.tag);
            holder.check = (CheckBox) convertView.findViewById(R.id.check);
            convertView.setTag(holder);

            holder.check.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Info person = (Info) cb.getTag();
                    person.setChecked(cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Info info = infoList.get(position);
        holder.tag.setText( info.getTag());
        holder.name.setText(info.getName());
        holder.check.setChecked(checkAll);
        holder.check.setEnabled(enableAll);
        holder.check.setTag(info);



        return convertView;

    }

    public boolean isCheckAll() {
        return checkAll;
    }

    public void setCheckAll(boolean checkAll) {
        this.checkAll = checkAll;
    }

    public boolean isEnableAll() {
        return enableAll;
    }

    public void setEnableAll(boolean enableAll) {
        this.enableAll = enableAll;
    }
}
