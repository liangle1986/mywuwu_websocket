package com.mywuwu.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mywuwu.common.utils.DataMessage;
import com.mywuwu.service.IGameLogin;
import com.mywuwu.service.IGameNotice;
import com.mywuwu.service.IWsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/18 10:38
 * @Description:
 */

@ServerEndpoint(value = "/mywuwu/websocket")
@Component
public class MyWebSocket {
    private final static Logger logger = LoggerFactory.getLogger(MyWebSocket.class);
    //数据库连接类
    public static IWsService wsService;

    // 游戏登录操作
    public static IGameLogin gameLogin;
    //消息
    public static KafkaTemplate kafkaTemplate;

    // 公告
    public static IGameNotice gameNotice;

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的CumWebSocket对象。
     */
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 记录当前用户的session对象
     */
    private static Map<String, Session> sessionPool = new ConcurrentHashMap<>();
    /**
     * 记录当前用户的session对象
     */
    private List<String> sessionGroup = new ArrayList<String>();

    /**
     * 连接建立成功调用的方法
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        webSocketSet.add(this);
        this.session = session;
        Map<String, String> pathParameters = session.getPathParameters();
        String userName = pathParameters.get("userName");
        System.out.println(userName + "==================");
        sessionPool.put(session.getId(), session);
        //添加在线人数
        addOnlineCount();
        System.out.println("新连接接入。当前在线人数为：" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        //从set中删除
        webSocketSet.remove(this);
        //在线数减1
        subOnlineCount();
        Map<String, String> pathParameters = session.getPathParameters();
        String token = pathParameters.get("token"); //从session中获取userId
        Map<String, String> map = new HashMap<>();
        map.put("token", session.getId());
        kafkaTemplate.send("closeMyWebsocket", JSON.toJSONString(map));
    }

    /**
     * 收到客户端消息后调用
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            JSONObject obj = JSON.parseObject(message);
            String msgType = obj.getString("msgType");
            String token = obj.getString("token");

            if ("20".equals(msgType)) {
                DataMessage ms = new DataMessage();
                ms.setMsgType(20);
                ms.setGameType(0);
                session.getBasicRemote().sendText(JSON.toJSONString(ms)); //心跳
            } else {
                sendMessage(message, session); //调用Kafka进行消息分发
                System.out.println(wsService.selectAll().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        sessionPool.remove(session.getId());
        sessionPool.remove(this);
    }

    /**
     * 减少在线人数
     */
    private void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }

    /**
     * 添加在线人数
     */
    private void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    /**
     * 当前在线人数
     *
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }


    // 此为单点消息
    public void sendOneMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为分组消息
    public void sendGroupMessage(String groupId, String message) {
        for (MyWebSocket socket : webSocketSet) {
            List<String> groupList = socket.sessionGroup;
            if (groupList != null && groupList.size() > 0) {
                for (String id : groupList) {
                    if (groupId.equals(id)) {
                        socket.session.getAsyncRemote().sendText(message);
                    }
                }
            }
        }
    }


    /**
     * kafka发送消息监听事件，有消息分发
     *
     * @param message
     * @author lll
     */
    public void kafkaReceiveMsg(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String sessionId = this.session.getId();
        if (sessionPool.get(sessionId) != null) {
            //进行消息发送
            try {
                sessionPool.get(sessionId).getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * kafka监听关闭websocket连接
     *
     * @param closeMessage
     */
    public void kafkaCloseWebsocket(String closeMessage) {
        // 有业务需要处理
        webSocketSet.remove(this);
//        sessionPool.remove(this.session.getId());
        sessionPool.remove(this);
    }

    /**
     * 发送消息
     *
     * @param message
     * @param session
     * @throws IOException
     */
    public void sendMessage(String message, Session session) {
        DataMessage ms = new DataMessage();
        try {
            if (StringUtils.isNotEmpty(message)) {
                JSONObject obj = JSON.parseObject(message);
                String msgType = obj.getString("msgType");
                String sessionId = session.getId();
                System.out.println("接收到消息=========================" + message);
                // 公告
                if ("50".equals(msgType)) {
                    ms = this.gameNotice.noticeContent();
                } else if ("1".equals(msgType)) {

                    ms = this.gameLogin.loginMessage(message);
//                    ms = this.loginMessage(message);
                }

                // 发送信息
                if (sessionPool.get(sessionId) != null) {
                    sessionPool.get(sessionId).getBasicRemote().sendText(JSON.toJSONString(ms));
                } else {
                    kafkaTemplate.send("chatMessage", JSON.toJSONString(ms));
                }
                kafkaTemplate.send("updateUserStatus", JSON.toJSONString(ms));
            }
        } catch (Exception e) {
        }

    }

}
