import com.alibaba.fastjson.JSONArray;
import com.yy.StartApplication;
import com.yy.domain.Session;
import com.yy.service.api.API12306Service;
import com.yy.service.util.SessionPoolService;
import com.yy.service.util.StationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartApplication.class)
public class QueryTicketTest {

    @Autowired
    API12306Service api12306Service;
    @Autowired
    SessionPoolService sessionPoolService;
    @Autowired
    StationService stationService;

    @Test
    public void testQuery() throws IOException {
        Session session = sessionPoolService.getSession(null);
        String date = "2020-04-30";
        String from = stationService.getCodeByName("北京");
        String to = stationService.getCodeByName("上海");
        JSONArray array = api12306Service.queryTickets(session, date, from, to);
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream("QueryTicketTest.csv"), "GBK");
        BufferedWriter bw = new BufferedWriter(ow);
        for (int i = 0; i < array.size(); ++i) {
            String str = array.getString(i);
            if (i == 0) {
                int len = str.split("\\|").length;
                String[] heads = new String[len];
                for (int j = 0; j < len; ++j) {
                    heads[j] = String.valueOf(j);
                }
                bw.write(String.join(",", heads));
                bw.newLine();
            }
            String[] strings = str.split("\\|");
            bw.write(String.join(",", strings));
            bw.newLine();
        }
        bw.close();
    }

}
