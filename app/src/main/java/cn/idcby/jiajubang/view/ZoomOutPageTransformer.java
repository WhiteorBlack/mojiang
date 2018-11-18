package cn.idcby.jiajubang.view;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 * Created on 2018/7/30.
 */

public class ZoomOutPageTransformer implements PageTransformer {
    final float SCALE_MAX = 0.8f;

    @Override
    public void transformPage(View page, float position) {
        float scale = (position < 0)
                ? ((1 - SCALE_MAX) * position + 1)
                : ((SCALE_MAX - 1) * position + 1);

        if(scale < SCALE_MAX){
            scale = SCALE_MAX ;
        }

        ViewCompat.setScaleX(page, scale);
        ViewCompat.setScaleY(page, scale);
        //为了滑动过程中，page间距不变，这里做了处理
        if(position < 0) {
            ViewCompat.setPivotX(page, page.getWidth());
            ViewCompat.setPivotY(page, page.getHeight() / 2);
        } else {
            ViewCompat.setPivotX(page, 0);
            ViewCompat.setPivotY(page, page.getHeight() / 2);
        }
    }
}
