package cn.idcby.jiajubang.wxapi;

import android.annotation.TargetApi;
import android.os.Build;

import com.tencent.mm.sdk.modelpay.PayReq;

import cn.idcby.jiajubang.application.MyApplication;


/**
 * 工具类
 * 
 * @author Wangsl
 * 
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class WxPayUtil {
	public static void WXPay(WeiXinPay wxp){
			if(wxp!=null){
				PayReq req = new PayReq();
				req.appId = wxp.getAppid();
				req.nonceStr = wxp.getNoncestr();
				req.packageValue = wxp.getPackages();
				req.partnerId = wxp.getPartnerid();
				req.prepayId = wxp.getPrepayid();
				req.sign = wxp.getSign();
				req.timeStamp = wxp.getTimestamp();
				MyApplication.getMsgApi().sendReq(req);
			}
	}
	
}
