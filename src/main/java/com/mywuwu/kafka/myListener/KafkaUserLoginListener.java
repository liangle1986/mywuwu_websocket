package com.mywuwu.kafka.myListener;

import com.mywuwu.common.utils.DataMessage;
import com.mywuwu.service.IGameLogin;
import com.mywuwu.socket.MyWebSocket;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * @Auther: 梁乐乐
 * @Date: 2019/1/8 15:28
 * @Description: 登录状态处理
 */
public class KafkaUserLoginListener {

    Logger logger = LoggerFactory.getLogger(MyKafkaListener.class);
    // 游戏登录操作
    public static IGameLogin gameLogin;
    /**
     *
     * @param record
     */
    @KafkaListener(topics = {"updateUserStatus"})
    public void GameLogin(ConsumerRecord<?, ?> record) {
        logger.info("chatMessage发送聊天消息监听："+record.value().toString());
        MyWebSocket chatWebsocket = new MyWebSocket();
        chatWebsocket.gameLogin.loginMessage(record.value().toString());
    }

}
