package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.view.FlowLayout;
import cn.idcby.jiajubang.view.MyCornerTextView;

/**
 * Created on 2018/4/10.
 */

public class AdapterServerRecommentList extends BaseAdapter {
    private Context context ;
    private List<ServiceList> mDataList ;
    private RvItemViewClickListener mClickListener ;
    private int flTvPadding ;

    public AdapterServerRecommentList(Context context, List<ServiceList> mDataList
            ,RvItemViewClickListener mClickListener ) {
        this.context = context;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
        this.flTvPadding = ResourceUtils.dip2px(context , 2) ;
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
        final int realPosition = i ;

        SerHolder holder ;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_server_recomment_list, viewGroup ,false) ;
            holder = new SerHolder(view) ;
            view.setTag(holder);
        }else{
            holder = (SerHolder) view.getTag();
        }

        ServiceList info = mDataList.get(i) ;
        if(info != null){
            String imgUrl = info.getHeadIcon() ;
            String title = info.getCreateUserName() ;
            String location = info.getPosition() ;
            String count = info.getSingleAmount() ;

            GlideUtils.loaderRound(context ,imgUrl ,holder.mIconIv);
            holder.mTitleTv.setText(title);
            holder.mLocationTv.setText(location);
            holder.mCountTv.setText("已接单：" + count);

            holder.mTypeLay.removeAllViews() ;
            holder.mPromiseLay.removeAllViews() ;

            List<WordType> mTypeList = info.getTypeList() ;
            if(mTypeList != null && mTypeList.size() > 0){
                int typeSize = mTypeList.size() ;
                if(typeSize > 3){
                    typeSize = 3 ;
                }

                for(int x = 0 ; x < typeSize ; x ++){
                    WordType wordType = mTypeList.get(x) ;

                    if(wordType != null){
                        MyCornerTextView tv = new MyCornerTextView(context) ;
                        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                , ViewGroup.LayoutParams.WRAP_CONTENT));
                        tv.setPadding(flTvPadding * 2 ,flTvPadding / 2 ,flTvPadding * 2,flTvPadding);
                        tv.setfilColor(Color.parseColor(wordType.getColorValue())).setCornerSize(flTvPadding);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,10) ;
                        tv.setTextColor(context.getResources().getColor(R.color.color_white)) ;
                        tv.setText(wordType.getItemName());

                        holder.mTypeLay.addView(tv) ;
                    }
                }
            }

            List<WordType> mPromiseList = info.getPromiseList() ;
            if(mPromiseList != null && mPromiseList.size() > 0){
                int typeSize = mPromiseList.size() ;
                if(typeSize > 3){
                    typeSize = 3 ;
                }

                for(int x = 0 ; x < typeSize ; x ++){
                    WordType wordType = mPromiseList.get(x) ;
                    if(wordType != null){
                        TextView tv = new TextView(context) ;
                        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                , ViewGroup.LayoutParams.WRAP_CONTENT));
                        tv.setPadding(flTvPadding ,flTvPadding/2,flTvPadding,flTvPadding/2);
                        tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_server_promise_tv)) ;
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP , 10);
                        tv.setTextColor(context.getResources().getColor(R.color.color_grey_88));
                        tv.setText(wordType.getItemName()) ;

                        holder.mPromiseLay.addView(tv) ;
                    }
                }
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
                }
            });
        }

        return view;
    }


    static class SerHolder{
        private ImageView mIconIv ;
        private TextView mTitleTv ;
        private TextView mLocationTv ;
        private TextView mCountTv ;
        private FlowLayout mTypeLay ;
        private FlowLayout mPromiseLay ;

        public SerHolder(View view) {
            mIconIv = view.findViewById(R.id.adapter_server_list_iv) ;
            mTitleTv = view.findViewById(R.id.adapter_server_list_title_tv) ;
            mLocationTv = view.findViewById(R.id.adapter_server_list_location_tv) ;
            mCountTv = view.findViewById(R.id.adapter_server_list_count_tv) ;
            mTypeLay = view.findViewById(R.id.adapter_server_list_type_lay) ;
            mPromiseLay = view.findViewById(R.id.adapter_server_list_promise_lay) ;
        }
    }

}
