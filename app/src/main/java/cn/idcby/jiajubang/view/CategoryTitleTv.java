package cn.idcby.jiajubang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.view.refresh.Util;

/**
 * D:
 */

public class CategoryTitleTv extends View {
    private String mTitleText;
    private int mTitleTextSize ;

    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    private TextPaint mTextPaint ;
    private Paint mLinePaint ;

    private int mTextPaddingLeft ;
    private int mTextPaddingRight ;
    private int mLineWidth ;

    public CategoryTitleTv(Context context) {
        this(context , null);
    }

    public CategoryTitleTv(Context context, AttributeSet attrs) {
        this(context, attrs ,0);
    }

    public CategoryTitleTv(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs , R.styleable.Line_textView_styleable) ;

        mLineWidth = array.getDimensionPixelSize(R.styleable.Line_textView_styleable_line_width , 0) ;
        int mLineHei = array.getDimensionPixelSize(R.styleable.Line_textView_styleable_line_height , 0) ;
        int mLineColor = array.getColor(R.styleable.Line_textView_styleable_line_color
                , getResources().getColor(R.color.color_grey_f3)) ;

        mTitleText = array.getString(R.styleable.Line_textView_styleable_tv_text) ;
        int titleSize = array.getInt(R.styleable.Line_textView_styleable_tv_size , 14) ;
        int titleColor = array.getColor(R.styleable.Line_textView_styleable_tv_color
                , getResources().getColor(R.color.color_nomal_text)) ;
        boolean isBold = array.getBoolean(R.styleable.Line_textView_styleable_tv_bold , false) ;
        mTextPaddingLeft = array.getDimensionPixelSize(R.styleable.Line_textView_styleable_tv_padding_left , 0) ;
        mTextPaddingRight = array.getDimensionPixelSize(R.styleable.Line_textView_styleable_tv_padding_right , 0) ;

        array.recycle();

        mLinePaint = new Paint() ;
        mLinePaint.setStrokeWidth(mLineHei);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.FILL);


        if(null == mTitleText){
            mTitleText = "" ;
        }
        mTitleTextSize = Util.dip2px(context , titleSize) ;

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(titleColor);
        mTextPaint.setTextSize(mTitleTextSize) ;
        mTextPaint.setTypeface(isBold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT) ;

        mBound = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec) ;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec) ;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec) ;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec) ;

        int width ;
        int height ;

        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize ;
        }else{
            mTextPaint.setTextSize(mTitleTextSize);
            mTextPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
            float textWidth = mBound.width();
            width = (int) (getPaddingLeft() + textWidth + getPaddingRight());
        }

        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        } else
        {
            mTextPaint.setTextSize(mTitleTextSize);
            mTextPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
            float textHeight = mBound.height();
            height = (int) (getPaddingTop() + textHeight + getPaddingBottom());
        }

        //左右多出 mLineWidth 和 mTextPadding
        setMeasuredDimension(width + mLineWidth * 2 + mTextPaddingLeft + mTextPaddingRight , height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(getPaddingLeft(), getHeight() / 2 + 2
                , getPaddingLeft() + mLineWidth ,getHeight() / 2 + 2 , mLinePaint) ;

        canvas.drawText(mTitleText
                ,getWidth() / 2 - mBound.width() / 2
                , getHeight() / 2 + mBound.height() / 2
                , mTextPaint);

        canvas.drawLine(getWidth() - mLineWidth - getPaddingRight() , getHeight() / 2 + 2
                , getWidth() - getPaddingRight() ,getHeight() / 2 + 2 , mLinePaint) ;
    }

    public void setText(String text){
        String str = text ;
        if(null == str){
            str = "" ;
        }

        if(mTitleText.equals(text)){
            return;
        }

        mTitleText = text ;
        postInvalidate();
    }

}
