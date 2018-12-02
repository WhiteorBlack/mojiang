package cn.idcby.jiajubang.utils;

import android.content.Context;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import cn.idcby.jiajubang.R;

/**
 * Created on 2018/3/28.
 */

public class StringUtils {


    /***********************************部分固定描述*************************************/

    public static String getPersonApplyTips(Context context){
        return "请先完成" + context.getResources().getString(R.string.apply_person_str) ;
    }

    /***********************************部分固定描述*************************************/



    /**
     * 判断null
     * @param temp temp
     * @return temp
     */
    public static String convertNull(String temp){
        if(null == temp){
            return "" ;
        }
        return temp.trim() ;
    }

    /**
     * 判断http
     * @param temp temp
     * @return temp
     */
    public static String convertHttpUrl(String temp){
        if(TextUtils.isEmpty(temp)){
            return "" ;
        }

        if(!temp.startsWith("http")){
            return "http://" + temp ;
        }
        return temp.trim() ;
    }



    /**
     * 判断null
     * @param temp temp
     * @return temp
     */
    public static String convertNullNoTrim(String temp){
        if(null == temp){
            return "" ;
        }
        return temp ;
    }


    /**
     * 把工作年份后面加上 年经验
     * @param workYear 3
     * @return 3年经验
     */
    public static String convertWorkYearExp(String workYear){
        if(null == workYear || "".equals(workYear.trim())){
            return "" ;
        }

        if(isNum(workYear) || isNum1(workYear)){
            return workYear + "年经验" ;
        }

        return workYear ;
    }

    /**
     * 把年龄后面加上 岁
     * @param age 30
     * @return 30岁
     */
    public static String convertAge(String age){
        if(null == age || "".equals(age.trim())){
            return "" ;
        }
        if(age.endsWith("岁")){
            return age ;
        }else{
            return age + "岁" ;
        }
    }

    /**
     * 把年龄后面去掉 岁
     * @param age 30岁
     * @return 30
     */
    public static String convertAgeNumber(String age){
        if(null == age || "".equals(age.trim())){
            return "" ;
        }
        if(age.endsWith("岁")){
            return age.substring(0,age.length() - 1) ;
        }else{
            return age;
        }
    }

    public static String convertStringNoPoint(String temp){
        if(null == temp || "".equals(temp.trim())){
            return "" ;
        }

        if(temp.endsWith(".00") || temp.endsWith(".0")){
            return temp.substring(0 ,temp.indexOf(".")) ;
        }

        return temp ;
    }

    /**
     * string --- float
     * @param temp string
     * @return float
     */
    public static float convertString2Float(String temp){
        if(null == temp || "".equals(temp.trim())){
            return 0F ;
        }

        try {
            DecimalFormat format = new DecimalFormat("0.00") ;
            return Float.valueOf(format.format(Float.valueOf(temp))) ;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0F ;
        }
    }

    /**
     * string --> count
     * @param temp tem
     * @return count
     */
    public static int convertString2Count(String temp){
        if(null == temp || "".equals(temp.trim())){
            return 0 ;
        }

        if(temp.endsWith(".00")||temp.endsWith(".0")){
            temp = temp.substring(0,temp.lastIndexOf(".")) ;
        }

        try {
            int count = Integer.valueOf(temp) ;
            return count < 0 ? 0 : count ;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0 ;
    }

    /**
     * 把日期精确到天
     * @param temp 2018-04-24 19:43:51
     * @return 2018-04-24
     */
    public static String convertDateToDay(String temp){
        if(TextUtils.isEmpty(temp)){
            return "" ;
        }
        if(temp.trim().contains(" ")){
            return temp.split(" ")[0] ;
        }else{
            return temp ;
        }
    }


    public static boolean isNum(String str){
        Pattern pattern = Pattern.compile("^-?[0-9]+");
        if(pattern.matcher(str).matches()){
            //数字
            return true;
        } else {
            //非数字
            return false;
        }
    }

    public static boolean isNum1(String str){
        //带小数的
        Pattern pattern = Pattern.compile("^[-+]?[0-9]+(\\.[0-9]+)?$");

        if(pattern.matcher(str).matches()){
            //数字
            return true;
        } else {
            //非数字
            return false;
        }
    }

    public static String getDistance(double distance){
        int disInte= (int) distance;
        String disString="";
        if (disInte<100){
            disString="距离<100米";
        }else if (disInte<1000){
            disString="距离<1km";
        }else {
            disString="距离"+disInte/1000+"km";
        }
        return disString;
    }

}
