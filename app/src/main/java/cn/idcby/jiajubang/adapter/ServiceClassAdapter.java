package cn.idcby.jiajubang.adapter;

import android.widget.ImageView;

import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.BaseCategory;
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;

public class ServiceClassAdapter extends EasyBindQuickAdapter<ServerCategory> {
    public ServiceClassAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void easyConvert(BindingViewHolder holder, ServerCategory item) {
        holder.getBinding().setVariable(BR.item, item);
        holder.getBinding().executePendingBindings();
        ImageView imageView = holder.getView(R.id.iv_class);
        if (imageView != null) {
            GlideUtils.loader(item.IconUrl, imageView);
        }
    }
}
