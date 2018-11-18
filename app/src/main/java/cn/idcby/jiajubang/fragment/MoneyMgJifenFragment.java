package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.MyBangMoneyBillListActivity;
import cn.idcby.jiajubang.activity.MyBangMoneyTransferActivity;

/**
 * 积分
 * Created on 2018/5/18.
 */

public class MoneyMgJifenFragment extends Fragment implements View.OnClickListener{
    private Activity mContext ;

    private View mView ;
    private TextView mMoneyTv ;

    private String integralStr ;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = getActivity() ;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(null == mView) {
            mView = inflater.inflate(R.layout.fragment_money_mg_jifen, container, false);

            mMoneyTv = mView.findViewById(R.id.frag_money_mg_jifen_tv) ;
            View withdrawLay = mView.findViewById(R.id.frag_money_mg_jifen_withdraw_lay) ;
            View rechargeLay = mView.findViewById(R.id.frag_money_mg_jifen_rechange_lay) ;
            View detailsLay = mView.findViewById(R.id.frag_money_mg_jifen_details_lay) ;

            withdrawLay.setOnClickListener(this);
            rechargeLay.setOnClickListener(this);
            detailsLay.setOnClickListener(this);
        }

        return mView ;
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.frag_money_mg_jifen_withdraw_lay == vId){//积分转账

        }else if(R.id.frag_money_mg_jifen_rechange_lay == vId){//赚取积分

        }else if(R.id.frag_money_mg_jifen_details_lay == vId){//积分明细
            Intent toLvIt = new Intent(mContext ,MyBangMoneyBillListActivity.class) ;
            startActivity(toLvIt);
        }
    }

    public void updateFragDisplay(UserInfo userInfo){
        if(null == userInfo){
            return ;
        }
        integralStr = userInfo.getIntegral() ;
        mMoneyTv.setText(integralStr) ;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mView != null){
            ViewGroup parent = (ViewGroup) mView.getParent() ;
            //此处容易出现NullPoint
            if(parent != null){
                parent.removeView(mView);
            }
        }
    }

}
