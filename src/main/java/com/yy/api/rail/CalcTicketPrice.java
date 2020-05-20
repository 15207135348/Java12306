package com.yy.api.rail;

import com.yy.constant.TrainType;

public class CalcTicketPrice {

    public static void main(String[] args) {

        double v = calc("G101", "一等座", 1318);
        System.out.println(v);
    }

    private static double calc(String trainCode, String seatType, double distance) {
        
        TrainType trainType = TrainType.getTrainType(trainCode);
        if (trainType == TrainType.G){
            if (seatType.endsWith("二等座")){
                return 0.485 * distance;
            }
            if (seatType.endsWith("一等座")){
                return 0.775 * distance;
            }
        }
        if (trainType == TrainType.D) {
            if (seatType.endsWith("二等座")){
                return 0.30855 * distance;
            }
            if (seatType.endsWith("一等座")){
                return 0.37026 * distance;
            }
        }
        else
        {
            double a = price1(distance);
            double b = price2(a, trainType);
            double c = price3(a, trainType);
            double d = price4(a, seatType);
            double e = price5(a, b, c, d, trainType);
            double f = price6();
            double g = price7(seatType);
            double h = price8(distance);
            return a + b + c + d + e + f + g + h;
        }
        return 0;
    }

    /**
     * 基本票价基本是按照里程计算出来的，基本票价的票价率是0.05861元/公里。
     * 但是，基本票价不能够直接用“票价率”×“里程”。
     * 因为考虑到旅客运输的单位成本会随着里程的增加而递减，所以客票有一个“递远递减”票价率。也就是说，里程越长，每公里的平均票价就越低。
     * 具体的递远递减率如下：
     * 1-200公里——0.05861元/公里(相当于基本票价率的100％)
     * 201-500公里——0.05275元/公里(相当于基本票价率的90％)
     * 501-1000公里——0.04689元/公里(相当于基本票价率的80％)
     * 1001-1500公里——0.04102元/公里(相当于基本票价率的70％)
     * 1501-2000公里——0.03517元/公里(相当于基本票价率的60％)
     * 3001公里及以上——0.02931元/公里(相当于基本票价率的50％)
     */
    private static double price1(double d) {

        final double r = 0.05861;
        if (d <= 200) {
            return r * d;
        }
        if (d <= 500) {
            return r * 200 + r * 0.9 * (d - 200);
        }
        if (d <= 1000) {
            return r * 200 + r * 0.9 * 300 + r * 0.8 * (d - 500);
        }
        if (d <= 1500) {
            return r * 200 + r * 0.9 * 300 + r * 0.8 * 500 + r * 0.7 * (d - 1000);
        }
        if (d <= 2000) {
            return r * 200 + r * 0.9 * 300 + r * 0.8 * 500 + r * 0.7 * 500 + r * 0.6 * (d - 1500);
        }
        return r * 200 + r * 0.9 * 300 + r * 0.8 * 500 + r * 0.7 * 500 + r * 0.6 * 500 + r * 0.5 * (d - 2000);
    }


    /**
     * 如果乘坐的火车是普快列车，那么：
     * 加快票价＝基本票价×20％
     * 如果乘坐的火车是K字头的快速列车，以及T字头的特快列车，Z字头的直达特快列车，那么：
     * 加快票价＝基本票价×40％
     */
    private static double price2(double price1, TrainType trainType) {
        if (trainType == TrainType.O) {
            return price1 * 0.2;
        } else {
            return price1 * 0.4;
        }
    }

    /**
     * 有空调的火车
     * 空调票价＝基本票价×25％
     */
    private static double price3(double price1, TrainType trainType) {
        if (trainType.isHasAirConditioning()) {
            return price1 * 0.25;
        }
        return 0;
    }

    /**
     * 硬卧上卧铺票价＝基本票价×110％
     * 硬卧中卧铺票价＝基本票价×120％
     * 硬卧下卧铺票价＝基本票价×130％
     * 软卧上卧铺票价＝基本票价×175％
     * 软卧下卧铺票价＝基本票价×195％
     */
    private static double price4(double price1, String seatType) {
        if (seatType.contains("硬卧")) {
            return price1 * 1.1;
        }
        if (seatType.contains("软卧")) {
            return price1 * 1.75;
        }
        return 0;
    }

    /**
     * 新型空调列车票价＝(基本票价+加快票价+空调票价+卧铺票价)×50％
     */
    private static double price5(double price1, double price2, double price3, double price4, TrainType trainType) {
        if (trainType.isNewAirConditioning()) {
            return (price1 + price2 + price3 + price4) * 0.5;
        }
        return 0;
    }

    /**
     * 电脑打印的车票，要付1元
     */
    private static double price6() {
        return 1;
    }

    /**
     * 卧铺票要加收订票费10元
     */
    private static double price7(String seatType) {
        if (seatType.contains("卧")) {
            return 10;
        }
        return 0;
    }


    /**
     * 里程超过200公里的长途旅客列车，收候车室空调费1元
     */
    private static double price8(double distance) {
        if (distance > 200) {
            return 1;
        }
        return 0;
    }
    
}
