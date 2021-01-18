package com.citydo.webrtcspringboot.service;


import com.alibaba.fastjson.JSON;
import com.citydo.webrtcspringboot.entity.Message;
import com.citydo.webrtcspringboot.websocket.Connection;
import com.citydo.webrtcspringboot.websocket.FileMessage;
import com.citydo.webrtcspringboot.websocket.ImgMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import sun.misc.BASE64Decoder;

import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 消息处理服务
 */

@Slf4j
@Service
public class MessageService {

    @Autowired
    private RoomService roomService;

    /**
     * 给房间内的所有人发送消息（包括自己）
     */
    public void sendMessageForEveryInRoom(Message message) {
        Set<Connection> room = roomService.queryRoomById(message.getRoomId());
        room.stream().forEach(t->{
            try {
                 t.getSession().getBasicRemote().sendText(JSON.toJSONString(message));
            } catch (IOException e) {
                log.error("发送消息失败: {}, {}", message.getUserId(), e);
            }
        });
    }

    /**
     * 给房间内的所有人发送图片（包括自己）
     */
    public void sendImgMessageForEveryInRoom(byte[] message) throws IOException {
        Set<ImgMessage> room = roomService.queryRoomByIdImg("lobby");
        room.stream().forEach(t->{
            try {
                t.getSession().getBasicRemote().sendBinary(ByteBuffer.wrap(message));
            } catch (IOException e) {
//                log.error("发送消息失败: {}, {}", message.getUserId(), e);
            }
        });
    }

    /**
     * 给房间内的所有人发文件列表（包括自己）
     */
    public void sendFileMessageForEveryInRoom(String message) throws IOException {
        Set<FileMessage> room = roomService.queryRoomByIdFile("lobby");
        room.stream().forEach(t->{
            try {
                t.getSession().getBasicRemote().sendText(message);
            } catch (IOException e) {
//                log.error("发送消息失败: {}, {}", message.getUserId(), e);
            }
        });
    }


    /**
     * 给房间除自己之外的所有人发送消息
     */
    public void sendMessageForEveryExcludeSelfInRoom(Message message) {
        Set<Connection> room = roomService.queryRoomById(message.getRoomId());
        room.stream().forEach(t->{
            try {
                if (!message.getUserId().equals(t.getUserId())) {
                     t.getSession().getBasicRemote().sendText(JSON.toJSONString(message));
                }
            } catch (IOException e) {
                log.error("{}->向房间:{}发送消息失败,{}",message.getUserId(), message.getRoomId(),e);
            }
        });
    }


    /**
     * 给在线的所有人发送消息（包括自己）
     */
    public void sendMessageForAllOnline(Message message) {
        Collection<Set<Connection>> rooms = roomService.queryAllRoom();
        rooms.stream().forEach(t-> t.stream().forEach(k->{
            try {
                k.getSession().getBasicRemote().sendText(JSON.toJSONString(message));
            } catch (IOException e) {
                log.error("{}用户发送广播失败:{}", message.getUserId(), e);
            }
        }));
    }

    /**
     * 给在线的所有人发送消息（包括自己）
     */
    public void sendImgMessageForAllOnline(byte[] message) {
        Collection<Set<Connection>> rooms = roomService.queryAllRoom();
        rooms.stream().forEach(t-> t.stream().forEach(k->{
            try {
                k.getSession().getBasicRemote().sendBinary(ByteBuffer.wrap(message));
            } catch (IOException e) {
//                log.error("{}用户发送广播失败:{}", message.getUserId(), e);
            }
        }));
    }

}
