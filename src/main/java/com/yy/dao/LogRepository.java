package com.yy.dao;

import com.yy.dao.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log,Integer> {

    Log findById(int id);
    List<Log> findAllByUser(String username);
}
