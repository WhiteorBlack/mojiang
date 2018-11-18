package cn.idcby.jiajubang.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;

import cn.idcby.jiajubang.R;

/**
 * Des	      ${友盟分享工具类}
 *
 * 2018-05-04 09:54:43
 * 添加默认thumb占位图
 */
public class ShareUtils {
    private static String SHARE_END_TIPS = "【陌匠】" ;
    private static String SHARE_DEFAULT_DESC = "陌匠" ;


    /**
     * @param mActivity  context
     * @param strTitle        标题
     * @param strUrl          h5地址，必须是网络格式
     * @param strImgurl       网络图片地址
     * @param strSubscriotion 描述
     * @param imageID         内部图片地址
     */
    public static void shareWeb(final Activity mActivity, String strTitle, String strUrl
            , String strImgurl, String strSubscriotion, int imageID) {
        UMWeb web = new UMWeb(strUrl);//连接地址
        web.setTitle(strTitle+SHARE_END_TIPS);//标题
        if (TextUtils.isEmpty(strSubscriotion)) {
            strSubscriotion = SHARE_DEFAULT_DESC;
        }
        web.setDescription(strSubscriotion);//描述
        if (TextUtils.isEmpty(strImgurl)) {
            web.setThumb(new UMImage(mActivity, imageID));  //本地缩略图
        } else {
            web.setThumb(new UMImage(mActivity, strImgurl));  //网络缩略图
        }

        ShareBoardConfig config = new ShareBoardConfig();//新建ShareBoardConfig               config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);//设置位置
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        config.setCancelButtonVisibility(true);
        config.setTitleTextColor(mActivity.getResources().getColor(R.color.color_nomal_text));//标题颜色
        config.setTitleVisibility(true);//设置title是否显示
        config.setMenuItemTextColor(mActivity.getResources().getColor(R.color.color_nomal_text));//设置item文字的字体颜色
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);//设置item背景形状
        config.setIndicatorVisibility(false);

        new ShareAction(mActivity).withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
//                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                    }
                    @Override
                    public void onResult(final SHARE_MEDIA share_media) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onError(final SHARE_MEDIA share_media, Throwable throwable) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mActivity, "分享失败", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    @Override
                    public void onCancel(final SHARE_MEDIA share_media) {
                    }
                }).open(config);
    }

    /**
     * @param mActivity
     * @param strTitle        标题
     * @param strUrl          h5地址，必须是网络格式
     * @param strImgurl       网络图片地址
     * @param strSubscriotion 描述
     */
    public static void shareWeb(final Activity mActivity, String strTitle, String strUrl, String strImgurl
            , String strSubscriotion) {
        shareWeb(mActivity, strTitle, strUrl, strImgurl, strSubscriotion, R.mipmap.ic_default_share_thumb);
    }

    /**
     * 分享链接
     */
    public static void shareWeb(final Activity activity, String WebUrl, String title, String description, String imageUrl, SHARE_MEDIA platform) {
        if(TextUtils.isEmpty(description)){
            description = SHARE_DEFAULT_DESC ;
        }

        UMWeb web = new UMWeb(WebUrl);//连接地址
        web.setTitle(title+SHARE_END_TIPS);//标题
        web.setDescription(description);//描述
        if (TextUtils.isEmpty(imageUrl)) {
            web.setThumb(new UMImage(activity, R.mipmap.ic_default_share_thumb));  //本地缩略图
        } else {
            web.setThumb(new UMImage(activity, imageUrl));  //网络缩略图
        }
        new ShareAction(activity)
                .setPlatform(platform)
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(final SHARE_MEDIA share_media) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(final SHARE_MEDIA share_media, final Throwable throwable) {
                        if (throwable != null) {
                            Log.d("throw", "throw:" + throwable.getMessage());
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    @Override
                    public void onCancel(final SHARE_MEDIA share_media) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "分享取消", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .share();

        //新浪微博中图文+链接
        /*new ShareAction(activity)
                .setPlatform(platform)
                .withText(description + " " + WebUrl)
                .withMedia(new UMImage(activity,imageID))
                .share();*/
    }
}
