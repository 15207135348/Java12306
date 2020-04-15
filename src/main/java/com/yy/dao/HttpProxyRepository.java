package com.yy.dao;

import com.yy.dao.entity.HttpProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public interface HttpProxyRepository extends JpaRepository<HttpProxy,Integer> {

    HttpProxy findById(int id);
    HttpProxy findByIpAndPort(String ip, int port);
    List<HttpProxy> findAll();
    List<HttpProxy> findAllByCheckTimeGreaterThan(Timestamp timestamp);
    List<HttpProxy> findAllByCheckTimeLessThan(Timestamp timestamp);
    void deleteByIpAndPort(String ip, int port);
    void deleteById(int id);
}
