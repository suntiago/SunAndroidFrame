package com.suntiago.sunandroidframe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suntiago.sunandroidframe.entity.FaceMap;
import com.suntiago.sunandroidframe.R;
import com.suntiago.sunandroidframe.util.Util;
import com.suntiago.sunandroidframe.adapter.FaceAdapter;
import com.suntiago.sunandroidframe.adapter.FacePageAdeapter;
import com.suntiago.sunandroidframe.adapter.MessageAdapter;
import com.suntiago.sunandroidframe.view.CirclePageIndicator;
import com.suntiago.sunandroidframe.view.JazzyViewPager;
import com.suntiago.sunandroidframe.view.JazzyViewPager.TransitionEffect;
import com.suntiago.sunandroidframe.view.MsgListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.rong.imlib.RongIMClient;

import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by yu.zai on 2016/4/5.
 */
public class ChatActivity extends Activity implements  View.OnTouchListener, View.OnClickListener, MsgListView.IXListViewListener, MessageAdapter.ScrotoBottom {
    public static final int FACE_NUM_PAGE = 6;
    public static final int FACE_NUM = 20;
    private static final String TAG = "ChatActivity";
    private static final int VOICE_LENGTH = 15;
    private static final int VOICE_LENGTH_END = 5;

    private static final int MSG_NORMAL = 0;
    private static final int MSG_SEC = 1;
    private static final int MSG_REC = 6;
    private static final int MSG_CANCEL = 2;
    private static final int MSG_SHORT = 7;
    private static final int MSG_SAMPLING = 3;
    private static final int MSG_COMPLETE = 5;
    private static final int MSG_READY = 4;

    public static final int STATUS_SHORT = 7;
    private MediaRecorder mMediaRecorder;
    private AudioManager mAudioManager;
    private BaseHandler mHandler;
    private float lastTouchY;
    private float mOffsetLimit;
    private int mStatus;
    private long mVoiceLength;
    private ViewHolder mViewHolder;
    private Context mContext;
    private Uri mCurrentRecUri;
    MessageAdapter mMessageAdapter;
    private List<Integer> faceList = new ArrayList<Integer>();// 存放表情资源的list
//    private LinearLayout mllFace;// 表情显示的布局
//    private JazzyViewPager mFaceViewPager;// 表情viewpager
    private int mCurrentPage = 0;// 表情页数
    private List<String> mKeyList;// 表情list
    private List<View> lvFace = new ArrayList<View>();
    /**
     * 表情viewPager切换效果
     */
    private static TransitionEffect mEffects[] = { TransitionEffect.Standard,
            TransitionEffect.Tablet, TransitionEffect.CubeIn,
            TransitionEffect.CubeOut, TransitionEffect.FlipVertical,
            TransitionEffect.FlipHorizontal, TransitionEffect.Stack,
            TransitionEffect.ZoomIn, TransitionEffect.ZoomOut,
            TransitionEffect.RotateUp, TransitionEffect.RotateDown,
            TransitionEffect.Accordion, };// 表情翻页效果
    private int defaultCount;

    private InputMethodManager mInputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zf_chat_main);
        Log.d(TAG, "onCreate");
        mContext = this;
        initData();
        initView();
    }

    private void initView() {
        if (mViewHolder == null) {
            mViewHolder = new ViewHolder();
            mViewHolder.rcChatPop = (FrameLayout) findViewById(R.id.rcChat_popup);

            mViewHolder.conversationListview = (ListView) findViewById(R.id.conversation_list);

            mViewHolder.msgListview = (MsgListView) findViewById(R.id.msg_listView);
//            mViewHolder.msgListview = (ListView) findViewById(R.id.msg_listView);
            mViewHolder.msgListview.setAdapter(mMessageAdapter);
            scrollToBottomListItem();
            mViewHolder.msgListview.setOnTouchListener(this);
            mViewHolder.msgListview.setPullLoadEnable(false);
            mViewHolder.msgListview.setXListViewListener(this);

            mViewHolder.chatMainText = (LinearLayout) findViewById(R.id.ll_chatmain_input);

            mViewHolder.textButtonswitch = (ImageButton) findViewById(R.id.ib_chatmain_msg);
            mViewHolder.textButtonswitch.setOnClickListener(this);
            mViewHolder.voiceButtonswitch = (ImageButton) findViewById(R.id.ib_chatmain_voice);
            mViewHolder.voiceButtonswitch.setOnClickListener(this);


            mViewHolder.faceButton = (ImageButton) findViewById(R.id.face_btn);
            mViewHolder.faceButton.setOnClickListener(this);

            mViewHolder.inputText = (EditText) findViewById(R.id.msg_et);
            mViewHolder.inputText.setText(loadDraft());
            mViewHolder.inputText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s == null) {
                        saveDraft("");
                        showSendOrExtraButton("");
                    } else {
                        saveDraft(s.toString());
                        showSendOrExtraButton(s.toString());
                    }
                }
            });

            mViewHolder.sendLayout = (RelativeLayout) findViewById(R.id.send_layout);
            mViewHolder.sendTextButton = (Button) findViewById(R.id.send_btn);
            mViewHolder.sendTextButton.setOnClickListener(this);

            mViewHolder.showExtraButton = (Button) findViewById(R.id.btn_chat_affix);
            mViewHolder.showExtraButton.setOnClickListener(this);

            mViewHolder.chatMainVoice = (LinearLayout) findViewById(R.id.ll_chatmain_voice);

            //发送语音 按钮
            mViewHolder.inputVoice = (TextView) findViewById(R.id.tv_chatmain_press_voice);
            mViewHolder.inputVoice.setOnTouchListener(this);

            mViewHolder.extraPanelLl = (LinearLayout) findViewById(R.id.ll_chatmain_affix);


            mViewHolder.takePicture = (TextView) findViewById(R.id.tv_chatmain_affix_take_picture);
            mViewHolder.album = (TextView) findViewById(R.id.tv_chatmain_affix_album);

            mViewHolder.faceLl = (LinearLayout) findViewById(R.id.face_ll);
            mViewHolder.faceViewPager = (JazzyViewPager) findViewById(R.id.face_pager);
            initFacePage();
        }

    }

    public void initData() {
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mHandler = new BaseHandler(this);

        this.mOffsetLimit = 70.0F * getResources().getDisplayMetrics().density;
        this.mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        Set<String> keySet = FaceMap.getInstance().getFaceMap()
                .keySet();
        mKeyList = new ArrayList<String>();
        mKeyList.addAll(keySet);
        mMessageAdapter = new MessageAdapter(mContext, Conversation.ConversationType.GROUP, "10000");
        mMessageAdapter.setScrotoBottom(this);
        initDataFace();

    }

    private void initDataFace() {
        Map<String, Integer> mFaceMap = FaceMap.getInstance().getFaceMap();
        int mCount = 0;
        for (Map.Entry<String, Integer> entry : mFaceMap.entrySet()) {
            // 此处只显示5张透明背景的表情
            if (mCount >= 0 && mCount <= 4) {
                int id = getPngFace(entry);
                if (id != 0) {
                    faceList.add(id);
                    mCount++;
                }

            } else {
                faceList.add(entry.getValue());
            }

        }
    }

    /**
     * 获取png的表情资源
     *
     * @param entry
     * @return
     */
    private int getPngFace(Map.Entry<String, Integer> entry) {
        String strName = getString(entry.getValue());
        String newName = strName.substring(strName.lastIndexOf("/") + 1,
                strName.lastIndexOf("."));
        newName = "d" + newName;
        int id = 0;
        // ===需要png的表情，来替换之后就可以gridview中item中的表情背景不是白色的了
        Field field;
        try {
            field = R.drawable.class.getDeclaredField(newName);
            id = Integer.parseInt(field.get(null).toString());
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume" +
                "");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mInputMethodManager = null;
        if (mHandler != null) {
            mHandler.removeMessages(MSG_SAMPLING);
            mHandler = null;
        }
        mAudioManager = null;
        if (mKeyList != null) {
            mKeyList.clear();
            mKeyList = null;
        }
        if (faceList != null) {
            faceList.clear();
            faceList = null;
        }

        if (lvFace != null) {
            lvFace.clear();
            lvFace = null;
        }

        if (mMessageAdapter != null) {
            mMessageAdapter.setScrotoBottom(null);
            mMessageAdapter = null;
        }
        if (mViewHolder != null) {
            mViewHolder.setNull();
            mViewHolder = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }
    public static class BaseHandler extends Handler {
        final WeakReference<ChatActivity> mActivityReference;

        BaseHandler(ChatActivity activity) {
            mActivityReference= new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            ChatActivity activity = mActivityReference.get();
            activity.handleMsg(msg);
        }
    }

    public boolean handleMsg(Message msg) {
        switch(msg.what) {
            case MSG_SEC:
                mViewHolder.popText.setVisibility(View.VISIBLE);
                mViewHolder.popText.setText(msg.arg1 + "s");
                if (msg.arg1 > 0) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_SEC, --msg.arg1, 0), 1000L);
                } else {
                    this.mHandler.sendEmptyMessage(MSG_COMPLETE);
                }
                break;
            case MSG_CANCEL:
                mViewHolder.popMessage.setText(R.string.rc_voice_cancel);
                mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_cancel);
                mViewHolder.popMessage.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                this.mStatus = 2;
                break;
            case MSG_SAMPLING:
                if(this.mStatus != 2 && this.mStatus != 7) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_SAMPLING), 150L);
                    int var4 = this.getCurrentVoiceDb();
                    switch(var4 / 5) {
                        case 0:
                            mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_1);
                            break;
                        case 1:
                            mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_2);
                            break;
                        case 2:
                            mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_3);
                            break;
                        case 3:
                            mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_4);
                            break;
                        case 4:
                            mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_5);
                            break;
                        case 5:
                            mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_6);
                            break;
                        case 6:
                            mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_7);
                            break;
                        default:
                            mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_8);
                    }
                }
                break;
            case MSG_READY:
                if(mViewHolder.mPopWindow == null) {
                    View db = this.getLayoutInflater().inflate(R.layout.rc_wi_vo_popup, null);
                    mViewHolder.popIcon = (ImageView)db.findViewById(android.R.id.icon);
                    mViewHolder.popText = (TextView)db.findViewById(android.R.id.text1);
                    mViewHolder.popMessage = (TextView)db.findViewById(android.R.id.message);
                    mViewHolder.mPopWindow = new PopupWindow(db, -1, -1);
                    View parent = (View)msg.obj;
                    mViewHolder.mPopWindow.showAtLocation(parent, 17, 0, 0);
                    mViewHolder.mPopWindow.setFocusable(true);
                    mViewHolder.mPopWindow.setOutsideTouchable(false);
                    mViewHolder.mPopWindow.setTouchable(false);
                }

                this.startRec();
                this.mStatus = 4;
                this.mVoiceLength = SystemClock.elapsedRealtime();
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_SEC, VOICE_LENGTH_END, 0), (VOICE_LENGTH - VOICE_LENGTH_END) * 1000L);
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_SAMPLING), 150L);
                mViewHolder.popMessage.setText(R.string.rc_voice_rec);
                mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_1);
                mViewHolder.popMessage.setBackgroundColor(0);
                mViewHolder.popText.setVisibility(View.GONE);
                break;
            case MSG_COMPLETE:
                this.mHandler.removeMessages(MSG_READY);
                this.mHandler.removeMessages(MSG_SEC);
                this.mHandler.removeMessages(MSG_CANCEL);
                if(mViewHolder.mPopWindow != null && mViewHolder.mPopWindow.isShowing()) {
                    mViewHolder.mPopWindow.dismiss();
                    mViewHolder.mPopWindow = null;
                }

                if(this.mStatus == 2) {
                    this.stopRec(false);
                } else if(this.mStatus == 7) {
                    mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_wraning);
                    mViewHolder.popMessage.setText(R.string.rc_voice_short);
                    this.stopRec(false);
                } else {
                    this.stopRec(true);
                }
                break;
            case MSG_REC:
                if(this.mStatus == 2) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_SAMPLING), 150L);
                }
                this.mStatus = 6;
                mViewHolder.popMessage.setText(R.string.rc_voice_rec);
                mViewHolder.popIcon.setImageResource(R.drawable.rc_ic_volume_1);
                mViewHolder.popMessage.setBackgroundColor(0);
            case 8:
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.tv_chatmain_press_voice) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            this.onActive(v.getContext());
                this.lastTouchY = event.getY();
                this.mHandler.obtainMessage(MSG_READY, v.getRootView()).sendToTarget();
//            mViewHolder.inputVoice.setBackgroundResource(io.rong.imkit.R.drawable.rc_btn_voice_hover);
            } else if (event.getAction() != MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_CANCEL) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (this.lastTouchY - event.getY() > this.mOffsetLimit) {
                        this.mHandler.obtainMessage(MSG_CANCEL).sendToTarget();
                    } else {
                        this.mHandler.obtainMessage(MSG_REC).sendToTarget();
                    }
                }
            } else {
//            mViewHolder.inputVoice.setBackgroundResource(io.rong.imkit.R.drawable.rc_btn_voice_normal);
                if (event.getEventTime() - event.getDownTime() < 1000L) {
                    this.mStatus = STATUS_SHORT;
                }
                this.mHandler.obtainMessage(MSG_COMPLETE).sendToTarget();
            }
        } else if (v.getId() == R.id.msg_listView || v.getId() == R.id.msg_et) {
                mInputMethodManager.hideSoftInputFromWindow(
                        mViewHolder.inputText.getWindowToken(), 0);
                mViewHolder.faceLl.setVisibility(View.GONE);
//                isFaceShow = false;
            return false;
        } else if (v.getId() == R.id.msg_et) {
                mInputMethodManager.showSoftInput(mViewHolder.inputText, 0);
                mViewHolder.faceLl.setVisibility(View.GONE);
//                isFaceShow = false;
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_chatmain_msg:
                switchToVoice();
                break;
            case R.id.ib_chatmain_voice:
                switchToMsg();
                break;
            case R.id.send_btn:
                sendTextMsg();
                break;
            case R.id.face_btn:
                int vi = mViewHolder.faceLl.getVisibility();
                mViewHolder.extraPanelLl.setVisibility(View.GONE);
                mViewHolder.faceLl.setVisibility(vi==View.VISIBLE?View.GONE:View.VISIBLE);
                break;
            case R.id.btn_chat_affix:
                int vii = mViewHolder.extraPanelLl.getVisibility();
                mViewHolder.faceLl.setVisibility(View.GONE);
                mViewHolder.extraPanelLl.setVisibility(vii==View.VISIBLE?View.GONE:View.VISIBLE);
                break;
        }
    }

    private void switchToMsg() {
        mViewHolder.chatMainVoice.setVisibility(View.GONE);
        mViewHolder.chatMainText.setVisibility(View.VISIBLE);
    }

    private void switchToVoice() {
        mViewHolder.chatMainVoice.setVisibility(View.VISIBLE);
        mViewHolder.chatMainText.setVisibility(View.GONE);
    }

    private void saveDraft(String s) {
        // TODO: 2016/4/7 add saving draft operations
    }

    private String loadDraft() {
        // TODO: 2016/4/7 add load draft form db operations
        return "";
    }

    private void showSendOrExtraButton(String s) {
        if (Util.isEmpty(s)) {
            mViewHolder.sendTextButton.setVisibility(View.GONE);
            mViewHolder.showExtraButton.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.sendTextButton.setVisibility(View.VISIBLE);
            mViewHolder.showExtraButton.setVisibility(View.GONE);
        }
    }

    private void sendTextMsg () {
        // TODO: 2016/4/7 add sendTextMsg

        String msg = mViewHolder.inputText.getText().toString();
        if (Util.isEmpty(msg)) {
            return;
        }
        TextMessage textMessage = TextMessage.obtain(msg);

        io.rong.imlib.model.Message message =
                io.rong.imlib.model.Message.obtain(
                        "10000",
                        Conversation.ConversationType.GROUP,
                        textMessage);

        if (RongIMClient.getInstance()!= null) {

            String pushCntent = "";
            if (mContext != null) {
//                pushCntent = mContext.getString(R.string.you_have_a_new_text_msg);
            }
            RongIMClient.getInstance().sendMessage(message, pushCntent, null, null, new RongIMClient.ResultCallback<io.rong.imlib.model.Message>() {
                @Override
                public void onSuccess(io.rong.imlib.model.Message message) {
                    Log.d(TAG, "send text onSuccess");
                    io.rong.imlib.model.Message.ReceivedStatus status = message.getReceivedStatus();
                    status.setListened();
                    RongIMClient.getInstance().setMessageReceivedStatus(message.getMessageId(), status);
                    Toast.makeText(mContext, "send text onSuccess", Toast.LENGTH_LONG).show();
                    mViewHolder.inputText.setText("");
                    refresh();
                }

                public void onError(RongIMClient.ErrorCode e) {
                }
            });
        }
    }

    private void sendVoiceMsg () {
        // TODO: 2016/4/7 add sendVoiceMsg
        if (this.mCurrentRecUri != null) {
            VoiceMessage voiceMessage = VoiceMessage.obtain(this.mCurrentRecUri, (int) (SystemClock.elapsedRealtime() - this.mVoiceLength) / 1000);
            io.rong.imlib.model.Message message =
                    io.rong.imlib.model.Message.obtain(
                            "10000",
                            Conversation.ConversationType.GROUP,
                            voiceMessage);

            if (RongIMClient.getInstance()!= null) {

                String pushCntent = "";
                if (mContext != null) {
//                pushCntent = mContext.getString(R.string.you_have_a_new_text_msg);
                }
                RongIMClient.getInstance().sendMessage(message, pushCntent, null, null, new RongIMClient.ResultCallback<io.rong.imlib.model.Message>() {
                    @Override
                    public void onSuccess(io.rong.imlib.model.Message message) {
                        Toast.makeText(mContext, "send Voice onSuccess", Toast.LENGTH_LONG).show();
                        io.rong.imlib.model.Message.ReceivedStatus status = message.getReceivedStatus();
                        status.setListened();
                        RongIMClient.getInstance().setMessageReceivedStatus(message.getMessageId(), status);
                        refresh();
                    }
                    public void onError(RongIMClient.ErrorCode e) {
                    }
                });
            }
        }
    }

    public void refresh() {
        if (mMessageAdapter != null) {
            mMessageAdapter.refreshMessage();
        }
    }
    private void startRec() {
//        RongContext.getInstance().getEventBus().post(VoiceInputOperationEvent.obtain(VoiceInputOperationEvent.STATUS_INPUTING));
        this.mAudioManager.setMode(AudioManager.MODE_NORMAL);

        try {
            this.mMediaRecorder = new MediaRecorder();

            try {
                int e = mContext.getResources().getInteger(R.integer.rc_audio_encoding_bit_rate);
                this.mMediaRecorder.setAudioSamplingRate(8000);
                this.mMediaRecorder.setAudioEncodingBitRate(e);
            } catch (Resources.NotFoundException var2) {
                var2.printStackTrace();
            }

            this.mMediaRecorder.setAudioChannels(1);
            this.mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            this.mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            this.mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            this.mCurrentRecUri = Uri.fromFile(new File(mContext.getCacheDir(), System.currentTimeMillis() + "temp.voice"));
            this.mMediaRecorder.setOutputFile(this.mCurrentRecUri.getPath());
            this.mMediaRecorder.prepare();
            this.mMediaRecorder.start();
        } catch (RuntimeException var3) {
            if(this.mMediaRecorder != null) {
                this.mMediaRecorder.reset();
                this.mMediaRecorder.release();
            }

            this.mMediaRecorder = null;
            var3.printStackTrace();
        } catch (IOException var4) {
            this.mMediaRecorder.reset();
            this.mMediaRecorder.release();
            this.mMediaRecorder = null;
            var4.printStackTrace();
        }

        this.mStatus = 4;
    }

    private void stopRec(boolean save) {
        boolean isError = false;
        if (this.mMediaRecorder != null) {
//            RongContext.getInstance().getEventBus().post(VoiceInputOperationEvent.obtain(VoiceInputOperationEvent.STATUS_INPUT_COMPLETE));

            try {
                this.mMediaRecorder.stop();
                this.mMediaRecorder.release();
                this.mMediaRecorder = null;
            } catch (RuntimeException var19) {
                var19.printStackTrace();
            }

            if (!save) {
                File length = new File(this.mCurrentRecUri.getPath());
                if(length.exists()) {
                    length.delete();
                }

                this.mCurrentRecUri = null;
            } else {
                int length1 = (int)((SystemClock.elapsedRealtime() - this.mVoiceLength) / 1000L + 400L);
                if (length1 == 400) {
                    return;
                }

                File file = new File(this.mCurrentRecUri.getPath());

                if (!file.exists()) {
                    return;
                }

                MediaPlayer player = new MediaPlayer();

                try {
                    FileInputStream e = new FileInputStream(file);
                    player.setDataSource(e.getFD());
                    player.prepare();
                } catch (IllegalArgumentException | IOException | IllegalStateException | SecurityException var14) {
                    isError = true;
                    var14.printStackTrace();
                } finally {
                    player.stop();
                    player.release();
                }

                if (isError) {
                    Toast.makeText(mContext, getResources().getString(R.string.rc_voice_failure), Toast.LENGTH_SHORT).show();
                    return;
                }
                sendVoiceMsg();

            }

            this.mStatus = 0;
        }
    }


    private int getCurrentVoiceDb() {
        return this.mMediaRecorder == null?0:this.mMediaRecorder.getMaxAmplitude() / 600;
    }


    /**
     * 表情viwepager
     */
    private void initFacePage() {
//        List<View> lvFace = new ArrayList<View>();
        for (int i = 0; i < FACE_NUM_PAGE; ++i) {
            lvFace.add(getGridView(i));
        }
        FacePageAdeapter adapter = new FacePageAdeapter(lvFace, mViewHolder.faceViewPager);
        mViewHolder.faceViewPager.setAdapter(adapter);
        mViewHolder.faceViewPager.setCurrentItem(mCurrentPage);
//        mFaceViewPager.setTransitionEffect(mEffects[mSpUtil.getFaceEffect()]);// 效果
        mViewHolder.faceViewPager.setTransitionEffect(mEffects[0]);// 效果 默认效果
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);// 圆点
        indicator.setViewPager(mViewHolder.faceViewPager);
        adapter.notifyDataSetChanged();
        mViewHolder.faceLl.setVisibility(View.GONE);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mCurrentPage = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // do nothing
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // do nothing
            }
        });

    }

    /**
     * 获取表情GridView
     *
     * @param i
     * @return
     */
    private GridView getGridView(int i) {
        GridView gv = new GridView(this);
        gv.setNumColumns(7);
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
        gv.setBackgroundColor(Color.TRANSPARENT);
        gv.setCacheColorHint(Color.TRANSPARENT);
        gv.setHorizontalSpacing(1);
        gv.setVerticalSpacing(1);
        gv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        gv.setGravity(Gravity.CENTER);

        gv.setAdapter(new FaceAdapter(this, i, faceList));

        gv.setOnTouchListener(forbidenScroll());
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                if (position == FACE_NUM) {// 删除键的位置
                    int selection = mViewHolder.inputText.getSelectionStart();
                    String text = mViewHolder.inputText.getText().toString();
                    if (selection > 0) {
                        String text2 = text.substring(selection - 1);
                        if ("]".equals(text2)) {
                            int start = text.lastIndexOf("[");
                            int end = selection;
                            mViewHolder.inputText.getText().delete(start, end);
                            return;
                        }
                        mViewHolder.inputText.getText().delete(selection - 1, selection);
                    }
                } else {// 选择表情==
                    int count = mCurrentPage * FACE_NUM + position;
                    defaultCount = count;
                    // 注释的部分，在EditText中显示字符串
                    // String ori = msgEt.getText().toString();
                    // int index = msgEt.getSelectionStart();
                    // StringBuilder stringBuilder = new StringBuilder(ori);
                    // stringBuilder.insert(index, keys.get(count));
                    // msgEt.setText(stringBuilder.toString());
                    // msgEt.setSelection(index + keys.get(count).length());

                    // 下面这部分，在EditText中显示表情
                    Bitmap bitmap = BitmapFactory.decodeResource(
                            getResources(), (Integer) FaceMap
                                    .getInstance().getFaceMap().values()
                                    .toArray()[count]);
                    if (bitmap != null) {
                        int rawHeigh = bitmap.getHeight();
                        int rawWidth = bitmap.getHeight();
                        // 设置表情的大小===
                        int newHeight = Util.dip2px(mContext, 30);
                        int newWidth = Util.dip2px(mContext, 30);
                        // 计算缩放因子
                        float heightScale = ((float) newHeight) / rawHeigh;
                        float widthScale = ((float) newWidth) / rawWidth;
                        // 新建立矩阵
                        Matrix matrix = new Matrix();
                        matrix.postScale(heightScale, widthScale);
                        // 设置图片的旋转角度
                        // matrix.postRotate(-30);
                        // 设置图片的倾斜
                        // matrix.postSkew(0.1f, 0.1f);
                        // 将图片大小压缩
                        // 压缩后图片的宽和高以及kB大小均会变化
                        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                rawWidth, rawHeigh, matrix, true);
                        ImageSpan imageSpan = new ImageSpan(mContext,
                                newBitmap);
                        String emojiStr = mKeyList.get(count);
                        SpannableString spannableString = new SpannableString(
                                emojiStr);
                        spannableString.setSpan(imageSpan,
                                emojiStr.indexOf('['),
                                emojiStr.indexOf(']') + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mViewHolder.inputText.append(spannableString);
                    } else {
                        String ori = mViewHolder.inputText.getText().toString();
                        int index = mViewHolder.inputText.getSelectionStart();
                        StringBuilder stringBuilder = new StringBuilder(ori);
                        stringBuilder.insert(index, mKeyList.get(count));
                        mViewHolder.inputText.setText(stringBuilder.toString());
                        mViewHolder.inputText.setSelection(index
                                + mKeyList.get(count).length());
                    }
//                    if (bitmap != null) {
//                        bitmap.recycle();
//                    }
                }
            }
        });
        return gv;
    }

    // 防止乱pageview乱滚动
    private View.OnTouchListener forbidenScroll() {
        return new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
    /**
     * @Description 滑动到列表底部
     */
    private void scrollToBottomListItem() {
        // todo eric, why use the last one index + 2 can real scroll to the
        // bottom?
        if (mViewHolder.msgListview != null && mMessageAdapter!= null) {
            mViewHolder.msgListview.setSelection(mMessageAdapter.getCount() + 1);
        }
    }

    @Override
    public void scroToBottom() {
        scrollToBottomListItem();
    }

    class ViewHolder {
        public ViewHolder() {
        }
        FrameLayout rcChatPop;//录音提示框
        PopupWindow mPopWindow;
        ImageView popIcon;
        TextView popText;
        TextView popMessage;
        ListView conversationListview;
        MsgListView msgListview;
//        ListView msgListview;
        LinearLayout chatMainText;
        ImageButton textButtonswitch;
        ImageButton faceButton;
        EditText inputText;
        RelativeLayout sendLayout;
        Button sendTextButton;
        Button showExtraButton;
        LinearLayout chatMainVoice;
        ImageButton voiceButtonswitch;
        TextView inputVoice;

        //拓展，传照片等
        LinearLayout extraPanelLl;
        TextView takePicture;
        TextView album;


        //表情
        LinearLayout faceLl;
        JazzyViewPager faceViewPager;
//        CirclePageIndicator indicator;
        public void setNull() {
            this.rcChatPop = null;
            this.mPopWindow = null;
            this.popIcon = null;
            this.popText = null;
            this.popMessage = null;
            this.conversationListview = null;
//        MsgListView msgListview;
            this.msgListview = null;
            this.chatMainText = null;
            this.textButtonswitch = null;
            this.faceButton = null;
            this.inputText = null;
            this.sendLayout = null;
            this.sendTextButton = null;
            this.showExtraButton = null;
            this.chatMainVoice = null;
            this.voiceButtonswitch = null;
            this.inputVoice = null;

            //拓展，传照片等
            this.extraPanelLl = null;
            this.takePicture = null;
            this.album = null;


            //表情
            this.faceLl = null;
            this.faceViewPager = null;
        }
    }

}
