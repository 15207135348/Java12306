package com.yy.dao.repository;

import com.yy.dao.entity.TrainTicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TrainTicketPriceRepository extends JpaRepository<TrainTicketPrice,Integer> {
    TrainTicketPrice findById(int id);


    @Query("select lowestPrice from TrainTicketPrice where trainCode=:trainCode and fromStation=:fromStation and toStation=:toStation")
    String findLowestPrice(@Param("trainCode") String trainCode, @Param("fromStation") String fromStation, @Param("toStation") String toStation);


    TrainTicketPrice findByTrainCodeAndFromStationAndToStation(String trainCode, String fromStation, String toStation);
}
