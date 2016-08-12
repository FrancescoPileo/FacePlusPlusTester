package com.univpm.s1055802.faceplusplustester.Utils.Adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Training.Training;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kekko on 03/05/16.
 */

public class CustomTrainingList extends ArrayAdapter<Training>{

    private final Activity context;
    private ArrayList<Training> trainingList;

    public CustomTrainingList(Activity context, int textViewResourceId,
                              ArrayList<Training> trainingList) {
        super(context, textViewResourceId, trainingList);
        this.context = context;
        this.trainingList = trainingList;

    }

    public class ViewHolder {

        private TextView status;
        private TextView type;
        private TextView targetName;
        private Training training;

        public TextView getStatus() {
            return status;
        }

        public void setStatus(TextView status) {
            this.status = status;
        }

        public TextView getType() {
            return type;
        }

        public void setType(TextView type) {
            this.type = type;
        }

        public TextView getTargetName() {
            return targetName;
        }

        public void setTargetName(TextView targetName) {
            this.targetName = targetName;
        }

        public Training getTraining() {
            return training;
        }

        public void setTraining(Training training) {
            this.training = training;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_text_2text, null);

            holder = new ViewHolder();
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.targetName = (TextView) convertView.findViewById(R.id.targetName);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (trainingList != null) {
            Training training = trainingList.get(position);
            holder.status.setText(training.getStatus());
            String trainingType = null;
            switch (training.getTarget()) {
                case Training.Target.PERSON:
                    trainingType = "Verify";
                    break;
                case Training.Target.GROUP:
                    trainingType = "Identify";
                    break;
                case Training.Target.FACESET:
                    trainingType = "Search";
                    break;
            }

            //cambio formato della data
            final String OLD_FORMAT = "yyyyMMdd_HHmmss";
            final String NEW_FORMAT = "dd/MM/yyyy HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = null;
            try {
                d = sdf.parse(trainingList.get(position).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sdf.applyPattern(NEW_FORMAT);
            String newDateString = sdf.format(d);

            holder.type.setText(newDateString + "  " + trainingType);
            holder.targetName.setText(training.getTarget() + ": " + training.getTargetName());
            holder.setTraining(training);
        }
        return convertView;

    }

    public void removeTrainingBySessionId(String sessionId){
        for (int i = 0; i < trainingList.size(); i++){
            if (sessionId.equals(trainingList.get(i).getSessionId())){
                trainingList.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    public void refreshData(ArrayList<Training> trainingList){
        this.trainingList = trainingList;
        notifyDataSetChanged();
    }
}
