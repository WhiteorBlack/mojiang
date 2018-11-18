package cn.idcby.jiajubang.view.refresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.idcby.jiajubang.R;

/**
 * 全透明背景
 */
public class NomalTransPullLayout extends FrameLayout implements MaterialHeadListener {
    private ImageView mPullIv ;
    private TextView mPullTv ;

    private AnimationDrawable animationDrawable;

    public NomalTransPullLayout(Context context) {
        this(context, null);
    }

    public NomalTransPullLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NomalTransPullLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context , R.layout.nomal_material_refresh_trans_header, this) ;

        init();
    }

    private void init() {
        ViewCompat.setAlpha(this, 0F);

        mPullIv = (ImageView) findViewById(R.id.nomal_material_refresh_iv);
        mPullTv = (TextView) findViewById(R.id.nomal_material_refresh_tv);

        setXml2FrameAnim1();
    }


    /**
     * 通过XML添加帧动画方法一
     */
    private void setXml2FrameAnim1() {
        // 把动画资源设置为imageView的背景,也可直接在XML里面设置
        mPullIv.setBackgroundResource(R.drawable.refresh_anim_bg_white);
        animationDrawable = (AnimationDrawable) mPullIv.getBackground();
    }


    /**
     * 开启转圈圈
     *
     * @param v
     */
    public void startAnim(View v) {
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    /**
     * 停止动画
     */
    public void cancelAnim() {
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    @Override
    public void onComlete(MaterialRefreshLayout materialRefreshLayout) {

        if(!mPullTv.getText().equals(getResources().getString(R.string.refresh_pull_text))){
            mPullTv.setText(getResources().getString(R.string.refresh_pull_text));
        }

        cancelAnim();
    }

    @Override
    public void onBegin(MaterialRefreshLayout materialRefreshLayout) {
        if(materialRefreshLayout.isRefreshing){
            return ;
        }
        ViewCompat.setAlpha(this, 0F);

        //2018-06-30 15:47:22 默认一直转
        startAnim(mPullIv);
    }

    @Override
    public void onPull(MaterialRefreshLayout materialRefreshLayout, float fraction) {
        if(materialRefreshLayout.isRefreshing){
            return ;
        }

        float a = Util.limitValue(1, fraction);

        //设置对应状态
        if(a >= 1F && !mPullTv.getText().equals(getResources().getString(R.string.refresh_release_text))){
            mPullTv.setText(getResources().getString(R.string.refresh_release_text));
        }else if(a < 1F && !mPullTv.getText().equals(getResources().getString(R.string.refresh_pull_text))){
            mPullTv.setText(getResources().getString(R.string.refresh_pull_text));
        }

        ViewCompat.setAlpha(this, a);
    }

    @Override
    public void onRelease(MaterialRefreshLayout materialRefreshLayout, float fraction) {


    }

    @Override
    public void onRefreshing(MaterialRefreshLayout materialRefreshLayout) {
//        startAnim(mPullIv);

        if(!mPullTv.getText().equals(getResources().getString(R.string.refresh_loading_text))){
            mPullTv.setText(getResources().getString(R.string.refresh_loading_text));
        }
    }
}
