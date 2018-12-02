package cn.idcby.commonlibrary.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created on 2016/9/18.
 */
public class MyUtils {


    private static final String REGEX_MOBILE =
            "^1\\d{10}$";

    private static final String REGEX_PAYPASS =
            "^\\d{6}$";

    public static final String REGEX_ID_CARD =
            "^[0-9][0-9]{16}[0-9|x|X]$";


    /***
     * 校验手机号是否为正确的格式
     *
     * @param phone
     * @return
     */
    public static boolean isRightPhone(String phone) {
        return phone.matches(REGEX_MOBILE);
    }
    /***
     * 校验手机号是否为正确的格式
     *
     * @param password
     * @return
     */
    public static boolean isRightPayPassWord(String password) {
        return password.matches(REGEX_PAYPASS);
    }


    public static boolean isRigghtIDCard(String idCard) {
        return idCard.matches(REGEX_ID_CARD);
    }

    public static boolean isRightMoney(String money){
        if(TextUtils.isEmpty(money) || (!money.trim().equals("0") && money.trim().startsWith("0") && !money.trim().startsWith("0."))){
            return false ;
        }
        return true ;
    }

    /***
     * 从下到上
     */
    public static void arrowAnimationBottomToTop(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 180f);
        animator.setDuration(200);
        animator.start();
    }

    /***
     * 从上到下
     */
    public static void arrowAnimationTopToBottom(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 180f, 0f);
        animator.setDuration(200);
        animator.start();
    }

    /***
     * 保留小数点一位
     *
     * @return
     */
    public static String getDoubleFor1(double number) {
        DecimalFormat df = new DecimalFormat("#.0");
        String numberFormat = df.format(Double.valueOf(number));
        return numberFormat;
    }

    /***
     * 拨打电话
     *
     * @param context
     * @param phone
     */
    public static void dialPhone(Context context, String phone) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    /***
     * 获取版本号
     */
    public static String getVersionName(Context context) {
        try {

            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String version = packageInfo.versionName;
//            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
//            String label = packageManager.getApplicationLabel(applicationInfo).toString();
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /***
     * 获取版本编号
     */
    public static int getVersionCode(Context context) {

        int versionCode = 0;
        try {

            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    public static void setViewDisappearWithAnima(View view) {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    public static boolean isEmpty(String string) {
        if (string == null) {
            return true;
        } else if ("null".equals(string)) {
            return true;
        } else {
            return TextUtils.isEmpty(string);
        }

    }

    public static String getDayByDate(String time) {
        String day = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(time);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            day = String.valueOf(calendar.get(Calendar.DATE));

        } catch (ParseException e) {
            e.printStackTrace();

        } finally {
            return day;
        }

    }


    public static String getMonthByDate(String time) {
        String month = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(time);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            month = String.valueOf(calendar.get(Calendar.MONTH) + 1);

        } catch (ParseException e) {
            e.printStackTrace();

        } finally {
            return month;
        }

    }


    public static String getYearByDate(String time) {
        String year = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(time);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            year = String.valueOf(calendar.get(Calendar.YEAR));

        } catch (ParseException e) {
            e.printStackTrace();

        } finally {
            return year;
        }

    }


    public static String cutString(int headCount, int footCount, String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String headString = content.substring(0, headCount);
        String footString = content.substring(content.length() - footCount);
        sb.append(headString)
                .append("****")
                .append(footString);
        return sb.toString();
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
            listItem.measure(desiredWidth, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void setBageShow(TextView tv, int number) {
        if (number == 0) {
            tv.setText("0");
//            tv.setVisibility(View.INVISIBLE);
        } else {
            tv.setVisibility(View.VISIBLE);
            if (number <= 99) {
                tv.setText(String.valueOf(number));
            } else {
                tv.setText("99+");
            }
        }
    }


    /**
     * 获取file的uri
     * @param file file
     * @return uri
     *
     * 需要适配 android N
     */
    public static Uri getFileUri(Context context , File file){
        if(null == file || null == context){
            return null ;
        }

        Uri uri ;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(context, "cn.idcby.jiajubang.fileprovider", file);
        }else{
            uri = Uri.fromFile(file) ;
        }

        return uri ;
    }
}
