package com.suntiago.sunandroidframe;

import android.app.Application;

import com.suntiago.sunandroidframe.db.DBManage;

/**
 * Created by yu.zai on 2016/2/22.
 */
public class SunApplication extends Application {
    @Override
    public void onCreate() {
        DBManage.init(this);
        super.onCreate();
    }
}
