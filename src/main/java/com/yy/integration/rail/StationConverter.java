package com.yy.integration.rail;

import com.yy.integration.API12306;
import com.yy.other.factory.SessionFactory;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationConverter {

    private static final Logger LOGGER = Logger.getLogger(StationConverter.class);

    private static Map<String, String> stationMap = new HashMap<>();
    private static List<String> stations = new ArrayList<>();
    private static final String path = "res/doc/station.txt";

    static {
        String data = API12306.getStations(SessionFactory.getSession(null));
        if (data == null) {
            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                data = bufferedReader.readLine();
            } catch (IOException e) {
                LOGGER.error("init stationMap failed");
                LOGGER.error(e.getMessage());
            }
        }
        if (data != null) {
            String[] strings = data.split("@");
            for (String s : strings) {
                String[] split = s.split("\\|");
                stationMap.put(split[1], split[2]);
                stationMap.put(split[2], split[1]);
                stations.add(split[1]);
            }
        }
    }

    public static String getCodeByName(String name) {
        return stationMap.get(name);
    }

    public static String getNameByCode(String code) {
        return stationMap.get(code);
    }

    public static List<String> getStations() {
        return stations;
    }
}
