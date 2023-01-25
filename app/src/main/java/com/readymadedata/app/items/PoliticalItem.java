package com.readymadedata.app.items;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PoliticalItem implements Serializable {
    @SerializedName("designation")
    public String designation;
    @SerializedName("facebook_username")
    public String facebook_username;
    @SerializedName("id")

    /* renamed from: id */
    public String f14id;
    @SerializedName("instgram_username")
    public String instagram_username;
    public boolean isDefault;
    @SerializedName("logo")
    public String logo;
    @SerializedName("name")
    public String name;
    @SerializedName("mobileNo")
    public String phone;
    @SerializedName("photo_a")
    public String photo1;
    @SerializedName("photo_b")
    public String photo2;
    @SerializedName("photo_c")
    public String photo3;
    @SerializedName("profile")
    public String profile;

    public PoliticalItem(String id, String name2, String logo2, String profile2, String photo12, String photo22, String photo32, String phone2, String facebook_username2, String instagram_username2, String designation2, boolean isDefault2) {
        this.f14id = id;
        this.name = name2;
        this.logo = logo2;
        this.profile = profile2;
        this.photo1 = photo12;
        this.photo2 = photo22;
        this.photo3 = photo32;
        this.phone = phone2;
        this.facebook_username = facebook_username2;
        this.instagram_username = instagram_username2;
        this.designation = designation2;
        this.isDefault = isDefault2;
    }
}
