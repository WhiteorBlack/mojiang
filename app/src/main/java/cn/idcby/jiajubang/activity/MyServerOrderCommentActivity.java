package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.StarView;

/**
 * 服务评价
 * Created on 2018/5/11.
 */

public class MyServerOrderCommentActivity extends BaseActivity{
    private String mOrderId ;

    private StarView mStarView ;
    private EditText mContentEv ;

    private LoadingDialog loadingDialog ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_my_server_order_comment ;
    }

    @Override
    public void initView() {
        mOrderId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID) ;

        mStarView = findViewById(R.id.acti_server_order_comment_star_view) ;
        mContentEv = findViewById(R.id.acti_server_order_comment_content_ev) ;
        TextView mSubmitTv = findViewById(R.id.acti_server_order_comment_submit_tv) ;
        mSubmitTv.setOnClickListener(this);
        mStarView.setCanClick(true);

        loadingDialog = new LoadingDialog(mContext) ;
        loadingDialog.setCancelable(false);

    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_server_order_comment_submit_tv == view.getId()){
            submitOrderComment() ;
        }
    }

    /**
     * 提交评论
     */
    private void submitOrderComment(){
        String content = mContentEv.getText().toString().trim() ;
        if("".equals(content)){
            mContentEv.requestFocus() ;
            mContentEv.setText("");
            ToastUtils.showToast(mContext ,"请输入评价");
            return ;
        }

        loadingDialog.show();

        int star = mStarView.getLevel() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("EvaluateStar" ,""+star) ;
        paramMap.put("EvaluateContent" ,content) ;
        paramMap.put("ParentEvaluateId" ,"") ;
        paramMap.put("EvaluateLevel" ,"1") ;
        paramMap.put("ServerOrderId" ,StringUtils.convertNull(mOrderId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_ORDER_COMMENT, paramMap
                , new RequestObjectCallBack<String>("submitOrderComment" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();

                        //这个可能用不上了，后期维护再判断
                        EventBus.getDefault().post(new BusEvent.ServerOrderRefresh(true)) ;

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true)) ;

                        DialogUtils.showCustomViewDialog(mContext, "评价成功"
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        setResult(RESULT_OK);
                                        finish() ;
                                    }
                                });
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }
}
