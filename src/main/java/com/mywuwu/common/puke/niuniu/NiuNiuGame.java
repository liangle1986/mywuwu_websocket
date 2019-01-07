package com.mywuwu.common.puke.niuniu;

import com.mywuwu.common.puke.card.Card;
import com.mywuwu.common.utils.MyWuWuStrUtils;
import com.mywuwu.common.utils.Person;
import com.mywuwu.common.utils.PokerUtils;
import com.mywuwu.common.utils.MyWuWuCollectionUtils;
import com.mywuwu.common.utils.cache.ConcurrentHashMapCacheUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class NiuNiuGame {

    /**
     * 最大支持玩家数目, 因为单副牌只有那么多, 修改发牌实现支持更多的玩家数目
     */
    private static final Integer MAX_PLAYER_COUNT = 10; // 50 / 5 = 10

    /**
     * 获取一手牌的牛
     */
    public static NiuResult getNiuResult(List<Card> personCards) {
        /**
         * 先把大于等于10的拿出去
         * 再找三个加起来等于10的倍数的
         * 再找两个加起来等于10的倍数的
         * 最后算牛几
         */
        NiuResult result = new NiuResult();
        List<Card> moreThanNine = new ArrayList<Card>();
        List<Card> lessThanTen = new ArrayList<Card>();

        for (Card card : personCards) {
            if (card.compareTo(PokerUtils.CARD_HEI_TAO_9) > 0 && card.compareTo(PokerUtils.CARD_FAN_PIAN_A) < 0) {
                moreThanNine.add(card);
            } else {
                lessThanTen.add(card);
            }
        }

        Integer niuNumber = getNiuNumber(lessThanTen);
        result.setNiuNumber(niuNumber);

        List<Card> sortCards = new ArrayList<Card>();
        Collections.sort(moreThanNine, Collections.<Card>reverseOrder());
        sortCards.addAll(moreThanNine);

        Collections.sort(lessThanTen, Collections.<Card>reverseOrder());
        List<Card> aList = new ArrayList<Card>(); // 记录A的牌
        for (Card card : lessThanTen) {
            if (card.getValue().equals(Card.POKER_VALUE_LIST.get(0))) {
                aList.add(card);
            }
        }
        if (aList.size() > 0) {
            lessThanTen.removeAll(aList);
            lessThanTen.addAll(aList);
        }

        sortCards.addAll(lessThanTen);
        result.setCardSortList(sortCards);

        return result;
    }


    /**
     * 获取牛的数字
     */
    private static int getNiuNumber(List<Card> lessThanTen) {
        if (lessThanTen.size() == 0) {
            return 10;
        }
        if (lessThanTen.size() == 1) {
            return getOneCardNum(lessThanTen.get(0));
        }
        if (lessThanTen.size() == 2) {
            return getNiuFrom2(lessThanTen);
        }
        if (lessThanTen.size() == 3) {
            return getNiuFrom3(lessThanTen);
        }
        if (lessThanTen.size() == 4) {
            return getNiuFrom4(lessThanTen);
        }
        if (lessThanTen.size() == 5) {
            return getNiuFrom5(lessThanTen);
        }
        return -1;

    }

    /**
     * 从两张牌里面找牛
     */
    private static int getNiuFrom2(List<Card> lessThanTen) {
        if (lessThanTen.size() == 2) {
            int niuNumber = (getOneCardNum(lessThanTen.get(0)) + getOneCardNum(lessThanTen.get(1))) % 10;
            if (niuNumber == 0) {
                return 10;
            }
            return niuNumber;

        } else {
            return -1;
        }
    }

    /**
     * 从三张里面找牛
     */
    private static int getNiuFrom3(List<Card> lessThanTen) {
        if (lessThanTen.size() == 3) {
            // 3
            int niuNumber = (getOneCardNum(lessThanTen.get(0)) + getOneCardNum(lessThanTen.get(1)) + getOneCardNum(lessThanTen.get(2))) % 10;
            if (niuNumber == 0) {
                return 10;
            }

            // 2+1
            List<List<Card>> subListCards2 = MyWuWuCollectionUtils.getAllSubList(lessThanTen, 2);
            for (List<Card> listCard : subListCards2) {
                niuNumber = getNiuFrom2(listCard);
                if (niuNumber == 10) {
                    List<Card> subList = MyWuWuCollectionUtils.getSubList(lessThanTen, listCard);
                    return getOneCardNum(subList.get(0));
                }
            }

            return -1;

        } else {
            return -1;
        }
    }

    /**
     * 从4账牌里面找牛  3 + 1 2 + 2
     */
    private static int getNiuFrom4(List<Card> lessThanTen) {
        if (lessThanTen.size() == 4) {
            // 3 + 1
            List<List<Card>> subListCards3 = MyWuWuCollectionUtils.getAllSubList(lessThanTen, 3);
            for (List<Card> listCard : subListCards3) {
                int niuNumber = getNiuFrom3(listCard);
                if (niuNumber == 10) {
                    Card card = MyWuWuCollectionUtils.getSubList(lessThanTen, listCard).get(0);
                    return getOneCardNum(card);
                }
            }

            // 2 + 2
            List<List<Card>> subListCards2 = MyWuWuCollectionUtils.getAllSubList(lessThanTen, 2);
            for (List<Card> listCard : subListCards2) {
                int niuNumber = getNiuFrom2(listCard);
                if (niuNumber == 10) {
                    List<Card> subList = MyWuWuCollectionUtils.getSubList(lessThanTen, listCard);
                    return getNiuFrom2(subList);
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    /**
     * 从五张里面找牛 3+2 2+2+1
     */
    private static int getNiuFrom5(List<Card> lessThanTen) {
        if (lessThanTen.size() == 5) {
            // 3+2
            List<List<Card>> subListCards3 = MyWuWuCollectionUtils.getAllSubList(lessThanTen, 3);
            for (List<Card> listCard : subListCards3) {
                int niuNumber = getNiuFrom3(listCard);
                if (niuNumber == 10) {
                    List<Card> subList = MyWuWuCollectionUtils.getSubList(lessThanTen, listCard);
                    return getNiuFrom2(subList);
                }
            }

            // 2+2+1
            List<List<Card>> subListCards2 = MyWuWuCollectionUtils.getAllSubList(lessThanTen, 2);
            for (List<Card> listCard : subListCards2) {
                int niuNumber = getNiuFrom2(listCard);
                if (niuNumber == 10) {
                    List<Card> subList = MyWuWuCollectionUtils.getSubList(lessThanTen, listCard);
                    niuNumber = getNiuFrom2(subList);
                    if (niuNumber == 10) {
                        Card card = MyWuWuCollectionUtils.getSubList(lessThanTen, listCard).get(0);
                        return getOneCardNum(card);
                    }
                }
            }
            return -1;
        } else {
            return -1;
        }
    }

    /**
     * 取一张的值
     */
    private static int getOneCardNum(Card card) {
        if (card.getValue().equals(Card.POKER_VALUE_LIST.get(0))) {
            return 1;
        }
        return Integer.valueOf(card.getValue());
    }

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
     * 一次发完所有的牌
     */
    public static void sendCard(String gameUUid) {
        List<Card> pokers = PokerUtils.getNewPokers();
        pokers = PokerUtils.deleteCard(Arrays.asList(new Card(Card.POKER_COLOR_LIST.get(1), Card.MAX_KING), new Card(Card.POKER_COLOR_LIST.get(0), Card.SMALL_KING)), pokers);
        PokerUtils.Shuffle(pokers);
        List<Person> gamePersonList = (List<Person>) ConcurrentHashMapCacheUtils.getCache(gameUUid);

        //是否开启概率
        int toOpen = 0;
        int personCount = gamePersonList.size();
        for (int cardCount = 0; cardCount < 5; cardCount++) {
            for (int index = 0; index < personCount; index++) {
                List<Card> cardList = gamePersonList.get(index).getCardList();

                /**
                 * 是否显示 1不显示
                 */
                Card showCard = pokers.get(0);
                if (cardCount == 4) {
                    showCard.setStatus(1);
                }
                cardList.add(showCard);
                pokers.remove(0);
                if (toOpen == 0) {
                    toOpen = gamePersonList.get(index).getGailv();
                }
            }
        }
        if (toOpen == 1)
            gamePersonList = getUserCardOrder(gamePersonList);

        ConcurrentHashMapCacheUtils.setCache(gameUUid, gamePersonList);
    }


    public static void showResult(String gameUUid) {
        List<Person> gamePersonList = (List<Person>) ConcurrentHashMapCacheUtils.getCache(gameUUid);
        // 排序
        getShowOrder(gamePersonList);
//        Person winPerson = gamePersonList.get(0);
//        System.out.println("this is winner ...");
//        showPerson(winPerson);


        System.out.println("all person info is ....");
        for (int index = 0; index < gamePersonList.size(); index++) {
            Person person = gamePersonList.get(index);
            showPerson(person);
        }
    }


    public static void showPerson(Person person) {
        System.out.println("person name is : " + person.getName());
        System.out.println("card is :");
        for (Card card : person.getCardList()) {
            System.out.println(card);
        }
        System.out.println("result is : " + getNiuResult(person.getCardList()));
    }


    public static void main(String[] args) {
        List<Map<String, String>> userList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("name", "qibin");
        map.put("gail", "0");
        userList.add(map);
        map = new HashMap<>();
        map.put("name", "yan");
        map.put("gail", "0");
        userList.add(map);
        map = new HashMap<>();
        map.put("name", "zhang");
        map.put("gail", "1");
        userList.add(map);
        String gameUUid = createGame(userList.size(), userList);
        sendCard(gameUUid);
        showResult(gameUUid);
    }

    private static class NiuResult implements Comparable {
        /**
         * 牛的数目
         */
        private Integer niuNumber;
        /**
         * 有序手牌
         */
        private List<Card> cardSortList;

        public Integer getNiuNumber() {
            return niuNumber;
        }

        public void setNiuNumber(Integer niuNumber) {
            this.niuNumber = niuNumber;
        }

        public List<Card> getCardSortList() {
            return cardSortList;
        }

        public void setCardSortList(List<Card> cardSortList) {
            this.cardSortList = cardSortList;
        }

        /**
         * 先判断牛的大小 在判断牌的大小 最后比最大的牌的花色
         *
         * @param o
         * @return
         */
        public int compareTo(Object o) {
            if (!(o instanceof NiuResult)) {
                return -1;
            }
            NiuResult niuResult = (NiuResult) o;

            if (this.getNiuNumber() != niuResult.getNiuNumber()) {
                return niuResult.getNiuNumber() - this.getNiuNumber();
            }

            return PokerUtils.commCompareTo(niuResult.getCardSortList(), this.getCardSortList());
        }

        @Override
        public String toString() {
            return "NiuResult{" +
                    "niuNumber=" + niuNumber +
                    ", cardSortList=" + cardSortList +
                    '}';
        }
    }


    /**
     * 根据概率发牌
     *
     * @param oldList 默认牌
     * @return 概率牌
     */
    private static List<Person> getUserCardOrder(List<Person> oldList) {

        // 排序
        getShowOrder(oldList);

        // 记录要替换的用户
        Person maxperson = oldList.get(0);
        Person gailiperson = null;
        NiuResult maxNiu = null;
        List<Person> newList = new ArrayList<>();
        // 发牌后调整牌大小给谁
        for (Person person : oldList) {
            // 记录
            if (person.getGailv() == 1) {
                gailiperson = person;
            }
        }


        /**
         * 计算完成替换
         */
        for (Person person : oldList) {
            Person newPer = new Person(person.getName(), person.getGailv());
            newPer.setCardList(person.getCardList());
            if (person.getName().equals(gailiperson.getName())) {
                newPer.setCardList(maxperson.getCardList());
            } else if (person.getName().equals(maxperson.getName())) {
                newPer.setCardList(gailiperson.getCardList());
            }
            newList.add(newPer);
        }
        return newList;
    }

    /**
     * 根据牌的大小排序
     *
     * @param personList
     */
    private static void getShowOrder(List<Person> personList) {
        Collections.sort(personList, new Comparator<Person>() {
            public int compare(Person o1, Person o2) {
                List<Card> personOneCards = o1.getCardList();
                List<Card> personTwoCards = o2.getCardList();

                NiuResult oneResult = getNiuResult(personOneCards);
                NiuResult twoResult = getNiuResult(personTwoCards);

                return oneResult.compareTo(twoResult);
            }
        });
    }
}
