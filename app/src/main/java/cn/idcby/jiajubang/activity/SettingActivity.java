package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.update.UpdateManager;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.jpush.android.api.JPushInterface;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created on 2018/3/22.
 */

public class SettingActivity extends BaseMoreStatusActivity {
    private LoadingDialog mDialog ;

    private TextView mUserNameTv ;
    private TextView mUserPhoneTv ;
    private TextView mCacheSizeTv ;
    private ImageView mUserIv ;

    private UserInfo mUserInfo ;
    private long mImageCacheSize = 0 ;


    @Override
    public void requestData() {
        showSuccessPage() ;

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        new GetCacheTask().execute() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_setting;
    }

    @Override
    public String setTitle() {
        return "设置";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        mUserInfo = (UserInfo) getIntent().getSerializableExtra(SkipUtils.INTENT_USER_INFO);

        mUserIv = findViewById(R.id.acti_setting_user_iv) ;
        mUserNameTv = findViewById(R.id.acti_setting_user_name_tv) ;
        mUserPhoneTv = findViewById(R.id.acti_setting_user_phone_tv) ;

        View mChangeUserLay = findViewById(R.id.acti_setting_change_user_info_lay) ;
        View mChangeAddressView = findViewById(R.id.acti_setting_change_address_tv) ;
        View mChangePhoneView = findViewById(R.id.acti_setting_change_phone_tv) ;
        View mChangePassView = findViewById(R.id.acti_setting_change_pass_tv) ;
        View mSetPayPassView = findViewById(R.id.acti_setting_set_payPass_tv) ;
        View mPayManagerView = findViewById(R.id.acti_setting_pay_manager_tv) ;
        View mChangePayPassView = findViewById(R.id.acti_setting_change_payPass_tv) ;
        View mLogoutTv = findViewById(R.id.acti_setting_logout_tv) ;
        View mAboutTv = findViewById(R.id.acti_setting_about_us_tv) ;
        View mClearMemoryLay = findViewById(R.id.acti_setting_clear_memory_lay) ;
        mCacheSizeTv = findViewById(R.id.acti_setting_clear_memory_tv) ;
        mClearMemoryLay.setOnClickListener(this);
        mAboutTv.setOnClickListener(this);
        mLogoutTv.setOnClickListener(this);
        mChangePayPassView.setOnClickListener(this);
        mSetPayPassView.setOnClickListener(this);
        mChangePassView.setOnClickListener(this);
        mChangePhoneView.setOnClickListener(this);
        mChangeUserLay.setOnClickListener(this);
        mChangeAddressView.setOnClickListener(this);
        mPayManagerView.setOnClickListener(this);
    }


    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_setting_logout_tv == vId){
            logoutApp() ;
        }else if(R.id.acti_setting_change_phone_tv == vId){
            ChangeLoginActivity.launch(mActivity ,ChangeLoginActivity.CHANGE_TYPE_PHONE ,1000) ;
        }else if(R.id.acti_setting_change_pass_tv == vId){
            ChangeLoginActivity.launch(mActivity ,ChangeLoginActivity.CHANGE_TYPE_LOGIN_PASS ,1001) ;
        }else if(R.id.acti_setting_set_payPass_tv == vId){
            ChangeLoginActivity.launch(mActivity ,ChangeLoginActivity.SET_TYPE_PAY_PASS ,1002) ;
        }else if(R.id.acti_setting_change_payPass_tv == vId){
            ChangeLoginActivity.launch(mActivity ,ChangeLoginActivity.CHANGE_TYPE_PAY_PASS ,1003) ;
        }else if(R.id.acti_setting_about_us_tv == vId){
            Intent toAbIt = new Intent(mContext ,AboutUsActivity.class) ;
            startActivity(toAbIt) ;
        }else if(R.id.acti_setting_change_user_info_lay == vId){
            Intent toAbIt = new Intent(mContext ,UserInfoActivity.class) ;
            startActivity(toAbIt) ;
        }else if(R.id.acti_setting_change_address_tv == vId){
            Intent toAbIt = new Intent(mContext ,MyAddressActivity.class) ;
            startActivity(toAbIt) ;
        }else if(R.id.acti_setting_pay_manager_tv == vId){
            Intent toMgIt = new Intent(mContext ,PayManagerActivity.class) ;
            startActivity(toMgIt) ;
        }else if(R.id.acti_setting_clear_memory_lay == vId){
            DialogUtils.showCustomViewDialog(mContext, "温馨提示", "确认要清除缓存吗？", null
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    clearMemory();
                }
            }, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    /**
     * 获取信息
     */
    private void getUserInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getMyInfo" ,mContext ,UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        if(bean != null){
                            mUserInfo = bean ;
                        }
                        updateDisplay() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateDisplay() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateDisplay() ;
                    }
                });
    }

    private void updateDisplay(){
        if(mUserInfo != null){
            String name = mUserInfo.getNickName() ;
            String phone = mUserInfo.getAccount() ;
            String imgUrl = mUserInfo.getHeadIcon() ;

            mUserNameTv.setText(name);
            mUserPhoneTv.setText("手机号：" + phone);
            GlideUtils.loaderUser(imgUrl ,mUserIv) ;
        }
    }

    /**
     * 退出登录
     */
    private void logoutApp(){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.LOGIN_OUT, paramMap
                , new RequestObjectCallBack<String>("logoutApp",mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        logOutHx() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        logOutHx() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        logOutHx() ;
                    }
                });
    }

    private void logOutHx(){
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                exitApp() ;
            }
            @Override
            public void onProgress(int progress, String status) {
            }
            @Override
            public void onError(int code, String message) {
                exitApp() ;
            }
        });
    }

    private void exitApp(){
        MyApplication.setHxLoginSuccess(false);
        LoginHelper.resetHxInfo(mContext) ;
        JPushInterface.stopPush(MyApplication.getInstance()) ;

        mDialog.dismiss() ;
        EventBus.getDefault().post(new BusEvent.LoginOutEvent(true)) ;

        LoginHelper.logout(mContext) ;
        SkipUtils.toLoginActivity(mContext) ;
        finish() ;
    }

    /**
     * 清除缓存
     */
    private void clearMemory(){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.setCancelable(false);
        mDialog.show() ;

        new ClearMemoryTask().execute() ;
    }

    private class ClearMemoryTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            GlideUtils.getInstance().clearImageAllCache() ;
            GlideUtils.clearAppCache() ;

            FileUtil.deleteFile(true ,FileUtil.getUploadCachePath()) ;
            FileUtil.deleteFile(true , UpdateManager.FILE_PATH) ;
            try {
                Thread.sleep(1000) ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(mDialog != null){
                mDialog.dismiss();
            }
            if(mCacheSizeTv != null){
                mCacheSizeTv.setText("0M") ;
            }
    //偷懒直接手动归零
//            new GetCacheTask().execute() ;
        }
    }

    /**
     * 获取缓存task
     */
    private class GetCacheTask extends AsyncTask<Void ,Void , String>{
        @Override
        protected String doInBackground(Void... params) {
            try {
                if(EasyPermissions.hasPermissions(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mImageCacheSize = GlideUtils.getInstance().getGlideImageCacheSize() ;
                }

                return GlideUtils.getFormatSize(mImageCacheSize) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0M";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            if(mDialog != null){
                mDialog.dismiss();
            }
            if(mCacheSizeTv != null){
                mCacheSizeTv.setText(aVoid) ;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserInfo() ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getMyInfo");
        NetUtils.cancelTag("logoutApp");
    }
}
