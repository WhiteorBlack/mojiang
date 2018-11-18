package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.idcby.jiajubang.Bean.ServerComment;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 服务评论
 * Created on 2018/5/24.
 */

public class ServerCommentAdapter extends BaseAdapter {
    private Context mContext ;
    private List<ServerComment> mDataList ;

    public ServerCommentAdapter(Context mContext, List<ServerComment> mDataList) {
        this.mContext = mContext;
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
        SdHolder holder ;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_server_comment_list ,viewGroup ,false) ;
            holder = new SdHolder(view) ;
            view.setTag(holder) ;
        }else{
            holder = (SdHolder) view.getTag();
        }

        ServerComment info = mDataList.get(i) ;
        if(info != null){
            String imgUrl = info.getCreateUserHeadIcon() ;
            String name = info.getCreateUserName() ;
            String time = info.getCreateDate() ;
            String content = info.getEvaluateContent() ;
            final String userId = info.getCreateUserId() ;

            holder.mNameTv.setText(name);
            holder.mTimeTv.setText(time);
            holder.mContentTv.setText(content);
            GlideUtils.loaderUser(imgUrl ,holder.mIv);

            holder.mIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SkipUtils.toOtherUserInfoActivity(mContext ,userId) ;
                }
            });
        }

        return view;
    }

    private static class SdHolder{
        private ImageView mIv ;
        private TextView mNameTv ;
        private TextView mTimeTv ;
        private TextView mContentTv ;
        private LinearLayout mReplyLay ;

        public SdHolder(View itemView) {
            this.mIv = itemView.findViewById(R.id.adapter_server_comment_user_iv) ;
            this.mNameTv = itemView.findViewById(R.id.adapter_server_comment_user_name_tv) ;
            this.mTimeTv = itemView.findViewById(R.id.adapter_server_comment_time_tv) ;
            this.mContentTv = itemView.findViewById(R.id.adapter_server_comment_content_tv) ;
            this.mReplyLay = itemView.findViewById(R.id.adapter_server_comment_reply_lay) ;
        }
    }
}
