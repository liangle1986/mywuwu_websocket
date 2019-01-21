package com.mywuwu.kafka.myListener;

import com.mywuwu.service.IWsService;
import com.mywuwu.socket.MyWebSocket;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/28 11:38
 * @Description:
 */
public class MyKafkaListener {
    Logger logger = LoggerFactory.getLogger(MyKafkaListener.class);

    /**
     * 发送聊天消息时的监听
     * @param record
     */
    @KafkaListener(topics = {"chatMessage"})
    public void listen(ConsumerRecord<?, ?> record) {
        logger.info("chatMessage发送聊天消息监听："+record.value().toString());
        System.out.println("chatMessage发送聊天消息监听："+record.value().toString());
        MyWebSocket chatWebsocket = new MyWebSocket();
        chatWebsocket.kafkaReceiveMsg(record.value().toString());
    }

    /**
     * 关闭连接时的监听
     * @param record
     */
    @KafkaListener(topics = {"closeMyWebsocket"})
    private void closeListener(ConsumerRecord<?, ?> record) {
        logger.info("closeWebsocket关闭websocket连接监听："+record.value().toString());
        MyWebSocket chatWebsocket = new MyWebSocket();
        chatWebsocket.kafkaCloseWebsocket(record.value().toString());
    }
}
