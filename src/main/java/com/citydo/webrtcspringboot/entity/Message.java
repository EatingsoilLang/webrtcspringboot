package com.citydo.webrtcspringboot.entity;


import java.util.Arrays;
import java.util.Objects;

public class Message {

    /**
     * 创建房间
     */
    public static final String TYPE_COMMAND_ROOM_ENTER = "enterRoom";
    /**
     * 获取房间
     */
    public static final String TYPE_COMMAND_ROOM_LIST = "roomList";
    /**
     * 对话
     */
    public static final String TYPE_COMMAND_DIALOGUE = "dialogue";
    /**
     * 图片
     */
    public static final String TYPE_COMMAND_IMG = "img";
    /**
     * 准备
     */
    public static final String TYPE_COMMAND_READY = "ready";
    /**
     * 离开
     */
    public static final String TYPE_COMMAND_OFFER = "offer";
    /**
     * 回答
     */
    public static final String TYPE_COMMAND_ANSWER = "answer";
    /**
     * 申请人
     */
    public static final String TYPE_COMMAND_CANDIDATE = "candidate";

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message message1 = (Message) o;
        return Objects.equals(getCommand(), message1.getCommand()) &&
                Objects.equals(getUserId(), message1.getUserId()) &&
                Objects.equals(getRoomId(), message1.getRoomId()) &&
                Objects.equals(getMessage(), message1.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommand(), getUserId(), getRoomId(), getMessage());
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public byte[] getImgMessage() {
        return imgMessage;
    }

    public void setImgMessage(byte[] imgMessage) {
        this.imgMessage = imgMessage;
    }

    @Override
    public String toString() {
        return "Message{" +
                "command='" + command + '\'' +
                ", userId='" + userId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", message='" + message + '\'' +
                ", imgMessage=" + Arrays.toString(imgMessage) +
                '}';
    }

    private String command;
    private String userId;
    private String roomId;
    private String message;
    private byte[] imgMessage;
}
