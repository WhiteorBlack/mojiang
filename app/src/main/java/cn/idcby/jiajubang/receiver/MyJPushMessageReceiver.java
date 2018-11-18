package cn.idcby.jiajubang.receiver;

import android.content.Context;

import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * 自定义JPush message 接收器,包括操作tag/alias的结果返回(仅仅包含tag/alias新接口部分)
 * */
public class MyJPushMessageReceiver extends JPushMessageReceiver {

    @Override
    public void onTagOperatorResult(Context context,JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onCheckTagOperatorResult(Context context,JPushMessage jPushMessage){
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);

        LogUtils.showLog("JIGUANG-Example" ,"设置别名回调---code=" + jPushMessage.getErrorCode() + ",alisa=" + jPushMessage.getAlias());

        if(jPushMessage.getErrorCode() == 0){//设置别名成功
            SPUtils.newIntance(context).setUserAliasState(true) ;
        }else{
            SPUtils.newIntance(context).setUserAliasState(false) ;
        }

    }
    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }
}
