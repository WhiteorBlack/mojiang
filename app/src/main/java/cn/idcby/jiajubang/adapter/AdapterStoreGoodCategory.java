package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
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

public class AdapterStoreGoodCategory extends RecyclerView.Adapter<AdapterStoreGoodCategory.SgcHolder> {
    private Context context ;
    private List<StoreGoodCategory> mDataList ;
    private String mStoreId ;

    public AdapterStoreGoodCategory(Context context, List<StoreGoodCategory> mDataList,String mStoreId) {
        this.context = context;
        this.mDataList = mDataList;
        this.mStoreId = mStoreId;
    }

    @Override
    public SgcHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SgcHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_store_good_category_item ,parent ,false));
    }

    @Override
    public void onBindViewHolder(SgcHolder holder, int position) {
        StoreGoodCategory category = mDataList.get(position) ;

        if(category != null){
            final String categoryId = category.getCategoryID() ;
            final String categoryName = category.getCategoryTitle() ;

            String title = category.getCategoryTitle() ;
            holder.mTitleTv.setText(title) ;

            List<StoreGoodCategory> childCate = category.getChildCategoryList() ;

            AdapterStoreGoodCategoryChild childAdapter = new AdapterStoreGoodCategoryChild(context
                    ,categoryId,categoryName ,childCate,mStoreId) ;

            holder.mChildRv.setLayoutManager(new GridLayoutManager(context ,4)) ;
            holder.mChildRv.setNestedScrollingEnabled(false);
            holder.mChildRv.setAdapter(childAdapter) ;

            holder.mTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toNomalGoodList(context ,categoryId ,categoryName ,mStoreId) ;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class SgcHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTv ;
        private RecyclerView mChildRv ;

        public SgcHolder(View itemView) {
            super(itemView);

            mTitleTv = itemView.findViewById(R.id.adapter_store_good_category_title_tv) ;
            mChildRv = itemView.findViewById(R.id.adapter_store_good_category_child_rv) ;
        }
    }
}
