package com.mywuwu.common.puke.doudizhu;

import com.mywuwu.common.puke.card.Card;
import com.mywuwu.common.utils.MyWuWuStrUtils;
import com.mywuwu.common.utils.Person;
import com.mywuwu.common.utils.PokerUtils;
import com.mywuwu.common.utils.cache.ConcurrentHashMapCacheUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Auther: 梁乐乐
 * @Date: 2019/1/8 18:14
 * @Description:
 */
public class DouDiZhuGame {
    /**
     * 创建游戏
     */
    public static String createGame(int count, List<Map<String, String>> objList) {

        if (count > objList.size()) {
            throw new RuntimeException("params is error");
        }


        String gameUUid = MyWuWuStrUtils.getYanString("mywuwuGame");
        List<Person> gamePersonList = new ArrayList<Person>();
        for (int i = 0; i < count; i++) {
            int to = StringUtils.isNumeric(objList.get(i).get("gail")) ? Integer.valueOf(objList.get(i).get("gail")) : 0;
            Person person = new Person(objList.get(i).get("name"), to);
            gamePersonList.add(person);
        }

        ConcurrentHashMapCacheUtils.setCache(gameUUid, gamePersonList);
        return gameUUid;
    }

    /**
     * 一次发完所有的牌留三张底牌
     */
    public static void sendCard(String gameUUid) {
        List<Card> pokers = PokerUtils.getNewPokers();
        PokerUtils.Shuffle(pokers);
        List<Person> gamePersonList = (List<Person>) ConcurrentHashMapCacheUtils.getCache(gameUUid);

        //是否开启概率
        int toOpen = 0;
        int personCount = gamePersonList.size();

        for (int i = 0; i < pokers.size(); i++) {
            // 留三张底牌
            if (i == (pokers.size() - 4)) {
                break;
            }
            for (int index = 0; index < personCount; index++) {
                List<Card> cardList = gamePersonList.get(index).getCardList();
                cardList.add(pokers.get(0));
                pokers.remove(0);
                if (toOpen == 0) {
                    toOpen = gamePersonList.get(index).getGailv();
                }
            }
        }
        ConcurrentHashMapCacheUtils.setCache(gameUUid, gamePersonList);
    }

}
