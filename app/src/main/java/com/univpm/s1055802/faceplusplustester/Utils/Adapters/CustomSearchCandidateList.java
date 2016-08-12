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

import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Recognition.Recognition;
import com.univpm.s1055802.faceplusplustester.Utils.Directories;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kekko on 03/05/16.
 */

public class CustomSearchCandidateList extends ArrayAdapter<Recognition.SearchResults.Candidate>{

    private final Activity context;
    private ArrayList<Recognition.SearchResults.Candidate> candidatesList;

    public CustomSearchCandidateList(Activity context, int textViewResourceId,
                                     ArrayList<Recognition.SearchResults.Candidate> candidatesList) {
        super(context, textViewResourceId, candidatesList);
        this.context = context;
        this.candidatesList = candidatesList;

    }

    public class ViewHolder {

        private ImageView face;
        private TextView similarity;
        private TextView faceId;
        private Recognition.SearchResults.Candidate tag;

        public Recognition.SearchResults.Candidate getTag() {
            return tag;
        }

        public void setTag(Recognition.SearchResults.Candidate tag) {
            this.tag = tag;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_face_2text, null);

            holder = new ViewHolder();
            holder.face = (ImageView) convertView.findViewById(R.id.face);
            holder.similarity = (TextView) convertView.findViewById(R.id.similarity);
            holder.faceId = (TextView) convertView.findViewById(R.id.faceId);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (candidatesList != null) {

            Recognition.SearchResults.Candidate candidate = candidatesList.get(position);

            File imgFile = new File(Directories.FACES + File.separator + candidate.getFaceId() + ".png" );
            if (imgFile.exists()){
                Bitmap myBit = BitmapFactory.decodeFile(Directories.FACES + File.separator + candidate.getFaceId() + ".png");
                holder.face.setImageBitmap(myBit);
            } else {
                holder.face.setImageResource(R.drawable.fpp_no_face);
            }

            holder.similarity.setText("Confidence: " + candidate.getSimilarity());
            holder.faceId.setText("Name: " + candidate.getFaceId());
            holder.tag = candidate;


        }
        return convertView;

    }
}
