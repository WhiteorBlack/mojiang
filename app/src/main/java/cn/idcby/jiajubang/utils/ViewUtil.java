package cn.idcby.jiajubang.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.R;

public class ViewUtil {
	
	/**
	 * 设置view的显示隐藏
	 * @param v v
	 * @param isVisible is visible
	 */
	public static void setViewVisible(View v , boolean isVisible){
		if(null == v){
			return ;
		}
		if(isVisible){
			if(v.getVisibility() != View.VISIBLE){
				v.setVisibility(View.VISIBLE) ;
			}
		}else{
			if(v.getVisibility() != View.GONE){
				v.setVisibility(View.GONE) ;
			}
		}
	}

	/**
	 * 改变页码在textview中的显示--当前页加粗加大，其他正常
	 * @param tv  textview
	 * @param tvSize  textview size
	 * @param page  string curpage and allpage
	 * @param isBlod  blod
	 */
	public static void convertPageToTextView(TextView tv , int tvSize , String page , boolean isBlod){
		if(null == page || "".equals(page.trim())){
			tv.setText("") ;
			return ;
		}

		int firstSpli = 0 ;
		int endSpli = page.indexOf("/") ;//分割符位置

		SpannableString span = new SpannableString(page);
		span.setSpan(new AbsoluteSizeSpan(tvSize), firstSpli, endSpli, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		if(isBlod){
			span.setSpan(new StyleSpan(Typeface.BOLD),firstSpli,endSpli, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		tv.setText(span) ;
	}

	/**
	 * 改变textview中的加粗
	 * @param tv  textview
	 * @param content  content
	 * @param start  start
	 * @param end  end
	 * @param isBlod  blod
	 */
	public static void convertTextViewStyle(TextView tv,String content,int start ,int end ,boolean isBlod){
		if(null == content || "".equals(content.trim())){
			tv.setText("") ;
			return ;
		}

		if(start < 0 || end <= start || end > content.length()){
			return ;
		}

		SpannableString span = new SpannableString(content);
		if(isBlod){
			span.setSpan(new StyleSpan(Typeface.BOLD),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}else{
			span.setSpan(new StyleSpan(Typeface.NORMAL),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		tv.setText(span) ;
	}

    /**
     * 设置textView内容不同颜色
     * @param tv textView
     * @param startP 开始位置
     * @param endP 结束位置
     * @param text 文字内容
     * @param color 变色的文字颜色
     */
	public static void convertDiffrentColorToTextView(TextView tv , int startP , int endP , String text , int color){
		if(null == tv){
			return ;
		}

		String strText = null == text ? "" : text.trim() ;
		int length = strText.length() ;

		if(length > 0 && startP >= 0 && startP < length && endP >=0 && startP < endP ){
            if(endP > length){
                endP = length ;
            }

            SpannableStringBuilder builder = new SpannableStringBuilder(strText);

            //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            builder.setSpan(colorSpan, startP, endP, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv.setText(builder) ;
        }else{
			tv.setText(strText) ;
		}
	}

	/**
	 * recycle view is arrive to bottom
	 * @param rv recyclerView
	 * @return true yes ; false no
     */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static boolean isRecycleView2Bottom(RecyclerView rv){
		//RecyclerView.canScrollVertically(1)的值表示是否能向上滚动，false表示已经滚动到底部
		//RecyclerView.canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
		return !rv.canScrollVertically(1) ;
	}

	/**
	 * recycle view is arrive to bottom
	 * @param rv recyclerView
	 * @return true yes ; false no
     */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static boolean isRecycleView2Top(RecyclerView rv){
		//RecyclerView.canScrollVertically(1)的值表示是否能向上滚动，false表示已经滚动到底部
		//RecyclerView.canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
		return !rv.canScrollVertically(-1) ;
	}

	/**
	 * recycle view is arrive to bottom
	 * @param recyclerView recyclerView
	 * @return true yes ; false no
	 */
	public static boolean isSlideToBottom(RecyclerView recyclerView) {
		if (recyclerView == null) return false;
		if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
				>= recyclerView.computeVerticalScrollRange())
			return true;
		return false;
	}




	/**
	 * scroll to position
	 * @param layoutManager LayoutManager
	 * @param rv RecyclerView
     * @param n position
     */
	public static void moveToPosition(LinearLayoutManager layoutManager , RecyclerView rv , int n) {
		//先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
		int firstItem = layoutManager.findFirstVisibleItemPosition();
		int lastItem = layoutManager.findLastVisibleItemPosition();

		int orientation = layoutManager.getOrientation() ;

		//然后区分情况
		if (n <= firstItem ){
			//当要置顶的项在当前显示的第一个项的前面时
			rv.scrollToPosition(n);
		}else if ( n <= lastItem ){
			//当要置顶的项已经在屏幕上显示时
            if(LinearLayoutManager.VERTICAL == orientation){
                int top = rv.getChildAt(n - firstItem).getTop();
                rv.scrollBy(0, top);
            }else{
                int right = rv.getChildAt(n - firstItem).getRight();
                rv.scrollBy(right, 0);
            }
		}else{
			//当要置顶的项在当前显示的最后一项的后面时
			rv.scrollToPosition(n);
		}
	}

	/**
	 * 给view设置新背景，并回收旧的背景图片<br>
	 * 注意：需要确定以前的背景不被使用
	 * @param v view
	 * @param resID resourceid
	 */
	@SuppressWarnings("deprecation")
	public static void setAndRecycleBackground(View v, int resID) {
        try {
            // 获得ImageView当前显示的图片
            Bitmap bitmap1 = null;
            if (v.getBackground() != null) {
                try {
                    //若是可转成bitmap的背景，手动回收
                    bitmap1 = ((BitmapDrawable) v.getBackground()).getBitmap();
                } catch (ClassCastException e) {
                    //若无法转成bitmap，则解除引用，确保能被系统GC回收
                    v.getBackground().setCallback(null);
                }
            }

            // 根据原始位图和Matrix创建新的图片
            v.setBackgroundResource(resID);
            // bitmap1确认即将不再使用，强制回收，这也是我们经常忽略的地方
            if (bitmap1 != null && !bitmap1.isRecycled()) {
                bitmap1.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	/**
	 * 获取加载中的提示Tv
	 * @param context c
	 * @return tv
	 */
	public static TextView getLoadingLvFooterView(Context context){
		TextView mFooterView = new TextView(context) ;
		mFooterView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT
				, ResourceUtils.dip2px(context , 40)));
		mFooterView.setText(context.getResources().getString(R.string.footer_loading_string));
		mFooterView.setTextSize(TypedValue.COMPLEX_UNIT_DIP , 14);
		mFooterView.setTextColor(context.getResources().getColor(R.color.color_grey_88)) ;
		mFooterView.setGravity(Gravity.CENTER);

		return mFooterView ;
	}


}
