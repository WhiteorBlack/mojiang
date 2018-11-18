package cn.idcby.jiajubang.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.fragment.MessageChatFagment;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 聊天
 * Created on 2018/4/21.
 */

public class MessageChatActivity extends BaseActivity {
    private TextView mTitleTv ;

    private String mUserId ;
    private String mUserHxId ;

    public String getCurrentChatUserId(){
        return StringUtils.convertNull(mUserHxId) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_message_chat ;
    }

    @Override
    public void initView() {
        mUserId = getIntent().getStringExtra(SkipUtils.INTENT_USER_ID) ;
        mUserHxId = getIntent().getStringExtra(SkipUtils.INTENT_USER_HX_ID) ;

        if("".equals(StringUtils.convertNull(mUserHxId))){
            ToastUtils.showToast(mContext ,"帐号有误，请重试");
            finish() ;
            return ;
        }

        mTitleTv = findViewById(R.id.acti_message_chat_title_tv) ;

        MessageChatFagment chatFagment = new MessageChatFagment() ;
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        args.putString(EaseConstant.EXTRA_USER_ID, mUserHxId);
        chatFagment.setArguments(args);

        FragmentManager transaction = getSupportFragmentManager();
        transaction.beginTransaction().add(R.id.acti_message_chat_con_lay, chatFagment).commit();

        getUserInfoByHxName(mUserHxId) ;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {

    }


    private void getUserInfoByHxName(String hxName){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ID" ,"1") ;
        paramMap.put("Code" ,hxName) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.USER_HX_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getUserInfos" + hxName ,mContext ,UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        if(bean != null){
                            mTitleTv.setText(bean.getRealName()) ;

                            MyApplication.saveContactToMap(bean);
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        EaseUser user = MyApplication.getUserInfo(mUserHxId) ;
                        if(user != null){
                            mTitleTv.setText(StringUtils.convertNull(user.getNickname())) ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        EaseUser user = MyApplication.getUserInfo(mUserHxId) ;
                        if(user != null){
                            mTitleTv.setText(StringUtils.convertNull(user.getNickname())) ;
                        }
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getUserInfos" + mUserHxId) ;
    }
}
