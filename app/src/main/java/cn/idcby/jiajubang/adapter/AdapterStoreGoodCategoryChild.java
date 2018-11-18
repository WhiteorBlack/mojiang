package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.StoreGoodCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/4/27.
 */

public class AdapterStoreGoodCategoryChild extends RecyclerView.Adapter<AdapterStoreGoodCategoryChild.SgccHolder> {
    private Context context ;
    private List<StoreGoodCategory> mDataList ;
    private String parentCateId ;
    private String parentCateName ;
    private String mStoreId ;

    public AdapterStoreGoodCategoryChild(Context context,String parentId ,String parentCateName , List<StoreGoodCategory> mDataList,String mStoreId) {
        this.context = context;
        this.mDataList = mDataList;
        this.parentCateId = parentId;
        this.parentCateName = parentCateName;
        this.mStoreId = mStoreId;
    }

    @Override
    public SgccHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SgccHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_store_good_category_child_item,parent ,false));
    }

    @Override
    public void onBindViewHolder(SgccHolder holder, int position) {
        StoreGoodCategory category = mDataList.get(position) ;

        if(category != null){

            final String title =category.getCategoryTitle() ;
            final String cateId = category.getCategoryID() ;
            holder.mTitleTv.setText(title) ;

            holder.mTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toNomalGoodList(context ,parentCateId ,parentCateName,title ,cateId ,mStoreId) ;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class SgccHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTv ;

        public SgccHolder(View itemView) {
            super(itemView);

            mTitleTv = itemView.findViewById(R.id.adapter_store_good_category_child_title_tv) ;
        }
    }
}
