package cn.idcby.jiajubang.activity;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.fragment.MyNeedsOfferFragment;
import cn.idcby.jiajubang.fragment.MySendFragment;

/**
 * 最新需求--我发布的、我的报价
 * Created on 2018/5/19.
 */

public class MyCenterNeedActivity extends BaseActivity {
    private TextView mTypeSendTv;
    private TextView mTypeOrderTv;

    private boolean mIsSend = true ;//我发布的

    private FrameLayout mContentLay;

    private FragmentManager mFragManager ;
    private MySendFragment mSendFragment ;
    private MyNeedsOfferFragment mOfferFragment ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_my_center_need;
    }

    @Override
    public void initView() {

        mTypeSendTv = findViewById(R.id.acti_my_center_type_left_tv) ;
        mTypeOrderTv = findViewById(R.id.acti_my_center_type_right_tv) ;
        mTypeSendTv.setOnClickListener(this);
        mTypeOrderTv.setOnClickListener(this);

        mContentLay = findViewById(R.id.acti_my_center_need_content_lay);

        initMySend() ;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    /**
     * 初始化 发布相关
     */
    private void initMySend(){
        mSendFragment = MySendFragment.getInstance(MySendFragment.SEND_TYPE_NEEDS) ;
        mOfferFragment = new MyNeedsOfferFragment() ;

        mFragManager = getSupportFragmentManager() ;
        mFragManager.beginTransaction()
                .add(mContentLay.getId()
                    ,mSendFragment)
                .add(mContentLay.getId() ,mOfferFragment)
                .commit() ;
        mFragManager.beginTransaction().show(mSendFragment).hide(mOfferFragment).commit() ;
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_center_type_left_tv == vId){
            changeSendType(true) ;
        }else if(R.id.acti_my_center_type_right_tv == vId){
            changeSendType(false) ;
        }
    }

    /**
     *  切换类型
     *  @param isSend is mIsSend
     */
    private void changeSendType(boolean isSend){
        if(isSend == mIsSend){
            return ;
        }

        if(mIsSend){
            mTypeSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeSendTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }else{
            mTypeOrderTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeOrderTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }

        if(isSend){
            mTypeSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeSendTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }else{
            mTypeOrderTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeOrderTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }

        mIsSend = isSend ;

        mFragManager.beginTransaction()
                .show(mIsSend ? mSendFragment : mOfferFragment)
                .hide(!mIsSend ? mSendFragment : mOfferFragment)
                .commit() ;
    }
}
