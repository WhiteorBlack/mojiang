package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.jiajubang.Bean.MessageListComment;
import cn.idcby.jiajubang.Bean.MessageListSystem;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterMessageList;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 消息列表
 * Created on 2018/4/21.
 */

public class MessageListActivity extends BaseActivity {
    private ListView mLv ;
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;

    private TextView mCountNeedTv ;
    private TextView mCountServerTv ;
    private TextView mCountGoodTv ;

    private int mMessageType = 0 ;//系统消息

    public static final int MESSAGE_TYPE_SYSTEM = 0 ;
    public static final int MESSAGE_TYPE_ORDER = 1 ;
    public static final int MESSAGE_TYPE_CIRCLE = 2 ;
    public static final int MESSAGE_TYPE_COMMENT = 3 ;

    private List<MessageListSystem> mSystemList = new ArrayList<>() ;
    private AdapterMessageList mSystemAdapter ;

    private List<MessageListComment> mNeedsList = new ArrayList<>() ;
    private AdapterMessageList mNeedsAdapter ;

    private List<MessageListComment> mCircleList = new ArrayList<>() ;
    private AdapterMessageList mCircleAdapter ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;



    public static void launch(Context context ,int type){
        Intent toLiIt = new Intent(context ,MessageListActivity.class) ;
        toLiIt.putExtra("messageType" ,type) ;
        context.startActivity(toLiIt) ;
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_message_list ;
    }

    @Override
    public void initView() {
        mMessageType = getIntent().getIntExtra("messageType" ,mMessageType) ;

        TextView mTitleTv = findViewById(R.id.acti_message_list_title_tv) ;
        mRefreshLay = findViewById(R.id.acti_message_list_refresh_lay) ;
        mLv = findViewById(R.id.acti_message_list_lv) ;
        mNullTv = findViewById(R.id.acti_message_list_null_tv) ;

        if(MESSAGE_TYPE_SYSTEM == mMessageType){
            mTitleTv.setText("系统消息");
            initSystemMessage() ;
        }else if(MESSAGE_TYPE_ORDER == mMessageType){
            mRefreshLay.setEnabled(false);

            mTitleTv.setText("订单消息");
            initOrderMessage() ;
        }else if(MESSAGE_TYPE_CIRCLE == mMessageType){
            //清空消息
            SPUtils.newIntance(mContext).clearUnreadMessageCount(SPUtils.MSG_TYPE_CIRCLE) ;

            mTitleTv.setText("互动消息");
            initCircleMessage() ;
        }else if(MESSAGE_TYPE_COMMENT == mMessageType){
            //清空消息
            SPUtils.newIntance(mContext).clearUnreadMessageCount(SPUtils.MSG_TYPE_COMMENT) ;

            mTitleTv.setText("评论消息");
            initCommentMessage() ;
        }
    }

    @Override
    public void initData() {
        getMessageInfo() ;
    }

    @Override
    public void initListener() {
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i2 > 5 && i + i1 >= i2){
                    getMessageInfo() ;
                }
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                getMessageInfo() ;
            }
        });

    }
    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.header_message_order_needs_lay == vId){
            //清空消息
            SPUtils.newIntance(mContext).clearUnreadMessageCount(SPUtils.MSG_TYPE_NEED) ;

//            MyOrderCenterActivity.launch(mContext ,0 ,MyOrderCenterActivity.ORDER_TYPE_NEED) ;
            MyOrderCenterAllActivity.launch(mContext ,0) ;

        }else if(R.id.header_message_order_server_lay == vId){
            //清空消息
            SPUtils.newIntance(mContext).clearUnreadMessageCount(SPUtils.MSG_TYPE_SERVER) ;

//            MyOrderCenterActivity.launch(mContext ,0 ,MyOrderCenterActivity.ORDER_TYPE_SERVER) ;
            MyOrderCenterAllActivity.launch(mContext ,0) ;

        }else if(R.id.header_message_order_goods_lay == vId){
            //清空消息
            SPUtils.newIntance(mContext).clearUnreadMessageCount(SPUtils.MSG_TYPE_GOOD) ;

//            MyOrderCenterActivity.launch(mContext ,0 ,MyOrderCenterActivity.ORDER_TYPE_GOOD) ;
            MyOrderCenterAllActivity.launch(mContext ,0) ;

        }

    }

    /**
     * 系统消息
     */
    private void initSystemMessage(){
        mSystemAdapter = new AdapterMessageList(mContext,true , mSystemList, null, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                MessageListSystem systemInfo = mSystemList.get(position) ;
                if(systemInfo != null){
                    SkipUtils.toShowWebActivity(mContext ,systemInfo.getFullHead() ,systemInfo.getH5Url()) ;
                }
            }
        }) ;
        mLv.setAdapter(mSystemAdapter) ;
    }

    /**
     * 订单消息
     */
    private void initOrderMessage(){
        //添加header
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_message_order_item ,null) ;
        mLv.addHeaderView(headerView);

        View needOrderLay = headerView.findViewById(R.id.header_message_order_needs_lay) ;
        View serverOrderLay = headerView.findViewById(R.id.header_message_order_server_lay) ;
        View goodOrderLay = headerView.findViewById(R.id.header_message_order_goods_lay) ;
        mCountNeedTv = headerView.findViewById(R.id.header_message_order_need_count_tv) ;
        mCountServerTv = headerView.findViewById(R.id.header_message_order_server_count_tv) ;
        mCountGoodTv = headerView.findViewById(R.id.header_message_order_good_count_tv) ;

        headerView.setOnClickListener(this);
        needOrderLay.setOnClickListener(this);
        serverOrderLay.setOnClickListener(this);
        goodOrderLay.setOnClickListener(this);

        AdapterMessageList mOrderAdapter = new AdapterMessageList(mContext) ;
        mLv.setAdapter(mOrderAdapter);
    }

    /**
     * 互动消息
     */
    private void initCircleMessage(){
        mCircleAdapter = new AdapterMessageList(mContext,true , null, mCircleList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                MessageListComment commentInfo = mCircleList.get(position) ;
                if(commentInfo != null){
                    Intent intent = new Intent(mContext, CircleDetailActivity.class);
                    intent.putExtra(SkipUtils.INTENT_ARTICLE_ID ,commentInfo.getPostID()) ;
                    startActivity(intent);
                }
            }
        }) ;
        mLv.setAdapter(mCircleAdapter) ;
    }

    /**
     * 评论消息
     */
    private void initCommentMessage(){
        mNeedsAdapter = new AdapterMessageList(mContext,false , null, mNeedsList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                MessageListComment commentInfo = mNeedsList.get(position) ;
                if(commentInfo != null){
                    Intent intent = new Intent(mContext, NeedsDetailsActivity.class);
                    intent.putExtra(SkipUtils.INTENT_NEEDS_ID ,commentInfo.getPostID()) ;
                    startActivity(intent);
                }
            }
        }) ;
        mLv.setAdapter(mNeedsAdapter) ;
    }

    /**
     * 根据分类获取消息
     */
    private void getMessageInfo(){
        mIsLoading = true ;
        if(mNullTv.getVisibility() != View.GONE){
            mNullTv.setVisibility(View.GONE);
        }

        if(MESSAGE_TYPE_SYSTEM == mMessageType){
            getSystemMsgInfo() ;
        }else if(MESSAGE_TYPE_CIRCLE == mMessageType){
           getCircleCommentInfo();
        }else if(MESSAGE_TYPE_COMMENT == mMessageType){
            getNeedCommentInfo();
        }
    }

    /**
     * 获取系统消息
     */
    private void getSystemMsgInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"10") ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MESSAGE_LIST_SYSTEM, paramMap
                , new RequestListCallBack<MessageListSystem>("getSystemMsgInfo",mContext ,MessageListSystem.class) {
                    @Override
                    public void onSuccessResult(List<MessageListSystem> bean) {
                        if(1 == mCurPage){
                            mSystemList.clear();
                        }
                        mSystemList.addAll(bean) ;
                        mSystemAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mIsMore =false ;
                        }else {
                            mIsMore = true ;
                            mCurPage ++ ;
                        }
                        finishRequest() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishRequest() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishRequest() ;
                    }
                });
    }

    /**
     * 获取需求消息
     */
    private void getNeedCommentInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Page" ,"1") ;
        paramMap.put("PageSize" ,"1") ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MESSAGE_LIST_NEEDS, paramMap
                , new RequestListCallBack<MessageListComment>("getNeedCommentInfo",mContext ,MessageListComment.class) {
                    @Override
                    public void onSuccessResult(List<MessageListComment> bean) {
                        if(1 == mCurPage){
                            mNeedsList.clear();
                        }
                        mNeedsList.addAll(bean) ;
                        mNeedsAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        finishRequest() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishRequest() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishRequest() ;
                    }
                });
    }

    /**
     * 获取互动消息
     */
    private void getCircleCommentInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Page" ,"" + mCurPage) ;
        paramMap.put("PageSize" ,"10") ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MESSAGE_LIST_CIRCLE, paramMap
                , new RequestListCallBack<MessageListComment>("getCircleCommentInfo",mContext ,MessageListComment.class) {
                    @Override
                    public void onSuccessResult(List<MessageListComment> bean) {
                        if(1 == mCurPage){
                            mCircleList.clear();
                        }
                        mCircleList.addAll(bean) ;
                        mCircleAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        finishRequest() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishRequest() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishRequest() ;
                    }
                });
    }

    private void finishRequest(){
        mIsLoading = false ;
        mRefreshLay.finishRefresh() ;

        if(MESSAGE_TYPE_SYSTEM == mMessageType){
            if(mSystemList.size() == 0){
                mNullTv.setVisibility(View.VISIBLE);
            }
        }else if(MESSAGE_TYPE_CIRCLE == mMessageType){
            if(mCircleList.size() == 0){
                mNullTv.setVisibility(View.VISIBLE);
            }
        }else if(MESSAGE_TYPE_COMMENT == mMessageType){
            if(mNeedsList.size() == 0){
                mNullTv.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 更新消息数量
     */
    private void getOrderMessageCount(){
        SPUtils spUtils = SPUtils.newIntance(mContext) ;
        //订单消息
        int needCount = spUtils.getUnreadMessageCount(SPUtils.MSG_TYPE_NEED) ;
        int serverCount = spUtils.getUnreadMessageCount(SPUtils.MSG_TYPE_SERVER) ;
        int goodCount = spUtils.getUnreadMessageCount(SPUtils.MSG_TYPE_GOOD) ;

        mCountNeedTv.setVisibility(needCount > 0 ? View.VISIBLE : View.GONE);
        mCountServerTv.setVisibility(serverCount > 0 ? View.VISIBLE : View.GONE);
        mCountGoodTv.setVisibility(goodCount > 0 ? View.VISIBLE : View.GONE);

        mCountNeedTv.setText("" + needCount);
        mCountServerTv.setText("" + serverCount);
        mCountGoodTv.setText("" + goodCount);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MESSAGE_TYPE_ORDER == mMessageType){
            getOrderMessageCount() ;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getSystemMsgInfo") ;
        NetUtils.cancelTag("getCircleCommentInfo") ;
        NetUtils.cancelTag("getNeedCommentInfo") ;

    }
}
