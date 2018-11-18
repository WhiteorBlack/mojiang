package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.SearchHistory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterSearchHistory;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.FlowLayout;
import okhttp3.Call;

/**
 *  搜索
 * Created on 2018/4/18.
 *
 * 只做简单的搜索内容输入，以及历史切换
 */

public class SearchIndexActivity extends BaseActivity {
    private TextView mRightTv ;
    private EditText mSearchEv ;

    //header
    private TextView mTypeNewsTv ;
    private TextView mTypeGoodsTv ;
    private TextView mTypeResumeTv ;
    private TextView mTypeUnuseTv ;
    private TextView mTypeInstallTv ;
    private TextView mTypeServerTv ;
    private TextView mTypeNeedsTv ;
    private TextView mTypeQuestionTv ;

    private FlowLayout mHotLay ;

    private TextView mClearHisTv ;

    private ListView mHistoryLv ;

    private List<SearchHistory> mSearchList = new ArrayList<>() ;
    private AdapterSearchHistory mAdapter ;

    private List<SearchHistory> mHotSearchList = new ArrayList<>() ;

    private int mSearchType = 0 ;

    private static final int SEARCH_TYPE_NEWS = 1 ;
    private static final int SEARCH_TYPE_GOODS = 2 ;
    private static final int SEARCH_TYPE_RESUMES = 3 ;
    private static final int SEARCH_TYPE_UNUSE = 4 ;
    private static final int SEARCH_TYPE_INSTALL = 5 ;
    private static final int SEARCH_TYPE_SERVER = 6 ;
    private static final int SEARCH_TYPE_NEEDS = 7 ;
    private static final int SEARCH_TYPE_QUESTION = 8 ;

    private LoadingDialog mDialog ;

    private static final int REQUEST_CODE_LOGIN = 1001 ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_search_index ;
    }

    @Override
    public void initView() {
        mRightTv = findViewById(R.id.acti_search_index_right_tv) ;
        mSearchEv = findViewById(R.id.acti_search_index_ev) ;

        mHistoryLv = findViewById(R.id.acti_search_index_lv) ;
        initLvHeader() ;

        mAdapter = new AdapterSearchHistory(mContext ,mSearchList) ;
        mHistoryLv.setAdapter(mAdapter);
    }

    /**
     * 初始化lv的header
     */
    private void initLvHeader(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.header_search_index , null) ;

        mTypeNewsTv = view.findViewById(R.id.header_search_type_news_tv) ;
        mTypeGoodsTv = view.findViewById(R.id.header_search_type_goods_tv) ;
        mTypeResumeTv = view.findViewById(R.id.header_search_type_resume_tv) ;
        mTypeUnuseTv = view.findViewById(R.id.header_search_type_unuse_tv) ;
        mTypeInstallTv = view.findViewById(R.id.header_search_type_install_tv) ;
        mTypeServerTv = view.findViewById(R.id.header_search_type_fuwu_tv) ;
        mTypeNeedsTv = view.findViewById(R.id.header_search_type_needs_tv) ;
        mTypeQuestionTv = view.findViewById(R.id.header_search_type_question_tv) ;

        mHotLay = view.findViewById(R.id.header_search_hot_lay) ;
        mClearHisTv = view.findViewById(R.id.header_search_clear_tv) ;

        mTypeNewsTv.setOnClickListener(this);
        mTypeGoodsTv.setOnClickListener(this);
        mTypeResumeTv.setOnClickListener(this);
        mTypeUnuseTv.setOnClickListener(this);
        mTypeInstallTv.setOnClickListener(this);
        mTypeServerTv.setOnClickListener(this);
        mTypeNeedsTv.setOnClickListener(this);
        mTypeQuestionTv.setOnClickListener(this);
        view.setOnClickListener(this);
        mClearHisTv.setOnClickListener(this);

        mHistoryLv.addHeaderView(view);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
        mRightTv.setOnClickListener(this);
        mHistoryLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchHistory history = mSearchList.get(i) ;
                if(history != null){
                    intentToListByType(history.getCoulmn() ,history.getKeyword()) ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;
        if(R.id.acti_search_index_right_tv == vId){
            toSearch() ;
        }else if(R.id.header_search_type_news_tv == vId){//资讯
            changeTvStyleByType(SEARCH_TYPE_NEWS) ;
        }else if(R.id.header_search_type_goods_tv == vId){//商品
            changeTvStyleByType(SEARCH_TYPE_GOODS) ;
        }else if(R.id.header_search_type_resume_tv == vId){//人才（简历）
            changeTvStyleByType(SEARCH_TYPE_RESUMES) ;
        }else if(R.id.header_search_type_unuse_tv == vId){//闲置
            changeTvStyleByType(SEARCH_TYPE_UNUSE) ;
        }else if(R.id.header_search_type_install_tv == vId){//安装
            changeTvStyleByType(SEARCH_TYPE_INSTALL) ;
        }else if(R.id.header_search_type_fuwu_tv == vId){//服务
            changeTvStyleByType(SEARCH_TYPE_SERVER) ;
        }else if(R.id.header_search_type_needs_tv == vId){//需求
            changeTvStyleByType(SEARCH_TYPE_NEEDS) ;
        }else if(R.id.header_search_type_question_tv == vId){//问答
            changeTvStyleByType(SEARCH_TYPE_QUESTION) ;
        }else if(R.id.header_search_clear_tv == vId){//清空记录
            DialogUtils.showCustomViewDialog(mContext, "清空记录", "清空历史记录？", null
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    clearAllHistory() ;
                }
            }, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
    }


    /**
     * 改变指定内容样式
     * @param type index
     */
    private void changeTvStyleByType(int type){
        if(type == mSearchType){
            return ;
        }

        //还原
        switch (mSearchType){
            case SEARCH_TYPE_NEWS:
                mTypeNewsTv.setTextColor(getResources().getColor(R.color.color_grey_text)) ;
                break;
            case SEARCH_TYPE_GOODS:
                mTypeGoodsTv.setTextColor(getResources().getColor(R.color.color_grey_text)) ;
                break;
            case SEARCH_TYPE_RESUMES:
                mTypeResumeTv.setTextColor(getResources().getColor(R.color.color_grey_text)) ;
                break;
            case SEARCH_TYPE_UNUSE:
                mTypeUnuseTv.setTextColor(getResources().getColor(R.color.color_grey_text)) ;
                break;
            case SEARCH_TYPE_INSTALL:
                mTypeInstallTv.setTextColor(getResources().getColor(R.color.color_grey_text)) ;
                break;
            case SEARCH_TYPE_SERVER:
                mTypeServerTv.setTextColor(getResources().getColor(R.color.color_grey_text)) ;
                break;
            case SEARCH_TYPE_NEEDS:
                mTypeNeedsTv.setTextColor(getResources().getColor(R.color.color_grey_text)) ;
                break;
            case SEARCH_TYPE_QUESTION:
                mTypeQuestionTv.setTextColor(getResources().getColor(R.color.color_grey_text)) ;
                break;
            default:
                break;
        }

        //切换
        switch (type){
            case SEARCH_TYPE_NEWS:
                mTypeNewsTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            case SEARCH_TYPE_GOODS:
                mTypeGoodsTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            case SEARCH_TYPE_RESUMES:
                mTypeResumeTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            case SEARCH_TYPE_UNUSE:
                mTypeUnuseTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            case SEARCH_TYPE_INSTALL:
                mTypeInstallTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            case SEARCH_TYPE_SERVER:
                mTypeServerTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            case SEARCH_TYPE_NEEDS:
                mTypeNeedsTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            case SEARCH_TYPE_QUESTION:
                mTypeQuestionTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
                break;
            default:
                break;
        }

        mSearchType = type ;
    }

    /**
     * 填充热门搜索
     */
    private void updateHotLay(){
        if(mHotSearchList.size() == 0){
            return ;
        }

        int lrPd = ResourceUtils.dip2px(mContext ,10) ;
        int tbPd = ResourceUtils.dip2px(mContext ,6) ;

        for(SearchHistory history : mHotSearchList){
            if(history != null){
                final String keys = history.getKeyword() ;
                final int column = history.getCoulmn() ;

                TextView tv = new TextView(mContext) ;
                tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_grey_f2_bg));
                tv.setPadding(lrPd ,tbPd ,lrPd,tbPd);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,14);
                tv.setTextColor(getResources().getColor(R.color.color_nomal_text));
                tv.setText(keys);

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intentToListByType(column ,keys) ;
                    }
                });

                mHotLay.addView(tv) ;
            }
        }
    }

    /**
     * 点击搜索
     */
    private void toSearch(){
        if(0 == mSearchType){
            ToastUtils.showToast(mContext ,"请先指定内容");
            return ;
        }

        String key = mSearchEv.getText().toString().trim() ;
        if("".equals(key)){
            ToastUtils.showToast(mContext ,"请输入要搜索的内容");
            mSearchEv.setText("");
            return ;
        }

        intentToListByType(key);
    }

    /**
     * 根据类型跳转到相应的activity
     * @param type type
     * @param key keys
     */
    private void intentToListByType(int type ,String key){
        //先调用一下统计接口，然后再跳转
        addSearchHisToServer(type ,key) ;

        Intent toLiIt = null ;
        if(SEARCH_TYPE_NEWS == type){
            toLiIt = new Intent(mContext ,NewsListActivity.class) ;
        }else if(SEARCH_TYPE_GOODS == type){
            toLiIt = new Intent(mContext ,SearchGoodListActivity.class) ;
        }else if(SEARCH_TYPE_RESUMES == type){
            toLiIt = new Intent(mContext ,ResumeHotListActivity.class) ;
        }else if(SEARCH_TYPE_UNUSE == type){
            toLiIt = new Intent(mContext ,UnuseGoodListActivity.class) ;
        }else if(SEARCH_TYPE_INSTALL == type){
            toLiIt = new Intent(mContext ,ServerListActivity.class) ;
        }else if(SEARCH_TYPE_SERVER == type){
            toLiIt = new Intent(mContext ,ServerListActivity.class) ;
        }else if(SEARCH_TYPE_NEEDS == type){
            toLiIt = new Intent(mContext ,NeedsListActivity.class) ;
        }else if(SEARCH_TYPE_QUESTION == type){
            toLiIt = new Intent(mContext ,QuestionListActivity.class) ;
        }

        if(toLiIt != null){
            toLiIt.putExtra(SkipUtils.INTENT_SEARCH_KEY ,key) ;
            startActivity(toLiIt) ;
        }
    }
    /**
     * 根据类型跳转到相应的activity
     * @param key keys
     */
    private void intentToListByType(String key){
        intentToListByType(mSearchType ,key) ;
    }

    /**
     * 获取热门搜索
     */
    private void getHotSearch(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.API_HOME_HOT_SEARCH, paramMap
                , new RequestListCallBack<SearchHistory>("getHotSearch" ,mContext ,SearchHistory.class) {
                    @Override
                    public void onSuccessResult(List<SearchHistory> bean) {
                        mHotSearchList.clear();
                        mHotSearchList.addAll(bean) ;

                        updateHotLay() ;
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
     * 把搜索内容传给服务器
     */
    private void addSearchHisToServer(int type ,String key){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Coulmn" ,"" + type) ;
        paramMap.put("Keyword" ,key) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.API_HOME_SEARCH_TO_SERVER, paramMap
                , new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                    }
                });
    }

    /**
     * 获取搜索历史
     */
    private void getHistory(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.API_HOME_SEARCH_HISTORY, paramMap
                , new RequestListCallBack<SearchHistory>("getHistory",mContext ,SearchHistory.class) {
                    @Override
                    public void onSuccessResult(List<SearchHistory> bean) {
                        mSearchList.clear();
                        mSearchList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        mClearHisTv.setVisibility(mSearchList.size() == 0 ? View.GONE : View.VISIBLE);
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
     * 清空搜索历史
     */
    private void clearAllHistory(){
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_LOGIN);
            return ;
        }

        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.API_HOME_SEARCH_HISTORY_CLEAR, paramMap
                , new RequestListCallBack<SearchHistory>("clearAllHistory",mContext ,SearchHistory.class) {
                    @Override
                    public void onSuccessResult(List<SearchHistory> bean) {
                        mDialog.dismiss();

                        mSearchList.clear();
                        mAdapter.notifyDataSetChanged();
                        mClearHisTv.setVisibility(mSearchList.size() == 0 ? View.GONE : View.VISIBLE);
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mHotSearchList.size() == 0){
            getHotSearch() ;
        }

        getHistory() ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_LOGIN == requestCode){
            if(RESULT_OK == resultCode){
                clearAllHistory() ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getHotSearch") ;
        NetUtils.cancelTag("getHistory") ;
        NetUtils.cancelTag("clearAllHistory") ;
    }
}
