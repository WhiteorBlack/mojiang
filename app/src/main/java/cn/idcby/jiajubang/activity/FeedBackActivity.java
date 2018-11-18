package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.Urls;

/**
 * <pre>
 *     author : hhh
 *     e-mail : xxx@xx
 *     time   : 2018/05/04
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedBackActivity extends BaseActivity {
    private EditText mFeedBackET;
    private EditText mPhoneET;
    private TextView mSubmitTV;
    private LoadingDialog mDialog;


    @Override
    public int getLayoutID() {
        return R.layout.activity_feedback;
    }

    @Override
    public void initView() {
        mFeedBackET = (EditText) findViewById(R.id.feedback_et);
        mPhoneET = (EditText) findViewById(R.id.feedback_phone_et);
        mSubmitTV = (TextView) findViewById(R.id.acti_feedback_submit_tv);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        mSubmitTV.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        if (view.getId() == R.id.acti_feedback_submit_tv){
            updatafeedbackdatas();
        }
    }

    private void updatafeedbackdatas() {
        String feedbackmsg=mFeedBackET.getText().toString().trim();
        String phone =mPhoneET.getText().toString().trim();
        if (TextUtils.isEmpty(feedbackmsg)){
            ToastUtils.showToast(mContext,"反馈内容不能为空");
        }else if(TextUtils.isEmpty(phone)){
            ToastUtils.showToast(mContext,"联系方式不能为空");
        }else {
            mDialog = new LoadingDialog(mContext) ;
            mDialog.show() ;
            Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
            paramMap.put("OpinionContent" , feedbackmsg) ;
            paramMap.put("Phone" , phone) ;
            NetUtils.getDataFromServerByPost(mContext, Urls.MY_FEEDBACK, paramMap
                    , new RequestObjectCallBack<String>("反馈",mContext,String.class) {
                        @Override
                        public void onSuccessResult(String bean) {
                            mDialog.dismiss();
                            DialogUtils.showCustomViewDialog(mContext, "提交成功", "确定"
                                    , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish() ;
                                }
                            });
                        }

                        @Override
                        public void onErrorResult(String str) {
                            mDialog.dismiss();
                        }

                        @Override
                        public void onFail(Exception e) {
                            mDialog.dismiss();
                        }
                    });
        }
    }
}
