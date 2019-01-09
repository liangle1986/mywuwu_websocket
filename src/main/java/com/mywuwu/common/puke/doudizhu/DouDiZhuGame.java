package com.mywuwu.common.puke.doudizhu;

import com.alibaba.fastjson.JSON;
import com.mywuwu.common.puke.card.Card;
import com.mywuwu.common.puke.niuniu.NiuNiuGame;
import com.mywuwu.common.utils.MyWuWuStrUtils;
import com.mywuwu.common.utils.Person;
import com.mywuwu.common.utils.PokerUtils;
import com.mywuwu.common.utils.cache.ConcurrentHashMapCacheUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

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

        int personCount = gamePersonList.size();

        for (int i = 0; i < 17; i++) {
            for (int index = 0; index < personCount; index++) {
                List<Card> cardList = gamePersonList.get(index).getCardList();
                cardList.add(pokers.get(0));
                pokers.remove(0);
            }

        }
        ConcurrentHashMapCacheUtils.setCache(gameUUid+ "_dipai", pokers);
        ConcurrentHashMapCacheUtils.setCache(gameUUid, gamePersonList);
    }

    public static void showResult(String gameUUid) {
        List<Person> gamePersonList = (List<Person>) ConcurrentHashMapCacheUtils.getCache(gameUUid);
        // 排序
//        getMaxToMinOrder(gamePersonList);

        for (int index = 0; index < gamePersonList.size(); index++) {
            Person person = gamePersonList.get(index);
            showPerson(person);
        }
        // 剩余扑克
        List<Card>  overPokers = (List<Card>) ConcurrentHashMapCacheUtils.getCache(gameUUid+ "_dipai");
        for (Card card : overPokers){
            System.out.println(card.toString());
        }
    }

    public static void showPerson(Person person) {
        System.out.println("person name is : " + person.getName());
        System.out.println("card is :");
//        for (Card card : person.getCardList()) {
//            System.out.println(card);
//        }
        System.out.println(JSON.toJSONString(person.getCardList()));
    }

    /**
     * 从大到小排序
     *
     * @param personList 用户牌信息
     */
    private static void getMaxToMinOrder(List<Person> personList) {
        Collections.sort(personList, new Comparator<Person>() {
            public int compare(Person o1, Person o2) {
                List<Card> personOneCards = o1.getCardList();
                List<Card> personTwoCards = o2.getCardList();
               return PokerUtils.commCompareToNoColor(personOneCards,personTwoCards);
            }
        });
    }


    public static void main(String[] args) {
        List<Map<String, String>> userList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("name", "qibin1");
        map.put("gail", "0");
        userList.add(map);
        map = new HashMap<>();
        map.put("name", "yan1");
        map.put("gail", "0");
        userList.add(map);
        map = new HashMap<>();
        map.put("name", "zhang1");
        map.put("gail", "1");
        userList.add(map);
        String gameUUid = createGame(userList.size(), userList);
        sendCard(gameUUid);
        showResult(gameUUid);
    }
}
