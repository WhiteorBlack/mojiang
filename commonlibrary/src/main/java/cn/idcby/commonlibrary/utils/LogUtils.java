package cn.idcby.commonlibrary.utils;

import android.util.Log;

import cn.idcby.commonlibrary.BuildConfig;

/**
 * Created on 2016/8/9.
 */
public class LogUtils {
    private static final int MAX_LOG_LENGTH = 3500 ;
    private static boolean isDeBug = BuildConfig.DEBUG;
    public final static String TAG = "mrrlb";

    public static void showLog(String tag, String msg) {

        if (isDeBug) {
            if (msg != null){
                if (msg.length() > MAX_LOG_LENGTH) {
                    for (int i = 0; i < msg.length(); i += MAX_LOG_LENGTH) {
                        //当前截取的长度<总长度则继续截取最大的长度来打印
                        if (i + MAX_LOG_LENGTH < msg.length()) {
                            Log.i(tag + i, msg.substring(i, i + MAX_LOG_LENGTH));
                        } else {
                            //当前截取的长度已经超过了总长度，则打印出剩下的全部信息
                            Log.i(tag + i, msg.substring(i, msg.length()));
                        }
                    }
                } else {
                    //直接打印
                    Log.i(tag, msg);
                }
            }

        }
    }

    public static void showLog(String msg) {

        if (isDeBug) {
            if (msg != null){
                if (msg.length() > MAX_LOG_LENGTH) {
                    for (int i = 0; i < msg.length(); i += MAX_LOG_LENGTH) {
                        //当前截取的长度<总长度则继续截取最大的长度来打印
                        if (i + MAX_LOG_LENGTH < msg.length()) {
                            Log.i(TAG + i, msg.substring(i, i + MAX_LOG_LENGTH));
                        } else {
                            //当前截取的长度已经超过了总长度，则打印出剩下的全部信息
                            Log.i(TAG + i, msg.substring(i, msg.length()));
                        }
                    }
                } else {
                    //直接打印
                    Log.i(TAG, msg);
                }
            }
        }
    }

}
