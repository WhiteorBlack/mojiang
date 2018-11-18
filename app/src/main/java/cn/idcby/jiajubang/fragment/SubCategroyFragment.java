package cn.idcby.jiajubang.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.dialog.LoadPage;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.SubCategoryBean;
import cn.idcby.jiajubang.Bean.SubListBeans;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.NeedsDetailsActivity;
import cn.idcby.jiajubang.adapter.HomeHotNewsAdapter;
import cn.idcby.jiajubang.adapter.SubscriptionListAdapter;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;
import okhttp3.Call;

/**
 * Created by Administrator on 2018-04-18.
 */

public class SubCategroyFragment extends LazyFragment {

    private SubCategoryBean mBeans;
    private MaterialRefreshLayout mRefreshLay;
    private ListView mListView;
    private TextView nodataview;

    private int mCurPage = 1;
    private final static int PAGE_SIZE = 10;
    private boolean mIsMore = true;
    private boolean mIsLoading = false;
    private SubscriptionListAdapter mAdapter;

    private List<SubListBeans> mSublistBean = new ArrayList<>();


    public static SubCategroyFragment getInstance(SubCategoryBean mBeans) {
        SubCategroyFragment sf = new SubCategroyFragment();
        sf.mBeans = mBeans;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_sub_card_recy, null);
        mListView = (ListView) v.findViewById(R.id.list_view);
        mRefreshLay = (MaterialRefreshLayout) v.findViewById(R.id.frag_news_refresh_lay);
        nodataview = (TextView) v.findViewById(R.id.tv_nodatas);
        getdatas();
        return v;
    }


    @Override
    public void onLazyLoad() {
        getdatas();
    }

    private void getdatas() {
        showSubListDatas();
        requestSublistdata();
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (!mIsLoading && mIsMore && i2 >= PAGE_SIZE && i + i1 >= i2) {
                    requestSublistdata();
                }
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1;
                mIsMore = true;
                requestSublistdata();
            }
        });
    }

    private void showNoDatas() {
        mListView.setVisibility(View.GONE);
        nodataview.setVisibility(View.VISIBLE);

    }

    private void showSubListDatas() {
        mAdapter = new SubscriptionListAdapter(getActivity(), mSublistBean);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent toNdIt = new Intent(mFragmentContext, NeedsDetailsActivity.class);
                toNdIt.putExtra(SkipUtils.INTENT_NEEDS_ID, mSublistBean.get(position).getNeedId());
                mFragmentContext.startActivity(toNdIt);
            }
        });
    }

    private void requestSublistdata() {
        mIsLoading = true;
        if (1 == mCurPage) {
            mSublistBean.clear();
            mAdapter.notifyDataSetChanged();
        }
        Map<String, String> paramMap = ParaUtils.getParaWithToken(getContext());
        paramMap.put("Type", String.valueOf(mBeans.getCoulmn()));
        paramMap.put("Page", String.valueOf(mCurPage));
        paramMap.put("PageSize", String.valueOf(PAGE_SIZE));
        paramMap.put("Keyword", "");

        NetUtils.getDataFromServerByPost(getContext(), Urls.SUB_SUBSCRIBE, paramMap,
                new RequestListCallBack<SubListBeans>("AppSub", getContext(), SubListBeans.class) {
                    @Override
                    public void onSuccessResult(List<SubListBeans> bean) {

                        mSublistBean.addAll(bean);
                        mAdapter.notifyDataSetChanged();

                        if (bean.size() < PAGE_SIZE) {
                            mIsMore = false;
                        } else {
                            mCurPage++;
                        }

                        finishRequest();


                    }

                    @Override
                    public void onErrorResult(String str) {
                        finishRequest();
                    }

                    @Override
                    public void onFail(Exception e) {
                        finishRequest();
                    }
                }
        );

    }

    private void finishRequest() {
        mRefreshLay.finishRefresh();
        mIsLoading = false;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getdatas();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
        } else {
            mSublistBean.clear();
            mCurPage = 1;
            getdatas();
        }
    }
}
