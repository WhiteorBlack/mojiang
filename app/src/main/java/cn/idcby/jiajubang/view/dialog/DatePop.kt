package cn.idcby.jiajubang.view.dialog

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import cn.idcby.jiajubang.R
import cn.idcby.jiajubang.utils.GetDateTimeUtil

import com.weigan.loopview.LoopView
import java.util.*

/**
 * 选择日期
 * Created by Slingge on 2018/1/17 0017.
 */

//maxDay true 使用未来时间
class DatePop(context: Context, var wheelViewCallBack: WheelViewCallBack2) : PopupWindow(context) {

    internal var position = 0
    internal var position2 = 0
    internal var position3 = 0

    private val yearList = ArrayList<String>()
    private val monthList = ArrayList<String>()
    private val dayList = ArrayList<String>()

    private var month = 0//当前月份
    private var day = 0//当天
    private var year = 0

    interface WheelViewCallBack2 {
        fun position(position1: String, position2: String, position3: String)
    }


    init {
        val v = LayoutInflater.from(context).inflate(R.layout.popup_address, null)
        val loopview = v.findViewById<View>(R.id.loopView) as LoopView
        val loopview2 = v.findViewById<View>(R.id.loopView2) as LoopView
        val loopview3 = v.findViewById<View>(R.id.loopView3) as LoopView
        loopview.setTextSize(16f)
        loopview2.setTextSize(16f)
        loopview3.setTextSize(16f)

        v.findViewById<View>(R.id.year).visibility = View.VISIBLE
        v.findViewById<View>(R.id.month).visibility = View.VISIBLE
        v.findViewById<View>(R.id.day).visibility = View.VISIBLE

        //设置是否循环播放
        //        loopView.setNotLoop();
        //滚动监听
        //设置原始数据

        if (yearList.isEmpty()) {
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)// 获取当前年份
            month = c.get(Calendar.MONTH) + 1// 获取当前月份
            day = c.get(Calendar.DAY_OF_MONTH)

            getYear()
            getMonth()
            getDay(year.toString(), month.toString())
        }

        loopview.setNotLoop()
        loopview.setItems(yearList)
        position = yearList.size - 21
        loopview.setCurrentPosition(position)

        loopview2.setNotLoop()
        loopview2.setItems(monthList.subList(0, month))
        position2 = month - 1
        loopview2.setCurrentPosition(position2)

        loopview3.setNotLoop()
        loopview3.setItems(dayList.subList(0, day))
        position3 = day - 1
        loopview3.setCurrentPosition(position3)
        wheelViewCallBack.position(yearList[position], monthList[month - 1], dayList[day - 1])

        loopview.setListener { index ->
            position = index

            position2 = 0
            if (yearList[position].toInt() == year) {
                loopview2.setItems(monthList.subList(0, month))
            } else {
                loopview2.setItems(monthList)
            }

            loopview2.setInitPosition(0)

            position3 = 0
            getDay(yearList[position], monthList[position2])
            loopview3.setItems(dayList)
            loopview3.setInitPosition(position3)

            wheelViewCallBack.position(yearList[position], monthList[position2], dayList[position3])
        }
        loopview2.setListener { index ->
            position2 = index

            position3 = 0

            getDay(yearList[position], monthList[position2])

            if (yearList[position].toInt() == year && monthList[position2].toInt() == month) {
                loopview3.setItems(dayList.subList(0, day))
            } else {
                loopview3.setItems(dayList)
            }

            loopview3.setInitPosition(0)

            wheelViewCallBack.position(yearList[position], monthList[position2], dayList[position3])
        }
        loopview3.setListener { index ->
            position3 = index
            wheelViewCallBack.position(yearList[position], monthList[position2], dayList[position3])
        }

        val tv_enter = v.findViewById<View>(R.id.tv_enter) as TextView
        tv_enter.setOnClickListener { v1 -> this@DatePop.dismiss() }

        //设置初始位置
        this.contentView = v
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置SelectPicPopupWindow弹出窗体的高
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.animationStyle = android.R.style.Animation_InputMethod
        this.isFocusable = true
        //		this.setOutsideTouchable(true);
        this.setBackgroundDrawable(BitmapDrawable())
        this.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
    }


    private fun getYear() {
        yearList.clear()
        for (i in year - 100..year) {
            yearList.add(i.toString())
        }

    }

    private fun getMonth() {
        monthList.clear()
        for (i in 1..12) {
            if (i < 10) {
                monthList.add("0" + i.toString())
            } else {
                monthList.add(i.toString())
            }
        }
    }

    private fun getDay(year: String, month: String) {
        val max = GetDateTimeUtil.MaxDayFromDay_OF_MONTH(year.toInt(), month.toInt())
        dayList.clear()
        for (i in 1..max) {
            if (i < 10) {
                dayList.add("0" + i.toString())
            } else {
                dayList.add(i.toString())
            }
        }
    }

}
