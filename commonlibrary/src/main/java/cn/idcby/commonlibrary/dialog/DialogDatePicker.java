package cn.idcby.commonlibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import cn.idcby.commonlibrary.R;
import cn.idcby.commonlibrary.widget.DatePicker.DatePicker;
import cn.idcby.commonlibrary.widget.DatePicker.DateTimePicker;


/**
 * @Function: 日期选择
 * @Date: 2016-06-23
 * @Time: 下午17:54
 * @author Zg
 */
public class DialogDatePicker extends Dialog {

    /**
     * 日期选择
     */
    private DatePicker mDatePicker;
    private DateTimePicker mDateTimePicker;
    private TextView tv_title,tv_negative,tv_positive;
    private Boolean needTime = false;

    public DialogDatePicker(Context context, Boolean needTime) {
        super(context, R.style.dialog_common);
        this.needTime = needTime;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_date_picker, null);
        tv_title = (TextView) mView.findViewById(R.id.tv_title);
        mDatePicker = (DatePicker) mView.findViewById(R.id.date_picker);
        mDateTimePicker = (DateTimePicker) mView.findViewById(R.id.date_time_picker);
        if(needTime){
            mDatePicker.setVisibility(View.GONE);
        }else {
            mDateTimePicker.setVisibility(View.GONE);
        }
        tv_negative = (TextView) mView.findViewById(R.id.tv_negative);
        tv_positive = (TextView) mView.findViewById(R.id.tv_positive);
        super.setContentView(mView);
    }


    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }
    public String getDate() {
        String[] date;
        String dateString;
        if(needTime){
            date  = mDateTimePicker.getDate();
            dateString = date[0]+"-"+date[1]+"-"+date[2]+" "+date[3]+":"+date[4]+":00";
        }else {
            date  = mDatePicker.getDate();
            dateString = TextUtils.join("-", date);
        }
        return dateString;
    }
    public String getDate1() {
        String[] date;
        String dateString;
        if(needTime){
            date  = mDateTimePicker.getDate();
            dateString = date[0]+"年"+date[1]+"月"+date[2]+"日";
        }else {
            date  = mDatePicker.getDate();
            dateString = date[0]+"年"+date[1]+"月"+date[2]+"日";
        }

        return dateString;
    }
    /**
     * 提示标题
     */
    public void setTitle(String string){
        tv_title.setText(string);
    }
    /**
     * 取消按钮 展示内容
     */
    public void setOnNegative(String string){
        tv_negative.setText(string);
    }
    /**
     * 确定按钮 展示内容
     */
    public void setOnPositive(String string){
        tv_positive.setText(string);
    }

    /**
     * 取消键监听器
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener){
        tv_negative.setOnClickListener(listener);
    }
    /**
     * 确定键监听器
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener){
        tv_positive.setOnClickListener(listener);
    }
}