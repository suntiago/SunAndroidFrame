package com.suntiago.sunandroidframe;

import android.content.Context;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

public final class RongCloudEvent implements RongIMClient.OnReceiveMessageListener {
    private static RongCloudEvent mRongCloudInstance;
    private MessageCallback mMessageCallback;
    Context mContext;
    public static void init(Context context) {
        if (mRongCloudInstance == null) {
            synchronized (RongCloudEvent.class) {
                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }
    }

    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }

    public RongCloudEvent(Context mContext) {
        this.mContext = mContext;
        initDefaultListener();
    }

    private void initDefaultListener() {
        RongIMClient.setOnReceiveMessageListener(this);
    }

    public void setOnMessageListen(MessageCallback callback) {
        this.mMessageCallback = callback;
    }
    interface MessageCallback {
        void onReceived(Message message, int left);
    }
    /**
     * 收到消息的处理。
     * @param message 收到的消息实体。
     * @param left 剩余未拉取消息数目。
     * @return
     */
    @Override
    public boolean onReceived(Message message, int left) {
        if (this.mMessageCallback != null) {
            this.mMessageCallback.onReceived(message, left);
        }
        return false;
    }
}