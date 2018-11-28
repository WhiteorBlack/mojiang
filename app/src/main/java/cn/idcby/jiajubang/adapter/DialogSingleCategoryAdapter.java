package cn.idcby.jiajubang.adapter;

import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.UnusedCategory;

public class DialogSingleCategoryAdapter extends EasyBindQuickAdapter<UnusedCategory> {
    public DialogSingleCategoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void easyConvert(BindingViewHolder holder, UnusedCategory item) {
        holder.getBinding().setVariable(BR.item,item);
        holder.getBinding().executePendingBindings();
    }
}
