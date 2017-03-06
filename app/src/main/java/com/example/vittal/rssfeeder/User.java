package com.example.vittal.rssfeeder;

/**
 * Created by vittal on 5/3/17.
 */

public class User {

    private String oAuthId;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String photoUrl;

    public String getoAuthId() {
        return oAuthId;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setoAuthId(String oAuthId) {
        this.oAuthId = oAuthId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
