package cn.idcby.jiajubang.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.jiajubang.Bean.MessageListComment;
import cn.idcby.jiajubang.Bean.MessageListSystem;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.MessageListActivity;
import cn.idcby.jiajubang.activity.MyOrderCenterAllActivity;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * Created on 2018/4/21.
 */

public class MessageConversationListFragment extends EaseConversationListFragment implements View.OnClickListener{

    private TextView mSysContentTv ;
    private TextView mSysTimeTv ;
    private TextView mCircleContentTv ;
    private TextView mCircleTimeTv ;
    private TextView mCommentContentTv ;
    private TextView mCommentTimeTv ;
    private TextView mOrderCountTv ;
    private TextView mCircleCountTv ;
    private TextView mCommentCountTv ;


    @Override
    protected void initView() {
        super.initView();

        hideTitleBar() ;

        View headerView = View.inflate(getActivity(), R.layout.header_message_center_item, null);
        conversationListView.addHeaderView(headerView);

        View systemView = headerView.findViewById(R.id.header_message_ct_system_lay) ;
        mSysContentTv = headerView.findViewById(R.id.header_message_ct_system_content_tv) ;
        mSysTimeTv = headerView.findViewById(R.id.header_message_ct_system_time_tv) ;
        View orderView = headerView.findViewById(R.id.header_message_ct_order_lay) ;
        View circleView = headerView.findViewById(R.id.header_message_ct_circle_lay) ;
        mCircleContentTv = headerView.findViewById(R.id.header_message_ct_circle_content_tv) ;
        mCircleTimeTv = headerView.findViewById(R.id.header_message_ct_circle_time_tv) ;
        View commentView = headerView.findViewById(R.id.header_message_ct_comment_lay) ;
        mCommentContentTv = headerView.findViewById(R.id.header_message_ct_comment_content_tv) ;
        mCommentTimeTv = headerView.findViewById(R.id.header_message_ct_comment_time_tv) ;

        mOrderCountTv = headerView.findViewById(R.id.header_message_ct_order_count_tv) ;
        mCircleCountTv = headerView.findViewById(R.id.header_message_ct_circle_count_tv) ;
        mCommentCountTv = headerView.findViewById(R.id.header_message_ct_comment_count_tv) ;

        systemView.setOnClickListener(this);
        orderView.setOnClickListener(this);
        circleView.setOnClickListener(this);
        commentView.setOnClickListener(this);
        headerView.setOnClickListener(this);

        getMessageInfo() ;
    }

    @Override
    protected void setUpView() {
        super.setUpView();

        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                EMConversation conversation = conversationListView.getItem(position - 1);//有个header
                if(conversation != null){
                    SkipUtils.toMessageChatActivity(getActivity() ,conversation.conversationId());
                }
            }
        }) ;
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.header_message_ct_system_lay == vId){
            MessageListActivity.launch(getContext() ,MessageListActivity.MESSAGE_TYPE_SYSTEM) ;
        }else if(R.id.header_message_ct_order_lay == vId){
//            MessageListActivity.launch(getContext() ,MessageListActivity.MESSAGE_TYPE_ORDER) ;
            //订单统一了
            MyOrderCenterAllActivity.launch(getContext() ,0) ;

        }else if(R.id.header_message_ct_circle_lay == vId){
            MessageListActivity.launch(getContext() ,MessageListActivity.MESSAGE_TYPE_CIRCLE) ;
        }else if(R.id.header_message_ct_comment_lay == vId){
            MessageListActivity.launch(getContext() ,MessageListActivity.MESSAGE_TYPE_COMMENT) ;
        }
    }


    /**
     * 获取所有信息
     */
    private void getMessageInfo(){
        getSystemMsgInfo() ;
        getNeedCommentInfo() ;
        getCircleCommentInfo() ;
    }


    /**
     * 获取系统消息
     */
    private void getSystemMsgInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(getContext()) ;
        paramMap.put("Page" ,"1") ;
        paramMap.put("PageSize" ,"1") ;
        NetUtils.getDataFromServerByPost(getContext(), Urls.MESSAGE_LIST_SYSTEM, paramMap
                , new RequestListCallBack<MessageListSystem>("getSystemMsgInfo",getContext() ,MessageListSystem.class) {
                    @Override
                    public void onSuccessResult(List<MessageListSystem> bean) {
                        if(bean.size() > 0){
                            MessageListSystem listSystem = bean.get(0) ;

                            if(listSystem != null){
                                String content = listSystem.getFullHead() ;
                                String time = listSystem.getCreateDate() ;

                                mSysContentTv.setText(content);
                                mSysTimeTv.setText(time);
                            }
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                    }
                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    /**
     * 获取需求消息
     */
    private void getNeedCommentInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(getContext()) ;
        paramMap.put("Page" ,"1") ;
        paramMap.put("PageSize" ,"1") ;
        NetUtils.getDataFromServerByPost(getContext(), Urls.MESSAGE_LIST_NEEDS, paramMap
                , new RequestListCallBack<MessageListComment>("getNeedCommentInfo",getContext() ,MessageListComment.class) {
                    @Override
                    public void onSuccessResult(List<MessageListComment> bean) {
                        if(bean.size() > 0){
                            MessageListComment listComment = bean.get(0) ;

                            if(listComment != null){
                                String content = listComment.getCommentContent() ;
                                String time = listComment.getCreateDate() ;

                                mCommentContentTv.setText(content);
                                mCommentTimeTv.setText(time);
                            }
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                    }
                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    /**
     * 获取互动消息
     */
    private void getCircleCommentInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(getContext()) ;
        paramMap.put("Page" ,"1") ;
        paramMap.put("PageSize" ,"1") ;
        NetUtils.getDataFromServerByPost(getContext(), Urls.MESSAGE_LIST_CIRCLE, paramMap
                , new RequestListCallBack<MessageListComment>("getCircleCommentInfo",getContext() ,MessageListComment.class) {
                    @Override
                    public void onSuccessResult(List<MessageListComment> bean) {
                        if(bean.size() > 0){
                            MessageListComment listComment = bean.get(0) ;

                            if(listComment != null){
                                String content = listComment.getCommentContent() ;
                                String time = listComment.getCreateDate() ;

                                mCircleContentTv.setText(content);
                                mCircleTimeTv.setText(time);
                            }
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                    }
                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden && mCircleTimeTv != null){
            getMessageInfo() ;
        }
    }

    public void getMessageCount(){
        SPUtils spUtils = SPUtils.newIntance(getContext()) ;
        int orderCount = spUtils.getUnreadMessageCountOrder() ;
        int circleCount = spUtils.getUnreadMessageCount(SPUtils.MSG_TYPE_CIRCLE) ;
        int commentCount = spUtils.getUnreadMessageCount(SPUtils.MSG_TYPE_COMMENT) ;

        mOrderCountTv.setVisibility(orderCount > 0 ? View.VISIBLE : View.GONE);
        mCircleCountTv.setVisibility(circleCount > 0 ? View.VISIBLE : View.GONE);
        mCommentCountTv.setVisibility(commentCount > 0 ? View.VISIBLE : View.GONE);

        mOrderCountTv.setText("" + (orderCount > 99 ? "99+" : orderCount));
        mCircleCountTv.setText("" + circleCount);
        mCommentCountTv.setText("" + commentCount);
    }

}
