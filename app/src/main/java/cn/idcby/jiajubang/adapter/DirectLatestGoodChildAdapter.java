package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created on 2018/5/5.
 */

public class DirectLatestGoodChildAdapter extends RecyclerView.Adapter<DirectLatestGoodChildAdapter.HscHolder> {
    private Context context ;
    private List<GoodList> mDataList ;
    private int mImgWidHei ;

    public DirectLatestGoodChildAdapter(Context context, List<GoodList> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
        this.mImgWidHei = (ResourceUtils.getScreenWidth(context) - ResourceUtils.dip2px(context ,2)) / 3;
    }

    @Override
    public HscHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HscHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_direct_good_child_item ,parent ,false) ,mImgWidHei);
    }

    @Override
    public void onBindViewHolder(HscHolder holder, int position) {
        GoodList info = mDataList.get(position) ;

        if(info != null){
            final String goodId = info.getProductID() ;

            GlideUtils.loaderGoodImage(info.getImgUrl() ,holder.mImageOneIv);

            holder.mImageOneIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    SkipUtils.toGoodDetailsActivity(context ,goodId) ;

                    EventBus.getDefault().post(new BusEvent.DirectSellingLatestEvent(true));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    static class HscHolder extends RecyclerView.ViewHolder{
        private ImageView mImageOneIv;

        public HscHolder(View itemView,int imgWidHei) {
            super(itemView);

            mImageOneIv = itemView.findViewById(R.id.adapter_direct_good_child_iv) ;

            mImageOneIv.getLayoutParams().width = imgWidHei ;
            mImageOneIv.getLayoutParams().height = (int) (imgWidHei / 1F);
        }
    }
}