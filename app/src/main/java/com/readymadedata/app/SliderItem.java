package com.readymadedata.app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SliderItem {

    @SerializedName("image")
    @Expose
    String imageUrl;

    @SerializedName("url")
    @Expose
    String promotionalUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPromotionalUrl() {
        return promotionalUrl;
    }

    public void setPromotionalUrl(String promotionalUrl) {
        this.promotionalUrl = promotionalUrl;
    }
}
