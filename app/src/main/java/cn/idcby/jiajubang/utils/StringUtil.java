package cn.idcby.jiajubang.utils;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;

public class StringUtil {

    public static boolean isEmpty(String value) {
        return TextUtils.isEmpty(value);
    }

    public static String getString(int resId, Object... formatArgs) {
        return MyApplication.getInstance().getString(resId, formatArgs);
    }

    public static int getColor(int resId) {
        return ContextCompat.getColor(MyApplication.getInstance(), resId == 0 ? R.color.transparent : resId);
    }

    public static Drawable getDrawable(int resId) {
        return resId == 0 ? null : ContextCompat.getDrawable(MyApplication.getInstance(), resId);
    }

    public static void setDrawableLeft(TextView textView, int resId) {
        setDrawableLeft(MyApplication.getInstance(), resId, textView, 5);
    }

    public static void setDrawableLeft(Context context, int id, TextView tv, int DrawablePaddin) {
        if (id != -1) {
            Drawable nav_up = context.getResources().getDrawable(id);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            tv.setCompoundDrawablePadding(DrawablePaddin);
            tv.setCompoundDrawables(nav_up, null, null, null);
        } else {
            tv.setCompoundDrawables(null, null, null, null);
        }

    }

    public static void setDrawableRight(Context context, int id, TextView tv, int DrawablePaddin) {
        if (id != -1) {
            Drawable nav_up = context.getResources().getDrawable(id);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            tv.setCompoundDrawablePadding(DrawablePaddin);

            tv.setCompoundDrawables(null, null, nav_up, null);
        } else {
            tv.setCompoundDrawables(null, null, null, null);
        }

    }

    public static String getUrlValue(String url, String key) {
        int start = url.indexOf(key) + key.length();
        int i = url.indexOf("&", start);
        return url.substring(start, i == -1 ? url.length() : i);
    }

    /*首字母大写*/
    public static String capitalize(String value) {
        if (isEmpty(value)) return "";
        String first = value.substring(0, 1).toUpperCase();
        return value.length() == 1 ? first : first + value.substring(1);
    }

    public static boolean checkPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        final String passwordRegular = "^[a-zA-Z0-9]{6,15}";
        Pattern p = Pattern.compile(passwordRegular);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        CharSequence inputStr = phoneNumber;
        //正则表达式
        String phone = "^1[34578]\\d{9}$";
        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void setMidLine(TextView textView, double f) {
        String s = get2String(f);
        textView.setText(s);
        TextPaint paint = textView.getPaint();
        paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    public static void getMidLine(TextView textView, String f) {
        if (TextUtils.isEmpty(f)) {
            return;
        }
        String s = get2String(Double.parseDouble(f));
        textView.setText(s);
        TextPaint paint = textView.getPaint();
        paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    public static String getSellPrice(double orgin, String capture) {
        String priString = "";
        if (TextUtils.isEmpty(capture)) {

            priString = "￥" + String.format("%.2f", orgin);
        } else {
            double capPrice = Double.parseDouble(capture);
            if (orgin > capPrice && capPrice > 0) {
                priString = "￥" + String.format("%.2f", capPrice);
            } else {
                priString = "￥" + String.format("%.2f", orgin);
            }
        }
        return priString;
    }

    public static String getPrice(TextView textView, double f) {
        TextPaint paint = textView.getPaint();
        paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        String s = "￥" + String.format("%.2f", f);
        return s;
    }

    public static String get2String(String f) {
        if (TextUtils.isEmpty(f)) {
            return "";
        } else {

            return "￥" + String.format("%.2f", Double.parseDouble(f));
        }
    }

    public static String get2String(double f) {
        return "￥" + String.format("%.2f", f);
    }

    /**
     * 判断库存是否为空
     *
     * @param stock
     * @return
     */
    public static boolean isStockEmpty(String stock) {
        if (TextUtils.isEmpty(stock)) {
            return true;
        }
        if (Integer.parseInt(stock) > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String getDealCount(String count){
        return "成交"+count+"单";
    }
    public static String getServerMoney(String count){
        return "¥"+count+"起";
    }
}
