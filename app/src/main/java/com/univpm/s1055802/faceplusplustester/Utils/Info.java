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

package com.univpm.s1055802.faceplusplustester.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kekko on 19/05/16.
 */
public class Info {

    protected String id;
    protected String name;
    protected String tag;
    protected boolean checked;

    public static final String PERSON = "person";
    public static final String GROUP = "group";
    public static final String FACESET = "faceset";

    public Info(JSONObject info, String infoType) throws JSONException{
        this.setId(info.getString(infoType + "_id"));
        this.setName(info.getString(infoType + "_name"));
        this.setTag(info.getString("tag"));
        this.setChecked(false);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * crea l'array list che serve per popolare la list view
     * @param jsonObject json contenente le informazioni sulla lista di persone
     * @return l'array list opportunamente settata
     */
    public static ArrayList<Info> infoArrayFromJson(JSONObject jsonObject, String infoType) {
        ArrayList<Info> array = new ArrayList<Info>();
        try {
            int infoNumber = jsonObject.getJSONArray(infoType).length();
            for (int i = 0; i < infoNumber; i++) {
                Info info = new Info(jsonObject.getJSONArray(infoType).getJSONObject(i), infoType);
                array.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;
    }
}
