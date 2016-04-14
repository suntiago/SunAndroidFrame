package com.suntiago.sunandroidframe.adapter;

/**
 * Created by yu.zai on 2016/4/5.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.suntiago.sunandroidframe.R;
import com.suntiago.sunandroidframe.entity.FaceMap;
import com.suntiago.sunandroidframe.util.Util;
import com.suntiago.sunandroidframe.view.GifTextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

@SuppressLint("NewApi")
public class MessageAdapter extends BaseAdapter {
    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    private Context mContext;
    private LayoutInflater mInflater;
    private Conversation.ConversationType mConversationType;
    private String mTargetId;

    List<Message> mMessages;
    ScrotoBottom scrotoBottom;
    TextViewHolder mTextViewHolder;
    VoiceViewHolder mVoiceViewHolder;
    public interface ScrotoBottom {
        void scroToBottom();
    }
    public MessageAdapter(Context context, List<String> msgList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public MessageAdapter(Context context, Conversation.ConversationType conversationType,  String targetId) {
        this.mContext = context;
        mConversationType = conversationType;
        this.mTargetId = targetId;
        this.mInflater = LayoutInflater.from(context);
        refreshMessage();
    }

    public void setScrotoBottom(ScrotoBottom scrotoBottom) {
        this.scrotoBottom = scrotoBottom;
    }

    private void scroTobottom() {
        if (scrotoBottom != null) {
            scrotoBottom.scroToBottom();
        }
    }
    public void refreshMessage() {
        refreshMessage(-1);
    }

    public void refreshMessage(int count) {
        RongIMClient.getInstance().getLatestMessages(mConversationType, mTargetId, count, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                mMessages = messages;
                notifyDataSetChanged();
                scroTobottom();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    public void loadMessage(int conut, final int oldestMessageId) {
        RongIMClient.getInstance().getHistoryMessages(mConversationType, mTargetId, oldestMessageId, conut, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                mMessages = messages;
                notifyDataSetChanged();
                scroTobottom();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    @Override
    public int getCount() {
        return mMessages!=null?mMessages.size():0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message msg= mMessages.get(getCount() - 1 - position);
        return getView(convertView, parent, msg);
    }

    private View getView(View convertView, ViewGroup parent, Message msg) {
        MessageContent messageContent = msg.getContent();
        if (messageContent instanceof VoiceMessage) {
            return getVoiceView(convertView, parent, msg);
        } else if (messageContent instanceof TextMessage) {
            return getTextView(convertView, parent, msg);
        }
        return convertView;
    }

    private View getTextView(View convertView, ViewGroup parent, Message msg) {
//        BaseViewHolder viewHolder = null;
        MessageContent messageContent = msg.getContent();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rc_item_message, parent, false);
            FrameLayout v = (FrameLayout) convertView.findViewById(R.id.rc_message_content);
            View vv = mInflater.inflate(R.layout.rc_item_text_message, parent, false);
            v.addView(vv);
            if (messageContent instanceof VoiceMessage) {
                mVoiceViewHolder = new VoiceViewHolder(convertView, parent, msg);
            } else if (messageContent instanceof TextMessage) {
                mTextViewHolder = new TextViewHolder(convertView, parent, msg);
            }
//            viewHolder = new ViewHolder(convertView, vv, msg);

//            convertView.setTag(viewHolder);
        }
//        else {
//            viewHolder = (ViewHolder)convertView.getTag();
//        }

        if (messageContent instanceof VoiceMessage) {
            initData(mVoiceViewHolder, msg);
        } else if (messageContent instanceof TextMessage) {
            initData(mTextViewHolder, msg);
        }


        return convertView;
    }

    private View getVoiceView(View convertView, ViewGroup parent, Message msg) {
        /*
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rc_item_message, parent, false);
            FrameLayout v =  (FrameLayout)convertView.findViewById(R.id.rc_message_content);
            View vv = mInflater.inflate(R.layout.rc_item_voice_message, parent, false);
            v.addView(vv);
            viewHolder = new ViewHolder(convertView, vv, msg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        initData(viewHolder, msg);*/
        return convertView;
    }

    public void initData(BaseViewHolder viewHolder, Message msg) {
        if (viewHolder==null && msg==null) {
            return;
        }
        setUserInfoView(viewHolder, msg);
        MessageContent messageContent = msg.getContent();
        Message.MessageDirection messageDirection = msg.getMessageDirection();
        if (messageContent instanceof VoiceMessage) {
            if (messageDirection == Message.MessageDirection.RECEIVE) {
                receiveVoice(viewHolder, msg, (VoiceMessage)messageContent);
            } else {
                sendVoice(viewHolder, msg, (VoiceMessage)messageContent);
            }
        } else if (messageContent instanceof TextMessage) {
            if (messageDirection == Message.MessageDirection.RECEIVE) {
                receiveText(viewHolder, msg, (TextMessage)messageContent);
            } else {
                sendText(viewHolder, msg, (TextMessage)messageContent);
            }
        }
    }

    private void receiveVoice(BaseViewHolder viewHolder, Message msg, VoiceMessage voiceMessage) {

    }

    private void sendVoice(BaseViewHolder viewHolder, Message msg, VoiceMessage voiceMessage) {

    }

    private void receiveText(BaseViewHolder viewHolder, Message msg, TextMessage textMessage) {

    }

    private void sendText(BaseViewHolder viewHolder, Message msg, TextMessage textMessage) {
        /*
        viewHolder.time_tv.setText(Util.timet(msg.getSentTime()));
        // 文字
        if (textMessage == null) {
            Log.d("sendText", "if (textMessage == null) {");
        }
        if (viewHolder.text_msg_tv == null) {
            Log.d("sendText", "viewHolder.text_msg_tv == null");
        }
        viewHolder.text_msg_tv.insertGif(convertNormalStringToSpannableString(textMessage.getContent() + " "));
        */
    }
    /**
     * 另外一种方法解析表情将[表情]换成fxxx
     *
     * @param message
     *            传入的需要处理的String
     * @return
     */
    private String convertNormalStringToSpannableString(String message) {
        String hackTxt;
        if (message.startsWith("[") && message.endsWith("]")) {
            hackTxt = message + " ";
        } else {
            hackTxt = message;
        }

        Matcher localMatcher = EMOTION_URL.matcher(hackTxt);
        while (localMatcher.find()) {
            String str2 = localMatcher.group(0);
            if (FaceMap.getInstance().getFaceMap().containsKey(str2)) {
                String faceName = mContext.getResources().getString(
                        FaceMap.getInstance().getFaceMap().get(str2));
                CharSequence name = options(faceName);
                message = message.replace(str2, name);
            }

        }
        return message;
    }

    /**
     * 取名字f010
     *
     * @param faceName
     */
    private CharSequence options(String faceName) {
        int start = faceName.lastIndexOf("/");
        CharSequence c = faceName.subSequence(start + 1, faceName.length() - 4);
        return c;
    }

    public void setUserInfoView(BaseViewHolder viewHolder, Message msg) {
        MessageContent messageContent = msg.getContent();
        Message.MessageDirection messageDirection = msg.getMessageDirection();
        if (messageDirection == Message.MessageDirection.RECEIVE) {
            viewHolder.right_head_iv.setVisibility(View.GONE);
            viewHolder.left_head_iv.setVisibility(View.VISIBLE);
            viewHolder.title_username_tv.setVisibility(View.VISIBLE);
            viewHolder.title_username_tv.setText(msg.getSenderUserId());
        } else {
            viewHolder.right_head_iv.setVisibility(View.VISIBLE);
            viewHolder.left_head_iv.setVisibility(View.GONE);
            viewHolder.title_username_tv.setVisibility(View.GONE);
        }
    }

    static class BaseViewHolder {
        LinearLayout time_ll;
        TextView time_tv;
        LinearLayout left_head_ll;
        ImageView left_head_iv;
        ImageView right_head_iv;
        TextView title_username_tv;
        LinearLayout send_status_ll;
        TextView send_status_tv;
        ProgressBar send_status_pgress;
        ImageView send_warning_iv;
        FrameLayout msg_content_fv;
        public BaseViewHolder() {}
        public BaseViewHolder(View convertView, View content, Message msg) {
            if (convertView == null || content == null || msg == null) {
                return;
            }
            time_ll = (LinearLayout) convertView.findViewById(R.id.time_ll);
            time_tv = (TextView) convertView.findViewById(R.id.rc_time);
            left_head_ll = (LinearLayout) convertView.findViewById(R.id.rc_left_ll);
            left_head_iv = (ImageView) convertView.findViewById(R.id.rc_left);
            right_head_iv = (ImageView) convertView.findViewById(R.id.rc_right);
            title_username_tv = (TextView) convertView.findViewById(R.id.rc_title);
            send_status_ll = (LinearLayout) convertView.findViewById(R.id.rc_layout);
            send_status_tv = (TextView) convertView.findViewById(R.id.rc_sent_status);
            send_status_pgress = (ProgressBar) convertView.findViewById(R.id.rc_progress);
            send_warning_iv = (ImageView) convertView.findViewById(R.id.rc_warning);
            msg_content_fv = (FrameLayout) convertView.findViewById(R.id.rc_message_content);
        }
    }

    static class TextViewHolder extends BaseViewHolder {
        GifTextView text_msg_tv;
        public TextViewHolder() {
            super();
        }
        public TextViewHolder(View convertView, View content, Message msg) {
            super(convertView, content, msg);
            if (convertView == null || content == null || msg == null) {
                return;
            }
            MessageContent messageContent = msg.getContent();
            if (messageContent instanceof TextMessage) {
                text_msg_tv = (GifTextView) content.findViewById(android.R.id.text1);
            }
        }
    }

    static class VoiceViewHolder extends BaseViewHolder {
        LinearLayout voice_msg_bg_ll;
        TextView voice_left_tv;
        ImageView voice_iv;
        TextView voice_right_tv;
        ImageView voice_unread_iv;
        public VoiceViewHolder() {
            super();
        }
        public VoiceViewHolder(View convertView, View content, Message msg) {
            super(convertView, content, msg);
            if (convertView == null || content == null || msg == null) {
                return;
            }
            MessageContent messageContent = msg.getContent();
            if (messageContent instanceof VoiceMessage) {
                voice_msg_bg_ll = (LinearLayout) content.findViewById(R.id.rc_voice_bg);
                voice_left_tv = (TextView) content.findViewById(R.id.rc_left);
                voice_iv = (ImageView) content.findViewById(R.id.rc_img);
                voice_right_tv = (TextView) content.findViewById(R.id.rc_right);
                voice_unread_iv = (ImageView) content.findViewById(R.id.rc_voice_unread);
            }
        }
    }
}