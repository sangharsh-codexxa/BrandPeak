package com.readymadedata.app.items;

import com.google.gson.annotations.SerializedName;

public class PersonalItem {
    @SerializedName("address")
    public String address;
    @SerializedName("email")
    public String email;
    @SerializedName("face_username")
    public String face_username;
    @SerializedName("id")

    /* renamed from: id */
    public String f13id;
    @SerializedName("insta_username")
    public String insta_username;
    public boolean isDefault;
    @SerializedName("logo")
    public String logo;
    @SerializedName("name")
    public String name;
    @SerializedName("mobileNo")
    public String phone;

    public String getId() {
        return this.f13id;
    }

    public void setId(String id) {
        this.f13id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo2) {
        this.logo = logo2;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email2) {
        this.email = email2;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone2) {
        this.phone = phone2;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address2) {
        this.address = address2;
    }

    public String getFace_username() {
        return this.face_username;
    }

    public void setFace_username(String face_username2) {
        this.face_username = face_username2;
    }

    public String getInsta_username() {
        return this.insta_username;
    }

    public void setInsta_username(String insta_username2) {
        this.insta_username = insta_username2;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public void setDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }
}
