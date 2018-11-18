package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.NeedsCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created on 2018/3/29.
 */

public class AdapterNeedsCategory extends BaseAdapter {
    private Context mContext ;
    private List<NeedsCategory> mDataList ;

    public AdapterNeedsCategory(Context mContext, List<NeedsCategory> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        NdcHolder holder ;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_needs_category , viewGroup , false) ;
            holder = new NdcHolder(view) ;
            view.setTag(holder) ;
        }else{
            holder = (NdcHolder) view.getTag();
        }

        NeedsCategory info = mDataList.get(i) ;
        if(info != null){
            String name = info.getCategoryTitle() ;
            String imgUrl = info.getImgUrl() ;

            holder.mNameTv.setText(name);
            GlideUtils.loader(MyApplication.getInstance() ,imgUrl , holder.mIconIv) ;
        }

        return view;
    }

    private static class NdcHolder{
        private ImageView mIconIv ;
        private TextView mNameTv ;

        public NdcHolder(View view){
            this.mIconIv = view.findViewById(R.id.adapter_needs_category_iv) ;
            this.mNameTv = view.findViewById(R.id.adapter_needs_category_name_tv) ;
        }
    }


}
