package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.fragment.MySendCircleFragment;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 我的圈子
 * Created on 2018/5/19.
 */

public class MyCenterCircleActivity extends BaseActivity {
    private FrameLayout mSendLay;

    private MySendCircleFragment mCircleFragment ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_my_center_circle;
    }

    @Override
    public void initView() {
        View rightTv = findViewById(R.id.acti_my_center_circle_send_tv) ;
        rightTv.setOnClickListener(this);

        mSendLay = findViewById(R.id.acti_my_center_circle_content_lay);

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
        mCircleFragment = new MySendCircleFragment() ;

        FragmentManager manager = getSupportFragmentManager() ;
        manager.beginTransaction().add(mSendLay.getId(),mCircleFragment).commit() ;
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_center_circle_send_tv == vId){
            SkipUtils.toSendCircleActivity(mActivity ,1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1000 == requestCode){
            if(RESULT_OK == resultCode){
                mCircleFragment.refreshListDisplay() ;
            }
        }
    }
}
