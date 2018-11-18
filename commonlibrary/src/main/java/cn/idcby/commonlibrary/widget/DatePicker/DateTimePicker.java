/*
 * AUTHOR：YOLANDA
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © ZhiMore. All Rights Reserved
 *
 */
package cn.idcby.commonlibrary.widget.DatePicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.Calendar;

import cn.idcby.commonlibrary.R;


/**
 * <br/>
 * Created in Jan 12, 2016 3:50:49 PM
 *
 * @author YOLANDA; QQ: 757699476
 */
public class DateTimePicker extends LinearLayout {

    /**
     * 年
     */
    private WheelView mViewYear;
    /**
     * 月
     */
    private WheelView mViewMonth;
    /**
     * 日
     */
    private WheelView mViewDay;
    /**
     * 时
     */
    private WheelView mViewHour;
    /**
     * 分
     */
    private WheelView mViewMin;
    /**
     * 秒
     */
    private WheelView mViewSec;
    /**
     * 不变的日历
     */
    private Calendar mNowCalendar;
    /**
     * 变化的日历
     */
    private Calendar mChangeCalendar;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DateTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttribute();
    }

    public DateTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute();
    }

    public DateTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateTimePicker(Context context) {
        this(context, null, 0);
    }

    private void initAttribute() {
        // 日历的实例（获取实时时间）
        mNowCalendar = Calendar.getInstance();

        LayoutInflater.from(getContext()).inflate(R.layout.view_date_time_picker, this, true);
        mViewYear = (WheelView) findViewById(R.id.wheelview_dialog_year);
        mViewMonth = (WheelView) findViewById(R.id.wheelview_dialog_month);
        mViewDay = (WheelView) findViewById(R.id.wheelview_dialog_day);
        mViewHour = (WheelView) findViewById(R.id.wheelview_dialog_time);
        mViewMin = (WheelView) findViewById(R.id.wheelview_dialog_min);
        mViewSec = (WheelView) findViewById(R.id.wheelview_dialog_sec);
        mViewSec.setVisibility(GONE);
        //设置显示行数
        mViewYear.setVisibleItems(4);
        mViewMonth.setVisibleItems(4);
        mViewDay.setVisibleItems(4);
        mViewHour.setVisibleItems(4);
        mViewMin.setVisibleItems(4);


        /** 初始化年的轮子 **/
        // 初始化保存年的数组
        NumericWheelAdapter yearWheelAdapter = new NumericWheelAdapter(getContext(), 1970, 2088, "%02d");
        yearWheelAdapter.setLabel(getContext().getString(R.string.year));
        mViewYear.setViewAdapter(yearWheelAdapter);// 设置年的数据适配
        mViewYear.addChangingListener(mYearMonthListener);
        mViewYear.setCyclic(true);

        /** 初始化月的轮子 **/
        NumericWheelAdapter monthWheelAdapter = new NumericWheelAdapter(getContext(), 1, 12, "%02d");
        monthWheelAdapter.setLabel(getContext().getString(R.string.month));
        mViewMonth.setViewAdapter(monthWheelAdapter);
        mViewMonth.addChangingListener(mYearMonthListener);
        mViewMonth.setCyclic(true);

        /** 初始化日的轮子 **/
        NumericWheelAdapter dayWheelAdapter = new NumericWheelAdapter(getContext(), 1, getDay(mNowCalendar.get(Calendar.YEAR), mNowCalendar.get(Calendar.MONTH) + 1), "%02d");
        dayWheelAdapter.setLabel(getContext().getString(R.string.day));
        mViewDay.setViewAdapter(dayWheelAdapter);
        mViewDay.setCyclic(true);
        /** 初始化时的轮子 **/
        NumericWheelAdapter hourWheelAdapter = new NumericWheelAdapter(getContext(), 0, 23, "%02d");
        hourWheelAdapter.setLabel(getContext().getString(R.string.hour));
        mViewHour.setViewAdapter(hourWheelAdapter);
        mViewHour.setCyclic(true);
        /** 初始化分的轮子 **/
        NumericWheelAdapter minWheelAdapter = new NumericWheelAdapter(getContext(), 0, 59, "%02d");
        minWheelAdapter.setLabel(getContext().getString(R.string.min));
        mViewMin.setViewAdapter(minWheelAdapter);
        mViewMin.setCyclic(true);
        // 初始化日期的年月日
        mViewYear.setCurrentItem(mNowCalendar.get(Calendar.YEAR) - 1970);
        mViewMonth.setCurrentItem(mNowCalendar.get(Calendar.MONTH));
        mViewDay.setCurrentItem(mNowCalendar.get(Calendar.DAY_OF_MONTH) - 1);
        mViewHour.setCurrentItem(mNowCalendar.get(Calendar.HOUR_OF_DAY));
        mViewMin.setCurrentItem(mNowCalendar.get(Calendar.MINUTE));
    }

    public void set() {
    }

    /**
     * 获取日期
     */
    public String[] getDate() {
        String year = String.valueOf(mViewYear.getCurrentItem() + 1970);
        String month = String.valueOf(mViewMonth.getCurrentItem() + 1);
        String day = String.valueOf(mViewDay.getCurrentItem() + 1);
        String hour = String.valueOf(mViewHour.getCurrentItem());
        String min = String.valueOf(mViewMin.getCurrentItem());
        return new String[]{year, month, day, hour, min};
    }

//    /**
//     * 获取年
//     */
//    public String getYear() {
//        return nianNum[mViewYear.getCurrentItem()];
//    }
//
//    /**
//     * 获取日
//     */
//    public String getMonth() {
//        return monthNum[mViewMonth.getCurrentItem()];
//    }
//
//    /**
//     * 获取日
//     */
//    public String getDay() {
//        return riNum[mViewDay.getCurrentItem()];
//    }

    /**
     * 年和月改变时联动日
     */
    private OnWheelChangedListener mYearMonthListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            int n_year = mViewYear.getCurrentItem() + 1970;//年
            int n_month = mViewMonth.getCurrentItem() + 1;//月
            initDay(n_year, n_month);
        }
    };

    /**
     * 更新日
     */
    private void initDay(int arg1, int arg2) {
        int maxDays = getDay(arg1, arg2);// 拿到这个月的最大的日
        NumericWheelAdapter dayWheelAdapter = new NumericWheelAdapter(getContext(), 1, maxDays, "%02d");
        dayWheelAdapter.setLabel(getContext().getString(R.string.day));
        mViewDay.setViewAdapter(dayWheelAdapter);// 设置日的轮子日期

        // 比较两个今天和选定的日的大小，之后选定小的；因为如果现在选定的是3.31，那么月切换到2月后，2月最大为29，那么就要选定29
        int curDay = Math.min(maxDays, mViewDay.getCurrentItem());
        mViewDay.setCurrentItem(curDay, true);
    }

    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

//	/**
//	 * 数字轮子的适配器，给当前项目高亮显示
//	 */
//	private class DateNumericAdapter extends NumericWheelAdapter {
//		/**
//		 * 要高亮的项目
//		 */
//		private int heigthItem;
//		/**
//		 * 当前项目的索引
//		 */
//		private int currentItem;
//
//		public DateNumericAdapter(Context context, int minValue, int maxValue, int heightItem) {
//			super(context, minValue, maxValue);
//			this.heigthItem = heightItem;
//			setTextSize(20);
//		}
//
//		@Override
//		protected void configureTextView(TextView view) {
//			super.configureTextView(view);
//			if (currentItem == heigthItem) {
//				view.setTextColor(Color.parseColor("#FF4F638B"));
//			}
//		}
//
//		@Override
//		public View getItem(int index, View cachedView, ViewGroup parent) {
//			currentItem = index;// 保存当前item
//			return super.getItem(index, cachedView, parent);
//		}
//	}
}
