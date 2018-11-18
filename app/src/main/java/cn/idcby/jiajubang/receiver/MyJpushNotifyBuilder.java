package cn.idcby.jiajubang.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Map;

import cn.jpush.android.api.DefaultPushNotificationBuilder;


/**
 * Created on 2018/6/5.
 */

public class MyJpushNotifyBuilder extends DefaultPushNotificationBuilder {
    public static final String ACTION_JPUSH_MSG_DISMISS = "cn.idcby.push.MSG_DIMESS" ;
    public static final int ACTION_JPUSH_MSG_DISMISS_REQUEST_CODE = 1 ;

    private Context mContext ;

    public MyJpushNotifyBuilder(Context context) {
        super();
        this.mContext = context ;
    }

    @Override
    public Notification buildNotification(Map<String, String> map) {
        Notification notification = super.buildNotification(map) ;
        notification.deleteIntent = PendingIntent.getBroadcast(mContext, ACTION_JPUSH_MSG_DISMISS_REQUEST_CODE, new Intent(ACTION_JPUSH_MSG_DISMISS), 0);
        return notification;
    }
}
