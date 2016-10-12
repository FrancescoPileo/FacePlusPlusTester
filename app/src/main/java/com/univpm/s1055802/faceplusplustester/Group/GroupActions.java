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

package com.univpm.s1055802.faceplusplustester.Group;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.univpm.s1055802.faceplusplustester.FppTester;
import com.univpm.s1055802.faceplusplustester.Utils.CallbackActions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 17/05/16.
 */
public class GroupActions {

    public static class SendCreate extends CallbackActions {

        public SendCreate(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette la creazione di un nuovo Group
         * @param groupName nome del Group
         * @param groupTag tag del Group
         * @param selectedPersons array di persone da assegnare al Group
         */
        public void sendCreation(final String groupName, final String groupTag, final ArrayList<String> selectedPersons){
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setGroupName(groupName);
                        postParameters.setTag(groupTag);
                        if (selectedPersons.size() != 0) {
                            postParameters.setPersonId(selectedPersons);
                        }
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).groupCreate(postParameters);
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e ) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static class GetGroupsList extends CallbackActions {

        public GetGroupsList(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Ottiene la lista dei Group
         */
        public void getGroupsList() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).infoGetGroupList();
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public static class DeleteGroups extends CallbackActions{

        public DeleteGroups(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette l'eliminazione di più Group
         * @param deleteList lista dei Group da eliminare
         */
        public void deleteGroups(final JSONObject deleteList) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ArrayList<String> deleteArray = new ArrayList<String>();
                        if (deleteList.getJSONArray("group_delete").length()>0) {
                            for (int i = 0; i < deleteList.getJSONArray("group_delete").length(); i++) {
                                deleteArray.add(deleteList.getJSONArray("group_delete").getString(i));
                            }
                            PostParameters params = new PostParameters();
                            params.setGroupName(deleteArray);
                            FppTester fppTester = (FppTester)activity.getApplication();
                            JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).groupDelete(params);
                            if (callback != null){
                                callback.processResult(result);
                            }
                        }
                    } catch (FaceppParseException | JSONException e ) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public static class DeleteGroup extends CallbackActions{

        public DeleteGroup(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette l'eliminazione di un Group
         * @param groupId Id del Group da eliminare
         */
        public void deleteGroup(final String groupId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setGroupId(groupId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).groupDelete(postParameters);
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });

                    }
                }
            }).start();
        }
    }

    public static class EditGroup extends CallbackActions{

        public EditGroup(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Permette la modifica di un Group esistente
         * @param groupId Id del Group da modificare
         * @param newName nuovo nome del Group
         * @param newTag nuovo tag del Group
         */
        public void editGroup(final String groupId, final String newName, final String newTag) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters parameters = new PostParameters();
                        parameters.setGroupId(groupId);
                        parameters.setName(newName);
                        parameters.setTag(newTag);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).groupSetInfo(parameters);
                        if (callback != null){
                            callback.processResult(result);
                        }

                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public static class AddPerson extends CallbackActions{

        public AddPerson(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Assegna una persona al Group specificarto
         * @param groupId Id del Group
         * @param personId Id della persona
         */
        public void addPerson(final String groupId, final String personId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters params = new PostParameters();
                        params.setGroupId(groupId);
                        params.setPersonId(personId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).groupAddPerson(params);
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e ) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public static class RemovePerson extends CallbackActions{

        public RemovePerson(AppCompatActivity activity){
            super(activity);
        }

        /**
         * Rimuove una persona assegnata ad un Group
         * @param groupId Id del Group
         * @param personId Id della persona
         */
        public void removePerson(final String groupId, final String personId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters params = new PostParameters();
                        params.setGroupId(groupId);
                        params.setPersonId(personId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).groupRemovePerson(params);
                        if (callback != null){
                            callback.processResult(result);
                        }
                    } catch (FaceppParseException e ) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public static class GetGroupInfo extends CallbackActions {

        public GetGroupInfo(AppCompatActivity activity) {
            super(activity);
        }

        /**
         * Ottiene le informazioni riguardanti un Group
         * @param groupId Id del Group
         */
        public void getGroupInfo(final String groupId) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PostParameters postParameters = new PostParameters();
                        postParameters.setGroupId(groupId);
                        FppTester fppTester = (FppTester)activity.getApplication();
                        JSONObject result = new HttpRequests(fppTester.getApiKey(), fppTester.getApiSecret(), fppTester.isCN(), fppTester.isDebug()).groupGetInfo(postParameters);
                        if (callback != null) {
                            callback.processResult(result);
                        }

                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
        }
    }
}
