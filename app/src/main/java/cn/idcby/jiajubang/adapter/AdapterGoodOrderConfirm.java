package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.Bean.GoodOrderConfirmList;
import cn.idcby.jiajubang.Bean.NomalGoodInfo;
import cn.idcby.jiajubang.Bean.OrderDetialBean;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/24.
 */

public class AdapterGoodOrderConfirm extends RecyclerView.Adapter<AdapterGoodOrderConfirm.GocHolder> {
    private Context context;
    private boolean isUnuse;
    private List<GoodOrderConfirmList> mDataList ;
    private RvItemViewClickListener mClickListener ;

    public AdapterGoodOrderConfirm(Context context, boolean isUnuse
            , List<GoodOrderConfirmList> mDataList,RvItemViewClickListener mClickListener) {
        this.context = context;
        this.isUnuse = isUnuse;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }


    @Override
    public GocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GocHolder(LayoutInflater.from(context).inflate(R.layout.adapter_good_confirm_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final GocHolder holder, int position) {
        final int realPosition = position ;

        holder.mStoreLay.setVisibility(isUnuse ? View.GONE : View.VISIBLE);

        GoodOrderConfirmList confirmList = mDataList.get(position) ;
        if (confirmList != null) {
            holder.mStoreNameTv.setText(confirmList.getStoreName());

            List<OrderDetialBean.CartModelListBean> goodList = confirmList.getGoodList() ;

            int count = 0 ;
            float money = 0F ;

            List<NomalGoodInfo> adapterList = new ArrayList<>(goodList.size()) ;
            for(OrderDetialBean.CartModelListBean model : goodList){
                int modelCount = StringUtils.convertString2Count(model.getQuantity()) ;
                count += modelCount;
                money += (StringUtils.convertString2Float(model.getSalePrice()) * modelCount) ;

                adapterList.add(new NomalGoodInfo(model.getImgUrl())) ;
            }

            holder.mMoneyTv.setText("¥" + StringUtils.convertStringNoPoint(StringUtils.convertString2Float(money+"")+""));
            holder.mGoodCountTv.setText("×" + count);

            String delivery = "" ;
            WordType deliveryType = confirmList.getDeliveryInfo() ;
            if(deliveryType != null){
                delivery = deliveryType.getItemName() ;
            }

            holder.mDeliveryTv.setText("".equals(delivery) ? "请选择" : delivery) ;

            AdapterGoodOrderImage imgAdapter = new AdapterGoodOrderImage(context,isUnuse, adapterList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.mGoodRv.setLayoutManager(layoutManager);
            holder.mGoodRv.setAdapter(imgAdapter);

            holder.mCommentEv.setTag(confirmList) ;
            holder.mCommentEv.clearFocus() ;

            holder.mCommentEv.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    GoodOrderConfirmList eCt = (GoodOrderConfirmList) holder.mCommentEv.getTag();
                    if(eCt != null){
                        eCt.setMessageInfo(arg0+"") ;
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                }
                @Override
                public void afterTextChanged(Editable arg0) {
                }
            });

            holder.mCommentEv.setText(StringUtils.convertNull(confirmList.getMessageInfo())) ;

            holder.mDeliveryTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener != null){
                        mClickListener.onItemClickListener(0 ,realPosition);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size();
    }


    static class GocHolder extends RecyclerView.ViewHolder {
        private View mStoreLay;
        private TextView mStoreNameTv;
        private RecyclerView mGoodRv;
        private TextView mGoodCountTv;
        private EditText mCommentEv;
        private TextView mMoneyTv;
        private TextView mDeliveryTv;

        public GocHolder(View itemView) {
            super(itemView);

            mStoreLay = itemView.findViewById(R.id.adapter_good_confirm_store_info_lay);
            mStoreNameTv = itemView.findViewById(R.id.adapter_good_confirm_store_name_tv);
            mGoodRv = itemView.findViewById(R.id.adapter_good_confirm_good_rv);
            mGoodCountTv = itemView.findViewById(R.id.adapter_good_confirm_good_count_tv);
            mCommentEv = itemView.findViewById(R.id.adapter_good_confirm_good_comment_ev);
            mMoneyTv = itemView.findViewById(R.id.adapter_good_confirm_good_all_money_tv);
            mDeliveryTv = itemView.findViewById(R.id.adapter_good_confirm_delivery_type_tv);
        }
    }
}
