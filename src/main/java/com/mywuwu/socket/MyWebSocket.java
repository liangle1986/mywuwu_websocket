package com.mywuwu.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mywuwu.service.IWsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
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

@ServerEndpoint(value = "/mywuwu/websocket/{userId}")
@Component
public class MyWebSocket {

    private static ApplicationContext applicationContext;
    //数据库连接类
    private IWsService wsService;
    //消息
    private KafkaTemplate kafkaTemplate;


    public static void setApplicationContext(ApplicationContext applicationContext) {
        MyWebSocket.applicationContext = applicationContext;

    }

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
    public void onOpen(Session session, @PathParam("userId") String userId) {

        // 注入
        if (this.wsService == null) {
            this.wsService = applicationContext.getBean(IWsService.class);
        }

        // 注入kafka
        if (kafkaTemplate == null) {
            kafkaTemplate = applicationContext.getBean(KafkaTemplate.class); //获取kafka的Bean实例
        }

        this.session = session;
        //加入set中
        webSocketSet.add(this);
//        System.out.println(wsService.selectAll().toString());
        //添加在线人数
        addOnlineCount();
//       JSONObject obj = JSON.parseObject("{" + message + "}");
//        String userId = obj.get("userId") + "" + MyWebSocket.onlineCount;
        sessionPool.put(userId, session);
//        String groupId = obj.get("groupId") + "";
//        if (groupId != null && !"".equals(groupId) && !"null".equalsIgnoreCase(groupId)) {
//            sessionGroup.add(groupId);
//        }
        try {
            this.sendInfo(session.getId() + "登录了");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("新连接接入。当前在线人数为：" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        webSocketSet.remove(this);
        //在线数减1
        subOnlineCount();
        System.out.println("有连接关闭。当前在线人数为：" + getOnlineCount());
        Map<String, String> pathParameters = session.getPathParameters();
        String userId = pathParameters.get("userId"); //从session中获取userId
        Map<String, String> map = new HashMap<>();
        map.put("username", userId);
        kafkaTemplate.send("closeWebsocket", JSON.toJSONString(map));
    }

    /**
     * 收到客户端消息后调用
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(wsService.selectAll().toString());
        System.out.println("客户端发送的消息：" + message);

        //TODO 这里可以进行优化。可以首先根据接收方的userId,即receiver_id判断接收方是否在当前服务器，若在，直接获取session发送即可就不需要走Kafka了，节约资源
        kafkaTemplate.send("chatMessage", s);
    }

    /**
     * 暴露给外部的群发
     *
     * @param message
     * @throws IOException
     */
    public static void sendInfo(String message) throws IOException {
        sendAll(message);
    }

    /**
     * 群发
     *
     * @param message
     */
    private static void sendAll(String message) {
        Arrays.asList(webSocketSet.toArray()).forEach(item -> {
            MyWebSocket myWebSocket = (MyWebSocket) item;
            //群发
            try {
                myWebSocket.sendAllMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("----websocket-------有异常啦");
        error.printStackTrace();
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

    /**
     * 发送信息
     *
     * @param message
     * @throws IOException
     */
    public void sendAllMessage(String message) throws IOException {
        //获取session远程基本连接发送文本消息
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
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

        String receiver_id = jsonObject.getString("receiver_id"); //接受者ID

        if (sessionPool.get(receiver_id) != null) {
            //进行消息发送
            try {
                sessionPool.get(receiver_id).getBasicRemote().sendText(message);
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
        JSONObject jsonObject = JSONObject.parseObject(closeMessage);
        String userId = jsonObject.getString("userId");
        sessionPool.remove(userId);
    }

}
