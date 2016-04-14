package com.suntiago.sunandroidframe.util;

import android.app.Activity;
import android.util.Log;

import com.suntiago.sunandroidframe.SunApplication;

import io.rong.imlib.RongIMClient;

/**
 * Created by yu.zai on 2016/4/5.
 */
public class Utils {
    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    public static void connect(Activity activity, String token) {
        String tokenDefault = "fifhoWfdxMJWcITjaqdJap57qdnCLbhtfwHLJm4+0bgl92F6bgXrw88J1TFc+ssPbF8VS5NPthKTNBar16WAcw==";
        if (activity.getApplicationInfo().packageName.equals(SunApplication.getCurProcessName(activity.getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIMClient.connect(token == null ? tokenDefault : token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {

                    Log.d("LoginActivity", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 *
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {

                    Log.d("LoginActivity", "--onSuccess---" + userid);
                }

                /**
                 * 连接融云失败
                 *
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }
}
