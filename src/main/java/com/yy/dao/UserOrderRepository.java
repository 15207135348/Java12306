package com.yy.dao;

import com.yy.dao.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder,Integer> {
    List<UserOrder> findAllByOpenId(String openId);
    UserOrder findByOrderId(String orderId);

    @Query("select queryCount from UserOrder where orderId=:orderId")
    int findQueryCountByOrderId(@Param("orderId") String orderId);

    @Query("select rushType from UserOrder where orderId=:orderId")
    int findRushTypesByOrderId(@Param("orderId") String orderId);

    @Modifying
    @Transactional
    @Query("update UserOrder set queryCount=:queryCount where orderId=:orderId")
    void updateQueryCountByOrderId(@Param("orderId") String orderId, @Param("queryCount") int queryCount);


    @Modifying
    @Transactional
    @Query("update UserOrder set queryCount=queryCount+:delta where orderId=:orderId")
    void addAndGetQueryCountByOrderId(@Param("orderId") String orderId, @Param("delta") int delta);

    @Modifying
    @Transactional
    @Query("delete from UserOrder where orderId =:orderId")
    void deleteByOrderId(@Param("orderId") String orderId);
}
