package com.citydo.webrtcspringboot.controller;

import com.citydo.webrtcspringboot.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class IndexController {

    private static String ipOfInet4Address;

    private static final String IP_CODE = "127.0.0.1";

    //拿到本机在wifi中的局域网ip
    static {
        // 获得本机的所有网络接口
        Enumeration<NetworkInterface> naifs;
        try {
            naifs = NetworkInterface.getNetworkInterfaces();
            while (naifs.hasMoreElements()) {
                NetworkInterface nif = naifs.nextElement();
                // 获得与该网络接口绑定的 IP 地址，一般只有一个
                Enumeration<InetAddress> addresses = nif.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // 获取IPv4 地址
                    if (addr instanceof Inet4Address) {
                        ipOfInet4Address = addr.getHostAddress();
                        log.info("网卡接口名称：{}",nif.getName());
                        log.info("网卡接口地址：{}",addr.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            log.error("获取数据失败：{}",e);
        }
    }

    @Value("${server.port}")
    private Integer port;

    @Autowired
    private RoomService roomService;

    @GetMapping("/getWebSocketUrl")
    public Map<String, String> getIpAddress(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>(1);
        if(IP_CODE.equals(request.getRemoteAddr())){
            //本地访问
            result.put("url", "wss:"+request.getRemoteAddr()+":"+port+ "/websocket");
        }else{
            //服务IP访问
            result.put("url", "wss:" + "192.168.1.106" +":"+port+ "/websocket");
        }
        return result;
    }

    @GetMapping("/getWebSocketUrlImg")
    public Map<String, String> getIpAddressImg(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>(1);
        if(IP_CODE.equals(request.getRemoteAddr())){
            //本地访问
            result.put("url", "wss:"+request.getRemoteAddr()+":"+port+ "/sendImage");
        }else{
            //服务IP访问
            result.put("url", "wss:" + "192.168.1.106" +":"+port+ "/sendImage");
        }
        return result;
    }

    @GetMapping("/getWebSocketUrlFile")
    public Map<String, String> getIpAddressFile(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>(1);
        if(IP_CODE.equals(request.getRemoteAddr())){
            //本地访问
            result.put("url", "wss:"+request.getRemoteAddr()+":"+port+ "/sendFile");
        }else{
            //服务IP访问
            result.put("url", "wss:" + "192.168.1.106" +":"+port+ "/sendFile");
        }
        return result;
    }

    @GetMapping("/queryCountInRoom")
    public Map<String, String> queryCountInRoom(String roomId) {
        Map<String, String> result = new HashMap<>(1);
        result.put("count", String.valueOf(roomService.queryCountInRoom(roomId)));
        return result;
    }

    @RequestMapping("/uploadImage")
    public  void uploadImage(MultipartHttpServletRequest request, HttpServletResponse response)throws Exception {
        try{
            Map getMap = request.getFileMap();
            String id = request.getParameter("id");
            MultipartFile mfile = (MultipartFile) getMap.get("file");
            InputStream file = mfile.getInputStream();
            byte[] fileByte = FileCopyUtils.copyToByteArray(file);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
