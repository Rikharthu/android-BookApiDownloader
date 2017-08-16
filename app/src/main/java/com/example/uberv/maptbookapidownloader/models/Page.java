package com.example.uberv.maptbookapidownloader.models;

import com.google.gson.annotations.SerializedName;

public class Page {
    @SerializedName("entitled")
    private boolean mIsEntitled;
    @SerializedName("accessBlocked")
    private boolean mIsAccessBlocked;
    @SerializedName("content")
    private String mContent;

    public Page() {
    }

    public Page(boolean isEntitled, boolean isAccessBlocked, String content) {
        mIsEntitled = isEntitled;
        mIsAccessBlocked = isAccessBlocked;
        mContent = content;
    }

    public boolean isEntitled() {
        return mIsEntitled;
    }

    public void setEntitled(boolean entitled) {
        mIsEntitled = entitled;
    }

    public boolean isAccessBlocked() {
        return mIsAccessBlocked;
    }

    public void setAccessBlocked(boolean accessBlocked) {
        mIsAccessBlocked = accessBlocked;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
