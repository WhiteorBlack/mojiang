package cn.idcby.jiajubang.view.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import cn.idcby.jiajubang.R;

/**
 * by youxuan
 * 修复 fragment 多次添加头尾到window窗口
 * 修复 刷新距离不够 消息不掉的bug
 */
public class MaterialRefreshLayout extends FrameLayout {

    private boolean refreshCanScroll;

    public boolean isRefreshCanScroll() {
        return refreshCanScroll;
    }

    public void setRefreshCanScroll(boolean refreshCanScroll) {
        this.refreshCanScroll = refreshCanScroll;
    }

    private final static int DEFAULT_WAVE_HEIGHT = 140;
    private final static int HIGHER_WAVE_HEIGHT = 180;
    private final static int DEFAULT_HEAD_HEIGHT = 70;
    private final static int hIGHER_HEAD_HEIGHT = 100;

    private NomalPullLayout mPullLayout ;
    private NomalTransPullLayout mPullTransLayout ;

    private boolean isTransHead ;
    private boolean isOverlay;
    protected float mWaveHeight;
    protected float mHeadHeight;
    private View mChildView;
    protected boolean isRefreshing;
    private float mTouchY;
    private float mTouchX;
    private float mCurrentY;
    private DecelerateInterpolator decelerateInterpolator;
    private float headHeight;
    private float waveHeight;
    private MaterialRefreshListener refreshListener;

    public MaterialRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public MaterialRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defstyleAttr) {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("can only have one child widget");
        }

        decelerateInterpolator = new DecelerateInterpolator(10);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MaterialRefreshLayout, defstyleAttr, 0);
        isOverlay = t.getBoolean(R.styleable.MaterialRefreshLayout_overlay, false);
        isTransHead = t.getBoolean(R.styleable.MaterialRefreshLayout_is_trans, false);

        /**attrs for materialWaveView*/
        headHeight = DEFAULT_HEAD_HEIGHT;
        waveHeight = DEFAULT_WAVE_HEIGHT;

        t.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Context context = getContext();
        mChildView = getChildAt(0);
        if (mChildView == null) {
            return;
        }

        setWaveHeight(Util.dip2px(context, waveHeight));
        setHeaderHeight(Util.dip2px(context, headHeight));

        if(isTransHead){
            if(null == mPullTransLayout){
                mPullTransLayout = new NomalTransPullLayout(context) ;
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , Util.dip2px(context, hIGHER_HEAD_HEIGHT));
                layoutParams.gravity = Gravity.TOP;
                mPullTransLayout.setLayoutParams(layoutParams);
                mPullTransLayout.setVisibility(View.GONE);
                setHeaderView(mPullTransLayout);
            }
        }else{
            if(null == mPullLayout){
                mPullLayout = new NomalPullLayout(context) ;
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , Util.dip2px(context, hIGHER_HEAD_HEIGHT));
                layoutParams.gravity = Gravity.TOP;
                mPullLayout.setLayoutParams(layoutParams);
                mPullLayout.setVisibility(View.GONE);
                setHeaderView(mPullLayout);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) return super.onInterceptTouchEvent(ev);
        if (isRefreshCanScroll() && isRefreshing) return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mTouchX = ev.getX();
                mCurrentY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();

                float dy = currentY - mTouchY;
                float dx = ev.getX() - mTouchX;

                if (dy > 0 && Math.abs(dx) <= Math.abs(dy) && !canChildScrollUp()) {
                    if (mPullLayout != null) {
                        mPullLayout.setVisibility(View.VISIBLE);
                        mPullLayout.onBegin(this);
                    }else if(mPullTransLayout != null){
                        mPullTransLayout.setVisibility(View.VISIBLE);
                        mPullTransLayout.onBegin(this);
                    }
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!isEnabled()) return super.onTouchEvent(e);
        if (isRefreshing) {
            return super.onTouchEvent(e);
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = e.getY();
                float dy = mCurrentY - mTouchY;
                dy = Math.min(mWaveHeight * 2, dy);
                dy = Math.max(0, dy);
                if (mChildView != null) {
                    float offsetY = decelerateInterpolator.getInterpolation(dy / mWaveHeight / 2) * dy / 2;
                    float fraction = offsetY / mHeadHeight;

                    startOrCancelRefresh(true) ;

                    if (mPullLayout != null) {
                        mPullLayout.getLayoutParams().height = (int) offsetY;
                        mPullLayout.requestLayout();
                        mPullLayout.onPull(this, fraction);
                    }else if(mPullTransLayout != null){
                        mPullTransLayout.getLayoutParams().height = (int) offsetY;
                        mPullTransLayout.requestLayout();
                        mPullTransLayout.onPull(this, fraction);
                    }
                    if (!isOverlay)
                        ViewCompat.setTranslationY(mChildView, offsetY);

                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mChildView != null) {
                    if (mPullLayout != null) {
                        if (isOverlay) {
                            if (mPullLayout.getLayoutParams().height > mHeadHeight) {

                                updateListener();

                                mPullLayout.getLayoutParams().height = (int) mHeadHeight;
                                mPullLayout.requestLayout();
                            } else {
                                mPullLayout.getLayoutParams().height = 0;
                                mPullLayout.requestLayout();

                                cancelRefresh() ;
                            }

                        } else {
                            if (ViewCompat.getTranslationY(mChildView) >= mHeadHeight) {
                                createAnimatorTranslationY(mChildView, mHeadHeight, mPullLayout);
                                updateListener();
                            } else {
                                createAnimatorTranslationY(mChildView, 0, mPullLayout);
                                mPullLayout.onComlete(this);

                                cancelRefresh() ;
                            }
                        }
                    }else if (mPullTransLayout != null) {
                        if (isOverlay) {
                            if (mPullTransLayout.getLayoutParams().height > mHeadHeight) {

                                updateListener();

                                mPullTransLayout.getLayoutParams().height = (int) mHeadHeight;
                                mPullTransLayout.requestLayout();
                            } else {
                                mPullTransLayout.getLayoutParams().height = 0;
                                mPullTransLayout.requestLayout();

                                cancelRefresh() ;
                            }

                        } else {
                            if (ViewCompat.getTranslationY(mChildView) >= mHeadHeight) {
                                createAnimatorTranslationY(mChildView, mHeadHeight, mPullTransLayout);
                                updateListener();
                            } else {
                                createAnimatorTranslationY(mChildView, 0, mPullTransLayout);
                                mPullTransLayout.onComlete(this);

                                cancelRefresh() ;
                            }
                        }
                    }
                }
                return true;
        }

        return super.onTouchEvent(e);
    }

    private void cancelRefresh(){
        startOrCancelRefresh(false) ;
    }

    private void startOrCancelRefresh(boolean start){
        if (refreshListener != null) {
            refreshListener.onRefreshStartOrCancel(start);
        }
    }

    public void updateListener() {
        isRefreshing = true;

        if (mPullLayout != null) {
            mPullLayout.onRefreshing(MaterialRefreshLayout.this);
        }else if(mPullTransLayout != null){
            mPullTransLayout.onRefreshing(MaterialRefreshLayout.this);
        }

        if (refreshListener != null) {
            refreshListener.onRefresh(MaterialRefreshLayout.this);
        }

    }

    public void createAnimatorTranslationY(final View v, final float h, final FrameLayout fl) {
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(v);
        viewPropertyAnimatorCompat.setDuration(250);
        viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
        viewPropertyAnimatorCompat.translationY(h);
        viewPropertyAnimatorCompat.start();
        viewPropertyAnimatorCompat.setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(View view) {
                float height = ViewCompat.getTranslationY(v);
                fl.getLayoutParams().height = (int) height;
                fl.requestLayout();
            }
        });
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, -1);
        }
    }


    public void finishRefreshing() {
        if (mChildView != null) {
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(mChildView);
            viewPropertyAnimatorCompat.setDuration(200);
            viewPropertyAnimatorCompat.y(ViewCompat.getTranslationY(mChildView));
            viewPropertyAnimatorCompat.translationY(0);
            viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
            viewPropertyAnimatorCompat.start();

            if (mPullLayout != null) {
                mPullLayout.onComlete(MaterialRefreshLayout.this);
            }else if(mPullTransLayout != null){
                mPullTransLayout.onComlete(MaterialRefreshLayout.this);
            }

            if (refreshListener != null) {
                refreshListener.onfinish();
            }
        }
        isRefreshing = false;
    }

    public void finishRefresh() {
        this.post(new Runnable() {
            @Override
            public void run() {
                finishRefreshing();
            }
        });
    }

    private void setHeaderView(final View headerView) {
        addView(headerView);
    }

    public void setHeader(final View headerView) {
        setHeaderView(headerView);
    }

    public void setFooderView(final View fooderView) {
        this.addView(fooderView);
    }

    public void setWaveHeight(float waveHeight) {
        this.mWaveHeight = waveHeight;
    }

    public void setHeaderHeight(float headHeight) {
        this.mHeadHeight = headHeight;
    }

    public void setMaterialRefreshListener(MaterialRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

}
