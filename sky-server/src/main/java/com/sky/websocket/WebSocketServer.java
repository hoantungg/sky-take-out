package com.sky.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {
    private static final Map<String, Session> sessionMap =
            new ConcurrentHashMap<>();
    @OnOpen
    public void open(
            @PathParam("sid") String sid,
            Session session
    ){
        log.info("建立WebSocket连接：{}", sid);
        sessionMap.put(sid, session);
    }
    @OnMessage
    public void onMessage(String message,@PathParam("sid") String sid){
        log.info(
                "收到客户端{}的消息：{}",
                sid,
                message
        );
    }
    @OnClose
    public void close(
            @PathParam("sid") String sid
    ) {
        log.info("断开WebSocket连接：{}", sid);

        sessionMap.remove(sid);
    }
    public void sendToAllClient (String message){
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions){
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("WebSocket消息发送失败", e);
            }
        }
    }




}
