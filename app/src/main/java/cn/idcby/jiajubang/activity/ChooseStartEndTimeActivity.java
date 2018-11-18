package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.DialogDatePicker;
import cn.idcby.commonlibrary.utils.DateCompareUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.R;

/**
 * 选择周期
 * Created on 2018/3/30.
 */

public class ChooseStartEndTimeActivity extends BaseActivity {
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;

    private boolean mIsChooseStartTime = true ;
    private DialogDatePicker dialogDatePicker;//选择年月日

    private String mStartTimeStr ;
    private String mEndTimeStr ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_choose_start_end_time;
    }

    @Override
    public void initView() {
        mStartTimeTv = findViewById(R.id.acti_choose_time_start_tv) ;
        mEndTimeTv = findViewById(R.id.acti_choose_time_end_tv) ;
        TextView mSubmitTv = findViewById(R.id.acti_choose_time_sub_tv) ;
        mSubmitTv.setOnClickListener(this) ;
        mStartTimeTv.setOnClickListener(this);
        mEndTimeTv.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_choose_time_start_tv == vId){
            mIsChooseStartTime = true ;
            datePicker("选择开始日期" , mStartTimeTv);
        }else if(R.id.acti_choose_time_end_tv == vId){
            mIsChooseStartTime = false ;
            datePicker("选择结束日期" , mEndTimeTv);
        }else if(R.id.acti_choose_time_sub_tv == vId){
            finishTimeChoose() ;
        }
    }

    /**
     * 结束周期选择
     */
    private void finishTimeChoose(){
        if(null == mStartTimeStr){
            ToastUtils.showToast(mContext ,"请选择开始日期") ;
            return ;
        }

        if(null == mEndTimeStr){
            ToastUtils.showToast(mContext ,"请选择结束日期") ;
            return ;
        }

        Intent reIt = new Intent() ;
        reIt.putExtra("startTime" , mStartTimeStr) ;
        reIt.putExtra("endTime" , mEndTimeStr) ;
        setResult(RESULT_OK , reIt);
        finish() ;
    }

    //日期选择器
    private void datePicker(String str, final TextView view) {
        view.setEnabled(false);
        dialogDatePicker = new DialogDatePicker(this, false);
        dialogDatePicker.setTitle(str);
        dialogDatePicker.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(true);
                dialogDatePicker.dismiss();
            }
        });
        dialogDatePicker.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //格式化时间
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sDateFormat.format(new java.util.Date());

                if (DateCompareUtils.compareDay(dialogDatePicker.getDate(),currentDate)) {
                    if(mIsChooseStartTime && mEndTimeStr != null
                            && !DateCompareUtils.compareDay(mEndTimeStr, dialogDatePicker.getDate())){
                        ToastUtils.showErrorToast(mContext, "开始日期不能大于结束日期");
                        return ;
                    }
                    if(!mIsChooseStartTime && mStartTimeStr != null
                            && !DateCompareUtils.compareDay(dialogDatePicker.getDate(),mStartTimeStr)){
                        ToastUtils.showErrorToast(mContext, "结束日期不能小于开始日期");
                        return ;
                    }

                    view.setEnabled(true);
                    view.setText(dialogDatePicker.getDate());
                    if(mIsChooseStartTime){
                        mStartTimeStr = dialogDatePicker.getDate();
                    }else{
                        mEndTimeStr = dialogDatePicker.getDate();
                    }
                    dialogDatePicker.dismiss();
                } else {
                    ToastUtils.showErrorToast(mContext, "日期不能小于当前时间");
                }
            }
        });

        dialogDatePicker.show();
    }

}
