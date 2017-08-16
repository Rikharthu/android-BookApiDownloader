package com.example.uberv.maptbookapidownloader.models;

import com.google.gson.annotations.SerializedName;

public class Section {
    @SerializedName("type")
    private String mType;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("id")
    private String mId;
    @SerializedName("index")
    private int mIndex;
    @SerializedName("seoUrl")
    private String mSeoUrl;

    public Section() {
    }

    public Section(String type, String title, String id, int index, String seoUrl) {
        mType = type;
        mTitle = title;
        mId = id;
        mIndex = index;
        mSeoUrl = seoUrl;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public String getSeoUrl() {
        return mSeoUrl;
    }

    public void setSeoUrl(String seoUrl) {
        mSeoUrl = seoUrl;
    }

    @Override
    public String toString() {
        return "Section{" +
                "mType='" + mType + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mId='" + mId + '\'' +
                ", mIndex=" + mIndex +
                ", mSeoUrl='" + mSeoUrl + '\'' +
                '}';
    }
}
