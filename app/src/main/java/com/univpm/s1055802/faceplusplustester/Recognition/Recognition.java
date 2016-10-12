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

package com.univpm.s1055802.faceplusplustester.Recognition;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kekko on 18/07/16.
 */
public class Recognition {
    /**
     * Classe che rappresenta il risultato di un Verify
     */
    public static class VerifyResult{

        private String confidence;
        private Boolean isSamePerson;
        private String sessionId;

        public VerifyResult(JSONObject result) throws JSONException{
            setConfidence(result.getString("confidence"));
            setSamePerson(result.getBoolean("is_same_person"));
            setSessionId(result.getString("session_id"));
        }

        public String getConfidence() {
            return confidence;
        }

        public void setConfidence(String confidence) {
            this.confidence = confidence;
        }

        public Boolean getSamePerson() {
            return isSamePerson;
        }

        public void setSamePerson(Boolean theSamePerson) {
            isSamePerson = theSamePerson;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    /**
     * Classe che rappresenta il risultato di un Identify
     */
    public static class IdentifyResults{

        int candidatesNumber;
        Candidate candidates[];
        String faceId;
        String sessionId;

        public IdentifyResults(JSONObject result) throws JSONException {
            candidatesNumber = result.getJSONArray("face").getJSONObject(0).getJSONArray("candidate").length();
            setCandidates(new Candidate[candidatesNumber]);
            for (int i=0; i<candidatesNumber; i++){
                setCandidate(new Candidate(result.getJSONArray("face").getJSONObject(0).getJSONArray("candidate").getJSONObject(i)), i);
            }
            setFaceId(result.getJSONArray("face").getJSONObject(0).getString("face_id"));
            setSessionId(result.getString("session_id"));
        }

        public IdentifyResults.Candidate[] getCandidates() {
            return candidates;
        }

        public void setCandidates(IdentifyResults.Candidate[] candidates) {
            this.candidates = candidates;
        }

        public IdentifyResults.Candidate getCandidate(int index){
            return candidates[index];
        }

        public void setCandidate(IdentifyResults.Candidate candidate, int index){
            this.candidates[index] = candidate;
        }

        public String getFaceId() {
            return faceId;
        }

        public void setFaceId(String faceId) {
            this.faceId = faceId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public static class Candidate {

            private String confidence;
            private String personId;
            private String personName;
            private String personTag;

            public Candidate(JSONObject candidate) throws JSONException{
                setConfidence(candidate.getString("confidence"));
                setPersonId(candidate.getString("person_id"));
                setPersonName(candidate.getString("person_name"));
                setPersonTag(candidate.getString("tag"));
            }

            public String getConfidence() {
                return confidence;
            }

            public void setConfidence(String confidence) {
                this.confidence = confidence;
            }

            public String getPersonId() {
                return personId;
            }

            public void setPersonId(String personId) {
                this.personId = personId;
            }

            public String getPersonName() {
                return personName;
            }

            public void setPersonName(String personName) {
                this.personName = personName;
            }

            public String getPersonTag() {
                return personTag;
            }

            public void setPersonTag(String personTag) {
                this.personTag = personTag;
            }
        }

    }

    /**
     * Classe che rappresenta il risultato di un Search
     */
    public static class SearchResults {

        int candidatesNumber;
        SearchResults.Candidate candidates[];
        String sessionId;

        public SearchResults(JSONObject result) throws JSONException {
            candidatesNumber = result.getJSONArray("candidate").length();
            setCandidates(new Candidate[candidatesNumber]);
            for (int i = 0; i < candidatesNumber; i++) {
                setCandidate(new Candidate(result.getJSONArray("candidate").getJSONObject(i)), i);
            }
            setSessionId(result.getString("session_id"));
        }

        public SearchResults.Candidate[] getCandidates() {
            return candidates;
        }

        public void setCandidates(SearchResults.Candidate[] candidates) {
            this.candidates = candidates;
        }

        public SearchResults.Candidate getCandidate(int index) {
            return candidates[index];
        }

        public void setCandidate(SearchResults.Candidate candidate, int index) {
            this.candidates[index] = candidate;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public static class Candidate {

            private String similarity;
            private String faceId;
            private String personTag;

            public Candidate(JSONObject candidate) throws JSONException {
                setSimilarity(candidate.getString("similarity"));
                setFaceId(candidate.getString("face_id"));
                setPersonTag(candidate.getString("tag"));
            }

            public String getSimilarity() {
                return similarity;
            }

            public void setSimilarity(String similarity) {
                this.similarity = similarity;
            }

            public String getFaceId() {
                return faceId;
            }

            public void setFaceId(String faceId) {
                this.faceId = faceId;
            }

            public String getPersonTag() {
                return personTag;
            }

            public void setPersonTag(String personTag) {
                this.personTag = personTag;
            }
        }
    }
}
