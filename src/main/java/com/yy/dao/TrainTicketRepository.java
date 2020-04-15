package com.yy.dao;

import com.yy.dao.entity.TrainTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TrainTicketRepository extends JpaRepository<TrainTicket,Integer> {
    TrainTicket findById(int id);
    List<TrainTicket> findAllByUserOrderId(String userOrderId);

    @Modifying
    @Transactional
    @Query("delete from TrainTicket where userOrderId =:userOrderId")
    void deleteAllByUserOrderId(@Param("userOrderId") String userOrderId);
}
