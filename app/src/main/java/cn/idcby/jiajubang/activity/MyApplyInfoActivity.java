package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.commonlibrary.view.LineView;
import cn.idcby.jiajubang.Bean.ApplyStateInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 我的认证
 *
 * 2018-08-14 14:49:03
 * 改：行业大咖、行业顾问暂时去掉
 *
 * 2018-09-11 16:38:47
 * 行业大咖显示
 *
 * 2018-09-13 18:59:12
 * 隐藏安装和服务认证
 */
public class MyApplyInfoActivity extends BaseMoreStatusActivity {
    private LineView mLvPerson;
    private LineView mLvCompany;
    private LineView mLvFactory;
    private LineView mLvStore;
    private LineView mLvInstall;
    private LineView mLvTrade;
    private LineView mLvIndustryAnswer;
    private LineView mLvQuestionMaster;

    private ApplyStateInfo mApplyInfo ;

    private LoadingDialog mLoadingDialog ;

    @Override
    public void requestData() {
        showSuccessPage();
        requestUserApplyInfo();
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_my_apply_info;
    }

    @Override
    public String setTitle() {
        return "我的认证";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        initView();
        initListenner();
    }


    private void initView() {
        mLvPerson = findViewById(R.id.lv_person);
        mLvCompany = findViewById(R.id.lv_company);
        mLvFactory = findViewById(R.id.lv_factory);
        mLvStore = findViewById(R.id.lv_store);
        mLvInstall = findViewById(R.id.lv_install);
        mLvTrade = findViewById(R.id.lv_trade);
        mLvIndustryAnswer = findViewById(R.id.lv_question_answer);
        mLvQuestionMaster = findViewById(R.id.lv_question_master);

    }

    private void initListenner() {
        mLvPerson.setOnClickListener(this);
        mLvCompany.setOnClickListener(this);
        mLvFactory.setOnClickListener(this);
        mLvStore.setOnClickListener(this);
        mLvInstall.setOnClickListener(this);
        mLvTrade.setOnClickListener(this);
        mLvIndustryAnswer.setOnClickListener(this);
        mLvQuestionMaster.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        if (i == R.id.lv_person) {
            goNextActivity(PersonApplyActivity.class, 1001);
        } else if (i == R.id.lv_company) {
            if(mApplyInfo != null && mApplyInfo.isPersonalFall()){
                ToastUtils.showToast(mContext ,StringUtils.getPersonApplyTips(mContext)) ;
            }else{
                goNextActivity(CompanyApplyActivity.class, 1001);
            }
        } else if (i == R.id.lv_factory) {
            if(mApplyInfo != null && mApplyInfo.isPersonalFall()){
                ToastUtils.showToast(mContext ,StringUtils.getPersonApplyTips(mContext));
            }else{
                goNextActivity(FactoryApplyActivity.class, 1001);
            }
        }  else if (i == R.id.lv_store) {
            if(mApplyInfo != null && mApplyInfo.isPersonalFall()){
                ToastUtils.showToast(mContext ,StringUtils.getPersonApplyTips(mContext));
            }else{
                goNextActivity(OpenStoreActivity.class, 1001);
            }
        } else if (i == R.id.lv_trade) {
            if(mApplyInfo != null && mApplyInfo.isPersonalFall()){
                ToastUtils.showToast(mContext ,StringUtils.getPersonApplyTips(mContext));
            }else{
                goNextActivity(ServerApplyActivity.class, 1001);
            }
        } else if (i == R.id.lv_install) {
            if(mApplyInfo != null && mApplyInfo.isPersonalFall()){
                ToastUtils.showToast(mContext ,StringUtils.getPersonApplyTips(mContext));
            }else{
                goNextActivity(InstallApplyActivity.class, 1001);
            }
        } else if (i == R.id.lv_question_answer) {//行业大咖
            if(mApplyInfo != null && mApplyInfo.isPersonalFall()){
                ToastUtils.showToast(mContext ,StringUtils.getPersonApplyTips(mContext));
            }else{
                goNextActivity(IndustryVApplyActivity.class, 1001) ;
            }
        } else if (i == R.id.lv_question_master) {
            if(mApplyInfo != null && mApplyInfo.isPersonalFall()){
                ToastUtils.showToast(mContext ,StringUtils.getPersonApplyTips(mContext));
            }else{
                goNextActivity(QuestionAnswerApplyActivity.class, 1001);
            }
        }
    }

    /**
     * 填充数据
     */
    private void updateDisplay(){
        if(null == mApplyInfo){
            return ;
        }

        String personalAuth = StringUtils.convertNull(mApplyInfo.getPersonalAuthenticationText()) ;
        String companyAuth = StringUtils.convertNull(mApplyInfo.getCompanyAuthenticationText()) ;
        String factoryAuth = StringUtils.convertNull(mApplyInfo.getFactoryAuthenticationText()) ;
        String storeAuth = StringUtils.convertNull(mApplyInfo.getShopAuthenticationText()) ;
        String serviceAuth = StringUtils.convertNull(mApplyInfo.getServiceAuthenticationText()) ;
        String installAuth = StringUtils.convertNull(mApplyInfo.getInstallAuthenticationText()) ;
        String questionMasterAuth = StringUtils.convertNull(mApplyInfo.getQAMasterAuthenticationText()) ;
        String industryAnswerAuth = StringUtils.convertNull(mApplyInfo.getIndustryVAuthenticationText()) ;

        mLvPerson.setRightText(personalAuth);
        mLvCompany.setRightText(companyAuth);
        mLvFactory.setRightText(factoryAuth);
        mLvStore.setRightText(storeAuth);
        mLvInstall.setRightText(installAuth);
        mLvTrade.setRightText(serviceAuth);
        mLvIndustryAnswer.setRightText(industryAnswerAuth);
        mLvQuestionMaster.setRightText(questionMasterAuth);
    }

    /**
     * 获取认证状态
     */
    private void requestUserApplyInfo() {
        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.show() ;

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_USER_APPLY_INFO, false, para,
                new RequestObjectCallBack<ApplyStateInfo>("获取用户认证信息", mContext,
                        ApplyStateInfo.class) {
                    @Override
                    public void onSuccessResult(ApplyStateInfo bean) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }

                        mApplyInfo = bean ;
                        updateDisplay() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss() ;
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1001 == requestCode){
            requestUserApplyInfo() ;
        }
    }
}
