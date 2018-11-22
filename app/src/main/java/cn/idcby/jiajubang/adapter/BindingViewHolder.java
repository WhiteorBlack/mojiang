package cn.idcby.jiajubang.adapter;

import android.databinding.ViewDataBinding;

import com.chad.library.adapter.base.BaseViewHolder;

/**
 * databinding viewHolder
 *
 * @param <T>
 */
public class BindingViewHolder<T extends ViewDataBinding> extends BaseViewHolder {

    private T mBinding;

    /**
     * databinding viewHolder
     *
     * @param binding
     */
    public BindingViewHolder(T binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public BindingViewHolder(T binding, boolean isAuto) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public T getBinding() {
        return mBinding;
    }
}
