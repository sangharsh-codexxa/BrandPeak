package com.readymadedata.app.items;

import com.google.gson.annotations.SerializedName;

public class CouponItem {

    @SerializedName("discount")
    public String discount;

    public CouponItem(String discount) {
        this.discount = discount;
    }
}
