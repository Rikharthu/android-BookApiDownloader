package com.example.uberv.maptbookapidownloader.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookMetadata {
    @SerializedName("digitalNid")
    private int mDigitalNid;
    @SerializedName("nid")
    private int mNid;
    @SerializedName("earlyAccess")
    private boolean mIsEarlyAccess;
    @SerializedName("tableOfContents")
    private List<Chapter> mChapters;

    public BookMetadata() {
    }

    public BookMetadata(int digitalNid, int nid, boolean isEarlyAccess, List<Chapter> chapters) {
        mDigitalNid = digitalNid;
        mNid = nid;
        mIsEarlyAccess = isEarlyAccess;
        mChapters = chapters;
    }

    public int getDigitalNid() {
        return mDigitalNid;
    }

    public void setDigitalNid(int digitalNid) {
        mDigitalNid = digitalNid;
    }

    public int getNid() {
        return mNid;
    }

    public void setNid(int nid) {
        mNid = nid;
    }

    public boolean isEarlyAccess() {
        return mIsEarlyAccess;
    }

    public void setEarlyAccess(boolean earlyAccess) {
        mIsEarlyAccess = earlyAccess;
    }

    public List<Chapter> getChapters() {
        return mChapters;
    }

    public void setChapters(List<Chapter> chapters) {
        mChapters = chapters;
    }
}
