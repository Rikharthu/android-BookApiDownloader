package com.example.uberv.maptbookapidownloader.models.responses;

import com.google.gson.annotations.SerializedName;

public class AuthData {
    @SerializedName("v1Token")
    private String mV1Token;
    @SerializedName("access")
    private String mAccess;
    @SerializedName("refresh")
    private String mRefresh;

    public AuthData() {
    }

    public AuthData(String v1Token, String access, String refresh) {
        mV1Token = v1Token;
        mAccess = access;
        mRefresh = refresh;
    }

    public String getV1Token() {
        return mV1Token;
    }

    public void setV1Token(String v1Token) {
        mV1Token = v1Token;
    }

    public String getAccess() {
        return mAccess;
    }

    public void setAccess(String access) {
        mAccess = access;
    }

    public String getRefresh() {
        return mRefresh;
    }

    public void setRefresh(String refresh) {
        mRefresh = refresh;
    }
}
