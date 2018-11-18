package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.Bean.QuestionAnswerApply;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 行业大咖--认证
 * Created on 2018/5/6.
 */

public class IndustryVApplyActivity extends BaseActivity{
    private EditText mReasonEv;
    private EditText mPhoneEv;
    private TextView mSubmitTv ;
    private TextView mTipsTv ;

    private LoadingDialog loadingDialog ;

    private QuestionAnswerApply mApplyInfo ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_industry_v_apply ;
    }

    @Override
    public void initView() {
        loadingDialog = new LoadingDialog(mContext) ;

        mReasonEv = findViewById(R.id.acti_industry_apply_reason_ev) ;
        mPhoneEv = findViewById(R.id.acti_industry_v_phone_ev) ;
        mSubmitTv = findViewById(R.id.acti_industry_apply_submit_tv) ;
        mSubmitTv.setOnClickListener(this);
        mTipsTv = findViewById(R.id.acti_industry_apply_tips_tv) ;
        mTipsTv.setOnClickListener(this);

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
        getRegistTipsAndToWeb(false) ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

       if(R.id.acti_industry_apply_submit_tv == vId){
            submitQuestion() ;
        }else if(R.id.acti_industry_apply_tips_tv == vId){//大咖说明
           getRegistTipsAndToWeb(true) ;
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
            mPhoneEv.setText(info.getApplyUserPhone());

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

        String phone = mPhoneEv.getText().toString().trim() ;
        if(!MyUtils.isRightPhone(phone)){
            ToastUtils.showToast(mContext,"请输入正确的联系方式");
            mPhoneEv.setSelection(phone.length()) ;
            mPhoneEv.requestFocus() ;
            return;
        }

        boolean isNoChange = true ;
        if(null == mApplyInfo
                || !title.equals(mApplyInfo.getApplyReason())
                || !mApplyInfo.getApplyUserPhone().equals(phone)){
            isNoChange = false ;
        }

        final Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("AuthenticationId", mApplyInfo != null ? mApplyInfo.getQAMasterAuthenticationId() : "");
        paramMap.put("ApplyReason" , title) ;
        paramMap.put("ApplyUserPhone" , phone) ;

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

        NetUtils.getDataFromServerByPost(mContext, Urls.INDUSTRY_V_APPLY,para,
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
        NetUtils.getDataFromServerByPost(mContext, Urls.INDUSTRY_V_APPLY_INFO,paramMap,
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


    /**
     * 获取用户注册协议，并且跳转
     */
    private void getRegistTipsAndToWeb(final boolean show){
        if(show){
            if (loadingDialog == null)
                loadingDialog = new LoadingDialog(mContext);
            loadingDialog.show();
        }

        Map<String,String> paramMap = ParaUtils.getAgreementTipsParam(mContext,ParaUtils.PARAM_INDUSTRY_V_DESC) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.ARTICLE_DETAIL_BY_CODE, paramMap
                , new RequestObjectCallBack<NewsDetail>("getRegistTips" ,mContext ,NewsDetail.class) {
                    @Override
                    public void onSuccessResult(NewsDetail bean) {
                        if(show){
                            loadingDialog.dismiss() ;
                        }

                        if(bean != null){
                            String title = bean.getTitle() ;
                            if("".equals(title)){
                                title = "行业大咖规则说明" ;
                            }
                            mTipsTv.setText(String.format(getResources().getString(R.string.desc_tips_def) ,title)) ;

                            if(show){
                                String contentUrl = bean.getContentH5Url() ;
                                SkipUtils.toShowWebActivity(mContext , title ,contentUrl);
                            }
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(show){
                            loadingDialog.dismiss() ;
                        }

                    }
                    @Override
                    public void onFail(Exception e) {
                        if(show){
                            loadingDialog.dismiss() ;
                        }

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getRegistTips") ;
    }
}
