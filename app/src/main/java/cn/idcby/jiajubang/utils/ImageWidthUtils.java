package cn.idcby.jiajubang.utils;

/**
 * Created on 2018/3/22.
 */

public class ImageWidthUtils {

    /**
     * 常规轮播图宽高比
     * @return w/h
     */
    public static float nomalBannerImageRote(){
        return 2.0F ;
    }

    /**
     * 首页推荐资讯图片宽高比
     * @return w / h
     */
    public static float getIndexHotNewsImageRote(){
        return (float) 25/17 ;
    }
    /**
     * 首页推荐资讯图片宽高比
     * @return w / h
     */
    public static float getIndexHotNewsBigImageRote(){
        return 1.8F ;
    }

    /**
     * 话题图片宽高比
     * @return w / h
     */
    public static float getTopicImageRote(){
        return 2.2F ;
    }

    /**
     * 服务详细图片宽高比
     * @return w / h
     */
    public static float getServerPicImageRote(){
        return 3/2F ;
    }

    /**
     * 图集展示屏占比--单图的时候
     * @param isHori 横图还是竖图
     * @return itemWidth / imageWidth
     */
    public static float getSingleImageItemSelfRote(boolean isHori){
        if(isHori){
            return (float) 5 / 3 ;
        }
        return (float) 2 / 1 ;
    }
}
