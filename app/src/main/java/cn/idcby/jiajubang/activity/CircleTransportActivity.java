package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 圈子转载
 * Created on 2018/9/14.
 */

public class CircleTransportActivity extends BaseActivity {
    private String mCircleId ;

    private EditText mContentEv ;

    private LoadingDialog mDialog ;

    public static void launch(Activity context ,String cirId ,String cirTitle
            ,String cirImage,String cirType,int requestCode){
        Intent toTrIt = new Intent(context ,CircleTransportActivity.class) ;
        toTrIt.putExtra("circleId" ,cirId) ;
        toTrIt.putExtra("circleTitle" ,cirTitle) ;
        toTrIt.putExtra("circleImgUrl" ,cirImage) ;
        toTrIt.putExtra("circleType" ,cirType) ;
        context.startActivityForResult(toTrIt ,requestCode) ;
    }
    public static void launch(Fragment context , String cirId , String cirTitle
            , String cirImage, String cirType, int requestCode){
        Intent toTrIt = new Intent(context.getContext() ,CircleTransportActivity.class) ;
        toTrIt.putExtra("circleId" ,cirId) ;
        toTrIt.putExtra("circleTitle" ,cirTitle) ;
        toTrIt.putExtra("circleImgUrl" ,cirImage) ;
        toTrIt.putExtra("circleType" ,cirType) ;
        context.startActivityForResult(toTrIt ,requestCode) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_circle_transport;
    }

    @Override
    public void initView() {
        super.initView();

        mCircleId = getIntent().getStringExtra("circleId") ;
        String circleTitle = getIntent().getStringExtra("circleTitle") ;
        String circleType = getIntent().getStringExtra("circleType") ;
        String circleImg = getIntent().getStringExtra("circleImgUrl") ;

        mContentEv = findViewById(R.id.acti_circle_transport_content_ev) ;
        TextView mCircleTitleTv = findViewById(R.id.acti_circle_transport_circle_title_tv) ;
        TextView mCircleTypeTv = findViewById(R.id.acti_circle_transport_circle_type_tv) ;
        ImageView mCircleIv = findViewById(R.id.acti_circle_transport_circle_iv) ;

        View mSubmitTv = findViewById(R.id.acti_circle_transport_send_tv) ;
        mSubmitTv.setOnClickListener(this);

        mCircleTitleTv.setText(StringUtils.convertNull(circleTitle)) ;
        mCircleTypeTv.setText(StringUtils.convertNull(circleType)) ;
        GlideUtils.loader(circleImg ,mCircleIv) ;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        if(view.getId() == R.id.acti_circle_transport_send_tv){
            submitTransport() ;
        }
    }


    /**
     * 提交转载
     */
    private void submitTransport(){
        String content = mContentEv.getText().toString().trim() ;
        if("".equals(content)){
            ToastUtils.showToast(mContext ,"说说分享心得...");
            return ;
        }

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
            mDialog.setCancelable(false);
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("SourcePostID" ,StringUtils.convertNull(mCircleId)) ;
        paramMap.put("BodyContent" , content) ;
        paramMap.put("ProvinceName" , MyApplication.LOCATION_PROVINCE) ;
        paramMap.put("CityName" , MyApplication.LOCATION_CITY) ;
        paramMap.put("Longitude" , MyApplication.getLongitude()) ;
        paramMap.put("Latitude" , MyApplication.getLatitude()) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SEND_CIRCLE_TRANSPORT, paramMap
                , new RequestObjectCallBack<String>("submitTransport",mContext,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss() ;

                        DialogUtils.showCustomViewDialog(mContext, "转发成功", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                setResult(RESULT_OK);
                                finish() ;
                            }
                        });
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss() ;
                    }
                });
    }


}