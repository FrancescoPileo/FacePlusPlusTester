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

import com.univpm.s1055802.faceplusplustester.Person.Person;
import com.univpm.s1055802.faceplusplustester.Utils.Info;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kekko on 09/05/16.
 */

/**
 * Classe che contiene tutte le informazioni riguardanti un Group
 */
public class Group extends Info {

    private Person[] persons;
    private int personNumber;


    public Group(JSONObject group) throws JSONException {
        super(group, Info.GROUP);

        if (!group.isNull("person")){
            this.setPersonNumber(group.getJSONArray("person").length());
            this.setPersons(new Person[this.getPersonNumber()]);
            for (int i = 0; i < this.getPersonNumber(); i++){
                this.setPerson(i, new Person(group.getJSONArray("person").getJSONObject(i)));
            }
        }
    }

    public int getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(int personNumber) {
        this.personNumber = personNumber;
    }


    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Person getPerson(int index){
        return persons[index];
    }

    public void setPerson(int index, Person person){
        this.persons[index] = person;

    }

}
