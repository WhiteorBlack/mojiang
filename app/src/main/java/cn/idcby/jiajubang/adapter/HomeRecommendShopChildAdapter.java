package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.HomeStore;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/5/5.
 */

public class HomeRecommendShopChildAdapter extends RecyclerView.Adapter<HomeRecommendShopChildAdapter.HscHolder> {
    private Context context ;
    private List<HomeStore> mDataList ;
    private int mImgWidHei ;

    public HomeRecommendShopChildAdapter(Context context, List<HomeStore> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
        this.mImgWidHei = (int) ((ResourceUtils.getScreenWidth(context)
                        - ResourceUtils.dip2px(context ,15)
                        - ResourceUtils.dip2px(context ,10) * 2
                        - ResourceUtils.dip2px(context ,3)* 2) / 4F);
    }

    @Override
    public HscHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HscHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_home_recomment_shop_child_item ,parent ,false) ,mImgWidHei);
    }

    @Override
    public void onBindViewHolder(HscHolder holder, int position) {
        HomeStore store = mDataList.get(position) ;

        if(store != null){
            final String storeId = store.getShopID() ;
            String storeName = store.getShopName() ;
            String storeCount = store.getProductCount() ;

            holder.mTitleTv.setText(storeName);
            holder.mCountTv.setText("共" + storeCount + "件商品");

            String imageOne = "" ;
            String imageTwo = "" ;
            List<ImageThumb> imgList = store.getProductAlbumsList() ;
            if(imgList != null && imgList.size() > 0){
                imageOne = imgList.get(0).getThumbImgUrl() ;
                if(imgList.size() > 1){
                    imageTwo = imgList.get(1).getThumbImgUrl() ;
                }
            }

            GlideUtils.loader(imageOne ,holder.mImageOneIv);
            GlideUtils.loader(imageTwo ,holder.mImageTwoIv);

            holder.mMainLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toStoreIndexActivity(context ,storeId) ;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class HscHolder extends RecyclerView.ViewHolder{
        private View mMainLay ;
        private TextView mTitleTv ;
        private TextView mCountTv ;
        private ImageView mImageOneIv;
        private ImageView mImageTwoIv ;

        public HscHolder(View itemView,int imgWidHei) {
            super(itemView);

            mMainLay = itemView.findViewById(R.id.adapter_home_recommend_child_main_lay) ;
            mTitleTv = itemView.findViewById(R.id.adapter_home_recommend_child_title_tv) ;
            mCountTv = itemView.findViewById(R.id.adapter_home_recommend_child_count_tv) ;
            mImageOneIv = itemView.findViewById(R.id.adapter_home_recommend_child_top_left_iv) ;
            mImageTwoIv = itemView.findViewById(R.id.adapter_home_recommend_child_top_right_one_lay) ;

            mImageOneIv.getLayoutParams().width = imgWidHei ;
            mImageOneIv.getLayoutParams().height = (int) (imgWidHei / 1F);
        }
    }
}