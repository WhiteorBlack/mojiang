package cn.idcby.jiajubang.utils;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

/**
 * Created on 2018/4/11.
 */

public class BannerImageLoader extends ImageLoader {
    private int mRadiu = 0 ;
    private boolean mIsCrop = true ;

    public BannerImageLoader(int radiu) {
        this.mRadiu = radiu;
    }
    public BannerImageLoader(boolean isCrop) {
        this.mIsCrop = isCrop;
    }

    public BannerImageLoader() {
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if(mRadiu > 0){
            GlideUtils.loaderRound((String)path , imageView, mRadiu) ;
        }else{
            GlideUtils.loader((String)path ,mIsCrop, imageView) ;
        }
    }
}
