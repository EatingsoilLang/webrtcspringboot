package com.citydo.webrtcspringboot.service;

import com.citydo.webrtcspringboot.websocket.Connection;
import com.citydo.webrtcspringboot.websocket.FileMessage;
import com.citydo.webrtcspringboot.websocket.ImgMessage;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 房间处理服务
 */
@Service
public class RoomService {
    private Map<String, Set<Connection>> rooms = new ConcurrentHashMap<>();
    private Map<String, Set<ImgMessage>> roomsImg = new ConcurrentHashMap<>();
    private Map<String, Set<FileMessage>> roomsFile = new ConcurrentHashMap<>();
    /**
     * 加入到大厅
     */
    public void enterLobby(Connection connection) {
        Set<Connection> lobby = rooms.get("lobby");
        if (lobby == null) {
            rooms.put("lobby", new HashSet<>());
            lobby = rooms.get("lobby");
            lobby.add(connection);
        } else {
            lobby.add(connection);
        }
    }
    /**
     * 加入到大厅
     */
    public void enterLobbyImg(ImgMessage connection) {
        Set<ImgMessage> lobby = roomsImg.get("lobby");
        if (lobby == null) {
            roomsImg.put("lobby", new HashSet<>());
            lobby = roomsImg.get("lobby");
            lobby.add(connection);
        } else {
            lobby.add(connection);
        }
    }
    /**
     * 加入到大厅
     */
    public void enterLobbyFile(FileMessage connection) {
        Set<FileMessage> lobby = roomsFile.get("lobby");
        if (lobby == null) {
            roomsFile.put("lobby", new HashSet<>());
            lobby = roomsFile.get("lobby");
            lobby.add(connection);
        } else {
            lobby.add(connection);
        }
    }

    /**
     * 离开大厅
     */
    public void leaveLobby(Connection connection) {
        Set<Connection> lobby = rooms.get("lobby");
        lobby.remove(connection);
    }
    /**
     * 离开大厅
     */
    public void leaveLobbyImg(ImgMessage connection) {
        Set<ImgMessage> lobby = roomsImg.get("lobby");
        lobby.remove(connection);
    }
    /**
     * 离开大厅
     */
    public void leaveLobbyFile(FileMessage connection) {
        Set<FileMessage> lobby = roomsFile.get("lobby");
        lobby.remove(connection);
    }

    /**
     * 加入指定的房间
     */
    public String enterRoom(String roomId, Connection connection) {
        String operate;
        Set<Connection> room = rooms.get(roomId);
        if (room == null) {
            rooms.put(roomId, new HashSet<>());
            room = rooms.get(roomId);
            room.add(connection);
            operate = "created";
        } else {
            room.add(connection);
            operate = "joined";
        }
        //离开大厅
        leaveLobby(connection);
        return operate;
    }

    /**
     * 离开指定的房间
     */
    public void leaveRoom(String roomId, Connection connection) {
        if (roomId != null) {
            Set<Connection> room = rooms.get(roomId);
            if (room != null) {
                room.remove(connection);
                if (room.size() == 0) {
                    rooms.remove(roomId);
                }
            }
        }

    }

    /**
     * 查询指定房间人数（包括自己）
     */
    public Integer queryCountInRoom(String roomId) {
        Set<Connection> room = rooms.get(roomId);
        return room == null ? 0 : room.size();
    }

    /**
     * 将用户踢出房间
     */
    public void removeUserFromRoom(String roomId, String userId) {
        Set<Connection> room = rooms.get(roomId);
        if (room != null) {
            room.stream().forEach(e->{
                if (e.getUserId().equals(userId)) {
                    room.remove(e);
                }
            });
        }
    }

    /**
     * 通过房间Id查询房间
     */
    public Set<Connection> queryRoomById(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * 通过房间Id查询房间
     */
    public Set<ImgMessage> queryRoomByIdImg(String roomId) {
        return roomsImg.get(roomId);
    }

    /**
     * 通过房间Id查询房间
     */
    public Set<FileMessage> queryRoomByIdFile(String roomId) {
        return roomsFile.get(roomId);
    }

    /**
     * 查询所有存在的房间名称
     */
    public Set<String> queryAllRoomName() {
        return rooms.keySet();
    }

    /**
     * 查询所有存在的房间
     */
    public Collection<Set<Connection>> queryAllRoom() {
        return rooms.values();
    }
}
