package cn.idcby.commonlibrary.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

import cn.idcby.commonlibrary.R;

/**
 * Created on 2018/3/22.
 */

public class ResourceUtils {
    private static TypedValue mTmpValue = new TypedValue();

    /**
     * 获取titlebar高度
     * return  dp高度
     */
    public static int getMyTitleBarHeight(Context context){
        return dip2px(context, getXmlDef(context , R.dimen.titleBar_height));
    }

    public static int getScreenWidth(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }


    /**
     * 获取resource资源xml里面的id的值，就像xml解析，只是取值，不转换（取的是整数，如果是小数，则去除小数点后面的了）
     * @param context
     * @param id
     * @return
     */
    public static int getXmlDef(Context context, int id){
        synchronized (mTmpValue) {
            TypedValue value = mTmpValue;
            context.getResources().getValue(id, value, true);
            return (int)TypedValue.complexToFloat(value.data);
        }
    }

    /**
     * 获取底部虚拟导航栏高度
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();

        int id = resources.getIdentifier(
                resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }

    /**
     * 获取屏幕状态栏高度
     * @param context c
     * @return int
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取view在屏幕中的y坐标
     * @param v v
     * @return point
     */
    public static int getViewOnScreenPoint(View v){
        if(v != null){
            int itemScreenPosi[] = new int[2] ;
            v.getLocationOnScreen(itemScreenPosi);
            return itemScreenPosi[1] ;
        }
        return 0 ;
    }

    /**
     * 获取屏幕原始尺寸高度，包括虚拟功能键高度
     * @param context
     * @return
     */
    public static int getDeviceHeight(Context context){
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi=displayMetrics.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取屏幕高度
     * @return
     */
    public static int getScreenHeight(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static String getPackageUrlScheme(){
        return "package:"  ;
    }

}
