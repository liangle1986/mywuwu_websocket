package com.mywuwu.mapper;

import com.mywuwu.common.mapper.MyMapper;
import com.mywuwu.entity.Test;

public interface TestMapper extends MyMapper<Test> {

    Integer selectCountAll();
}