package rmk.virtusa.com.quizmaster.model;

import java.util.Date;

public class Chat {

    private String chatId;

    private boolean isMedia;

    private String chat;

    private Date sentTime;

    private Date receivedTime;

    public Chat() {

    }

    public Chat(String chatId, boolean isMedia, String chat, Date sentTime, Date receivedTime) {
        this.sentTime = sentTime;
        this.isMedia = isMedia;
        this.receivedTime = receivedTime;
        this.chat = chat;
        this.chatId = chatId;
    }


    public String getChatId() {
        return this.chatId;
    }


    public void setChatId(String chatId) {
        this.chatId = chatId;
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
