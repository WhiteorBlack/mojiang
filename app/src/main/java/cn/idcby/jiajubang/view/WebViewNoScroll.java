package cn.idcby.jiajubang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class WebViewNoScroll extends WebView {

	public WebViewNoScroll(Context context) {
		super(context);
	}

	public WebViewNoScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebViewNoScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
