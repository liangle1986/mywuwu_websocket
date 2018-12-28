package com.mywuwu.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
}
