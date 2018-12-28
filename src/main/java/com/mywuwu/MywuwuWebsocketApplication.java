package com.mywuwu;

import com.mywuwu.common.ds.DynamicDataSourceRegister;
import com.mywuwu.socket.MyWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import tk.mybatis.spring.annotation.MapperScan;

@Import(DynamicDataSourceRegister.class)
@MapperScan(basePackages = "com.mywuwu.mapper")
@SpringBootApplication
public class MywuwuWebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MywuwuWebsocketApplication.class, args);
//		SpringApplication springApplication = new SpringApplication(MywuwuWebsocketApplication.class);
//		ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);
//		MyWebSocket.setApplicationContext(configurableApplicationContext);//解决WebSocket不能注入的问题
	}

}

