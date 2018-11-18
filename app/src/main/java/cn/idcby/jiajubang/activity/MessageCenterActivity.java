package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.hyphenate.chat.EMClient;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.fragment.MessageConversationListFragment;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 消息中心
 * Created on 2018/4/21.
 */

public class MessageCenterActivity extends BaseActivity {
    private MessageConversationListFragment conversationListFragment ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_message_center ;
    }

    @Override
    public void initView() {

        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity ,1000) ;
        }else{
            showContentAndLoad();
        }
    }

    /**
     * 显示内容
     */
    private void showContentAndLoad(){
        conversationListFragment = new MessageConversationListFragment() ;
        FragmentManager transaction = getSupportFragmentManager();
        transaction.beginTransaction().add(R.id.acti_message_center_con_lay, conversationListFragment).commit();
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

    @Override
    protected void onResume() {
        super.onResume();

        if(conversationListFragment != null){
            conversationListFragment.getMessageCount() ;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(RESULT_OK == resultCode){
                showContentAndLoad();
            }else{
                finish() ;
            }
        }
    }
}
