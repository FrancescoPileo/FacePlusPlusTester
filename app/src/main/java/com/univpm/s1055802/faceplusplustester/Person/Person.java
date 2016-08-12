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
