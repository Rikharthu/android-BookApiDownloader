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
    @SerializedName("imageUrl")
    private String mImageUrl;
    @SerializedName("title")
    private String mTitle;

    public BookMetadata() {
    }

    public BookMetadata(int mDigitalNid, int mNid, boolean mIsEarlyAccess, List<Chapter> mChapters, String mImageUrl, String mTitle) {
        this.mDigitalNid = mDigitalNid;
        this.mNid = mNid;
        this.mIsEarlyAccess = mIsEarlyAccess;
        this.mChapters = mChapters;
        this.mImageUrl = mImageUrl;
        this.mTitle = mTitle;
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

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
