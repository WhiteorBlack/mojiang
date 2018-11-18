package cn.idcby.jiajubang.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.utils.AppUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.NomalH5Bean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterHelper;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 关于我们
 * Created on 2018/5/2.
 */

public class AboutUsActivity extends BaseMoreStatusActivity {
    private List<NomalH5Bean> mDataList = new ArrayList<>() ;
    private AdapterHelper mAdapter ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private List<AdvBanner> mTopBannerList = new ArrayList<>() ;
    private Banner mBanner ;


    @Override
    public void requestData() {
        requestBanner() ;
        getAboutList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_about_us;
    }

    @Override
    public String setTitle() {
        return "关于我们";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        TextView mVersionTv = findViewById(R.id.acti_about_version_tv) ;
        ListView mLv = findViewById(R.id.acti_about_lv) ;

        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_about_us_item,null) ;
        mBanner = headerView.findViewById(R.id.header_about_us_banner) ;

        mLv.addHeaderView(headerView);

        mAdapter = new AdapterHelper(mContext ,mDataList) ;
        mLv.setAdapter(mAdapter);
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && mDataList.size() >= 10 && i + i1 >= i2){
                    getAboutList();
                }
            }
        });

        mBanner.getLayoutParams().height = (int) (ResourceUtils.getScreenWidth(mContext) / ImageWidthUtils.nomalBannerImageRote());
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置轮播时间
        mBanner.setDelayTime(5000);
        mBanner.setImageLoader(new BannerImageLoader()) ;
        mBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                SkipUtils.intentToOtherByAdvId(mContext ,mTopBannerList.get(position - 1)) ;
            }
        });

        mVersionTv.setText("版本号 " + AppUtils.getInstance(mContext).getVersionName()) ;
    }

    @Override
    public void dealOhterClick(View view) {

    }

    private void getAboutList(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"10") ;
        paramMap.put("Keyword" ,"AboutUs") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.HELPER_URL, paramMap
                , new RequestListCallBack<NomalH5Bean>("getAboutList" ,mContext ,NomalH5Bean.class) {
                    @Override
                    public void onSuccessResult(List<NomalH5Bean> bean) {
                        showSuccessPage() ;

                        if(1 == mCurPage){
                            mDataList.clear();
                        }

                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        mIsLoading = false ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showSuccessPage() ;

                        mIsLoading = false ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        showSuccessPage() ;

                        mIsLoading = false ;
                    }
                });
    }

    /**
     * 顶部banner
     */
    private void requestBanner() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", "AppAboutUsHead");
        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, false, para,
                new RequestListCallBack<AdvBanner>("getTopBanner", mContext, AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mTopBannerList.clear();
                        mTopBannerList.addAll(bean) ;
                        List<String> imageUrl = new ArrayList<>(mTopBannerList.size()) ;
                        for(AdvBanner banner : mTopBannerList){
                            imageUrl.add(banner.getImgUrl()) ;
                        }
                        mBanner.update(imageUrl) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                    }
                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getAboutList") ;
        NetUtils.cancelTag("getTopBanner") ;

    }
}
