package cn.idcby.commonlibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期比较
 * Created by Zg on 2016/9/18.
 */
public class DateCompareUtils {

    /**
     * 展示消息提示 0：date1大  1：date2大  -1：error
     * @param date1 date1
     * @param date2 date2
     * @return true date1 大于或等于 date2
     */
    public static Boolean compareDay(String date1, String date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date time1 = simpleDateFormat.parse(date1);
            Date time2 = simpleDateFormat.parse(date2);
            if (time1.getTime() - time2.getTime() >= 0) {
                return true;//date1大于date2 或等于
            } else {
                return false;//date2 时间戳较大
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 比较日期大小，精确到day
     * @param date1 date1
     * @param date2 date2
     * @return true date1 小于 date2
     */
    public static Boolean compareDayLess(String date1, String date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date time1 = simpleDateFormat.parse(date1);
            Date time2 = simpleDateFormat.parse(date2);
            if (time1.getTime() - time2.getTime() < 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 比较日期大小，精确到day
     * @param date1 date1
     * @param date2 date2
     * @return true date1 大于 date2
     */
    public static Boolean compareDayBiger(String date1, String date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date time1 = simpleDateFormat.parse(date1);
            Date time2 = simpleDateFormat.parse(date2);
            if (time1.getTime() - time2.getTime() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}
