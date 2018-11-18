package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.QuestionAnswerApply;
import cn.idcby.jiajubang.Bean.QuestionCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 行业顾问--认证
 * Created on 2018/4/19.
 */

public class QuestionAnswerApplyActivity extends BaseActivity{
    private EditText mReasonEv;
    private EditText mContentEv ;
    private TextView mCategoryTv ;
    private TextView mSubmitTv ;

    private String mCategoryId = null ;

    private final static int REQUEST_CODE_FOR_CATEGORY = 1005;

    private LoadingDialog loadingDialog ;

    private QuestionAnswerApply mApplyInfo ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_question_answer_apply ;
    }

    @Override
    public void initView() {
        loadingDialog = new LoadingDialog(mContext) ;

        mReasonEv = findViewById(R.id.acti_answner_apply_reason_ev) ;
        mContentEv = findViewById(R.id.acti_answner_apply_use_ev) ;
        mCategoryTv = findViewById(R.id.acti_answner_apply_category_tv) ;
        mSubmitTv = findViewById(R.id.acti_answner_apply_submit_tv) ;
        mSubmitTv.setOnClickListener(this);

        mReasonEv.requestFocus() ;

        if(!LoginHelper.isPersonApplyAcross(mContext)){
            DialogUtils.showCustomViewDialog(mContext, "温馨提示" ,StringUtils.getPersonApplyTips(mContext)
                    ,null , "去认证", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            SkipUtils.toApplyActivity(mContext);
                            finish() ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            finish() ;
                        }
                    });
        }
    }

    @Override
    public void initData() {
        getApplyInfo() ;
    }

    @Override
    public void initListener() {
        mCategoryTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_answner_apply_category_tv == vId){
            Intent toCtIt = new Intent(mContext ,ChooseQuestionCategoryActivity.class) ;
            startActivityForResult(toCtIt ,REQUEST_CODE_FOR_CATEGORY) ;
        }else if(R.id.acti_answner_apply_submit_tv == vId){
            submitQuestion() ;
        }
    }

    /**
     * 填充内容
     */
    private void updateUi(QuestionAnswerApply info){
        if(info != null){
            mSubmitTv.setText(mContext.getResources().getString(R.string.apply_modify_text)) ;
            mApplyInfo = info ;

            mReasonEv.setText(info.getApplyReason());
            mContentEv.setText(info.getApplyUserPhone());
            mCategoryTv.setText(info.getQACategoryName()) ;
            mCategoryId = info.getQACategoryId() ;

            mReasonEv.requestFocus() ;
            mReasonEv.setSelection(mReasonEv.getText().length()) ;
        }
    }

    /**
     * 提交
     */
    private void submitQuestion(){
        String title = mReasonEv.getText().toString().trim() ;
        if("".equals(title)){
            ToastUtils.showToast(mContext,"请输入申请原因");
            mReasonEv.setText("");
            mReasonEv.requestFocus() ;
            return;
        }

        String content = mContentEv.getText().toString().trim() ;
        if("".equals(content)){
            ToastUtils.showToast(mContext,"请输入用途");
            mContentEv.setText("");
            mContentEv.requestFocus() ;
            return;
        }

        if(null == mCategoryId){
            ToastUtils.showToast(mContext,"请选择行业分类");
            return ;
        }

        boolean isNoChange = true ;
        if(null == mApplyInfo
                || !title.equals(mApplyInfo.getApplyReason())
                || !mApplyInfo.getApplyUserPhone().equals(content)
                || !mApplyInfo.getQACategoryId().equals(mCategoryId)){
            isNoChange = false ;
        }

        final Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("AuthenticationId", mApplyInfo != null ? mApplyInfo.getQAMasterAuthenticationId() : "");
        paramMap.put("QACategoryId" , mCategoryId) ;
        paramMap.put("ApplyReason" , title) ;
        paramMap.put("ApplyPurpose" , content) ;

        if(isNoChange){
            DialogUtils.showCustomViewDialog(mContext, "温馨提示！", "您未做任何修改，是否继续？", null
                    , "继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish() ;
                        }
                    });
            return ;
        }

        submitApplyRequest(paramMap) ;
    }

    private void submitApplyRequest(Map<String,String> para){
        loadingDialog.show() ;

        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_ANSWER_APPLY,para,
                new RequestObjectCallBack<String>("questionApply" , mContext , String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        DialogUtils.showCustomViewDialog(mContext,
                                getResources().getString(R.string.apply_submit_success)
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();

                        ToastUtils.showErrorToast(mContext , "提交失败") ;
                    }
                }) ;
    }

    /**
     * 获取认证信息
     */
    private void getApplyInfo(){
        loadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_ANSWER_APPLY_INFO,paramMap,
                new RequestObjectCallBack<QuestionAnswerApply>("questionApplyInfo" , mContext , QuestionAnswerApply.class) {
                    @Override
                    public void onSuccessResult(QuestionAnswerApply bean) {
                        loadingDialog.dismiss();
                        updateUi(bean) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                }) ;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOR_CATEGORY == requestCode){
            if(RESULT_OK == resultCode && data != null){
                QuestionCategory mQuestionCategory = (QuestionCategory) data.getSerializableExtra(SkipUtils.INTENT_QUESTION_CATEGORY_INFO);

                if(mQuestionCategory != null){
                    mCategoryId = mQuestionCategory.getIndustryCategoryID() ;
                    String title = mQuestionCategory.getCategoryTitle() ;
                    mCategoryTv.setText(title) ;
                }else{
                    mCategoryId = null ;
                    mCategoryTv.setText("请选择") ;
                }
            }
        }
    }

}
