package com.example.uberv.maptbookapidownloader.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chapter {
    @SerializedName("type")
    private String mType;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("id")
    private String mId;
    @SerializedName("index")
    private int mIndex;
    @SerializedName("children")
    private List<Section> mSections;

    public Chapter() {
    }

    public Chapter(String type, String title, String id, int index, List<Section> sections) {
        mType = type;
        mTitle = title;
        mId = id;
        mIndex = index;
        mSections = sections;
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

    public List<Section> getSections() {
        return mSections;
    }

    public void setSections(List<Section> sections) {
        mSections = sections;
    }
}
