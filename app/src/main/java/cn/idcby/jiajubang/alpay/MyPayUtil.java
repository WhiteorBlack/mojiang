package cn.idcby.jiajubang.alpay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author ld
 * @ClassName: MyPayUtil
 * @Description: 支付宝支付辅助类
 * @date 2015年5月19日 下午3:33:20
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyPayUtil {
    public interface IZhifuBaoPay {
        public void checkDeviceResult(boolean checkResult);

        public void payResult(boolean payResult);
    }

    private IZhifuBaoPay pay;
    private Activity context;
    private Fragment fragment;

    public MyPayUtil(Activity context2) {
        super();
        this.context = context2;
        try {
            this.pay = (IZhifuBaoPay) context2;
        } catch (Exception e) {
            throw new ClassCastException(context2.toString()
                    + " Must implent IZhifuBaoPay");
        }
    }

    public MyPayUtil(Fragment fragment) {
        super();
        this.fragment = fragment;
        context = fragment.getActivity();
        try {
            this.pay = (IZhifuBaoPay) fragment;
        } catch (Exception e) {
            throw new ClassCastException(fragment.toString()
                    + " Must implent IZhifuBaoPay");
        }
    }

    public void pay(final Activity context, ZFBPay zfbPay) {
        String orderInfo = getOrderInfo(zfbPay);

        // 对订单做RSA 签名
        String sign = sign(orderInfo, zfbPay.getPrivateKey());
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo,true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private static final int SDK_PAY_FLAG = 9;
    private static final int SDK_CHECK_FLAG = 8;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(context, "支付成功", Toast.LENGTH_LONG).show();
                        pay.payResult(true);
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        Toast.makeText(context, "取消支付",
                                Toast.LENGTH_SHORT).show();
                        pay.payResult(false);
                    } else {
                        pay.payResult(false);
                        Toast.makeText(context, payResult.getMemo(),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(context, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(ZFBPay zfbPay) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + zfbPay.getPartner() + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + zfbPay.getSeller_id() + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + zfbPay.getOut_trade_no() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + zfbPay.getSubject() + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + zfbPay.getBody() + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + zfbPay.getTotal_fee() + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + zfbPay.getNotify_url()
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//		orderInfo += "&return_url=\""+URLs.NORMAL_URL+"?act=payment&op=return\"";
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content, String rsaPrivate) {
        return SignUtils.sign(content, rsaPrivate);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";

    }
}
