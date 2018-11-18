package cn.idcby.jiajubang.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;

/**
 * 
 * @ClassName: WXPayEntryActivity 
 * @Description: 微信支付结果页
 * @author WangFusheng 
 * @date 2015年8月10日 下午1:16:52
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
    	api = WXAPIFactory.createWXAPI(this, Paramters.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	@Override
	public void onReq(BaseReq req) {
		
	}
	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			EventBus.getDefault().post(new BusEvent.WxPayEvent(resp.errCode)) ;
			WXPayEntryActivity.this.finish();
		}
	}
}