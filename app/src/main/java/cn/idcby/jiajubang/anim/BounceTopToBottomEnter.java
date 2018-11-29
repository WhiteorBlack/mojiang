package cn.idcby.jiajubang.anim;

import android.animation.ObjectAnimator;
import android.util.DisplayMetrics;
import android.view.View;

import com.flyco.animation.BaseAnimatorSet;

import cn.idcby.jiajubang.utils.ScreenUtil;

public class BounceTopToBottomEnter extends BaseAnimatorSet {
    @Override
    public void setAnimation(View view) {
        DisplayMetrics dm = view.getContext().getResources().getDisplayMetrics();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, "alpha", 0, 1, 1, 1),//
                ObjectAnimator.ofFloat(view, "translationY", -200 , 10*dm.density, 0, 0));
    }
}
