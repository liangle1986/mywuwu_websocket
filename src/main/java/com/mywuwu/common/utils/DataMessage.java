package com.mywuwu.common.utils;

/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/29 15:08
 * @Description:
 */
public class DataMessage<T> {
    private String code = "0";
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public DataMessage(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public DataMessage() {
    }
}
