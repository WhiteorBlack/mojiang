package cn.idcby.commonlibrary.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import cn.idcby.commonlibrary.R;


/**
 * Created by mrrlb on 2016/10/31.
 */
public class LoadingDialog {
    private TextView mTvLoading;
    private Context mContext;
    private View view;
    private Dialog mDialog;


    public LoadingDialog(Context mContext) {
        this.mContext = mContext;
        init();
    }


    private void init() {
        mDialog = new Dialog(mContext, R.style.custom_dialog);
        view = View.inflate(mContext, R.layout.view_loading, null);
        mTvLoading = (TextView) view.findViewById(R.id.tv_loading_text);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(view);
    }

    public void show() {
        if (!mDialog.isShowing())
            mDialog.show();
    }


    public void dismiss() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    public void setCancelable(boolean cancelable){
        if(mDialog != null){
            mDialog.setCancelable(cancelable) ;
        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void setLoadingText(String desc) {
        mTvLoading.setText(desc);
        if("".equals(desc)){
            mTvLoading.setVisibility(View.GONE);
        }else{
            mTvLoading.setVisibility(View.VISIBLE) ;
        }
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener){
        if(mDialog != null && listener != null){
            mDialog.setOnCancelListener(listener);
        }
    }

}
