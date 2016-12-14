package com.itcast.zbc.mediaplayer.network.netmodel;

/**
 * Created by jimubox on 4/21/2015.
 */
public class ErrorMsg {
    private int statusCode;
    private String ErrorString;
    private String tag;


    public String getTag() {return tag;}

    public void setTag(String tag) {this.tag = tag;}

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorString() {
        return ErrorString;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setErrorString(String errorString) {
        ErrorString = errorString;
    }

    public ErrorMsg(String errorString) {
        ErrorString = errorString;
    }
}
