package cn.idcby.jiajubang.adapter;

import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.Address;
import cn.idcby.jiajubang.Bean.CategoryBean;

public class DoubleCategoryAdapter extends EasyBindQuickAdapter<CategoryBean> {
    public DoubleCategoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void easyConvert(BindingViewHolder holder, CategoryBean item) {
        holder.getBinding().setVariable(BR.item,item);
        holder.getBinding().executePendingBindings();
    }
}
