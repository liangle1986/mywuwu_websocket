package com.mywuwu.common.puke.zhajinhua;

import com.alibaba.fastjson.JSON;
import com.mywuwu.common.puke.card.Card;
import com.mywuwu.common.utils.MyWuWuStrUtils;
import com.mywuwu.common.utils.Person;
import com.mywuwu.common.utils.PokerUtils;
import com.mywuwu.common.utils.cache.ConcurrentHashMapCacheUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @Auther: 梁乐乐
 * @Date: 2019/1/9 11:30
 * @Description:
 */
public class ZhaJinHuaGame {

    /**
     * 最大支持玩家数目, 因为单副牌只有那么多, 修改发牌实现支持更多的玩家数目
     */
    private static final Integer MAX_PLAYER_COUNT = 10; // 50 / 3 = 17

    /**
     * 创建游戏
     */
    public static String createGame(int count, List<Map<String, String>> objList) {

        if (count > objList.size()) {
            throw new RuntimeException("params is error");
        }
        if (count > MAX_PLAYER_COUNT) {
            throw new RuntimeException("player count is more than " + MAX_PLAYER_COUNT);
        }

        String gameUUid = MyWuWuStrUtils.getYanString("mywuwuZJHGame");
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
        PokerUtils.deleteCard(Arrays.asList(new Card(Card.POKER_COLOR_LIST.get(1), Card.MAX_KING), new Card(Card.POKER_COLOR_LIST.get(0), Card.SMALL_KING)), pokers);
        PokerUtils.Shuffle(pokers);
        List<Person> gamePersonList = (List<Person>) ConcurrentHashMapCacheUtils.getCache(gameUUid);

        int personCount = gamePersonList.size();

        for (int i = 0; i < 3; i++) {
            for (int index = 0; index < personCount; index++) {
                List<Card> cardList = gamePersonList.get(index).getCardList();
                cardList.add(pokers.get(0));
                pokers.remove(0);
            }

        }
        ConcurrentHashMapCacheUtils.setCache(gameUUid, gamePersonList);
    }

    public static void showResult(String gameUUid) {
        List<Person> gamePersonList = (List<Person>) ConcurrentHashMapCacheUtils.getCache(gameUUid);

        for (int index = 0; index < gamePersonList.size(); index++) {
            Person person = gamePersonList.get(index);
            showPerson(person);
        }
    }

    public static void showPerson(Person person) {
        System.out.println("person name is : " + person.getName());
        System.out.println("card is :");
//        for (Card card : person.getCardList()) {
//            System.out.println(card);
//        }

        System.out.println("是否顺子" + PokerUtils.isStraight(person.getCardList(), true));
        System.out.println("是否同花" + PokerUtils.verifyIsSameFlower(person.getCardList()));
        System.out.println("是否炸弹" + PokerUtils.isBmob(person.getCardList()));
        System.out.println("是否对子" + PokerUtils.isDouble(person.getCardList()));
        System.out.println("是否532" + PokerUtils.isSpecial(person.getCardList()));
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
                return PokerUtils.commCompareToNoColor(personOneCards, personTwoCards);
            }
        });
    }


    public static void main(String[] args) {
        List<Map<String, String>> userList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("name", "qibin2");
        map.put("gail", "0");
        userList.add(map);
        map = new HashMap<>();
        map.put("name", "yan2");
        map.put("gail", "0");
        userList.add(map);
        map = new HashMap<>();
        map.put("name", "zhang2");
        map.put("gail", "1");
        userList.add(map);
        String gameUUid = createGame(userList.size(), userList);
//        sendCard(gameUUid);
//        showResult(gameUUid);

        List<Card> list = new ArrayList<>();
        Card card = new Card(Card.POKER_COLOR_LIST.get(0),Card.POKER_VALUE_LIST.get(9));
        list.add(card);
        card = new Card(Card.POKER_COLOR_LIST.get(1),Card.POKER_VALUE_LIST.get(11));
        list.add(card);
        card = new Card(Card.POKER_COLOR_LIST.get(2),Card.POKER_VALUE_LIST.get(12));
        list.add(card);
        System.out.println("是否顺子" + PokerUtils.isStraight(list, true));
        System.out.println("是否同花" + PokerUtils.verifyIsSameFlower(list));
        System.out.println("是否炸弹" + PokerUtils.isBmob(list));
        System.out.println("是否对子" + PokerUtils.isDouble(list));
        System.out.println("是否532" + PokerUtils.isSpecial(list));
    }
}
