package cn.idcby.jiajubang.view.refresh;

public abstract class MaterialRefreshListener {
    public void onfinish(){};
    public abstract void onRefresh(MaterialRefreshLayout materialRefreshLayout);
    public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout){};
    public void onRefreshStartOrCancel(boolean isStart){} ;
}
