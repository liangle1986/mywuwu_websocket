package com.mywuwu.service;


import com.mywuwu.entity.Test;

import java.util.List;

/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/27 10:55
 * @Description:
 */
public interface IWsService {
    List<Test> selectAll();
    int selectCount();
}
