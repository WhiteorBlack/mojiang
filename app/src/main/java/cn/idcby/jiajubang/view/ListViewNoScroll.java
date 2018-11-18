package cn.idcby.jiajubang.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

@SuppressLint("NewApi")
public class ListViewNoScroll extends ListView {

	public ListViewNoScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);  
	}

	public ListViewNoScroll(Context context, AttributeSet attrs) {
		super(context, attrs);  
	}

	public ListViewNoScroll(Context context) {
		super(context);  
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
