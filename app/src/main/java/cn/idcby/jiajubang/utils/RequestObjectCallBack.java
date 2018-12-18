package cn.idcby.jiajubang.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ResultBean;
import cn.idcby.jiajubang.activity.LoginActivityNew;
import cn.idcby.jiajubang.activity.MyApplyInfoActivity;
import cn.idcby.jiajubang.events.BusEvent;
import okhttp3.Call;

/**
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2018/2/9.
 */

public abstract class RequestObjectCallBack<T> extends StringCallback {
    private Context mContext;
    private Activity mActivity;
    private String tag;
    private boolean showToast = true;
    private Class<T> cla;

    public RequestObjectCallBack(String tag, Context mContext
            , Class<T> cla) {
        this.tag = tag;
        this.mContext = mContext;

        this.cla = cla;
    }

    public RequestObjectCallBack(String tag, boolean showToast, Context mContext
            , Class<T> cla) {
        this.tag = tag;
        this.showToast = showToast;
        this.mContext = mContext;
        this.cla = cla;
    }

    public RequestObjectCallBack(String tag, Activity mContext
            , Class<T> cla) {
        this.tag = tag;
        this.mActivity = mContext;

        this.cla = cla;
    }


    public abstract void onSuccessResult(T bean);

    public abstract void onErrorResult(String str);

    public abstract void onFail(Exception e);


    @Override
    public void onError(Call call, Exception e, int id) {
        if (showToast) {
            ToastUtils.showServerErrorToast(null == mContext ? mActivity : mContext);
        }
        LogUtils.showLog("RequestManage", "onFail=" + e.getMessage());
        onFail(e);
    }

    @Override
    public void onResponse(String response, int id) {

        //        0：请求成功；
        //        100：请求错误
        //        101：签名错误
        //        102：参数错误
        //        103：Token验证失败；
        //        104：保存失败；
        //        105：Url过期
        //        500：服务器内部错误；
        //        999：请求内部处理错误；


        LogUtils.showLog("RequestManage", tag + ">>>>" + "result=" + response);

        try {
            ResultBean resultBean = JSON.parseObject(response, ResultBean.class);

            String message = resultBean.message;
            if (message.isEmpty()) {
                message = response;
            }

            if (0 == resultBean.errorCode && resultBean.type == 1) {
                if (resultBean.resultData != null) {

                    String resultDataJson = JSON.toJSONString(resultBean.resultData);
                    T t = JSON.parseObject(resultDataJson, cla);
                    onSuccessResult(t);

                } else {
                    if (cla == String.class) {
                        onSuccessResult((T) response);
                    } else {
                        onSuccessResult((T) resultBean);
                    }
                }

            }else if (102==resultBean.errorCode) {
                if (showToast){
                    ToastUtils.showErrorToast(mContext,resultBean.message);
                }
                onErrorResult(response);
            }else if (103 == resultBean.errorCode) {
                if (showToast) {
                    showTokenOutTimeDialog();
                }
            } else if (108 == resultBean.errorCode) {//未通过实名认证
                showNoApplyDialog(resultBean.message);
                onErrorResult(message);
            } else {
                if (showToast) {
                    ToastUtils.showErrorToast(null == mContext ? mActivity : mContext, resultBean.message);
                }
                onErrorResult(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onErrorResult(response);
        }
    }

    private void showTokenOutTimeDialog() {
        DialogUtils.showCustomViewDialog(null == mContext ? mActivity : mContext, "温馨提示", "您的帐号已经在其他设备登录", null
                , "重新登录", (dialogInterface, i) -> {
                    dialogInterface.dismiss();

                    Intent toLoIt = new Intent(null == mContext ? mActivity : mContext, LoginActivityNew.class);
                    toLoIt.putExtra("isGoMain", false);
                    (null == mContext ? mActivity : mContext).startActivity(toLoIt);
                }, "取消", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    EventBus.getDefault().post(new BusEvent.LoginOutEvent(true));

//                        Intent toMiIt = new Intent(null == mContext ? mActivity : mContext , MainActivity.class) ;
//                        toMiIt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        toMiIt.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        (null == mContext ? mActivity : mContext).startActivity(toMiIt) ;
                });
    }


    private void showNoApplyDialog(String message) {
        if ("".equals(StringUtils.convertNull(message))) {
            message = "您尚未完成实名认证，是否去认证？";
        }
        DialogUtils.showCustomViewDialog(null == mContext ? mActivity : mContext, "温馨提示", message, null
                , "去认证", (dialogInterface, i) -> {
                    dialogInterface.dismiss();

                    Intent toApIt = new Intent(null == mContext ? mActivity : mContext, MyApplyInfoActivity.class);
                    (null == mContext ? mActivity : mContext).startActivity(toApIt);
                }, "取消", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    if (mActivity != null) {
                        mActivity.finish();
                    }
                });
    }

}