package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.AppManager;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.commonlibrary.view.LineView;
import cn.idcby.jiajubang.R;

/**
 * <pre>
 *     author : hhh
 *     e-mail : xxx@xx
 *     time   : 2018/05/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Deprecated
public class MyBangMoneyActivity extends BaseActivity {

    private LinearLayout myGoBack;
    private LinearLayout myConsumer;
    private TextView myBangmoney;
    private TextView myNoBangmoney;
    private LineView myTransfer;
    private LineView myEarnMoney;
    private String integralStr;

    @Override
    public int getLayoutID() {
        return R.layout.activity_bang_money;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTransparentForImageView(mActivity ,null);

        integralStr = getIntent().getStringExtra("integral");
        myConsumer = (LinearLayout) findViewById(R.id.consumer_details_bangmoney_ll);
        myGoBack = (LinearLayout) findViewById(R.id.my_bangmoney_goback_ll);
        myBangmoney=(TextView)findViewById(R.id.acti_my_bangmoney_money_tv);
        myNoBangmoney=(TextView)findViewById(R.id.mybangmoney_nobang_tv);
        myTransfer=(LineView) findViewById(R.id.acti_my_transfer_accounts_lay);
        myEarnMoney=(LineView) findViewById(R.id.acti_my_bangmoney_earn_lay);
    }

    @Override
    public void initData() {
        myBangmoney.setText(integralStr);
        if (!TextUtils.isEmpty(integralStr)||integralStr.equals("0")){
            myNoBangmoney.setText("积分可以兑换礼物，快去获取更多积分吧");
        }
    }

    @Override
    public void initListener() {
        myGoBack.setOnClickListener(this);
        myConsumer.setOnClickListener(this);
        myTransfer.setOnClickListener(this);
        myEarnMoney.setOnClickListener(this);

    }

    @Override
    public void dealOhterClick(View view) {
        switch (view.getId()) {
            case R.id.consumer_details_bangmoney_ll:
//                ToastUtils.showToast(mContext,"明细");
                Intent toLvIt = new Intent(mContext ,MyBangMoneyBillListActivity.class) ;
                startActivity(toLvIt);
                break;
            case R.id.my_bangmoney_goback_ll:
                AppManager.getAppManager().finishActivity();
                break;
            case R.id.acti_my_transfer_accounts_lay:
                ToastUtils.showToast(mContext,"转账");
                    Intent intent=new Intent(mContext,MyBangMoneyTransferActivity.class);
                    intent.putExtra("integral",integralStr);
                startActivity(intent);
                break;
            case R.id.acti_my_bangmoney_earn_lay:
                ToastUtils.showToast(mContext,"赚取");
                break;
        }
    }
}
