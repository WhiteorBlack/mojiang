package cn.idcby.jiajubang.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.SubCategoryBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.MyGreenIndicatorAdapter;
import cn.idcby.jiajubang.fragment.MySubsNeedFragment;
import cn.idcby.jiajubang.fragment.MySubsUnuseFragment;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018-04-17.
 */

public class MySubscriptionActivity extends BaseActivity {
    private MagicIndicator mSubsIndicator ;
    private ViewPager subViewpager;
    private TextView subAddsubscritp;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private TextView nodatatv;
    private TextView mCancletv;

    private List<String> titles = new ArrayList<>() ;
    private List<SubCategoryBean> mSubBeanlist = new ArrayList<>();
    private MyGreenIndicatorAdapter myIndicatorAdapter ;

    private String coulmnid;


    @Override
    public int getLayoutID() {
        return R.layout.activity_mine_subscription;
    }

    @Override
    public void initView() {
        subAddsubscritp = findViewById(R.id.tv_addsub);
        subViewpager = findViewById(R.id.subviewpager);
        mSubsIndicator = findViewById(R.id.acti_my_subscription_indicator);
        nodatatv = findViewById(R.id.tv_nodata);
        mCancletv = findViewById(R.id.tv_right);
    }


    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        subAddsubscritp.setOnClickListener(this);
        mCancletv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        if (view.getId() == R.id.tv_addsub) {
            goNextActivity(AddSubscriptionActivity.class);
        } else if (view.getId() == R.id.tv_right) {
            if (mSubBeanlist.size() > 0) {
                cancleDialgoshow();
            }
        }
    }

    private void cancleDialgoshow() {
        final String[] items = new String[mSubBeanlist.size()];
        final String[] itemids = new String[mSubBeanlist.size()];

        for (int i = 0, n = mSubBeanlist.size(); i < n; i++) {
            items[i] = String.valueOf(mSubBeanlist.get(i).getCoulmnText());
            itemids[i] = String.valueOf(mSubBeanlist.get(i).getTakeCoulmnId());
        }
        coulmnid=itemids[0];
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.login_logo)//设置标题的图片
                .setTitle("请选择想取消的项目")//设置对话框的标题
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        coulmnid=itemids[which];
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancleSubBycoulmnid(coulmnid);

                    }
                }).create();
        dialog.show();
    }

    private void cancleSubBycoulmnid(String coulmnid) {
        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("Code", coulmnid);
        NetUtils.getDataFromServerByPost(mContext, Urls.SUB_CANCLECOULMN, paramMap,
                new RequestObjectCallBack<String>("cancleSub",mContext,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        ToastUtils.showToast(mContext ,"取消订阅成功");
                        resumMether();
                    }

                    @Override
                    public void onErrorResult(String str) {

                    }

                    @Override
                    public void onFail(Exception e) {
                        ToastUtils.showToast(mContext ,"取消订阅失败");
                    }
                }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();

        resumMether();
    }
    private void resumMether() {
        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("Code", "AppSub");
        NetUtils.getDataFromServerByPost(mContext, Urls.SUB_CATEGORY, paramMap,
                new RequestListCallBack<SubCategoryBean>("subscritp", mContext, SubCategoryBean.class) {
                    @Override
                    public void onSuccessResult(List<SubCategoryBean> bean) {
                        mSubBeanlist.clear();
                        mSubBeanlist.addAll(bean) ;
                        showCategroyList();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        showCategroyList();
                    }

                    @Override
                    public void onFail(Exception e) {
                        showCategroyList();
                    }
                }

        );
    }

    private void showNoData() {
        subViewpager.setVisibility(View.GONE);
        mSubsIndicator.setVisibility(View.GONE);
        nodatatv.setVisibility(View.VISIBLE);
        mCancletv.setVisibility(View.GONE);
        subAddsubscritp.setVisibility(View.VISIBLE);
    }

    private void showCategroyList() {
        titles.clear();
        mFragments.clear();
        if(mAdapter != null){
            myIndicatorAdapter.notifyDataSetChanged() ;
            mAdapter.notifyDataSetChanged();
        }

        if(mSubBeanlist.size() == 0){
            showNoData() ;
           return ;
        }

        subViewpager.setVisibility(View.VISIBLE);
        mSubsIndicator.setVisibility(View.VISIBLE);
        mCancletv.setVisibility(View.VISIBLE);
        nodatatv.setVisibility(View.GONE);

        for (SubCategoryBean subCategoryBean : mSubBeanlist) {
            if(subCategoryBean != null){
                int cateType = subCategoryBean.getCoulmn() ;

                if(SubClassificationActivity.SUBS_TYPE_UNUSE == cateType){//闲置
                    mFragments.add(new MySubsUnuseFragment()) ;
                }else if(SubClassificationActivity.SUBS_TYPE_NEED == cateType){//需求
                    mFragments.add(new MySubsNeedFragment()) ;
                }

                titles.add(subCategoryBean.getCoulmnText()) ;
            }
        }

        if(null == mAdapter){
            myIndicatorAdapter = new MyGreenIndicatorAdapter(titles, subViewpager);
            CommonNavigator commonNavigator = new CommonNavigator(mContext);
            commonNavigator.setAdjustMode(true);
            commonNavigator.setSkimOver(true);
            commonNavigator.setAdapter(myIndicatorAdapter);
            mSubsIndicator.setNavigator(commonNavigator);

            mAdapter = new MyPagerAdapter(getSupportFragmentManager(), mSubBeanlist);
            subViewpager.setAdapter(mAdapter);
            ViewPagerHelper.bind(mSubsIndicator, subViewpager);
        }else{
            mSubsIndicator.getNavigator().notifyDataSetChanged();
            myIndicatorAdapter.notifyDataSetChanged() ;
            mAdapter.notifyDataSetChanged();
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        List<SubCategoryBean> bean;

        public MyPagerAdapter(FragmentManager fm, List<SubCategoryBean> bean) {
            super(fm);
            this.bean = bean;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return bean.get(position).getCoulmnText();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
