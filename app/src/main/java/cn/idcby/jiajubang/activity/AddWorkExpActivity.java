package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.DialogDatePicker;
import cn.idcby.commonlibrary.utils.DateCompareUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.WorkExperience;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 添加工作经历
 * Created on 2018/3/27.
 */

public class AddWorkExpActivity extends BaseMoreStatusActivity {
    private TextView mDataStartTv ;
    private TextView mDataEndTv ;
    private EditText mComNameEv ;
    private EditText mComSalaEv ;
    private EditText mComJobEv ;

    private DialogDatePicker dialogDatePicker;//选择年月日

    private boolean mDataStart = true ;
    private String mStartTime ;
    private String mEndTime ;



    @Override
    public void requestData() {
        showSuccessPage();
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_add_work_exp ;
    }

    @Override
    public String setTitle() {
        return "工作经历";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mDataStartTv = findViewById(R.id.acti_add_work_exp_start_time_tv) ;
        mDataEndTv = findViewById(R.id.acti_add_work_exp_end_time_tv) ;
        mComNameEv = findViewById(R.id.acti_add_work_exp_com_name_ev) ;
        mComSalaEv = findViewById(R.id.acti_add_work_exp_salary_ev) ;
        mComJobEv = findViewById(R.id.acti_add_work_exp_job_ev) ;
        TextView mSubTv = findViewById(R.id.acti_add_work_exp_sub_tv) ;

        mDataStartTv.setOnClickListener(this);
        mDataEndTv.setOnClickListener(this);
        mSubTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_add_work_exp_start_time_tv == vId){
            mDataStart = true ;
            datePicker("选择开始日期" ,mDataStartTv) ;
        }else if(R.id.acti_add_work_exp_end_time_tv == vId){
            mDataStart = false ;
            datePicker("选择结束日期" ,mDataEndTv) ;
        }else if(R.id.acti_add_work_exp_sub_tv == vId){
            subWorkExpAdd() ;
        }
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
                if (DateCompareUtils.compareDay(currentDate, dialogDatePicker.getDate())) {
                    if(mDataStart && mEndTime != null
                            && DateCompareUtils.compareDay(dialogDatePicker.getDate(), mEndTime)){
                        ToastUtils.showErrorToast(mContext, "开始日期不能大于结束日期");
                        return ;
                    }
                    if(!mDataStart && mStartTime != null
                            && DateCompareUtils.compareDay(mStartTime, dialogDatePicker.getDate())){
                        ToastUtils.showErrorToast(mContext, "结束日期不能小于开始日期");
                        return ;
                    }

                    view.setEnabled(true);
                    view.setText(dialogDatePicker.getDate());
                    if(mDataStart){
                        mStartTime = dialogDatePicker.getDate();
                    }else{
                        mEndTime = dialogDatePicker.getDate();
                    }
                    dialogDatePicker.dismiss();
                } else {
                    ToastUtils.showErrorToast(mContext, "日期不能大于当前时间");
                    return;
                }
            }
        });
        dialogDatePicker.show();
    }

    /**
     * 添加
     */
    private void subWorkExpAdd(){
        if(null == mStartTime){
            ToastUtils.showErrorToast(mContext, "请选择开始日期");
            return ;
        }
        if(null == mEndTime){
            ToastUtils.showErrorToast(mContext, "请选择结束日期");
            return ;
        }

        String comName = mComNameEv.getText().toString().trim() ;
        if("".equals(comName)){
            mComNameEv.setText("") ;
            mComNameEv.requestFocus() ;
            ToastUtils.showErrorToast(mContext, "请输入公司名称");
            return ;
        }

        String comJob = mComJobEv.getText().toString().trim() ;
        if("".equals(comJob)){
            mComJobEv.setText("") ;
            mComJobEv.requestFocus() ;
            ToastUtils.showErrorToast(mContext, "请输入任职职位");
            return ;
        }

        String comSala = mComSalaEv.getText().toString().trim() ;
        if("".equals(comSala)){
            mComSalaEv.setText("") ;
            mComSalaEv.requestFocus() ;
            ToastUtils.showErrorToast(mContext, "请输入薪资");
            return ;
        }

        WorkExperience workExp = new WorkExperience(mStartTime , mEndTime ,comName , comJob , comSala) ;
        Intent intent = new Intent() ;
        intent.putExtra(SkipUtils.INTENT_WORK_EXP ,workExp) ;
        setResult(RESULT_OK , intent) ;
        finish() ;
    }
}
