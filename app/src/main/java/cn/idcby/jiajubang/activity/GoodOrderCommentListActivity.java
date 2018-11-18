package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.jiajubang.Bean.GoodOrderGood;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterMyGoodOrderCommentGood;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 订单评价--商品列表
 * Created on 2018/4/28.
 */

@Deprecated
public class GoodOrderCommentListActivity extends BaseActivity {
    private String mOrderId ;

    private AdapterMyGoodOrderCommentGood mAdapter ;
    private List<GoodOrderGood> mGoodList ;

    private int mCurPosition ;

    private static final int REQUEST_CODE_EDIT = 1000 ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_good_order_comment_list ;
    }

    @Override
    public void initView() {
        mOrderId = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID));
        mGoodList = (List<GoodOrderGood>) getIntent().getSerializableExtra(SkipUtils.INTENT_ORDER_GOOD_INFO);

        if("".equals(mOrderId) || null == mGoodList || mGoodList.size() == 0){
            DialogUtils.showCustomViewDialog(mContext, "订单信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });

            return ;
        }

        mAdapter = new AdapterMyGoodOrderCommentGood(mContext, mGoodList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    GoodOrderGood good = mGoodList.get(position) ;
                    if(good != null && !good.isGoodComment()){
                        mCurPosition = position ;

                        Intent toCmIt = new Intent(mContext ,GoodOrderCommentEditActivity.class) ;
                        toCmIt.putExtra(SkipUtils.INTENT_ORDER_ID,mOrderId) ;
                        toCmIt.putExtra(SkipUtils.INTENT_ORDER_GOOD_INFO ,good) ;
                        startActivityForResult(toCmIt ,REQUEST_CODE_EDIT) ;
                    }
                }

            }
        }) ;

        RecyclerView recyclerView = findViewById(R.id.acti_good_order_comment_good_rv) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_EDIT == requestCode){
            if(RESULT_OK == resultCode){
                EventBus.getDefault().post(new BusEvent.GoodOrderRefresh(true)) ;

                mGoodList.get(mCurPosition).setGoodComment(true);
                mAdapter.notifyDataSetChanged() ;
            }
        }

    }
}
