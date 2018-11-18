package cn.idcby.jiajubang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyLetterListView extends View {

	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	String[] b = {"A", "B", "C", "F", "G", "H",  "J","L","N", "Q", "S", "T",  "X","Y", "Z" };
	int choose = -1;
	Paint paint = new Paint();

	public String[] getTipsB() {
		return b;
	}

	public MyLetterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLetterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLetterListView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / b.length;
		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.parseColor("#8c8c8c"));
			paint.setTextSize(32);
			paint.setAntiAlias(true);
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * (i + 1) - singleHeight / 2;
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * b.length);
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				if (oldChoose != c && listener != null) {
					if (c >= 0 && c < b.length) {
						listener.onTouchingLetterChanged(b[c]);
						choose = c;
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (oldChoose != c && listener != null) {
					if (c >= 0 && c < b.length) {
						listener.onTouchingLetterChanged(b[c]);
						choose = c;
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				choose = -1;
				invalidate();
				break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}
}
