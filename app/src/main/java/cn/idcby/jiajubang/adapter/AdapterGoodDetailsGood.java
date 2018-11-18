package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 商品详细--相关产品
 * Created on 2018/8/15.
 */

public class AdapterGoodDetailsGood extends BaseAdapter {
    private Context mContext ;
    private List<GoodList> mDataList ;
    private int mImgWid ;

    public AdapterGoodDetailsGood(Context mContext, List<GoodList> mDataList, int spanSize) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mImgWid = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext ,12)
                - ResourceUtils.dip2px(mContext ,12) * spanSize
                - ResourceUtils.dip2px(mContext ,6) * (spanSize - 1)) / spanSize ;
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
        GsHolder holder ;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_good_details_sample_good,viewGroup ,false) ;
            holder = new GsHolder(view,mImgWid) ;

            view.setTag(holder);
        }else{
            holder = (GsHolder) view.getTag();
        }

        GoodList info = mDataList.get(i) ;
        if(info != null){
            final String goodId = info.getProductID() ;
            String imgUrl = info.getImgUrl() ;
            String goodName = info.getTitle() ;
            String price = info.getSalePrice() ;
            String count = info.getSaleNumber() ;

            holder.mGoodNameTv.setText(goodName);
            holder.mGoodPriceTv.setText(price);
            holder.mGoodCountTv.setText(count + "人付款");
            GlideUtils.loader(imgUrl ,holder.mIv) ;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toGoodDetailsActivity(mContext ,goodId) ;
                }
            });
        }

        return view;
    }

    private static class GsHolder{
        private ImageView mIv ;
        private TextView mGoodNameTv ;
        private TextView mGoodPriceTv ;
        private TextView mGoodCountTv ;

        public GsHolder(View view,int imgWid) {
            mIv = view.findViewById(R.id.adapter_good_details_good_iv) ;
            mGoodNameTv = view.findViewById(R.id.adapter_good_details_good_name_tv) ;
            mGoodPriceTv = view.findViewById(R.id.adapter_good_details_good_price_tv) ;
            mGoodCountTv = view.findViewById(R.id.adapter_good_details_good_count_tv) ;

            mIv.getLayoutParams().width = imgWid ;
            mIv.getLayoutParams().height = imgWid ;
        }
    }

}
