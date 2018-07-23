package rmk.virtusa.com.quizmaster.model;

import java.util.Date;

public class Chat {

    private String senderUid;

    private boolean isMedia;

    private int mediaType;

    private String chat;

    private Date sentTime;

    private Date receivedTime;

    public Chat() {

    }

    public Chat(String senderUid, boolean isMedia, String chat, Date sentTime, Date receivedTime) {
        this.sentTime = sentTime;
        this.isMedia = isMedia;
        this.receivedTime = receivedTime;
        this.chat = chat;
        this.senderUid = senderUid;
    }

    public Chat(String senderUid, boolean isMedia, int mediaType, String chat, Date sentTime, Date receivedTime) {
        this.sentTime = sentTime;
        this.isMedia = isMedia;
        this.mediaType = mediaType;
        this.receivedTime = receivedTime;
        this.chat = chat;
        this.senderUid = senderUid;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getSenderUid() {
        return this.senderUid;
    }


    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }


    public boolean getIsMedia() {
        return this.isMedia;
    }


    public void setIsMedia(Boolean isMedia) {
        this.isMedia = isMedia;
    }


    public String getChat() {
        return this.chat;
    }


    public void setChat(String chat) {
        this.chat = chat;
    }


    public Date getSentTime() {
        return this.sentTime;
    }


    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }


    public Date getReceivedTime() {
        return this.receivedTime;
    }


    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }


}
