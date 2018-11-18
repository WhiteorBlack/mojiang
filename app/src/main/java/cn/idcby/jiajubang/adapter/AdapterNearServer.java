package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 附近--附近服务（安装大师）adapter
 */

public class AdapterNearServer extends RecyclerView.Adapter<AdapterNearServer.HsViewHolder> {
    private Context mContext;
    private List<ServiceList> mData = new ArrayList<>();
    private RvItemViewClickListener mClickListener ;

    public AdapterNearServer(Context mContext, List<ServiceList> data , RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mData = data;
        this.mClickListener = mClickListener;
    }

    @Override
    public AdapterNearServer.HsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_near_server_list, parent ,false);
        return new HsViewHolder(view ,mClickListener);
    }

    @Override
    public void onBindViewHolder(HsViewHolder holder, int position) {

        ServiceList info = mData.get(position) ;
        if(info != null){
            String imgUrl = info.getHeadIcon() ;
            String name = info.getCreateUserName() ;
            String count = info.getSingleAmount() ;

            holder.mNameTv.setText(StringUtils.convertNull(name));
            holder.mCountTv.setText("接单：" + StringUtils.convertNull(count));
            GlideUtils.loaderUser(imgUrl, holder.mHeadIv);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class HsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mHeadIv;
        private TextView mNameTv;
        private TextView mCountTv ;

        public HsViewHolder(View itemView ,final RvItemViewClickListener clickListener) {
            super(itemView);

            mHeadIv = itemView.findViewById(R.id.adapter_near_server_list_iv) ;
            mNameTv = itemView.findViewById(R.id.adapter_near_server_list_name_tv) ;
            mCountTv = itemView.findViewById(R.id.adapter_near_server_list_count_tv) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClickListener(0 , getAdapterPosition()) ;
                    }
                }
            });
        }
    }
}
