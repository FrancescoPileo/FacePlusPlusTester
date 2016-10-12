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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Recognition.Recognition;

import java.util.ArrayList;

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
