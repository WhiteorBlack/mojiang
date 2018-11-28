package cn.idcby.jiajubang.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

/**
 * Desc : 用于简化Adapter 使用Databinding 后的处理；
 */
public abstract class EasyBindQuickAdapter<T> extends BaseQuickAdapter<T, BindingViewHolder> {
    private int pageCount = -1;

    public EasyBindQuickAdapter(int layoutResId, List<T> data) {
        super(layoutResId, data);
    }

    public EasyBindQuickAdapter(List<T> data) {
        super(data);
    }

    public EasyBindQuickAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        View view = mLayoutInflater.inflate(layoutResId, parent, false);
        //自动适配view
        return view;
    }

    /**
     * 使用 DataBinding.inflate itemView
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    protected BindingViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(mLayoutInflater, mLayoutResId, parent, false);
        return new BindingViewHolder<>(binding);
    }

    /**
     * 对ItemView 进行viewModel 的绑定
     *
     * @param holder
     * @param item
     */
    @Override
    protected void convert(BindingViewHolder holder, T item) {
        easyConvert(holder, item);
        ViewDataBinding binding = holder.getBinding();
        binding.executePendingBindings();
    }

    protected abstract void easyConvert(BindingViewHolder holder, T item);


    /**
     * 添加分页数据
     *
     * @param list   数据源
     * @param pageNo 页码
     */
    public void setPagingData(List<T> list, int pageNo) {
        if (list == null) return;
        if (pageNo == 1) {
            setNewData(list);
        } else {
            addData(list);
        }
        if (list.size() == 0 || list == null||list.size()<10 ) {
            loadMoreEnd();
        } else {
            loadMoreComplete();
        }
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    //    @Override
//    public void loadMoreEnd() {
//        super.loadMoreEnd();
//
////        getViewByPosition(getLoadMoreViewPosition(), R.id.tv_loading).setVisibility(View.GONE);
////        getViewByPosition(getLoadMoreViewPosition(),R.id.tv_load_end).setVisibility(View.VISIBLE);
//    }


    /**
     * 加载更多失败
     */
    @Override
    public void loadMoreFail() {

        super.loadMoreFail();
    }
}
