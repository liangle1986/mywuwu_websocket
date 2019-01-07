package com.mywuwu.common.utils;

import com.mywuwu.common.puke.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Person {
    /**
     * 姓名
     */
    private String name;

    /**
     *  默认为0 调整后为1
     */
    private int gailv = 0;

    /**
     *
     */
    private List<Card> cardList;

    public Person(String name, int gailv) {
        this.name = name;
        this.gailv = gailv;
        this.cardList = new ArrayList<>();
    }

    public Person() {
    }

    public int getGailv() {
        return gailv;
    }

    public void setGailv(int gailv) {
        this.gailv = gailv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }
}
