package cn.idcby.commonlibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by mrrlb on 2016/11/7.
 */
public class AppUtils {

    public final static String APPID = "AndroidJJB001";
    public final static String APPSECRET = "HNZCjjbApp";
//    public final static String DES_KEY = "idcby001";
//    public static final String JIAMI = "ZICBDYC";

    private Context mContext;

    public AppUtils(Context mContext) {
        this.mContext = mContext;
    }


    public static AppUtils getInstance(Context mContext) {
        return new AppUtils(mContext);
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public String getVersionName() {
        String version = null;
        try {

            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public String getDevSysInfo() {
        return android.os.Build.VERSION.RELEASE;
    }

    public String getDevTypeInfo() {
        return Build.MODEL;
    }


    public String getIMEL() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";
        try {
            imei = telephonyManager.getDeviceId();

            if(!TextUtils.isEmpty(imei)){
                return imei;
            }

            //序列号（sn）
            String sn = telephonyManager.getSimSerialNumber();

            if(!TextUtils.isEmpty(sn)){
                return sn;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return imei;
        }
        return imei;
    }


    /**
     * 获取当前程序的版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int version = packInfo.versionCode;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static boolean isAppMastUpdate(Context context){
        return getVersionCode(context) < 39 ;
    }
}
