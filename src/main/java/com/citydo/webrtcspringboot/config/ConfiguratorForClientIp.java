package com.citydo.webrtcspringboot.config;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 在modifyHandshake方法中，可以将储存在httpSession中的clientIp，转移到ServerEndpointConfig中
 */
public class ConfiguratorForClientIp extends ServerEndpointConfig.Configurator {

    /**
     * 把HttpSession中保存的ClientIP放到ServerEndpointConfig中
     * @param config
     * @param request
     * @param response
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        config.getUserProperties().put("clientIp", httpSession.getAttribute("clientIp"));
    }
}
