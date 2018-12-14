package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.jiajubang.Bean.FocusResult;
import cn.idcby.jiajubang.Bean.UserVic;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterVicUserList;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created on 2018/3/24.
 * 行业大咖
 */

public class VicUserActivity extends BaseActivity {
    private LoadingDialog loadingDialog;

    private TextView mSearchKeyTv;
    private MaterialRefreshLayout mRefreshLay;
    private ListView mLv;

    private String mSearchKey;

    private AdapterVicUserList mAdapter;
    private List<UserVic> mDataList = new ArrayList<>();
    private int mCurPage = 1;
    private boolean mIsMore = true;
    private boolean mIsLoading = false;

    private int mCurPosition;

    private static final int REQUEST_CODE_SEARCH = 1001;


    private void init() {
        loadingDialog = new LoadingDialog(mContext);

        View mSearchLay = findViewById(R.id.acti_vic_user_list_search_lay);
        mSearchKeyTv = findViewById(R.id.acti_vic_user_list_search_key_tv);
        View mApplyTv = findViewById(R.id.acti_vic_user_right_tv);
        mSearchLay.setOnClickListener(this);
        mApplyTv.setOnClickListener(this);

        mRefreshLay = findViewById(R.id.acti_vic_user_refresh_lay);
        mLv = findViewById(R.id.acti_vic_user_lv);

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1;
                getVicUserList();
            }
        });

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (mIsMore && !mIsLoading && i2 > 5 && i1 + i >= i2) {
                    getVicUserList();
                }
            }
        });

        mAdapter = new AdapterVicUserList(mContext, mDataList, new RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if (0 == type) {
                    mCurPosition = position;
                    if (LoginHelper.isNotLogin(mContext)) {
                        SkipUtils.toLoginActivityForResult(mActivity, 1000);
                    } else {
                        changeFocusState();
                    }
                } else if (1 == type) {
                    String userId = mDataList.get(position).getUserId();
                    SkipUtils.toOtherUserInfoActivity(mContext, userId);
                }
            }

            @Override
            public void onItemLongClickListener(int type, int position) {
            }
        });
        mLv.setAdapter(mAdapter);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_vic_user_list;
    }

    @Override
    public void initView() {
        super.initView();

        init();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        super.initData();

        loadingDialog.show();
        getVicUserList();
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId();

        if (R.id.acti_vic_user_list_search_lay == vId) {
            SkipUtils.toSearchNomalActivity(mActivity, mSearchKey, REQUEST_CODE_SEARCH);
        } else if (R.id.acti_vic_user_right_tv == vId) {
            Intent mApIt = new Intent(mContext, IndustryVApplyActivity.class);
            startActivity(mApIt);
        }

    }

    /**
     * 大咖列表
     */
    private void getVicUserList() {
        mIsLoading = true;

        Map<String, String> paramMap = ParaUtils.getParaNece(mContext);
        paramMap.put("PageSize", "20");
        paramMap.put("Page", "" + mCurPage);
        paramMap.put("Keyword", StringUtils.convertNull(mSearchKey));

        NetUtils.getDataFromServerByPost(mContext, Urls.USER_FOCUS_LIST, false, paramMap
                , new RequestListCallBack<UserVic>("getVicUserList", mContext, UserVic.class) {
                    @Override
                    public void onSuccessResult(List<UserVic> bean) {
                        mRefreshLay.finishRefresh();
                        loadingDialog.dismiss();

                        if (1 == mCurPage) {
                            mDataList.clear();
                        }

                        mDataList.addAll(bean);
                        mAdapter.notifyDataSetChanged();

                        if (bean.size() == 0) {
                            mIsMore = false;
                        } else {
                            mIsMore = true;
                            mCurPage++;
                        }

                        mIsLoading = false;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                        mIsLoading = false;
                        mRefreshLay.finishRefresh();
                    }

                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                        mIsLoading = false;
                        mRefreshLay.finishRefresh();
                    }
                });
    }

    /**
     * 关注、取消关注
     */
    private void changeFocusState() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("FollowType", "1");
        paramMap.put("ResourceId", mDataList.get(mCurPosition).getUserId());

        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, paramMap
                , new RequestObjectCallBack<FocusResult>("changeFocusState", mContext, FocusResult.class) {
                    @Override
                    public void onSuccessResult(FocusResult bean) {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }

                        mDataList.get(mCurPosition).setIsFollow(bean);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (1000 == requestCode) {
            if (RESULT_OK == resultCode) {
                changeFocusState();
            }
        } else if (REQUEST_CODE_SEARCH == requestCode) {
            if (RESULT_OK == resultCode && data != null) {
                mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if ("".equals(StringUtils.convertNull(mSearchKey))) {
                    mSearchKeyTv.setText("搜索昵称/手机号");
                } else {
                    mSearchKeyTv.setText(mSearchKey);
                }

                loadingDialog.show();
                mCurPage = 1;
                getVicUserList();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getVicUserList");
    }
}
