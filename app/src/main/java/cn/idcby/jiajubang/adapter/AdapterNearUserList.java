package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.UserNear;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/4/11.
 */

public class AdapterNearUserList extends BaseAdapter {
    private Context context ;
    private List<UserNear> mDataList ;

    public AdapterNearUserList(Context context, List<UserNear> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
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
        NearUserHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_near_user_list, viewGroup ,false) ;
            holder = new NearUserHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (NearUserHolder) view.getTag();
        }

        UserNear info = mDataList.get(i) ;
        if(info != null){
            final String userId = info.getCreateUserId() ;
            String imgUrl = info.getCreateUserHeadIcon() ;
            String title = info.getNickName() ;
            String desc = info.getPersonalitySignature() ;
            String age = info.getAge() ;
            boolean isMan = "1".equals(info.getGender()) ;

            holder.mAgeTv.setBackgroundDrawable(context.getResources().getDrawable(isMan
                    ? R.drawable.bg_near_user_age_man
                    : R.drawable.bg_near_user_age_women)) ;
            holder.mAgeTv.setText(age);

            holder.mTitleTv.setText(title);
            holder.mContentTv.setText(desc);
            GlideUtils.loaderUser(imgUrl ,holder.mIconIv);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toOtherUserInfoActivity(context ,userId);
                }
            });
        }

        return view;
    }


    static class NearUserHolder {
        private ImageView mIconIv ;
        private TextView mAgeTv;
        private TextView mTitleTv ;
        private TextView mContentTv;

        public NearUserHolder(View view) {
            mIconIv = view.findViewById(R.id.adapter_near_user_list_iv) ;
            mAgeTv = view.findViewById(R.id.adapter_near_user_list_age_tv) ;
            mTitleTv = view.findViewById(R.id.adapter_near_user_list_title_tv) ;
            mContentTv = view.findViewById(R.id.adapter_near_user_list_content_tv) ;
        }
    }

}
