package com.mywuwu;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @Auther: 梁乐乐
 * @Date: 2019/1/8 14:35
 * @Description:
 */
public class SpringBootStartApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MywuwuWebsocketApplication.class);
    }
}
