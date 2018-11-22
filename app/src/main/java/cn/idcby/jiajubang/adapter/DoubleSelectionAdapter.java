package cn.idcby.jiajubang.adapter;

import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.SiftWorkPost;

public class DoubleSelectionAdapter extends EasyBindQuickAdapter<SiftWorkPost> {
    public DoubleSelectionAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void easyConvert(BindingViewHolder holder, SiftWorkPost item) {
        holder.getBinding().setVariable(BR.item,item);
        holder.getBinding().executePendingBindings();
    }
}
