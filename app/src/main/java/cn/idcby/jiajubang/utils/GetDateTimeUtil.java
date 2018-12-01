package cn.idcby.jiajubang.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * 获取要选择的时间
 * 时间、秒数
 * Created by Slingge on 2016/10/14 0014.
 */

public class GetDateTimeUtil {


    /**
     * 获取未来num天日期
     */
    public static List<String> getSevendate(int num) {
        List<String> dates = new ArrayList<>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int year = c.get(Calendar.YEAR);// 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int day = MaxDayFromDay_OF_MONTH(year, mMonth);//当月最大日期
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当前日份的日期号码

        for (int i = 0; i < num; i++) {
            if (mDay > day) {
                if (mMonth == 12) {
                    mMonth = 1;
                    year = year + 1;
                } else {
                    mMonth = mMonth + 1;
                }
                mDay = 1;
            }
            String date = year + "/" + mMonth + "/" + mDay;
            dates.add(date);
            mDay++;
        }
        return dates;
    }

    /**
     * 获取格式年月日
     */
    public static String getYMD() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int year = c.get(Calendar.YEAR);// 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int day = c.get(Calendar.DAY_OF_MONTH);
        return year + "/" + mMonth + "/" + day;
    }


    /**
     * 获取格式年月日
     */
    private static SimpleDateFormat sdf;

    public static String getYMDHMS() {
        long l = System.currentTimeMillis();
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return sdf.format(l);
    }


    /**
     * 得到当年当月的最大日期
     **/
    public static int MaxDayFromDay_OF_MONTH(int years, int months) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, years);
        time.set(Calendar.MONTH, months - 1);//注意,Calendar对象默认一月为0
        int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
        return day;
    }


    /**
     * 获可选择的营业时间
     */
    public static List<String> getSevenTime(int st, int en) {
        List<String> times = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            if (st == en) {
                break;
            }
            times.add(st + ":" + "00");
            times.add(st + ":" + "30");
            st++;
        }
        return times;
    }


    /**
     * 获取当天可选时间
     */
    public static List<String> getBusinessTime(int num) {
        List<String> times = new ArrayList<>();
        final Calendar mCalendar = Calendar.getInstance();
        long time = System.currentTimeMillis();
        mCalendar.setTimeInMillis(time);
        int mHour = 0;
        if (num == 0) {//当天的日期选择当前时间一小时之后
            mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        }
        for (int i = 0; i < 24 - mHour; i++) {
            if ((mHour + 1 + i) == 24) {
                times.add("00");
            } else {
                times.add(mHour + 1 + i + "");
            }
        }
        return times;
    }

    /**
     * 获取分钟
     */
    public static List<String> getMin() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            if ((i + "").length() == 1) {
                list.add("0" + i + "");
            } else {
                list.add(i + "");
            }
        }
        return list;
    }


    /**
     * 返回年份
     */
    public static List<String> getYear() {
        List<String> yearList = new ArrayList<>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int year = c.get(Calendar.YEAR);// 获取当前年份
        for (int i = year; i > 1950; i--) {
            yearList.add(i + "");
        }
        return yearList;
    }

    /**
     * 返回月份
     */
    public static List<String> getMon() {
        List<String> monList = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            if ((i + "").length() == 1) {
                monList.add("0" + i + "");
            } else {
                monList.add(i + "");
            }
        }
        return monList;
    }

    /**
     * 返回30天
     */
    public static List<String> get30Day() {
        List<String> day30List = new ArrayList<>();
        for (int i = 1; i < 31; i++) {
            if ((i + "").length() == 1) {
                day30List.add("0" + i + "");
            } else {
                day30List.add(i + "");
            }
        }
        return day30List;
    }

    /**
     * 返回天数
     */
    public static List<String> getDay(String year, String mon) {
        List<String> dayList = new ArrayList<>();
        for (int i = 1; i < MaxDayFromDay_OF_MONTH(Integer.valueOf(year), Integer.valueOf(mon)) + 1; i++) {
            if ((i + "").length() == 1) {
                dayList.add("0" + i + "");
            } else {
                dayList.add(i + "");
            }
        }
        return dayList;
    }

    /**
     * 借款时间是否超过2年，720天
     */
    public boolean more2Year(long l) {
        long yearL = dayToMsec("720");
        if (l > yearL) {
            return true;
        }
        return false;
    }

    /**
     * 借款时间是否小于未来7天
     */
    public boolean min7Day(long l) {
        long yearL = dayToMsec("7");
        if (l < yearL) {
            return true;
        }
        return false;
    }


    /**
     * 毫秒转时间
     */
    public static String toDate(long l) {
        long Imm = new Date().getTime();
        SimpleDateFormat sdf;
        if (Imm - l < 86400) {
            sdf = new SimpleDateFormat("HH:mm");
        } else {
            sdf = new SimpleDateFormat("MM-dd HH:mm");
        }
        return sdf.format(l);
    }


    /**
     * 秒数转日期
     */
    public static String longToDate(long l) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(l);
    }


    /**
     * 获取当前秒数
     */
    public long getLong() {
        //这就是距离1970年1月1日0点0分0秒的毫秒数
        return System.currentTimeMillis() / 1000;
    }


    /**
     * 日期转换为毫秒数
     */
    public long dateToMsec(String date) {
        long l = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date d = sdf.parse(date);
            l = (d.getTime() / 1000) + minuteLoanMsec();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * 时分的秒数
     */
    public long minuteLoanMsec() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//时分秒的秒数
        Date d2 = null;
        try {
            d2 = df.parse(df.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d2.getTime() / 1000;
    }

    /**
     * 天数转毫秒数
     */
    public long dayToMsec(String day) {
        long l = Integer.valueOf(day) * 24 * 60 * 60;
        return l + getLong();
    }

    /**
     * 借款时间是否小于一个月
     */
    public boolean lessMonth(long l) {
        long month = dayToMsec("30");//一个月的毫秒数
        if (l <= month) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 借款时间秒数转借款多少天
     */
    public int secondsToDay(long l) {
        int day = (int) (l - getLong() + minuteLoanMsec()) / (24 * 60 * 60);
        return day;
    }

    /**
     * 分几期还款
     */
    public int severalStages(long l) {
        int i = secondsToDay(l) / 30;//30天
        if (i < 1) {
            return 0;
        }
        int Remainder = (secondsToDay(l)) % 30;
        if (Remainder >= 1) {
            return i + 1;
        }
        return i;
    }


    /**
     * 计算每期还款时间
     */
    public String eachTime(long l) {
        long seconds = 30 * 24 * 60 * 60;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (l <= seconds) {
            return sdf.format(l * 1000);
        } else {
            long newl = getLong();
            StringBuilder date = new StringBuilder();
            int i = 0;
            while (newl < l) {
                if (i > 0) {
                    date.append(sdf.format(newl * 1000) + ",");
                }
                i++;
                newl += seconds;
            }
            date.append(sdf.format(l * 1000) + ",");
            String dateStr = date.toString();
            return dateStr.substring(0, dateStr.length() - 1);
        }
    }

    public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static long convert2long(String date) {
        try {
            if (!TextUtils.isEmpty(date)) {
                if (sdf == null) {
                    sdf = new SimpleDateFormat(TIME_FORMAT);
                }
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(date));
                return c.getTimeInMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

}
