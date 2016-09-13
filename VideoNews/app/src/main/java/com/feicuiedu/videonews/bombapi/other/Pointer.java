package com.feicuiedu.videonews.bombapi.other;

import com.google.gson.annotations.SerializedName;

/**
 * 通用POINTER
 *
 * 作者：yuanchao on 2016/8/18 0018 12:00
 * 邮箱：yuanchao@feicuiedu.com
 */
public class Pointer {
//    "__type": "Object",
//    "className": "_User",
//    "objectId": "D5vlAAAJ",

    @SerializedName("__type")
    private String type = "Pointer";

    private String className;

    private String objectId;

    public Pointer(String className, String objectId) {
        this.className = className;
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public String getClassName() {
        return className;
    }

    public String getObjectId() {
        return objectId;
    }
}
