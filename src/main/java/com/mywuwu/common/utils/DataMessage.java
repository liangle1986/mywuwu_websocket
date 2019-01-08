package com.mywuwu.common.utils;

/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/29 15:08
 * @Description:
 */
public class DataMessage<T> {
    private int code = 0;
    // 消息类型0：错误 1 进入大厅 12 解散房间成功
    /**
     * 0：错误 1 进入大厅 2 战绩 3  用户反馈 4 公告通知
     * 12 解散房间成功 5 创建房间
     * 6 进入房间 7 点击准备 8  发牌消息 17 查询玩家信息
     * 24 小结算  25 大结算 9 解散房间 10 同意解散房间 11 不同意解散房间
     * 13 刷新房间 16 返回大厅 18 玩家离开20分钟，房间解散  19 聊天消息
     * 20  心跳包 14 离线 15 上线 21  房卡更新   23 地理位置信息同步到服务端
     * 26 商城列表 27 绑定推荐码 28 确认是否绑定  29 商城购买 30 准备倒计时
     * <p>
     * ---------------------------------茶楼相关接口------------------------------------
     * 80 创建茶楼 84 茶楼玩家列表 91 待审核玩家列表 85 移出茶楼 98 设置/取消店小二
     * 83 同意加入茶楼 83 拒绝加入茶楼 89 8个牌桌玩家列表 99 8个牌桌玩家列表  87 进入茶楼下的某个牌桌玩游戏
     * 88 加入茶楼 82 删除茶楼 90 退出茶楼 81 茶楼列表（我创建的茶楼） 53 茶楼列表（我创建的茶楼） 94 我加入的茶楼列表
     * 92 茶楼战绩列表  93 茶楼战绩列表 95 退出茶楼到大厅 52 已开房排行榜 50 土豪榜 51 土豪榜 97 大赢家
     * 96 茶楼设置 86 进入茶楼
     * ------------------------------扎金花特有接口------------------------------------------
     * 301 自动比牌 300 跟注 303 看牌 302 比牌 304 弃牌
     * <p>
     * ---------------------------牛牛特有接口----------------------------------------------
     * 100 准备抢庄 101 抢庄 102 抢庄超过10s时间限制 103 准备压分 104 压分
     * 105 亮牌 106 搓牌
     */
    private int msgType;
    /**
     * 游戏类型
     */
    private int gameType;
    /**
     * noticeContent ：这个是公告
     */
    private T data;

    /**
     * token
     */
//    private String token;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }

    public DataMessage(int code, int msgType, int gameType, T data) {
        this.code = code;
        this.msgType = msgType;
        this.gameType = gameType;
        this.data = data;
//        this.token = token;
    }

    public DataMessage() {
    }
}