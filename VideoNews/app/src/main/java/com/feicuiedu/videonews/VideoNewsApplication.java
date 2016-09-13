package com.feicuiedu.videonews;

import android.app.Application;

import com.feicuiedu.videonews.commons.ToastUtils;

/**
 * 作者：yuanchao on 2016/8/19 0019 10:03
 * 邮箱：yuanchao@feicuiedu.com
 */
public class VideoNewsApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        ToastUtils.init(this);
    }
}