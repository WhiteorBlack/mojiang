package cn.idcby.jiajubang.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

import cn.idcby.commonlibrary.utils.ResourceUtils;

public class GlideRoundTransform extends BitmapTransformation {

    private Paint mBorderPaint;
    private int mBorderWidth = 0 ;
    private static float radiusBorder = 0f;
    private static float radiusBitmap = 0f;

    public GlideRoundTransform(Context context) {
        super();

        radiusBitmap = (float) ResourceUtils.dip2px(context , 5) ;//如果只是圆角，不要边框时，默认5dp
    }
    public GlideRoundTransform(Context context , int radius) {
        super();

        radiusBitmap = (float) ResourceUtils.dip2px(context , radius) ;
    }

    public GlideRoundTransform(Context context,int radius , int borderWidth, int borderColor) {
        this(context,borderWidth,borderColor);
        radiusBorder = (float) ResourceUtils.dip2px(context , radius) ;
    }
    public GlideRoundTransform(Context context, int borderWidth, int borderColor) {
        super();

        radiusBorder = (float) ResourceUtils.dip2px(context , 5) ;
        radiusBitmap = (float) ResourceUtils.dip2px(context , 3) ;

        mBorderWidth = ResourceUtils.dip2px(context , borderWidth);

        mBorderPaint = new Paint();
        mBorderPaint.setDither(true);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        return circleCrop(pool, bitmap);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);

        int width = source.getWidth() ;
        int height = source.getHeight() ;

        if (mBorderPaint != null) {
            RectF rectFb = new RectF(0f, 0f, width, height);
            canvas.drawRoundRect(rectFb, radiusBorder, radiusBorder, mBorderPaint);
        }

        RectF rectF = new RectF(mBorderWidth / 2, mBorderWidth / 2 , width - mBorderWidth /2, height - mBorderWidth /2);
        canvas.drawRoundRect(rectF, radiusBitmap, radiusBitmap, paint);

        return result;
    }

    public String getId() {
        return getClass().getName() + Math.round(mBorderWidth);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {

    }
}