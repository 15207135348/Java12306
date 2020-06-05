package com.yy.integration.rail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yy.integration.API12306;
import com.yy.other.constant.SeatType;
import com.yy.other.constant.TicketField;
import com.yy.dao.entity.TrainTicketPrice;
import com.yy.other.domain.HttpSession;
import com.yy.other.domain.Train;
import com.yy.common.util.TimeFormatUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class TicketInquirer {

    private static final Logger LOGGER = Logger.getLogger(TicketInquirer.class);


    public static List<Train> getTrains(HttpSession session, String date, String fromStation, String toStation, boolean useProxy) {

        //验证车站号是否正确
        String fromStationCode = StationConverter.getCodeByName(fromStation);
        String toStationCode = StationConverter.getCodeByName(toStation);
        if (fromStation == null || toStation == null) {
            LOGGER.error(String.format("getTrains: 找不到车站【%s】和【%s】", fromStation, toStation));
            return null;
        }
        JSONArray array = API12306.queryTickets(session, date, fromStationCode, toStationCode, useProxy);
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

    public static long getExpireTime(HttpSession session, String dates, String fromStation, String toStation, String trainCodes) {

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
                //抢票至开车前40分钟
                expireTime = timestamp - 2400000;
                break;
            }
        }
        return expireTime;
    }

    public static TrainTicketPrice queryTrainPrice(HttpSession session, String fromStation, String toStation, String trainCode) {

        String date = TimeFormatUtil.stamp2Date(System.currentTimeMillis() + 240 * 3600000, "yyyy-MM-dd");
        //验证车站号是否正确
        String fromStationCode = StationConverter.getCodeByName(fromStation);
        String toStationCode = StationConverter.getCodeByName(toStation);
        if (fromStation == null || toStation == null) {
            LOGGER.error(String.format("gettrain: 找不到车站【%s】和【%s】", fromStation, toStation));
            return null;
        }
        JSONArray array = API12306.queryTickets(session, date, fromStationCode, toStationCode, true);
        if (array == null) {
            LOGGER.error("查询余票失败，检查IP是否被封？");
            return null;
        }
        String trainNo = null, fromStationNo = null, toStationNo = null, seatTypes = null;
        for (int i = 0; i < array.size(); ++i) {
            String string = array.getString(i);
            String[] ticketInfo = string.split("\\|");
            if (trainCode.equals(ticketInfo[TicketField.TRAIN_CODE.getI()])) {
                trainNo = ticketInfo[TicketField.TRAIN_NO.getI()];
                fromStationNo = ticketInfo[TicketField.FROM_STATION_NO.getI()];
                toStationNo = ticketInfo[TicketField.TO_STATION_NO.getI()];
                seatTypes = ticketInfo[TicketField.SEAT_TYPES.getI()];
                break;
            }
        }
        if (trainNo == null) {
            return null;
        }
        JSONObject prices = API12306.queryTicketPrices(session, trainNo,
                fromStationNo, toStationNo, seatTypes, date);
        if (prices == null) {
            return null;
        }
        LOGGER.info(String.format("【%s】列车查询车票价格成功", trainCode));
        JSONObject map = new JSONObject();
        for (String key : prices.keySet()) {
            SeatType seatType = SeatType.find(key);
            if (seatType != null) {
                map.put(seatType.getName(), prices.getString(key));
            }
        }
        String lowestPrice = prices.getString("WZ");
        return new TrainTicketPrice(trainCode, fromStation, toStation, lowestPrice, map.toJSONString());
    }
}
