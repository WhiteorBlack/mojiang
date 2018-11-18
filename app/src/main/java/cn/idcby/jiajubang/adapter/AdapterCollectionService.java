package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ServerDetailActivity;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.view.FlowLayout;
import cn.idcby.jiajubang.view.MyCornerTextView;

/**
 *  我的收藏--服务（）
 *  2018-04-08
 */

public class AdapterCollectionService extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context ;
    private List<ServiceList> mDataList;
    private LayoutInflater inflater ;
    private int flTvPadding ;
    private int flTvCorner ;

    public AdapterCollectionService(Context context , List<ServiceList> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
        this.inflater = LayoutInflater.from(context) ;
        this.flTvPadding = ResourceUtils.dip2px(context , 2) ;
        this.flTvCorner = ResourceUtils.dip2px(context , 8) ;
    }

    @Override
    public int getItemCount() {
        return  null == mDataList ? 0 : mDataList.size() ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyColHolder(inflater.inflate(R.layout.adapter_collection_service_list
                , parent , false)) ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyColHolder){
            MyColHolder mHolder = (MyColHolder) holder;
            ServiceList info = mDataList.get(position) ;
            if(info != null){
                final String serverUserId = info.getCreateUserId() ;

                String title = info.getCreateUserName() ;
                String imgUrl = info.getHeadIcon() ;
                String singleAmount = info.getSingleAmount() ;
                String praiseRate = info.getPraiseRate() ;
                String desc = info.getServiceDescription() ;
                String location = info.getPosition() ;

                mHolder.mTitleTv.setText(title);
                mHolder.mJiedanTv.setText("月接单：" + singleAmount);
                mHolder.mHaopingTv.setText("好评：" + praiseRate);
                mHolder.mContentTv.setText(desc);
                mHolder.mLocationTv.setText(location);
                GlideUtils.loaderUser(imgUrl , mHolder.mIv) ;

                mHolder.mTypeLay.removeAllViews() ;
                List<WordType> typeList = info.getTypeList() ;
                if(typeList != null && typeList.size() > 0){
                    int size = typeList.size() ;
                    if(size > 5){
                        size = 5 ;
                    }

                    for(int x = 0 ; x < size ; x ++){
                        WordType serType = typeList.get(x) ;
                        if(serType != null){
                            MyCornerTextView tv = new MyCornerTextView(context) ;
                            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                    , ViewGroup.LayoutParams.WRAP_CONTENT));
                            tv.setPadding(flTvPadding*3 ,flTvPadding ,flTvPadding*3,flTvPadding);
                            tv.setfilColor(Color.parseColor(serType.getColorValue())).setCornerSize(flTvCorner);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,8) ;
                            tv.setTextColor(context.getResources().getColor(R.color.color_white)) ;
                            tv.setText(serType.getItemName());

                            mHolder.mTypeLay.addView(tv) ;
                        }
                    }
                }

                mHolder.mMainLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ServerDetailActivity.launch(context ,serverUserId,false) ;
                    }
                });
            }
        }
    }

    private static class MyColHolder extends RecyclerView.ViewHolder{
        private RelativeLayout mMainLay ;
        private ImageView mIv;
        private TextView mTitleTv;
        private TextView mJiedanTv;
        private TextView mHaopingTv;
        private FlowLayout mTypeLay;
        private RecyclerView mImageRv ;
        private TextView mContentTv ;
        private TextView mLocationTv;

        public MyColHolder(View itemView) {
            super(itemView);

            mMainLay =  itemView.findViewById(R.id.adapter_collection_service_main_lay);
            mIv = itemView.findViewById(R.id.adapter_collection_service_com_iv);
            mTitleTv = itemView.findViewById(R.id.adapter_collection_service_title_tv);
            mJiedanTv = itemView.findViewById(R.id.adapter_collection_service_jiedan_tv);
            mHaopingTv = itemView.findViewById(R.id.adapter_collection_service_haoping_tv);
            mTypeLay = itemView.findViewById(R.id.adapter_collection_service_type_lay);
            mImageRv = itemView.findViewById(R.id.adapter_collection_service_image_rv);
            mContentTv = itemView.findViewById(R.id.adapter_collection_service_content_tv);
            mLocationTv = itemView.findViewById(R.id.adapter_collection_service_location_tv);
        }
    }

}
