package cn.idcby.jiajubang.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.view.CustomBarChart;

/**
 * <pre>
 *     author : hhh
 *     e-mail : xxx@xx
 *     time   : 2018/05/03
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MyOperateActivity extends BaseMoreStatusActivity{

    String[] xLabel = {"0","板凳", "桌椅", "电脑", "家居", "地毯", "其他"};
    String[] yLabel = {"0", "100", "200", "300", "400", "500", "600"};
    int[] data1 = {300, 500, 550, 500, 300, 700, 800, 750, 550, 600, 400, 300, 400, 600, 500};
    List<int[]> data = new ArrayList<>();
    List<Integer> color = new ArrayList<>();


    private TextView mTitle,mTime,mFristCustomBarChart,mSecondCustomBarChart,mThirdCustomBarChart;
    private LinearLayout mFristCustomBarChartll,mSecondCustomBarChartll,mThirdCustomBarChartll;


    @Override
    public void requestData() {
        showSuccessPage();
        initRequestData();

    }

    private void initRequestData() {
        data.add(data1);
        color.add(R.color.colorPrimaryDark);
        color.add(R.color.red);
        color.add(R.color.colorPrimaryDark);

    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_my_operate_info;
    }

    @Override
    public String setTitle() {
        return "经营分析";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        initView();
    }

    private void initView() {
        //            operate_title_name_tv,operate_time_tv,operate_title_name_frist_tv
        mTitle=(TextView)findViewById(R.id.operate_title_name_tv);
        mTime=(TextView)findViewById(R.id.operate_time_tv);
        mFristCustomBarChart=(TextView)findViewById(R.id.operate_title_name_frist_tv);
        mSecondCustomBarChart=(TextView)findViewById(R.id.operate_title_name_second_tv);
        mThirdCustomBarChart=(TextView)findViewById(R.id.operate_title_name_third_tv);

        mFristCustomBarChartll=(LinearLayout)findViewById(R.id.operate_customBarChart_frist);
        mSecondCustomBarChartll=(LinearLayout)findViewById(R.id.operate_customBarChart_second);
        mThirdCustomBarChartll=(LinearLayout)findViewById(R.id.operate_customBarChart_third);

        mFristCustomBarChartll.addView(new CustomBarChart(this, xLabel, yLabel, data, color));
        mSecondCustomBarChartll.addView(new CustomBarChart(this, xLabel, yLabel, data, color));
        mThirdCustomBarChartll.addView(new CustomBarChart(this, xLabel, yLabel, data, color));
    }

    @Override
    public void dealOhterClick(View view) {

    }
}
