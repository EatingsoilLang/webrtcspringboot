package com.citydo.webrtcspringboot.websocket;

import com.alibaba.fastjson.JSON;
import com.citydo.webrtcspringboot.config.ConfiguratorForClientIp;
import com.citydo.webrtcspringboot.entity.Message;
import com.citydo.webrtcspringboot.service.CommandService;
import com.citydo.webrtcspringboot.service.MessageService;
import com.citydo.webrtcspringboot.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.websocket.*;

import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @Package com.citydo.webrtcspringboot.websocket
 * @ClassName ImgMessage
 * @Description TODO
 * @Author LangShengJie
 * @Date Created in 2020/12/11 16:00
 */
@ServerEndpoint(value = "/sendImage", configurator = ConfiguratorForClientIp.class)
@Component
@Slf4j
public class ImgMessage{
        /**
         * 在线总人数
         */
        private static volatile AtomicInteger onlineCount = new AtomicInteger(0);
        private static RoomService roomService;
        private static MessageService messageService;
        private static CommandService commandService;
        @Autowired
        public void setRoomService(RoomService roomService) {
                ImgMessage.roomService = roomService;
        }
        @Autowired
        public void setMessageService(MessageService messageService) {
                ImgMessage.messageService = messageService;
        }
        @Autowired
        public void setCommandService(CommandService commandService) {
                ImgMessage.commandService = commandService;
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



        public void setRoomId(String roomId) {
                this.roomId = roomId;
        }
        public Session getSession() {
                return session;
        }

        public void setSession(Session session) {
                this.session = session;
        }


        @OnMessage(maxMessageSize = 500000000)
        public void getMessage(Session session , byte[] stringMessage) throws IOException {
                messageService.sendImgMessageForEveryInRoom(stringMessage);
        }
        @OnClose
        public void onClose(Session session) {
                //离开大厅
                roomService.leaveLobbyImg(this);
                //即使连接错误，回调了onError方法，最终也会回调onClose方法，所有退出房间写在这里比较合适
//                roomService.leaveRoom(roomId, this);
                //在线数减1
//                log.info("用户: {}, 关闭连接，退出房间: {}, 当前在线人数为:{}", ip, roomId, onlineCount.addAndGet(-1));
        }
        @OnOpen
        public void start(Session session){
                this.session = session;
                ip = (String) session.getUserProperties().get("clientIp");
                //未进任何房间时，将本次连接放到大厅里面
                roomService.enterLobbyImg(this);
                log.info("用户: {}, 连接到服务器,当前在线人数为：{}", ip, onlineCount.incrementAndGet());
        }
}
