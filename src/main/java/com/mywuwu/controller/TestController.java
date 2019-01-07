package com.mywuwu.controller;

import com.alibaba.fastjson.JSON;
import com.mywuwu.entity.Test;
import com.mywuwu.service.IWsService;
import com.mywuwu.socket.MyWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/18 11:18
 * @Description:
 */
@RestController
@CrossOrigin
public class TestController {
    @Autowired
    private MyWebSocket webSocket;

    @Autowired
    private IWsService wsService;

    @GetMapping("/sendAllWebSocket")
    public String test() {
//        try {
////            webSocket.sendAllMessage("清晨起来打开窗，心情美美哒~");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "websocket群体发送！";
    }

    @RequestMapping("/sendOneWebSocket")
    public String sendOneWebSocket(String userId) {
//        wsService.selectAll();
        webSocket.sendOneMessage(userId, "只要你乖给你买条gai！");
        return "websocket单人发送";
    }

    @RequestMapping("/sendGroupWebSocket")
    public String sendGroupWebSocket(String groupId) {
        List<Test> lists = wsService.selectAll();
        webSocket.sendGroupMessage(groupId, "只要你乖给你买" + JSON.toJSONString(lists));
        return "websocket分组发送" + JSON.toJSONString(lists);
    }

}
