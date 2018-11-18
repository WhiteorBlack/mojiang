package cn.idcby.jiajubang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import cn.idcby.jiajubang.R;

/**
 * Created on 2018/4/11.
 */

public class MyCornerTextView extends AppCompatTextView {
    private int mBorderWidth = 0;
    private int mBorderWidthColor = Color.YELLOW;
    private int mCornersize = 8; //我们默认以px为单位
    private Paint mCornerPaint; //边框画笔   文字我们只用系统的 TextView


    @Override
    public boolean removeCallbacks(Runnable action) {
        return super.removeCallbacks(action);
    }

    public MyCornerTextView(Context context) {
        this(context,null);
    }

    public MyCornerTextView(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyCornerTextView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context,attrs);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyCornerTextView);
        mBorderWidth = (int) array.getDimension(R.styleable.MyCornerTextView_borderWidth,mBorderWidth);
        mBorderWidthColor = array.getColor(R.styleable.MyCornerTextView_borderWidthColor,mBorderWidthColor);
        mCornersize = (int) array.getDimension(R.styleable.MyCornerTextView_cornersize,mCornersize);
        array.recycle();

        mCornerPaint =  new Paint();
        mCornerPaint.setAntiAlias(true);
        mCornerPaint.setDither(true);
        mCornerPaint.setStrokeWidth(mBorderWidth);
        mCornerPaint.setStyle(Paint.Style.FILL_AND_STROKE); //实心 只画边框也画心
        mCornerPaint.setColor(mBorderWidthColor);
        if(mBorderWidth > 0){
            mCornerPaint.setStyle(Paint.Style.STROKE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rectF = new RectF(mBorderWidth/2,mBorderWidth/2,getMeasuredWidth()-mBorderWidth,getMeasuredHeight()-mBorderWidth);
        canvas.drawRoundRect(rectF,mCornersize,mCornersize,mCornerPaint);
        super.onDraw(canvas);
    }

    /**
     * 设置 corner 边角
     * @param size
     */
    public void setCornerSize(int size) { //设置的单位默认是px

        mCornersize  =size;
        invalidate();

    }
    //设置颜色

    public MyCornerTextView setfilColor (int color) {
        this.mBorderWidthColor = color;
        mCornerPaint.setColor(mBorderWidthColor);
        return this;
    }
    //设置颜色
    public MyCornerTextView setBorderWidth (int width) {
        this.mBorderWidth = width;
        if(mBorderWidth > 0){
            mCornerPaint.setStyle(Paint.Style.STROKE);
        }else{
            mCornerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        return this;
    }
}
