package com.univpm.s1055802.faceplusplustester.Utils.Adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Recognition.Recognition;

import java.util.ArrayList;

/**
 * Created by kekko on 03/05/16.
 */

public class CustomIdentifyCandidateList extends ArrayAdapter<Recognition.IdentifyResults.Candidate>{

    private final Activity context;
    private ArrayList<Recognition.IdentifyResults.Candidate> candidatesList;

    public CustomIdentifyCandidateList(Activity context, int textViewResourceId,
                                       ArrayList<Recognition.IdentifyResults.Candidate> candidatesList) {
        super(context, textViewResourceId, candidatesList);
        this.context = context;
        this.candidatesList = candidatesList;

    }

    public class ViewHolder {

        private TextView confidence;
        private TextView personName;
        private Recognition.IdentifyResults.Candidate tag;

        public Recognition.IdentifyResults.Candidate getTag() {
            return tag;
        }

        public void setTag(Recognition.IdentifyResults.Candidate tag) {
            this.tag = tag;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_2text, null);

            holder = new ViewHolder();
            holder.confidence = (TextView) convertView.findViewById(R.id.confidence);
            holder.personName = (TextView) convertView.findViewById(R.id.personName);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (candidatesList != null) {
            Recognition.IdentifyResults.Candidate candidate = candidatesList.get(position);
            holder.confidence.setText("Confidence: " + candidate.getConfidence());
            holder.personName.setText("Name: " + candidate.getPersonName() );
            holder.tag = candidate;
        }
        return convertView;

    }
}
