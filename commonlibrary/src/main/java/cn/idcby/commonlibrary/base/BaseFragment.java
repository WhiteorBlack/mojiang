package cn.idcby.commonlibrary.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.idcby.commonlibrary.dialog.LoadPage;
import cn.idcby.commonlibrary.utils.MyUtils;


/**
 * Created on 2016/10/31.
 */
public abstract class BaseFragment extends Fragment {
    public Fragment mFragment;
    public Context mContext;
    public FragmentActivity mActivity;
    public LoadPage loadPage;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity() ;
        mFragment = this ;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getContext();

        if (loadPage == null) {
            loadPage = new LoadPage(mContext) {
                @Override
                public View createSuccessView() {
                    return BaseFragment.this.createSuccessView(setSuccessViewId());
                }
            };
        }
        initData();
        loadPage.setOnRequestDatasAgain(new LoadPage.OnRequestDatasAgain() {
            @Override
            public void onRequestDatasAgain() {
                initData();
            }
        });
        return loadPage;
    }

    private void initData() {
        loadPage.showLoadingPage();
        if (!MyUtils.isNetworkConnected(mContext)) {
            //网络异常
            loadPage.showNetErrorPage();
            return;
        }
        requestData();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (MyUtils.isNetworkConnected(mContext)
                && loadPage.showNetErrorPage()) {
            loadPage.showSuccessPage() ;
        }
    }

    protected abstract void requestData();

    protected abstract void initView(View view);

    protected abstract int setSuccessViewId();

    protected abstract void initListener();

    public View createSuccessView(int layoutId) {
        View successView = View.inflate(mContext, layoutId, null);
        initView(successView);
        initListener();
        return successView;
    }

    public void goNextActivity(Class<?> activity) {
        Intent intent = new Intent(mContext, activity);
        startActivity(intent);
    }

    public void goNextActivity(Class<?> activity, Bundle data) {
        Intent intent = new Intent(mContext, activity);
        intent.putExtras(data);
        startActivity(intent);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }
}
