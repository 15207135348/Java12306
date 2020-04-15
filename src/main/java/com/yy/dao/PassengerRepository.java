package com.yy.dao;

import com.yy.dao.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Integer> {
    Passenger findById(int id);
    List<Passenger> findAllByUsername(String username);
    Passenger findByUsernameAndName(String username, String name);
}
