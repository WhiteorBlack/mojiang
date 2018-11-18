package cn.idcby.commonlibrary.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.idcby.commonlibrary.R;


/**
 * Created by mrrlb on 2016/10/31.
 */
public abstract class LoadPage extends FrameLayout {




    private View loadingView;//加载中的view
    private View errorView;//加载失败view
    private View emptyView;//加载为空view
    private View successView;//加载成功view
    private View noNetView;//没有网络的View

    private OnRequestDatasAgain onRequestDatasAgain;

    public interface OnRequestDatasAgain {
        public abstract void onRequestDatasAgain();
    }


    public void setOnRequestDatasAgain(OnRequestDatasAgain onRequestDatasAgain) {
        this.onRequestDatasAgain = onRequestDatasAgain;
    }

    public LoadPage(Context context) {
        super(context);
        init();
    }

    public LoadPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 将不同的布局添加到帧布局中
     */
    private void init() {

        if (loadingView == null) {
            loadingView = createLoadingView();
            this.addView(loadingView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
        }


        if (errorView == null) {
            errorView = createErrorView();
            this.addView(errorView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (emptyView == null) {
            emptyView = createEmptyView();
            this.addView(emptyView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (noNetView == null) {
            noNetView = createNoNetView();
            this.addView(noNetView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (successView == null) {
            successView = createSuccessView();
            this.addView(successView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
        }

        initDefaultStatus();

    }

    private void initDefaultStatus() {
        showLoadingPage();
    }


    private View createNoNetView() {
        View view = View.inflate(getContext(), R.layout.page_no_net, null);
        view.findViewById(R.id.tv_again).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新请求网络数据
                if (onRequestDatasAgain != null)
                    onRequestDatasAgain.onRequestDatasAgain();
            }
        });
        return view;
    }

    private View createEmptyView() {
        return View.inflate(getContext(), R.layout.page_empty, null);
    }

    private View createErrorView() {

        View view = View.inflate(getContext(), R.layout.page_error, null);
        view.findViewById(R.id.tv_again).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新请求网络数据
                if (onRequestDatasAgain != null)
                    onRequestDatasAgain.onRequestDatasAgain();
            }
        });
        return view;
    }

    private View createLoadingView() {

        return View.inflate(getContext(), R.layout.page_loading, null);
    }


    public abstract View createSuccessView();


    public void showLoadingPage() {
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        noNetView.setVisibility(View.GONE);
        successView.setVisibility(View.GONE);
    }

    public void showErrorPage() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        noNetView.setVisibility(View.GONE);
        successView.setVisibility(View.GONE);
    }

    public void showEmptyPage() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        noNetView.setVisibility(View.GONE);
        successView.setVisibility(View.GONE);
    }

    public boolean showNetErrorPage() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        noNetView.setVisibility(View.VISIBLE);
        successView.setVisibility(View.GONE);
        return true ;
    }


    public void showSuccessPage() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        noNetView.setVisibility(View.GONE);
        successView.setVisibility(View.VISIBLE);
    }


}
