package com.mywuwu.service.Impl;

import com.mywuwu.common.utils.DataMessage;
import com.mywuwu.service.IGameNotice;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.mywuwu.service
 * @Description： TODO
 * @Author: 梁乐乐
 * @Date: Created in 2018/12/31 11:00
 * @Company: ywuwu.com
 * @Copyright: Copyright (c) 2018
 * @Version: 0.0.1
 * @Modified By:
 */
@Service
public class GameNoticeImpl implements IGameNotice {
    /**
     * 获取游戏公告
     *
     * @return
     */
    public DataMessage noticeContent() {
        DataMessage<Map<String, String>> ms = new DataMessage<>();
        try {
            ms.setMsgType(4);
            ms.setGameType(0);
            //查询巩固
            Map<String, String> notice = new HashMap<>();
            notice.put("noticeContent", "游戏忠告:文明游戏，禁止赌博及其他违法行为  游戏代理及相关咨询加微信：17302139567,电话:17302139567,QQ:37781552");
            ms.setData(notice);
            return ms;
        } catch (Exception e) {
            ms.setCode(1);
            return ms;
        }
    }
}
