package com.yy.service.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yy.constant.SeatType;
import com.yy.constant.TicketField;
import com.yy.dao.TrainTicketPriceRepository;
import com.yy.dao.entity.TrainTicketPrice;
import com.yy.domain.Session;
import com.yy.domain.Train;
import com.yy.service.api.API12306Service;
import com.yy.service.util.SessionPoolService;
import com.yy.service.util.StationService;
import com.yy.util.TimeFormatUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@EnableScheduling
public class QueryTrainService {

    private static final Logger LOGGER = Logger.getLogger(QueryTrainService.class);

    @Autowired
    API12306Service api12306Service;
    @Autowired
    StationService stationService;

    @Autowired
    SessionPoolService sessionPoolService;

    @Autowired
    TrainTicketPriceRepository trainTicketPriceRepository;

    public List<Train> getTrains(Session session, String date, String fromStation, String toStation, boolean useProxy) {

        //验证车站号是否正确
        String fromStationCode = stationService.getCodeByName(fromStation);
        String toStationCode = stationService.getCodeByName(toStation);
        if (fromStation == null || toStation == null) {
            LOGGER.error(String.format("getTrains: 找不到车站【%s】和【%s】", fromStation, toStation));
            return null;
        }
        JSONArray array = api12306Service.queryTickets(session, date, fromStationCode, toStationCode, useProxy);
        if (array == null) {
            LOGGER.error("查询余票失败，检查IP是否被封？");
            return null;
        }
        List<Train> list = new ArrayList<>();
        for (int i = 0; i < array.size(); ++i) {
            String string = array.getString(i);
            String[] ticketInfo = string.split("\\|");
            if (!ticketInfo[TicketField.REMARK.getI()].equals("预订")) {
                continue;
            }
            Train train = new Train();
            train.setTrainCode(ticketInfo[TicketField.TRAIN_CODE.getI()]);
            train.setSecretStr(ticketInfo[TicketField.SECRET_STR.getI()]);
            train.setRemark(ticketInfo[TicketField.REMARK.getI()]);
            train.setCanBackup(ticketInfo[TicketField.CAN_BACKUP.getI()].equals("1"));
            train.setOther(ticketInfo[TicketField.OTHER.getI()]);
            train.setFromStation(fromStation);
            train.setToStation(toStation);

            train.setFromTime(ticketInfo[TicketField.FROM_TIME.getI()]);
            train.setToTime(ticketInfo[TicketField.TO_TIME.getI()]);
            train.setDuration(ticketInfo[TicketField.DURATION.getI()]);
            train.setFromDate(ticketInfo[TicketField.START_DATE.getI()]);
            long t1 = TimeFormatUtil.date2Stamp(train.getFromDate() + " " + train.getFromTime(), "yyyyMMdd HH:mm");
            long delta = Integer.parseInt(train.getDuration().split(":")[0]) * 3600000 + Integer.parseInt(train.getDuration().split(":")[1]) * 60000;
            long t2 = t1 + delta;
            train.setToDate(TimeFormatUtil.stamp2Date(t2, "yyyyMMdd"));
            //座位类型及其数量
            for (SeatType seatType : SeatType.values()) {
                train.setTicketCount(seatType.getName(), ticketInfo[seatType.getI()]);
            }
//            String trainNo = ticketInfo[TicketField.TRAIN_NO.getI()];
//            String fromStationNo = ticketInfo[TicketField.FROM_STATION_NO.getI()];
//            String toStationNo = ticketInfo[TicketField.TO_STATION_NO.getI()];
//            String seatTypes = ticketInfo[TicketField.SEAT_TYPES.getI()];
//            train.setTrainNo(trainNo);
//            train.setFromStationNo(fromStationNo);
//            train.setToStationNo(toStationNo);
//            train.setSeatTypes(seatTypes);
            list.add(train);
        }
        return list;
    }

    public void setPrices(List<Train> trains) {
        for (Train train : trains) {
            String lowestPrice = trainTicketPriceRepository.findLowestPrice(train.getTrainCode(), train.getFromStation(), train.getToStation());
            if (lowestPrice != null) {
                train.setLowestPrice(lowestPrice);
            } else {
                train.setLowestPrice("¥256");
//                String date = TimeFormatUtil.stamp2Date(System.currentTimeMillis()+24*3600000, "yyyy-MM-dd");
//                JSONObject prices = api12306Service.queryTicketPrices(
//                    sessionPoolService.getSession(null), train.getTrainNo(),
//                    train.getFromStationNo(), train.getToStationNo(), train.getSeatTypes(), date);
//                while (prices == null) {
//                    LOGGER.info(String.format("【%s】列车查询车票价格失败", train.getTrainCode()));
//                    prices = api12306Service.queryTicketPrices(
//                        sessionPoolService.getSession(null), train.getTrainNo(),
//                        train.getFromStationNo(), train.getToStationNo(), train.getSeatTypes(), date);
//                }
//                LOGGER.info(String.format("【%s】列车查询车票价格成功", train.getTrainCode()));
//                lowestPrice = prices.getString("WZ");
//                if (lowestPrice != null)
//                {
//                    train.setLowestPrice(lowestPrice);
//                }
//                else
//                {
//                    train.setLowestPrice("¥257");
//                }
            }
        }
    }


    public long getExpireTime(Session session, String dates, String fromStation, String toStation, String trainCodes) {

        String[] dateArr = dates.split("/");
        Arrays.sort(dateArr, Comparator.reverseOrder());
        long expireTime = 0;
        List<Train> trains = getTrains(session, dateArr[0], fromStation, toStation, false);
        Set<String> set = new HashSet<>();
        Collections.addAll(set, trainCodes.split("/"));
        //从晚到早进行遍历
        for (int i = trains.size() - 1; i >= 0; --i) {
            if (set.contains(trains.get(i).getTrainCode())) {
                String timeStr = dateArr[0] + " " + trains.get(i).getFromTime();
                long timestamp = TimeFormatUtil.date2Stamp(timeStr, "yyyy-MM-dd HH:mm");
                expireTime = timestamp - 1000 * 3600 * 2;
                break;
            }
        }
        return expireTime;
    }

//    @PostConstruct
    private void queryAndSaveTrainPrice() {
        Thread thread = new Thread(() -> {
            Session session = sessionPoolService.getSession(null);
            List<String> stations = stationService.getStations();
            for (String from : stations) {
                for (String to : stations) {
                    if (!from.equals(to)) {
                        try {
                            queryAndSaveTrainPrice(session, from, to);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                }
            }
        });
        thread.start();
    }

    private void queryAndSaveTrainPrice(Session session, String fromStation, String toStation) {

        String date = TimeFormatUtil.stamp2Date(System.currentTimeMillis() + 240 * 3600000, "yyyy-MM-dd");
        //验证车站号是否正确
        String fromStationCode = stationService.getCodeByName(fromStation);
        String toStationCode = stationService.getCodeByName(toStation);
        if (fromStation == null || toStation == null) {
            LOGGER.error(String.format("gettrain: 找不到车站【%s】和【%s】", fromStation, toStation));
            return;
        }
        JSONArray array = api12306Service.queryTickets(session, date, fromStationCode, toStationCode, true);
        if (array == null) {
            LOGGER.error("查询余票失败，检查IP是否被封？");
            return;
        }
        for (int i = 0; i < array.size(); ++i) {
            String string = array.getString(i);
            String[] ticketInfo = string.split("\\|");
            String trainCode = ticketInfo[TicketField.TRAIN_CODE.getI()];
            String trainNo = ticketInfo[TicketField.TRAIN_NO.getI()];
            String fromStationNo = ticketInfo[TicketField.FROM_STATION_NO.getI()];
            String toStationNo = ticketInfo[TicketField.TO_STATION_NO.getI()];
            String seatTypes = ticketInfo[TicketField.SEAT_TYPES.getI()];
            if (seatTypes.isEmpty()) {
                LOGGER.info(String.format("列车【%s】没有任何坐席：【%s】", trainCode, ticketInfo[TicketField.REMARK.getI()]));
                continue;
            }
            String lowestPrice = trainTicketPriceRepository.findLowestPrice(trainCode, fromStation, toStation);
            if (lowestPrice != null) {
                continue;
            }
            JSONObject prices = api12306Service.queryTicketPrices(session, trainNo,
                    fromStationNo, toStationNo, seatTypes, date);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (prices == null) {
                LOGGER.info(String.format("【%s】列车查询车票价格失败", trainCode));
                prices = api12306Service.queryTicketPrices(session, trainNo,
                        fromStationNo, toStationNo, seatTypes, date);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LOGGER.info(String.format("【%s】列车查询车票价格成功", trainCode));
            JSONObject map = new JSONObject();
            for (String key : prices.keySet()) {
                SeatType seatType = SeatType.find(key);
                if (seatType != null) {
                    map.put(seatType.getName(), prices.getString(key));
                }
            }
            lowestPrice = prices.getString("WZ");
            if (lowestPrice != null) {
                TrainTicketPrice trainPrice = new TrainTicketPrice(trainCode, fromStation, toStation, lowestPrice, map.toJSONString());
                trainTicketPriceRepository.save(trainPrice);
            }
        }
    }

}
