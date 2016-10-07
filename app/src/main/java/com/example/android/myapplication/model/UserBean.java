package com.example.android.myapplication.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

/**
 * Created by abedch on 9/9/2015.
 */
public class UserBean {

    private Integer idAppUser;
    private String name;
    private Integer groupRole;
    private Collection<?> showrooms;

    public void setFromJson(JSONObject jsonObject) throws JSONException {
        this.idAppUser = jsonObject.getInt("idAppUser");
        this.name = jsonObject.getString("name");
        this.groupRole = jsonObject.getInt("groupRole");

    }


    public Integer getIdAppUser() {
        return idAppUser;
    }
    public void setIdAppUser(Integer idAppUser) {
        this.idAppUser = idAppUser;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getGroupRole() {
        return groupRole;
    }
    public void setGroupRole(Integer groupRole) {
        this.groupRole = groupRole;
    }
    public Collection<?> getShowrooms() {
        return showrooms;
    }
    public void setShowrooms(Collection<?> showrooms) {
        this.showrooms = showrooms;
    }
}
