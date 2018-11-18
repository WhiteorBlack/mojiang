package cn.idcby.jiajubang.utils;

import android.content.Context;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import cn.idcby.commonlibrary.utils.AppUtils;
import cn.idcby.commonlibrary.utils.LogUtils;

/**
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2017/3/1.
 */

public class NetUtils {


    public static void getDataFromServerByPost(Context mContext, String url
            , boolean isLogin, Map<String, String> para, StringCallback callback) {

        PostFormBuilder postFormBuilder = OkHttpUtils
                .post()
                .url(url);

        if (isLogin) {
            if(para != null){
                para.put("Version",StringUtils.convertNullNoTrim(AppUtils.getInstance(mContext).getVersionName())) ;
                para.put("AppId",AppUtils.APPID) ;
                para.put("AppSecret",AppUtils.APPSECRET) ;
                para.put("DevSysInfo",StringUtils.convertNullNoTrim(AppUtils.getInstance(mContext).getDevSysInfo())) ;
                para.put("IMEI",StringUtils.convertNullNoTrim(AppUtils.getInstance(mContext).getIMEL())) ;
            }
        }

        if (para != null){
            for (Map.Entry<String, String> entry : para.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                postFormBuilder.addParams(StringUtils.convertNullNoTrim(key)
                        , StringUtils.convertNullNoTrim(value));
            }

            LogUtils.showLog("RequestParam" , "url=" + url + ",******params=" + para.toString()) ;
        }

        postFormBuilder.build()
                .execute(callback);

    }

    public static void getDataFromServerByPost(Context mContext, String url, Map<String, String> para, StringCallback callback) {
        PostFormBuilder postFormBuilder = OkHttpUtils
                .post()
                .url(url);

        if (para != null){
            for (Map.Entry<String, String> entry : para.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                postFormBuilder.addParams(StringUtils.convertNullNoTrim(key)
                        , StringUtils.convertNullNoTrim(value));
            }

            LogUtils.showLog("RequestParam" , "url=" + url + ",******params=" + para.toString()) ;
        }

        postFormBuilder.build()
                .execute(callback);

    }


    public static void getDataFromServerByGet(Context mContext, String url
            , StringCallback callback) {

        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(callback);

    }

    public static void getDataFromServerByGet(Context mContext, String url
            , String headKey, String headValue, StringCallback callback) {

        OkHttpUtils
                .get()
                .addHeader(headKey, headValue)
                .url(url)
                .build()
                .execute(callback);

    }

    public static void cancelTag(String... tags){
        for(String tag : tags){
            OkHttpUtils.getInstance().cancelTag(tag);
        }
    }
}
