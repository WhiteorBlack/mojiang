package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.UserVic;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;

/**
 * Created on 2018/3/24.
 */

public class AdapterVicUserList extends BaseAdapter {
    private Context context ;
    private List<UserVic> mData ;
    private RecyclerViewClickListener mClickListener ;

    public AdapterVicUserList(Context context, List<UserVic> mData ,RecyclerViewClickListener mClickListener) {
        this.context = context;
        this.mData = mData;
        this.mClickListener = mClickListener;
    }

    @Override
    public int getCount() {
        return null == mData ? 0 : mData.size() ;
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        VicHolder holder ;
        final int realPosi = i ;

        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_vic_user_list , viewGroup ,false) ;

            holder = new VicHolder(view) ;

            view.setTag(holder);
        }else{
            holder = (VicHolder) view.getTag();
        }

        UserVic userVic = mData.get(i) ;
        if(userVic != null){
            String imgUrl = userVic.getHeadIcon() ;
            String name = userVic.getRealName() ;
            String location = userVic.getPosition() ;
            String fans = userVic.getFollowerNumber() + "粉丝" ;

            holder.mNameTv.setText(name) ;
            holder.mFansTv.setText(fans);
            holder.mLocationTv.setText(location);

            boolean isFocused = 1 == userVic.getIsFollow() ;
            holder.mFocusTv.setText(isFocused ? "已关注" : "+关注");
            holder.mFocusTv.setBackgroundDrawable(context.getResources().getDrawable(isFocused
                    ? R.drawable.round_focus_bg_focused
                    : R.drawable.round_focus_bg_nomal) );
            holder.mFocusTv.setTextColor(context.getResources().getColor(isFocused
                    ? R.color.color_grey_88
                    : R.color.color_theme));

            GlideUtils.loaderUser(imgUrl ,holder.mHeadIv) ;

            holder.mFocusTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosi);
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(1 ,realPosi);
                    }
                }
            });
        }

        return view;
    }

    static class VicHolder {
        private ImageView mHeadIv ;
        private TextView mFocusTv ;
        private TextView mNameTv ;
        private TextView mLocationTv;
        private TextView mFansTv ;

        public VicHolder(View view) {
            mHeadIv = view.findViewById(R.id.adapter_vic_user_head_iv) ;
            mFocusTv = view.findViewById(R.id.adapter_vic_user_focus_tv) ;
            mNameTv = view.findViewById(R.id.adapter_vic_user_name_tv) ;
            mLocationTv = view.findViewById(R.id.adapter_vic_user_location_tv) ;
            mFansTv = view.findViewById(R.id.adapter_vic_user_fans_tv) ;
        }
    }

}
