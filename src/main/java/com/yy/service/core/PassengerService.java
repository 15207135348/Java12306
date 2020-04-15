package com.yy.service.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yy.dao.PassengerRepository;
import com.yy.dao.entity.Passenger;
import com.yy.domain.Session;
import com.yy.service.api.API12306Service;
import com.yy.service.util.SessionPoolService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PassengerService {

    private static final Logger LOGGER = Logger.getLogger(PassengerService.class);

    @Autowired
    API12306Service api12306Service;
    @Autowired
    SessionPoolService sessionPoolService;
    @Autowired
    PassengerRepository passengerRepository;

    public List<Passenger> getPassengers(String username) {
        List<Passenger> list = new ArrayList<>();
        Session session = sessionPoolService.getSession(username);
        JSONArray array = api12306Service.getPassengers(session);
        if (array == null) {
            LOGGER.error("getPassengers: api12306Service.getPassengers(session)==null");
            return list;
        }
        for (int i = 0; i < array.size(); ++i) {
            JSONObject object = array.getJSONObject(i);
            //{"passenger_name":"鲍秀女","sex_code":"F","sex_name":"女","born_date":"1900-01-01 00:00:00","country_code":"CN","passenger_id_type_code":"1","passenger_id_type_name":"中国居民身份证","passenger_id_no":"3623***********127","passenger_type":"1","passenger_flag":"0","passenger_type_name":"成人","mobile_no":"15070362137","phone_no":"","email":"","address":"","postalcode":"","first_letter":"BXN","recordCount":"4","isUserSelf":"N","total_times":"99","delete_time":"1900/06/30","allEncStr":"844399c79daf041cddeb06027c9a7b960486d9a952823fbc12707f876345900251be7e3b8d162cd4b98f2a16aaa3fbda","isAdult":"Y","isYongThan10":"N","isYongThan14":"N","isOldThan60":"N","gat_born_date":"","gat_valid_date_start":"","gat_valid_date_end":"","gat_version":""}
            Passenger passenger = new Passenger();
            passenger.setName(object.getString("passenger_name"));
            passenger.setType(object.getString("passenger_type_name"));
            passenger.setIdNo(object.getString("passenger_id_no"));
            passenger.setIdType(object.getString("passenger_id_type_name"));
            passenger.setIfReceive("Y".equals(object.getString("if_receive")));
            passenger.setMobile(object.getString("mobile_no").isEmpty() ? object.getString("phone_no") : object.getString("mobile_no"));
            passenger.setUsername(username);
            list.add(passenger);
        }
        return list;
    }

    public void saveOrUpdatePassengers(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            Passenger passenger1 = passengerRepository.findByUsernameAndName(passenger.getUsername(), passenger.getName());
            if (passenger1 == null) {
                passengerRepository.save(passenger);
                LOGGER.info("添加乘客人");
            } else {
                passenger.setId(passenger1.getId());
                passengerRepository.save(passenger);
                LOGGER.info("更新乘客人");
            }
        }
    }



//    @Autowired
//    WxAccountRepository wxAccountRepository;
//    @Autowired
//    Login12306Service login12306Service;
//
//    @PostConstruct
//    private void init(){
//
//        List<WxAccount> list = wxAccountRepository.findAll();
//        for (WxAccount wxAccount : list)
//        {
//            if (wxAccount.getUsername()!=null && wxAccount.getPassword()!=null){
//
//                login12306Service.login(wxAccount.getUsername(), wxAccount.getPassword());
//                List<Passenger> passengers = getPassengers(wxAccount.getUsername());
//                saveOrUpdatePassengers(passengers);
//            }
//        }
//    }



}
