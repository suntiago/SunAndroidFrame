package com.suntiago.sunandroidframe.entity;

import android.os.Parcel;
import android.os.Parcelable;

import io.rong.common.ParcelUtils;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * Created by yu.zai on 2016/4/8.
 */
public class RongMessage implements Parcelable {
        private Conversation.ConversationType conversationType;
        private String targetId;
        private int messageId;
        private RongMessage.MessageDirection messageDirection;
        private String senderUserId;
        private RongMessage.ReceivedStatus receivedStatus;
        private RongMessage.SentStatus sentStatus;
        private long receivedTime;
        private long sentTime;
        private String objectName;
        private MessageContent content;
        private String extra;
        public static final Creator<RongMessage> CREATOR = new Creator() {
            public RongMessage createFromParcel(Parcel source) {
                return new RongMessage(source);
            }

            public RongMessage[] newArray(int size) {
                return new RongMessage[size];
            }
        };

        public RongMessage() {
        }

        public RongMessage(io.rong.imlib.NativeObject.Message msg) {
            this.conversationType = Conversation.ConversationType.setValue(msg.getConversationType());
            this.targetId = msg.getTargetId();
            this.messageId = msg.getMessageId();
            this.messageDirection = !msg.getMessageDirection()? RongMessage.MessageDirection.SEND: RongMessage.MessageDirection.RECEIVE;
            this.senderUserId = msg.getSenderUserId();
            this.receivedStatus = new RongMessage.ReceivedStatus(msg.getReadStatus());
            this.sentStatus = RongMessage.SentStatus.setValue(msg.getSentStatus());
            this.receivedTime = msg.getReceivedTime();
            this.sentTime = msg.getSentTime();
            this.objectName = msg.getObjectName();
            this.extra = msg.getExtra();
        }

        public static RongMessage obtain(String targetId, Conversation.ConversationType type, MessageContent content) {
            RongMessage obj = new RongMessage();
            obj.setTargetId(targetId);
            obj.setConversationType(type);
            obj.setContent(content);
            return obj;
        }

        public Conversation.ConversationType getConversationType() {
            return this.conversationType;
        }

        public void setConversationType(Conversation.ConversationType conversationType) {
            this.conversationType = conversationType;
        }

        public String getTargetId() {
            return this.targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public int getMessageId() {
            return this.messageId;
        }

        public void setMessageId(int messageId) {
            this.messageId = messageId;
        }

        public RongMessage.MessageDirection getMessageDirection() {
            return this.messageDirection;
        }

        public void setMessageDirection(RongMessage.MessageDirection messageDirection) {
            this.messageDirection = messageDirection;
        }

        public RongMessage.ReceivedStatus getReceivedStatus() {
            return this.receivedStatus;
        }

        public void setReceivedStatus(RongMessage.ReceivedStatus receivedStatus) {
            this.receivedStatus = receivedStatus;
        }

        public RongMessage.SentStatus getSentStatus() {
            return this.sentStatus;
        }

        public void setSentStatus(RongMessage.SentStatus sentStatus) {
            this.sentStatus = sentStatus;
        }

        public long getReceivedTime() {
            return this.receivedTime;
        }

        public void setReceivedTime(long receivedTime) {
            this.receivedTime = receivedTime;
        }

        public long getSentTime() {
            return this.sentTime;
        }

        public void setSentTime(long sentTime) {
            this.sentTime = sentTime;
        }

        public String getObjectName() {
            return this.objectName;
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public MessageContent getContent() {
            return this.content;
        }

        public void setContent(MessageContent content) {
            this.content = content;
        }

        public String getExtra() {
            return this.extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String getSenderUserId() {
            return this.senderUserId;
        }

        public void setSenderUserId(String senderUserId) {
            this.senderUserId = senderUserId;
        }

        public int describeContents() {
            return 0;
        }

        public RongMessage(Parcel in) {
            String className = ParcelUtils.readFromParcel(in);
            Class loader = null;

            try {
                loader = Class.forName(className);
            } catch (ClassNotFoundException var5) {
                var5.printStackTrace();
            }

            this.setTargetId(ParcelUtils.readFromParcel(in));
            this.setMessageId(ParcelUtils.readIntFromParcel(in).intValue());
            this.setSenderUserId(ParcelUtils.readFromParcel(in));
            this.setReceivedTime(ParcelUtils.readLongFromParcel(in).longValue());
            this.setSentTime(ParcelUtils.readLongFromParcel(in).longValue());
            this.setObjectName(ParcelUtils.readFromParcel(in));
            this.setContent((MessageContent)ParcelUtils.readFromParcel(in, loader));
            this.setExtra(ParcelUtils.readFromParcel(in));
            this.setConversationType(Conversation.ConversationType.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
            this.setMessageDirection(RongMessage.MessageDirection.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
            this.setReceivedStatus(new RongMessage.ReceivedStatus(ParcelUtils.readIntFromParcel(in).intValue()));
            this.setSentStatus(RongMessage.SentStatus.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
        }

        public void writeToParcel(Parcel dest, int flags) {
            ParcelUtils.writeToParcel(dest, this.getContent().getClass().getName());
            ParcelUtils.writeToParcel(dest, this.getTargetId());
            ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getMessageId()));
            ParcelUtils.writeToParcel(dest, this.getSenderUserId());
            ParcelUtils.writeToParcel(dest, Long.valueOf(this.getReceivedTime()));
            ParcelUtils.writeToParcel(dest, Long.valueOf(this.getSentTime()));
            ParcelUtils.writeToParcel(dest, this.getObjectName());
            ParcelUtils.writeToParcel(dest, this.getContent());
            ParcelUtils.writeToParcel(dest, this.getExtra());
            ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getConversationType().getValue()));
            ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getMessageDirection() == null?0:this.getMessageDirection().getValue()));
            ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getReceivedStatus() == null?0:this.getReceivedStatus().getFlag()));
            ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getSentStatus() == null?0:this.getSentStatus().getValue()));
        }

        public boolean equals(Object o) {
            return o == null?false:(o instanceof RongMessage ?this.messageId == ((RongMessage)o).getMessageId():super.equals(o));
        }

        public static class ReceivedStatus {
            private static final int READ = 1;
            private static final int LISTENED = 2;
            private static final int DOWNLOADED = 4;
            private int flag = 0;
            private boolean isRead = false;
            private boolean isListened = false;
            private boolean isDownload = false;

            public ReceivedStatus(int flag) {
                this.flag = flag;
                this.isRead = (flag & 1) == 1;
                this.isListened = (flag & 2) == 2;
                this.isDownload = (flag & 4) == 4;
            }

            public int getFlag() {
                return this.flag;
            }

            public boolean isRead() {
                return this.isRead;
            }

            public boolean isListened() {
                return this.isListened;
            }

            public void setListened() {
                this.flag |= 2;
                this.isListened = true;
            }

            public void setRead() {
                this.flag |= 1;
                this.isRead = true;
            }

            public boolean isDownload() {
                return this.isDownload;
            }

            public void setDownload() {
                this.flag |= 4;
                this.isDownload = true;
            }
        }

        public static enum SentStatus {
            SENDING(10),
            FAILED(20),
            SENT(30),
            RECEIVED(40),
            READ(50),
            DESTROYED(60);

            private int value = 1;

            private SentStatus(int value) {
                this.value = value;
            }

            public int getValue() {
                return this.value;
            }

            public static RongMessage.SentStatus setValue(int code) {
                RongMessage.SentStatus[] arr$ = values();
                int len$ = arr$.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    RongMessage.SentStatus c = arr$[i$];
                    if(code == c.getValue()) {
                        return c;
                    }
                }

                return SENDING;
            }
        }

        public static enum MessageDirection {
            SEND(1),
            RECEIVE(2);

            private int value = 1;

            private MessageDirection(int value) {
                this.value = value;
            }

            public int getValue() {
                return this.value;
            }

            public static RongMessage.MessageDirection setValue(int code) {
                RongMessage.MessageDirection[] arr$ = values();
                int len$ = arr$.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    RongMessage.MessageDirection c = arr$[i$];
                    if(code == c.getValue()) {
                        return c;
                    }
                }

                return SEND;
            }
        }
}
