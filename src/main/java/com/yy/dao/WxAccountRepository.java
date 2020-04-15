package com.yy.dao;


import com.yy.dao.entity.WxAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WxAccountRepository extends JpaRepository<WxAccount,Integer> {

    WxAccount findById(int id);
    WxAccount findByOpenId(String openId);
    void deleteByOpenId(String openId);
}
