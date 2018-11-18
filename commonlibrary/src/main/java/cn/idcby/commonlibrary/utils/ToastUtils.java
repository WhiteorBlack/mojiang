package cn.idcby.commonlibrary.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.idcby.commonlibrary.R;


/**
 * Created on 2016/8/5.
 */
public class ToastUtils {

    private static Toast toast = null;
    private static Toast toastText = null;

    public static void showToast(Context context, String content) {
        if(null == toastText){
            toastText = Toast.makeText(context, content, Toast.LENGTH_SHORT) ;
            toastText.setGravity(Gravity.CENTER , 0, 100);
        }else{
            toastText.setText(content) ;
        }
        toastText.show();
    }

    public static void showNetErrorToast(Context context) {

        View toastView = View.inflate(context, R.layout.layout_toast, null);
        TextView tvContent = (TextView) toastView.findViewById(R.id.tv_desc);
        ImageView imgIcon = (ImageView) toastView.findViewById(R.id.img_icon);
        tvContent.setText("网络连接异常,请检查网络设置");
        imgIcon.setImageResource(R.mipmap.no_net);
        if (toast == null)
            toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();

    }


    public static void showErrorToast(Context context, String content) {
        View toastView = View.inflate(context, R.layout.layout_toast, null);
        TextView tvContent = (TextView) toastView.findViewById(R.id.tv_desc);
        ImageView imgIcon = (ImageView) toastView.findViewById(R.id.img_icon);
        tvContent.setText(content);
        imgIcon.setImageResource(R.mipmap.toast_error);
        if (toast == null)
            toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }


    public static void showOkToast(Context context, String content) {
        View toastView = View.inflate(context, R.layout.layout_toast, null);
        TextView tvContent = (TextView) toastView.findViewById(R.id.tv_desc);
        ImageView imgIcon = (ImageView) toastView.findViewById(R.id.img_icon);
        tvContent.setText(content);
        imgIcon.setImageResource(R.mipmap.ok);
        if (toast == null)
            toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }


    public static void showToastWithImage(Context context, String content, int imgID) {

        View toastView = View.inflate(context, R.layout.layout_toast, null);
        TextView tvContent = (TextView) toastView.findViewById(R.id.tv_desc);
        ImageView imgIcon = (ImageView) toastView.findViewById(R.id.img_icon);
        tvContent.setText(content);
        imgIcon.setImageResource(imgID);
        if (toast == null)
            toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }


    public static void showServerErrorToast(Context context) {

        View toastView = View.inflate(context, R.layout.layout_toast, null);
        TextView tvContent = (TextView) toastView.findViewById(R.id.tv_desc);
        ImageView imgIcon = (ImageView) toastView.findViewById(R.id.img_icon);
        tvContent.setText("连接不到服务器");
        imgIcon.setImageResource(R.mipmap.no_net);
        if (toast == null)
            toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();

    }

}
