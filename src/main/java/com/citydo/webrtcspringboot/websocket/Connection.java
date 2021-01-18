package com.citydo.webrtcspringboot.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.citydo.webrtcspringboot.config.ConfiguratorForClientIp;
import com.citydo.webrtcspringboot.entity.Message;
import com.citydo.webrtcspringboot.service.CommandService;
import com.citydo.webrtcspringboot.service.MessageService;
import com.citydo.webrtcspringboot.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Data 由于@Data重写了hashCode()和equals()方法，
 * 会导致Set<Connection> remove元素时，找不到正确的元素，
 * 应用@Setter @Getter @ToString替换
 * @ServerEndpoint 不是单例模式
 */
@ServerEndpoint(value = "/websocket", configurator = ConfiguratorForClientIp.class)
@Component
@Slf4j
public class Connection {


    /**
     * 在线总人数
     */
    private static volatile AtomicInteger onlineCount = new AtomicInteger(0);


    private static RoomService roomService;


    private static MessageService messageService;


    private static CommandService commandService;

    @Autowired
    public void setRoomService(RoomService roomService) {
        Connection.roomService = roomService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        Connection.messageService = messageService;
    }

    @Autowired
    public void setCommandService(CommandService commandService) {
        Connection.commandService = commandService;
    }

    /**
     * 某个客户端的ip
     */
    private String ip;

    /**
     * 某个客户端的userID
     */
    private String userId;

    /**
     * 某个客户端的roomNo
     */
    private String roomId;

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "ip='" + ip + '\'' +
                ", userId='" + userId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", session=" + session +
                '}';
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        ip = (String) session.getUserProperties().get("clientIp");
        //未进任何房间时，将本次连接放到大厅里面
        roomService.enterLobby(this);
        log.info("用户: {}, 连接到服务器,当前在线人数为：{}", ip, onlineCount.incrementAndGet());
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        //离开大厅
        roomService.leaveLobby(this);
        //即使连接错误，回调了onError方法，最终也会回调onClose方法，所有退出房间写在这里比较合适
        roomService.leaveRoom(roomId, this);
        //在线数减1
        log.info("用户: {}, 关闭连接，退出房间: {}, 当前在线人数为:{}", ip, roomId, onlineCount.addAndGet(-1));
    }

    /**
     * 连接发生错误时调用的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户连接错误: {}",ip);
        error.printStackTrace();
    }

    /**
     * 收到客户端消息后调用的方法
     * @param stringMessage 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(Session session, String stringMessage) throws FileNotFoundException {
        System.out.println(stringMessage);
        Message message = JSON.parseObject(stringMessage, Message.class);
        log.info("收到来自: {}, 信息：{}", ip, JSON.toJSONString(message));
        switch (message.getCommand()) {
            //创建或者加入房间
            case Message.TYPE_COMMAND_ROOM_ENTER:
                this.userId = message.getUserId();
                this.roomId = message.getRoomId();
                enterRoom(message);
                //服务器主动向所有在线的人推送房间列表
                pushRoomList();
                break;
            case Message.TYPE_COMMAND_DIALOGUE:
                messageService.sendMessageForEveryInRoom(message);
                break;
            case Message.TYPE_COMMAND_ROOM_LIST:
                //前端从服务器拉取房间列表
                pullRoomList(message);
                break;
            case Message.TYPE_COMMAND_READY:
            case Message.TYPE_COMMAND_OFFER:
            case Message.TYPE_COMMAND_ANSWER:
            case Message.TYPE_COMMAND_CANDIDATE:
                messageService.sendMessageForEveryExcludeSelfInRoom(message);
                break;
            case Message.TYPE_COMMAND_IMG:
//                System.out.println(message.getImgMessage().toString());
                /*try {
                    FileInputStream fs = new FileInputStream("D:\\bjh.jpg");
                    byte[] content = new byte[fs.available()];
                    fs.read(content);
                    ByteBuffer byteBuffer = ByteBuffer.wrap(content);
                    RemoteEndpoint.Basic basic = session.getBasicRemote();
                    basic.sendBinary(byteBuffer);
                    fs.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
//                FileOutputStream output=new FileOutputStream(new File("D:\\"+message.getMessage().split(":")[0]));
                break;
            default:
        }
    }

    /**
     * 返回给自己是加入房间还是创建房间
     * @param message
     */
    private void  enterRoom(Message message) {
        message.setMessage(roomService.enterRoom(roomId, this));
        try {
            session.getBasicRemote().sendText(JSON.toJSONString(message));
        } catch (IOException e) {
            log.error("加入房间还是创建房间失败: {}", e);
        }
    }

    private void pullRoomList(Message message) {
        message.setMessage(JSON.toJSONString(roomService.queryAllRoomName(), SerializerFeature.WriteNullListAsEmpty));
        try {
            log.info(JSON.toJSONString(message));
            session.getBasicRemote().sendText(JSON.toJSONString(message));
        } catch (IOException e) {
           log.error("获取数据失败：{}", e);
        }
    }

    private void pushRoomList() {
        //告诉每个终端更新房间列表
        Message roomListMessage = new Message();
        roomListMessage.setCommand(Message.TYPE_COMMAND_ROOM_LIST);
        roomListMessage.setMessage(JSON.toJSONString(roomService.queryAllRoomName(),SerializerFeature.WriteNullListAsEmpty));
        messageService.sendMessageForAllOnline(roomListMessage);
    }
}
