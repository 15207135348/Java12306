package com.yy.service.util;

import com.yy.service.api.API12306Service;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class StationService {

    private static final Logger LOGGER = Logger.getLogger(StationService.class);

    private static Map<String, String> stationMap = new HashMap<>();
    private static List<String> stations = new ArrayList<>();

    @Value("${12306.station_file_path}")
    private String path;
    @Autowired
    private API12306Service api12306Service;
    @Autowired
    private SessionPoolService sessionPoolService;
    @PostConstruct
    void init(){
        String data = api12306Service.getStations(sessionPoolService.getSession(null));
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
        if (data != null)
        {
            String[] strings = data.split("@");
            for (String s : strings)
            {
                String[] split = s.split("\\|");
                stationMap.put(split[1], split[2]);
                stationMap.put(split[2], split[1]);
                stations.add(split[1]);
            }
        }
    }

    public String getCodeByName(String name){
        return stationMap.get(name);
    }

    public String getNameByCode(String code){
        return stationMap.get(code);
    }

    public List<String> getStations()
    {
        return stations;
    }
}
