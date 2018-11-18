package cn.idcby.commonlibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.idcby.commonlibrary.R;

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
 * Created by mrrlb on 2018/2/5.
 */

public class LineView extends LinearLayout {

    private String title;
    private Boolean isShowRightArrow;
    private Boolean isShowLeftIcon;
    private Boolean isShowRightText;
    private int leftIcon;
    private String rightText;

    private TextView mTvTitle;
    private ImageView mImgArrow;
    private ImageView mImgLeftIcon;
    private TextView mTvRight;

    public LineView(Context context) {
        super(context);
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);

    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setGravity(Gravity.CENTER_VERTICAL);

        inflate(context,R.layout.view_line, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineView);

        int titleTextSize = typedArray.getInteger(R.styleable.LineView_titleTvSize ,12) ;
        int rightTextSize = typedArray.getInteger(R.styleable.LineView_rightTvSize ,10) ;
        title = typedArray.getString(R.styleable.LineView_titleText);
        isShowRightArrow = typedArray.getBoolean(R.styleable.LineView_rightArrowIsShow, true);
        isShowLeftIcon = typedArray.getBoolean(R.styleable.LineView_leftIconIsShow, false);
        isShowRightText = typedArray.getBoolean(R.styleable.LineView_rightTextIsShow, false);
        if (isShowLeftIcon) {
            leftIcon = typedArray.getResourceId(R.styleable.LineView_leftIcon, 0);
        }
        if (isShowRightText) {
            rightText = typedArray.getString(R.styleable.LineView_rightText);
        }

        typedArray.recycle();


        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mImgArrow = (ImageView) findViewById(R.id.img_arrow);
        mImgLeftIcon = (ImageView) findViewById(R.id.img_icon_left);
        mTvRight = (TextView) findViewById(R.id.tv_right_text);
        mTvTitle.setText(title);
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,titleTextSize) ;
        mTvRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,rightTextSize) ;

        if (isShowRightArrow) {
            mImgArrow.setVisibility(View.VISIBLE);
        } else {
            mImgArrow.setVisibility(View.GONE);
        }

        if (isShowLeftIcon) {
            mImgLeftIcon.setVisibility(View.VISIBLE);
            mImgLeftIcon.setImageResource(leftIcon);
        } else {
            mImgLeftIcon.setVisibility(View.GONE);
        }

        if (isShowRightText) {
            mTvRight.setVisibility(View.VISIBLE);
            mTvRight.setText(rightText);
        } else {
            mTvRight.setVisibility(View.GONE);
        }

    }

    public void setTitleText(String titleText){
        this.title = titleText ;
        mTvTitle.setText(titleText);
    }

    public void setRightText(String rightText) {
        this.rightText = rightText ;
        mTvRight.setText(rightText);
    }

    public String getRightText() {
        return mTvRight.getText().toString().trim();
    }
}
