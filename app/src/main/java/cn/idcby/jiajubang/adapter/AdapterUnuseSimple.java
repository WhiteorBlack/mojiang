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
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * 闲置详细--相似闲置
 * Created on 2018/9/13.
 */

public class AdapterUnuseSimple extends RecyclerView.Adapter<AdapterUnuseSimple.UsHolder> {
    private Context mContext ;
    private List<UnuseGoodList> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private int mWidHei ;

    public AdapterUnuseSimple(Context mContext, List<UnuseGoodList> mDataList, RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
        this.mWidHei = (int) (ResourceUtils.getScreenWidth(mContext) / 3F);
    }

    @Override
    public UsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UsHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_unuse_simple_good ,parent ,false) ,mWidHei ,mClickListener);
    }

    @Override
    public void onBindViewHolder(UsHolder holder, int position) {
        UnuseGoodList info = mDataList.get(position) ;

        if(info != null){
            String imgUrl = info.getImgUrl() ;
            String name = info.getTitle() ;
            String price = info.getShowPrice() ;

            holder.mNameTv.setText(name);
            holder.mPriceTv.setText(price) ;
            GlideUtils.loaderGoodImage(imgUrl,holder.mPicIv) ;
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size();
    }

    public static class UsHolder extends RecyclerView.ViewHolder{
        private ImageView mPicIv ;
        private TextView mNameTv ;
        private TextView mPriceTv ;

        public UsHolder(View itemView, int widHei , final RvItemViewClickListener clickListener) {
            super(itemView);

            mPicIv = itemView.findViewById(R.id.adapter_unuse_simple_iv) ;
            mNameTv = itemView.findViewById(R.id.adapter_unuse_simple_name_tv) ;
            mPriceTv = itemView.findViewById(R.id.adapter_unuse_simple_price_tv) ;

            mPicIv.getLayoutParams().width = widHei ;
            mPicIv.getLayoutParams().height = widHei ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClickListener(0 ,getAdapterPosition());
                    }
                }
            });
        }
    }
}
