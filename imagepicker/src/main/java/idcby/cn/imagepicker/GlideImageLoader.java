package idcby.cn.imagepicker;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2017/1/10.
 */

public class GlideImageLoader implements ImageLoader {

    private static final long serialVersionUID = 1L;

    @Override
    public void displayImage(Context context, String path, ImageView imageView) {

        Log.i("mrrlb", "图片的地址>>>" + path);
//        Glide.with(context)
//                .load(path)
//                .placeholder(R.drawable.global_img_default)
//                .error(R.drawable.global_img_default)
//                .centerCrop()
//                .into(imageView);
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.global_img_default)
                        .centerCrop()
                        .error(R.drawable.global_img_default))
                .into(imageView);

    }
}
