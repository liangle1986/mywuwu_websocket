package com.mywuwu.socket;

import com.mywuwu.service.IGameLogin;
import com.mywuwu.service.IGameNotice;
import com.mywuwu.service.IWsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/18 10:37
 * @Description:
 */
@Configuration
public class WebSocketConfig {
    /**
     * 注入ServerEndpointExporter，
     * 这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
    @Autowired
    public void setWsService(IWsService wsService) {
        MyWebSocket.wsService = wsService;
    }
    @Autowired
    public void setGameLoginService(IGameLogin gameLogin) {
        MyWebSocket.gameLogin = gameLogin;
    }
    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        MyWebSocket.kafkaTemplate = kafkaTemplate;
    }
    @Autowired
    public void setGameNoticeService(IGameNotice gameNotice) {
        MyWebSocket.gameNotice = gameNotice;
    }
}
