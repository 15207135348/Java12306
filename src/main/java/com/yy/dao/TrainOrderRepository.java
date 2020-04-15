package com.yy.dao;

import com.yy.dao.entity.TrainOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TrainOrderRepository extends JpaRepository<TrainOrder,Integer> {

    TrainOrder findByUserOrderId(String userOrderId);

    @Modifying
    @Transactional
    @Query("delete from TrainOrder where userOrderId =:userOrderId")
    void deleteByUserOrderId(@Param("userOrderId") String userOrderId);


    @Modifying
    @Transactional
    @Query("update TrainOrder set status=:status where userOrderId =:userOrderId")
    void updateStatusByUserOrderId(@Param("status") String status, @Param("userOrderId") String userOrderId);

}
