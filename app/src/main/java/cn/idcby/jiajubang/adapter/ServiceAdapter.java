package cn.idcby.jiajubang.adapter;

import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.ServiceList;

public class ServiceAdapter extends EasyBindQuickAdapter<ServiceList> {
    public ServiceAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void easyConvert(BindingViewHolder holder, ServiceList item) {
        holder.getBinding().setVariable(BR.item, item);
        holder.getBinding().executePendingBindings();
    }
}
