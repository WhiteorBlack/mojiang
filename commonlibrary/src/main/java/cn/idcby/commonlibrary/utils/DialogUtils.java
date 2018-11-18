package cn.idcby.commonlibrary.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import cn.idcby.commonlibrary.view.MyCustomDialog;

/**
 * Created on 2018/3/23.
 */

public class DialogUtils {

    public static void showCustomViewDialog(Context context , String message
           , String posiText , DialogInterface.OnClickListener listener1){
        showCustomViewDialog(context , "温馨提示" , message , null ,posiText ,listener1) ;
    }

    /**
     * 自定义dialog
     * @param context context
     * @param title title
     * @param message message
     * @param contentView if != null 显示这个view，否则就显示message
     * @param posiText 按钮1名称
     * @param listener1 按钮1监听
     * @param nageText 按钮2名称
     * @param listener2 按钮2监听
     */
    public static void showCustomViewDialog(Context context , String title , String message
            , View contentView , String posiText , DialogInterface.OnClickListener listener1 , String nageText , DialogInterface.OnClickListener listener2){

        MyCustomDialog.Builder builder = new MyCustomDialog.Builder(context) ;
        if(title != null){
            builder.setTitle(title) ;
        }
        if(message != null){
            builder.setMessage(message) ;
        }
        if(contentView != null){
            builder.setContentView(contentView) ;
        }
        builder.setPositiveButton(posiText, listener1) ;
        builder.setNegativeButton(nageText, listener2) ;
        builder.create().show();
    }
    /**
     * 自定义dialog
     * @param context context
     * @param title title
     * @param message message
     * @param contentView if != null 显示这个view，否则就显示message
     * @param posiText 按钮1名称
     * @param listener1 按钮1监听
     */
    public static void showCustomViewDialog(Context context , String title , String message
            , View contentView , String posiText , DialogInterface.OnClickListener listener1){

        MyCustomDialog.Builder builder = new MyCustomDialog.Builder(context) ;
        if(title != null){
            builder.setTitle(title) ;
        }
        if(message != null){
            builder.setMessage(message) ;
        }
        if(contentView != null){
            builder.setContentView(contentView) ;
        }
        builder.setPositiveButton(posiText, listener1) ;
        builder.setNegativeButton(null, null) ;
        builder.create().show();
    }

    /**
     * 隐藏输入法
     * @param view
     */
    public static void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示输入法
     * @param view
     */
    public static void showKeyBoard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.showSoftInput(view , InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 显示dialog
     */
    public static void showProgressDialog(Dialog dialog){
        if(dialog != null && !dialog.isShowing()){
            dialog.show() ;
        }
    }
    /**
     * 隐藏dialog
     * @param dialog dialog
     */
    public static void hideProgress(Dialog dialog){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    /**
     * 改变Dialog的宽高
     * 在dialog.show()之后调用
     * @param dlg
     */
    public static void setDialogWindowAttr(Dialog dlg ,int width , int height){
        Window window = dlg.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        if(width > 0){
            lp.width = width;//宽高可设置具体大小
        }
        if(height > 0){
            lp.height = height;
        }
        dlg.getWindow().setAttributes(lp);
    }
}
