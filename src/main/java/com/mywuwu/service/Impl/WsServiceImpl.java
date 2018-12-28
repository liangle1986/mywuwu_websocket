package com.mywuwu.service.Impl;

import com.mywuwu.entity.Test;
import com.mywuwu.mapper.TestMapper;
import com.mywuwu.service.IWsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/27 10:56
 * @Description:
 */
@Service
public class WsServiceImpl implements IWsService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public List<Test> selectAll() {
        return testMapper.selectAll();
    }
    @Override
    public int selectCount(){
        return testMapper.selectCountAll();
//        return 0;
    }
}
