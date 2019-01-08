package com.mywuwu.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mywuwu.common.utils.DataMessage;
import com.mywuwu.service.IGameLogin;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @Package: com.mywuwu.service.Impl
 * @Description： TODO
 * @Author: 梁乐乐
 * @Date: Created in 2018/12/31 10:47
 * @Company: ywuwu.com
 * @Copyright: Copyright (c) 2018
 * @Version: 0.0.1
 * @Modified By:
 */
@Service
public class GameLoginImpl implements IGameLogin {

    /**
     * 游戏登录后需要把游戏修改成登录状态
     * @param message
     * @return
     */
    public DataMessage loginMessage(String message) {
        DataMessage ms = new DataMessage();
        try {
            if (StringUtils.isNotEmpty(message)) {
                JSONObject obj = JSON.parseObject(message);
                String token = obj.getString("token");
                String msgType = obj.getString("msgType");
                String gameType = obj.getString("gameType");
                ms.setMsgType(StringUtils.isNumeric(msgType) ? Integer.valueOf(msgType) : 0);
                ms.setGameType(StringUtils.isNumeric(gameType) ? Integer.valueOf(gameType) : 1);
                JSONObject msg = obj.getJSONObject("msg");
                if (msg != null) {
                    //  "msg":{"playerId":132526}}
                    String userId = msg.getString("playerId");
                    System.out.println(userId);
                }
            }
            return ms;
        } catch (Exception e) {
            ms.setCode(1);
            return ms;
        }
    }
}

