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

import cn.idcby.jiajubang.Bean.UserNear;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 附近--附近的人
 */

public class AdapterNearUser extends RecyclerView.Adapter<AdapterNearUser.HsViewHolder> {
    private Context mContext;
    private List<UserNear> mData = new ArrayList<>();
    private RvItemViewClickListener mClickListener ;

    public AdapterNearUser(Context mContext, List<UserNear> data , RvItemViewClickListener mClickListener) {
        this.mContext = mContext;
        this.mData = data;
        this.mClickListener = mClickListener;
    }


    @Override
    public AdapterNearUser.HsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_near_user, parent ,false);
        return new HsViewHolder(view ,mClickListener);
    }

    @Override
    public void onBindViewHolder(HsViewHolder holder, int position) {

        UserNear info = mData.get(position) ;
        if(info != null){
            final String userId = info.getCreateUserId() ;
            String imgUrl = info.getCreateUserHeadIcon() ;
            String age = info.getAge() ;
            String sex = info.getGender() ;

            if("1".equals(sex)){//男
                holder.mAgeTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_near_user_age_man)) ;
            }else if("2".equals(sex)){//女
                holder.mAgeTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_near_user_age_women)) ;
            }else{
                holder.mAgeTv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_near_user_age_other)) ;
            }

            holder.mAgeTv.setText(age);
            GlideUtils.loaderUser(imgUrl, holder.mHeadIv);

            holder.mHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toOtherUserInfoActivity(mContext ,userId);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class HsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mHeadIv;
        private TextView mAgeTv;

        public HsViewHolder(View itemView ,final RvItemViewClickListener clickListener) {
            super(itemView);

            mHeadIv = itemView.findViewById(R.id.adapter_near_user_iv) ;
            mAgeTv = itemView.findViewById(R.id.adapter_near_user_age_tv) ;

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
