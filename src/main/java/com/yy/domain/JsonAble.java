package com.yy.domain;

import com.alibaba.fastjson.JSON;

public interface JsonAble {
    default Object toJSON(){
        return JSON.toJSON(this);
    }
}
