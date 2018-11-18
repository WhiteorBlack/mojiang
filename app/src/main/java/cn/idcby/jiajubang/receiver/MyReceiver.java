package cn.idcby.jiajubang.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.idcby.commonlibrary.utils.FlagUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.CircleDetailActivity;
import cn.idcby.jiajubang.activity.MyApplyInfoActivity;
import cn.idcby.jiajubang.activity.MyGoodOrderDetailsActivity;
import cn.idcby.jiajubang.activity.MyNeedOrderDetailsActivity;
import cn.idcby.jiajubang.activity.MyServerOrderDetailsActivity;
import cn.idcby.jiajubang.activity.NeedsDetailsActivity;
import cn.idcby.jiajubang.activity.UnuseGoodDetailsActivity;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.jpush.android.api.JPushInterface;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * 自定义接收器--接收通知
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JIGUANG-Example";

	//同类型的消息，覆盖消息（用同一个id）
	private static final int NOTIFI_TYPE_NEED_ORDER = 1 ;

	//8.0适配
	public static final String CHANNEL_NOMAL_ID = "nomalChannel" ;//普通通知
	public static final String CHANNEL_NOMAL_NAME = "消息通知" ;//普通通知


	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			LogUtils.showLog(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			String action = intent.getAction() ;

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				LogUtils.showLog(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
				LogUtils.showLog(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {

			    if(bundle != null){
                    int count = SPUtils.newIntance(MyApplication.getInstance()).changeUnreadMessage(true) ;
                    ShortcutBadger.applyCount(MyApplication.getInstance() ,count) ;
                    context.sendBroadcast(new Intent(FlagUtils.FLAG_BROADCAST_MSG_COUNT_CHANGE));
                }

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
				LogUtils.showLog(TAG, "[MyReceiver] 用户点击打开了通知");

                int count = SPUtils.newIntance(MyApplication.getInstance()).changeUnreadMessage(false) ;
                ShortcutBadger.applyCount(MyApplication.getInstance() ,count) ;
                context.sendBroadcast(new Intent(FlagUtils.FLAG_BROADCAST_MSG_COUNT_CHANGE));

                toActivityByType(context ,bundle) ;

			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(action)) {

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(action)) {

			}else if(MyJpushNotifyBuilder.ACTION_JPUSH_MSG_DISMISS.equals(action)) {
                LogUtils.showLog(TAG, "[MyReceiver] 用户清除了通知");

                //不起作用


			} else {

			}
		} catch (Exception e){

		}

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtils.showLog(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
                    LogUtils.showLog(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

        LogUtils.showLog(TAG, "processCustomMessage--message=" + message + ",extras=" + extras);

        showNotify(context ,"新消息来啦" ,message ,null) ;
	}


	private void showNotify(Context context,String title ,String content ,Intent intent){
	    String msgType = "" ;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            builder = new NotificationCompat.Builder(context);
        }else{
            builder = new NotificationCompat.Builder(context,getNotificationChannel(msgType)) ;
        }
        Notification notification = builder
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setColor(context.getResources().getColor(R.color.color_theme))
//                .setSound(Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.rae))
                .build();
        long[] vibrates = { 0, 300, 200, 300 };
        notification.vibrate = vibrates ;
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.sound = uri;
        manager.notify(getNotificationId(msgType), notification);
    }

    private String getNotificationChannel(String msgType){
	    return CHANNEL_NOMAL_ID ;
    }

    private int getNotificationId(String msgType){
        return NOTIFI_TYPE_NEED_ORDER ;
    }

    private void toActivityByType(Context context ,Bundle bundle){
        String type = "" ;
        String optionId = "" ;
        try {
            JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
            if(json.has("PushType") && json.has("PushId")){
                type = json.getString("PushType") ;
                optionId = json.getString("PushId") ;
            }
        } catch (JSONException e) {
        }

        if(TextUtils.isEmpty(type) || TextUtils.isEmpty(optionId)){
            return ;
        }

        String optionIdKey = null ;
        Class<?> clz = null ;

        if("1".equals(type)){//需求

            clz = NeedsDetailsActivity.class ;
            optionIdKey = SkipUtils.INTENT_NEEDS_ID ;

        }else if("2".equals(type)){//圈子评论

            clz = CircleDetailActivity.class ;
            optionIdKey = SkipUtils.INTENT_ARTICLE_ID ;

        }else if("3".equals(type)){//需求订单

            clz = MyNeedOrderDetailsActivity.class ;
            optionIdKey = SkipUtils.INTENT_ORDER_ID ;

        }else if("4".equals(type)){//服务订单

            clz = MyServerOrderDetailsActivity.class ;
            optionIdKey = SkipUtils.INTENT_ORDER_ID ;

        }else if("5".equals(type)){//闲置

            clz = UnuseGoodDetailsActivity.class ;
            optionIdKey = SkipUtils.INTENT_UNUSE_ID ;

        }else if("6".equals(type)){//认证
            clz = MyApplyInfoActivity.class ;
        }else if("7".equals(type) || "8".equals(type)){//商品订单详情
            clz = MyGoodOrderDetailsActivity.class ;
            optionIdKey = SkipUtils.INTENT_ORDER_ID ;
        }

        if(clz != null){
            Intent toCtIt = new Intent(context, clz) ;
            if(optionIdKey != null){
                toCtIt.putExtra(optionIdKey , optionId) ;
            }
            toCtIt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP ) ;
            context.startActivity(toCtIt) ;
        }
	}


}
