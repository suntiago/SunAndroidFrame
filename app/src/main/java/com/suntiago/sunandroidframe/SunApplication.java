package com.suntiago.sunandroidframe;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.suntiago.sunandroidframe.db.DBManage;

import io.rong.imlib.RongIMClient;

/**
 * Created by yu.zai on 2016/2/22.
 */
public class SunApplication extends Application {
    Context application;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
//        DBManage.init(application);
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        Log.d("application", "before init");
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            Log.d("application", "init");
            RongIMClient.init(application);
            RongCloudEvent.init(application);
        }
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
