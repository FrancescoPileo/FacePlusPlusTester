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

package com.univpm.s1055802.faceplusplustester.Person;

import com.univpm.s1055802.faceplusplustester.Faceset.Faceset;
import com.univpm.s1055802.faceplusplustester.Group.Group;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kekko on 09/05/16.
 */

/**
 * Classe che contiene tutte le informazioni di una Person
 */
public class Person extends Faceset{

    private int groupNumber;
    private Group[] groups;

    public Person(JSONObject person) throws JSONException{
        super(person, Info.PERSON);

        if (!person.isNull("group")){
            this.setGroupNumber(person.getJSONArray("group").length());
            this.setGroups(new Group[getGroupNumber()]);
            for (int i=0; i<this.getGroupNumber(); i++)
                this.setPersonGroup(i, new Group(person.getJSONArray("group").getJSONObject(i)));
        }
    }

    public void setPersonGroup(int index, Group group){
        this.groups[index] = group;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public Group[] getGroups() {
        return groups;
    }

    public void setGroups(Group[] groups) {
        this.groups = groups;
    }

}
