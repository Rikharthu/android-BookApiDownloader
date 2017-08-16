package com.example.uberv.maptbookapidownloader.models.responses;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> {

    @SerializedName("status")
    private String mStatus;
    @SerializedName("httpStatus")
    private int mHttpStatus;
    @SerializedName("data")
    private T mData;

    public BaseResponse() {
    }

    public BaseResponse(String status, int httpStatus, T data) {
        mStatus = status;
        mHttpStatus = httpStatus;
        mData = data;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public int getHttpStatus() {
        return mHttpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        mHttpStatus = httpStatus;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }
}
