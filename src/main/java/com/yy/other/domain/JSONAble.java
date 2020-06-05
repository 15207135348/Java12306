package com.yy.other.domain;

import com.alibaba.fastjson.JSON;

public interface JSONAble {
    default Object toJSON() {
        return JSON.toJSON(this);
    }
}
