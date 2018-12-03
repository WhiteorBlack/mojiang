package cn.idcby.jiajubang.adapter;

import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.view.FlowLayout;

public class ServiceAdapter extends EasyBindQuickAdapter<ServiceList> {
    public ServiceAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void easyConvert(BindingViewHolder holder, ServiceList item) {
        holder.getBinding().setVariable(BR.item, item);
        holder.getBinding().executePendingBindings();
        ImageView imageView = holder.getView(R.id.imageView2);
        GlideUtils.loader(item.HeadIcon, imageView);
        FlowLayout flowLayout = holder.getView(R.id.linearLayout2);
        flowLayout.setSingleLine(true);
        List<WordType> mPromiseList = item.getPromiseList();
        if (mPromiseList != null && mPromiseList.size() > 0) {
            int typeSize = mPromiseList.size();
            flowLayout.removeAllViews();
            for (int x = 0; x < typeSize; x++) {
                WordType wordType = mPromiseList.get(x);
                if (wordType != null) {
                    TextView tv = new TextView(mContext);
                    ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                            , ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv.setLayoutParams(params);
                    tv.setPadding(8, 3, 8, 3);

                    tv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_server_list_promise_tv));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                    tv.setTextColor(mContext.getResources().getColor(R.color.yancy_green500));
                    tv.setText(wordType.getItemName());

                    flowLayout.addView(tv);
                }
            }
        }
    }
}
