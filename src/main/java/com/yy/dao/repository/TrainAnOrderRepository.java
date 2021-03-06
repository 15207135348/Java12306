package com.yy.dao.repository;

import com.yy.dao.entity.TrainAnOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TrainAnOrderRepository extends JpaRepository<TrainAnOrder,Integer> {

    TrainAnOrder findByUserOrderId(String userOrderId);

    @Modifying
    @Transactional
    @Query("delete from TrainAnOrder where userOrderId =:userOrderId")
    void deleteByUserOrderId(@Param("userOrderId") String userOrderId);


    @Modifying
    @Transactional
    @Query("update TrainAnOrder set status=:status where userOrderId =:userOrderId")
    void updateStatusByUserOrderId(@Param("status") String status, @Param("userOrderId") String userOrderId);

}
