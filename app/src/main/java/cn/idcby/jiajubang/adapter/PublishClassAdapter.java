package cn.idcby.jiajubang.adapter;

import android.widget.ImageView;

import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.PublishClass;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;

public class PublishClassAdapter extends EasyBindQuickAdapter<PublishClass> {
    public PublishClassAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void easyConvert(BindingViewHolder holder, PublishClass item) {
        holder.getBinding().setVariable(BR.item, item);
        holder.getBinding().executePendingBindings();
        ImageView imageView = holder.getView(R.id.iv_class);
        imageView.setBackgroundResource(item.getResId());
    }
}
