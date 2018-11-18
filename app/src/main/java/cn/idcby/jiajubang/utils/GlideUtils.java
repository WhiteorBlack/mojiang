package cn.idcby.jiajubang.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.math.BigDecimal;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.view.GlideCircleTransform;
import cn.idcby.jiajubang.view.GlideRoundTransform;
import cn.idcby.jiajubang.view.GlideRoundTransforms;


public class GlideUtils {

    private static GlideUtils inst;

    public static GlideUtils getInstance() {
        if (inst == null) {
            inst = new GlideUtils();
        }
        return inst;
    }

    public static void loaderCircle(String url, ImageView view){
        Glide.with(MyApplication.getInstance())
                .load(url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .transform(new GlideCircleTransform())
                        .placeholder(R.mipmap.default_icon)
                        .error(R.mipmap.default_icon))
                .into(view) ;
    }

    public static void loaderRound(String url, ImageView view ,int radius){
        if(radius < 0){
            radius = 0 ;
        }
        Glide.with(MyApplication.getInstance())
                .load(url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .transform(new GlideRoundTransforms(radius))
                        .placeholder(R.mipmap.default_icon)
                        .error(R.mipmap.default_icon))
                .into(view) ;
    }

    public static void loaderRoundBorder(String url, ImageView view ,int borderWid ,int borderColor){
        Glide.with(MyApplication.getInstance())
                .load(url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .transform(new GlideRoundTransform(MyApplication.getInstance()
                                ,borderWid ,borderColor))
                        .placeholder(R.mipmap.default_icon)
                        .error(R.mipmap.default_icon))
                .into(view) ;
    }

    public static void loaderRound(Context context ,String url, ImageView view){
        loaderRound(url , view , 5) ;
    }

    public static void loader(String url, ImageView view) {
        Glide.with(MyApplication.getInstance())
                .load("".equals(StringUtils.convertNull(url)) ? R.mipmap.default_icon : url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .placeholder(R.mipmap.default_icon)
                        .centerCrop()
                        .error(R.mipmap.default_icon))
                .into(view);
    }

    public static void loader(String url, boolean isCrop ,ImageView view) {
        RequestOptions options ;
        if(isCrop){
            options = new RequestOptions()
                    .dontAnimate()
                    .centerCrop()
                    .placeholder(R.mipmap.default_icon)
                    .error(R.mipmap.default_icon) ;
        }else{
            options = new RequestOptions()
                    .dontAnimate()
                    .placeholder(R.mipmap.default_icon)
                    .error(R.mipmap.default_icon) ;
        }
        Glide.with(MyApplication.getInstance())
                .load("".equals(StringUtils.convertNull(url)) ? R.mipmap.default_icon : url)
                .apply(options)
                .into(view);
    }

    public static void loader(final Context context, int url, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .placeholder(R.mipmap.default_icon)
                        .centerCrop()
                        .error(R.mipmap.default_icon))
                .into(view);
    }

    public static void loader(final Context context, String url, ImageView view) {
        if (url != null)

            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .dontAnimate()
                            .placeholder(R.mipmap.default_icon)
                            .centerCrop()
                            .error(R.mipmap.default_icon))
                    .into(view);
        else
            view.setImageResource(R.mipmap.default_icon);
    }

    public static void loaderAddPic(String url, ImageView view) {
        if (!TextUtils.isEmpty(url))
            Glide.with(MyApplication.getInstance())
                    .load(url)
                    .apply(new RequestOptions()
                            .dontAnimate()
                            .placeholder(R.mipmap.default_icon)
                            .centerCrop()
                            .error(R.mipmap.default_icon))
                    .into(view);
        else
            view.setImageResource(R.mipmap.addpic);
    }

    public static void loaderNoType(final Context context, String url, ImageView view) {
        if (url != null)

            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .dontAnimate()
                            .placeholder(R.mipmap.default_icon)
                            .error(R.mipmap.default_icon))
                    .into(view);
        else
            view.setImageResource(R.mipmap.default_icon);
    }

    public static void loaderNoDef(String url, ImageView view) {
        if (url != null)
            Glide.with(MyApplication.getInstance())
                    .load(url)
                    .apply(new RequestOptions()
                            .dontAnimate())
                    .into(view);
    }

    public static void loaderUser(String url, ImageView view) {
        if (url != null)
            Glide.with(MyApplication.getInstance())
                    .load(url)
                    .apply(new RequestOptions()
                            .dontAnimate()
                            .transform(new GlideRoundTransform(MyApplication.getInstance(),3,1
                                    ,MyApplication.getInstance()
                                    .getResources().getColor(R.color.user_ic_border_color)))
//                            .placeholder(R.mipmap.default_head_icon)
                            .error(R.mipmap.default_head_icon))
                    .into(view) ;
        else
            view.setImageResource(R.mipmap.default_head_icon);
    }

    public static void loaderUserCircle(String url, ImageView view) {
        if (url != null)
            Glide.with(MyApplication.getInstance())
                .load(url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .transform(new GlideCircleTransform())
                        .placeholder(R.mipmap.default_head_icon)
                        .error(R.mipmap.default_head_icon))
                .into(view) ;
        else
            view.setImageResource(R.mipmap.default_head_icon);
    }

    public static void loaderUserRound(String url,int radius,ImageView view) {
        if (url != null)
            Glide.with(MyApplication.getInstance())
                .load(url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .transform(new GlideRoundTransform(MyApplication.getInstance() ,radius))
                        .placeholder(R.mipmap.default_head_icon)
                        .error(R.mipmap.default_head_icon))
                .into(view) ;
        else
            view.setImageResource(R.mipmap.default_head_icon);
    }

    public static void loaderUserRound(String url, ImageView view) {
        loaderUserRound(url ,5,view) ;
    }

    public static void loader(Context context, String url, int erroImg, int emptyImg, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .placeholder(emptyImg)
                        .centerCrop()
                        .error(erroImg))
                .into(view);
    }


    public static void loader(Context context, int url, int erroImg, int emptyImg, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .placeholder(emptyImg)
                        .centerCrop()
                        .error(erroImg))
                .into(view);
    }

    /*****************部分显示模块start*********************/


    /**
     * 加载商品图片
     * @param url 图片地址
     * @param view 图集
     *
     *  由于商品图要求展示全部图片，会使用fitCenter属性，那么默认图就需要调整了，需要用一个方图
     */
    public static void loaderGoodImage(String url, ImageView view) {
        Glide.with(MyApplication.getInstance())
                .load("".equals(StringUtils.convertNull(url)) ? R.mipmap.default_good_icon : url)
                .apply(new RequestOptions()
                        .dontAnimate()
                        .placeholder(R.mipmap.default_good_icon)
                        .centerCrop()
                        .error(R.mipmap.default_good_icon))
                .into(view);
    }




    /*****************end*********************/


    /**
     * 格式化单位
     *
     * @param size size
     * @return size
     */
    public static String getFormatSize(double size) {

        if(0 == size){
            return "0M" ;
        }

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
    }
    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    public long getGlideImageCacheSize() {
        try {
            String path = MyApplication.getInstance().getCacheDir()
                    + "/"+ InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR ;

            String exterPaht = MyApplication.getInstance().getExternalCacheDir()
                    + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;

            long interSize = getFolderSize(new File(path)) ;
            long exterSize = getFolderSize(new File(exterPaht)) ;

            return interSize + exterSize ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            if(fileList != null){
                for (File aFileList : fileList) {
                    if (aFileList.isDirectory()) {
                        size = size + getFolderSize(aFileList);
                    } else {
                        size = size + aFileList.length();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 判断外置sdcard是否可以正常使用
     *
     * @return true has false no
     */
    public static boolean existsSdcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable();
    }

    /**
     * 清除缓存--应用本身的缓存
     */
    public static void clearAppCache() {
        FileUtil.deleteFile(false,MyApplication.getInstance().getCacheDir());
        if (existsSdcard()) {
            FileUtil.deleteFile(false,MyApplication.getInstance().getExternalCacheDir());
        }
    }

    /**
     * 清除图片磁盘缓存
     */
    public void clearImageDiskCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(MyApplication.getInstance()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(MyApplication.getInstance()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存
     */
    public void clearImageMemoryCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(MyApplication.getInstance()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 清除图片所有缓存
     */
    public void clearImageAllCache() {
        clearImageDiskCache();
        clearImageMemoryCache();

        String ImageExternalCatchDir= MyApplication.getInstance().getExternalCacheDir()
                + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;

        String localCacheDir = MyApplication.getInstance().getCacheDir()
                + "/"+InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR ;

        FileUtil.deleteFile(true,ImageExternalCatchDir);
        FileUtil.deleteFile(true, localCacheDir);
    }

}
