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
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created  on 2018-04-26.
 */

public class NearStoreListAdapter extends RecyclerView.Adapter<NearStoreListAdapter.ViewHolder>{
    private Context mContext;
    private List<HomeStore> listData;
    private int mImageWidHei ;

    public NearStoreListAdapter(Context mContext ,List<HomeStore> listData) {
        this.mContext = mContext;
        this.listData = listData;
        this.mImageWidHei = (int) ((ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext ,40)) / 3F);
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_near_store_list, viewGroup, false);
        return new ViewHolder(view,mImageWidHei);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final HomeStore storeBean = listData.get(position) ;
        if(storeBean != null){
            String name = storeBean.getShopName() ;
            String location = storeBean.getShopPostion() ;
            int saleCount = storeBean.getSaleCount() ;
            int goodCount = StringUtils.convertString2Count(storeBean.getProductCount()) ;
            String type = storeBean.getType() ;

            viewHolder.mStorenameTV.setText(name);
            viewHolder.mStoreLocationTV.setText(location);
            viewHolder.mStoreSaleCountTV.setText("销量" + saleCount);
            viewHolder.mStoreGoodCountTV.setText("共" + goodCount + "件商品");
            viewHolder.mStoreTypeTV.setText(type);
            viewHolder.mStoreTypeTV.setVisibility("".equals(type) ? View.GONE : View.VISIBLE);

            List<ImageThumb> imageThumbs = storeBean.getProductAlbumsList() ;
            int size = imageThumbs.size() ;

            viewHolder.mStoreOneIV.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
            viewHolder.mStoreTwoIV.setVisibility(size > 1 ? View.VISIBLE : View.GONE);
            viewHolder.mStoreThreeIV.setVisibility(size > 2 ? View.VISIBLE : View.GONE);

            if(size > 0){
                GlideUtils.loader(imageThumbs.get(0).getThumbImgUrl() ,viewHolder.mStoreOneIV);
            }
            if(size > 1){
                GlideUtils.loader(imageThumbs.get(1).getThumbImgUrl() ,viewHolder.mStoreTwoIV);
            }
            if(size > 2){
                GlideUtils.loader(imageThumbs.get(2).getThumbImgUrl() ,viewHolder.mStoreThreeIV);
            }

            viewHolder.mParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toStoreIndexActivity(mContext ,storeBean.getShopID()) ;
                }
            });
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return null == listData ? 0 : listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mParentView ;
        private ImageView mStoreOneIV;
        private ImageView mStoreTwoIV;
        private ImageView mStoreThreeIV;
        private TextView mStorenameTV;
        private TextView mStoreLocationTV;
        private TextView mStoreSaleCountTV;
        private TextView mStoreTypeTV;
        private TextView mStoreGoodCountTV;

        public ViewHolder(View view,int widHei) {
            super(view);

            mParentView = view ;
            mStoreOneIV = view.findViewById(R.id.adapter_near_store_one_iv);
            mStoreTwoIV = view.findViewById(R.id.adapter_near_store_two_iv);
            mStoreThreeIV = view.findViewById(R.id.adapter_near_store_three_iv);
            mStorenameTV = view.findViewById(R.id.adapter_near_store_name_tv);
            mStoreLocationTV = view.findViewById(R.id.adapter_near_store_location_tv);
            mStoreSaleCountTV = view.findViewById(R.id.adapter_near_store_sale_count_tv);
            mStoreTypeTV = view.findViewById(R.id.adapter_near_store_type_tv);
            mStoreGoodCountTV = view.findViewById(R.id.adapter_near_store_good_count_tv);

            mStoreOneIV.getLayoutParams().width = widHei ;
            mStoreOneIV.getLayoutParams().height = widHei ;
            mStoreTwoIV.getLayoutParams().width = widHei ;
            mStoreTwoIV.getLayoutParams().height = widHei ;
            mStoreThreeIV.getLayoutParams().width = widHei ;
            mStoreThreeIV.getLayoutParams().height = widHei ;
        }
    }
}
