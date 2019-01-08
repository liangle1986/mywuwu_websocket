package com.mywuwu.controller;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;

/**
 * @Auther: 梁乐乐
 * @Date: 2019/1/8 17:56
 * @Description:
 */
@ClientEndpoint
public class WebSocketTest {
    private String deviceId;

    private Session session;

    public WebSocketTest () {

    }

    public WebSocketTest (String deviceId) {
        this.deviceId = deviceId;
    }

    protected boolean start() {
        WebSocketContainer Container = ContainerProvider.getWebSocketContainer();
        String uri = "ws://192.168.6.248:8074/mywuwu_websocket/mywuwu/websocket";
        System.out.println("Connecting to " + uri);
        try {
            session = Container.connectToServer(WebSocketTest.class, URI.create(uri));
            System.out.println("count: " + deviceId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        for (int i = 0; i< 10002; i++) {
            WebSocketTest wSocketTest = new WebSocketTest(String.valueOf(i));
            if (!wSocketTest.start()) {
                System.out.println("测试结束！");
                break;
            }
        }
    }
}
