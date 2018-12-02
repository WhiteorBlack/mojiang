package cn.idcby.jiajubang.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import cn.idcby.jiajubang.R;


/**
 * Created by Administrator on 2016/8/19.
 */
public class CountDownTimerUtils extends CountDownTimer {

    private TextView mTextView;
    private boolean change = true;
    private OnTimeFinishListener onTimeFinishListener;

    public interface OnTimeFinishListener {
        public abstract void onTimeFinish();
    }

    public void setOnTimeFinishListener(OnTimeFinishListener onTimeFinishListener) {
        this.onTimeFinishListener = onTimeFinishListener;
    }

    public CountDownTimerUtils(TextView mTextView) {
        super(60000, 1000);
        this.mTextView = mTextView;
    }

    public CountDownTimerUtils(TextView mTextView, boolean change) {
        super(60000, 1000);
        this.mTextView = mTextView;
        this.change = change;
    }

    @Override
    public void onTick(long millisUntilFinished) {

        mTextView.setClickable(false); //设置不可点击
        mTextView.setEnabled(false);
        mTextView.setText(millisUntilFinished / 1000 + "S重新发送");  //设置倒计时时间
        if (change)
            mTextView.setBackgroundResource(R.drawable.shape_gray_bg); //设置按钮为灰色，这时是不能点击的

        /**
         * 超链接 URLSpan
         * 文字背景颜色 BackgroundColorSpan
         * 文字颜色 ForegroundColorSpan
         * 字体大小 AbsoluteSizeSpan
         * 粗体、斜体 StyleSpan
         * 删除线 StrikethroughSpan
         * 下划线 UnderlineSpan
         * 图片 ImageSpan
         * http://blog.csdn.net/ah200614435/article/details/7914459
         */
//        SpannableString spannableString = new SpannableString(mTextView.getText().toString());  //获取按钮上的文字
//        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#FE8084"));
//        /**
//         * public void setSpan(Object what, int start, int end, int flags) {
//         * 主要是start跟end，start是起始位置,无论中英文，都算一个。
//         * 从0开始计算起。end是结束位置，所以处理的文字，包含开始位置，但不包含结束位置。
//         */
//        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色
//        mTextView.setText(spannableString);

    }

    @Override
    public void onFinish() {
        mTextView.setText("获取验证码");
        mTextView.setClickable(true);//重新获得点击
        mTextView.setEnabled(true);
        if (change)
            mTextView.setBackgroundResource(R.drawable.shape_green_with_circle_corner);  //还原背景色
        if (onTimeFinishListener != null)
            onTimeFinishListener.onTimeFinish();
    }

    public void setFinish() {
        mTextView.setText("获取验证码");
        mTextView.setClickable(true);//重新获得点击
        mTextView.setEnabled(true);
        mTextView.setBackgroundResource(R.drawable.shape_green_with_circle_corner);  //还原背景色
        onTimeFinishListener.onTimeFinish();
    }
}
