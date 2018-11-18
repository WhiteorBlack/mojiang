package cn.idcby.jiajubang.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.CommentResult;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.interf.AddCommentCallBack;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 评论
 * Created on 2018/5/29.
 */

public class AddCommentPopup extends PopupWindow implements View.OnClickListener{
    private Activity mActivity;
    private View mParentView ;

    private EditText mEtContent;

    private AddCommentCallBack mCommentCallBack ;

    private LoadingDialog loadingDialog;
    private int mCommentLevel;
    private String mArticleID;
    private String mParentID;
    private int mCommentType = 0 ;// 0 资讯 1 圈子 2 需求 3闲置
    public static final int COMMENT_TYPE_NEWS = 0 ;
    public static final int COMMENT_TYPE_CIRCLE = 1 ;
    public static final int COMMENT_TYPE_NEED = 2 ;
    public static final int COMMENT_TYPE_UNUSE = 3 ;


    @SuppressLint("WrongConstant")
    public AddCommentPopup(Activity context, int type , View parentView , AddCommentCallBack callBack) {
        super(context);

        mActivity = context ;
        mCommentType = type ;
        mParentView = parentView ;
        mCommentCallBack = callBack ;

        loadingDialog = new LoadingDialog(mActivity) ;
        loadingDialog.setCancelable(false) ;

        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_add_comment,null) ;
        setContentView(contentView);

        mEtContent = contentView.findViewById(R.id.popup_add_comment_content_ev) ;
        TextView mOkTv = contentView.findViewById(R.id.popup_add_comment_send_tv) ;
        TextView mCancelTv = contentView.findViewById(R.id.popup_add_comment_cancel_tv) ;

        mOkTv.setOnClickListener(this);
        mCancelTv.setOnClickListener(this);

        //设置宽与高
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        //设置进出动画
        setAnimationStyle(R.style.add_comment_popup_anim_style);

        //设置背景只有设置了这个才可以点击外边和BACK消失
        setBackgroundDrawable(new BitmapDrawable());

        //设置可以获取集点
        setFocusable(true);//因为有个EditText，所以自己不要焦点

        //设置点击外边可以消失
        setOutsideTouchable(true);

        //防止PopupWindow被软件盘挡住
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = mActivity.getWindow().getAttributes();
                layoutParams.alpha=1.0f;
                mActivity.getWindow().setAttributes(layoutParams);

                NetUtils.cancelTag("sendComment-->type=" + mCommentType);

                showHiddenInput(false) ;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.popup_add_comment_send_tv == vId){//发表
            sendComment() ;
        }else if(R.id.popup_add_comment_cancel_tv == vId){
            showHiddenInput(false) ;
            dismiss() ;
        }
    }

    public void displayDialog(String articleID,int commentLevel, String parentID){
        mArticleID = articleID ;
        mCommentLevel = commentLevel ;
        mParentID = parentID ;

        mEtContent.setText("") ;

        showAtLocation(mParentView, Gravity.BOTTOM,0,0);
        lightOff();
        showHiddenInput(true) ;
    }

    /**
     * 显示时屏幕变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams layoutParams = mActivity.getWindow().getAttributes();
        layoutParams.alpha=0.3f;
        mActivity.getWindow().setAttributes(layoutParams);
    }


    private void showHiddenInput(boolean show){

        LogUtils.showLog("testPopupHidden" ,"show=" + show) ;

        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(show){
            //这里给它设置了弹出的时间，
            imm.toggleSoftInput(1000, InputMethodManager.HIDE_NOT_ALWAYS);
        }else{
            //参数：1，自己的EditText。2，时间。
            imm.hideSoftInputFromWindow(mEtContent.getWindowToken(), 0);
        }
    }


    private void sendComment() {
        if (LoginHelper.isNotLogin(mActivity)) {
            SkipUtils.toLoginActivity(mActivity);
            return;
        }

        String content = mEtContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showErrorToast(mActivity, "请输入内容");
            return;
        }

        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mActivity);
        if (mCommentLevel == 2) {
            para.put("ParentID", StringUtils.convertNull(mParentID));
        }
        para.put("Id", StringUtils.convertNull(mArticleID));
        para.put("CommentLevel", String.valueOf(mCommentLevel));
        para.put("CommitContent", content);

        String urls = Urls.ADD_COMMENT_ARTICLE_DETAIL ;

        if(COMMENT_TYPE_CIRCLE == mCommentType){
            urls = Urls.CIRCLE_COMMENT_ADD ;
        }else if(COMMENT_TYPE_NEED == mCommentType){
            urls = Urls.NEEDS_COMMENT_ADD ;
        }else if(COMMENT_TYPE_UNUSE == mCommentType){
            urls = Urls.UNUSE_COMMENT_ADD ;
        }

        NetUtils.getDataFromServerByPost(mActivity, urls, false, para,
                new RequestObjectCallBack<CommentResult>("sendComment-->type=" + mCommentType, mActivity, CommentResult.class) {
                    @Override
                    public void onSuccessResult(CommentResult bean) {

                        if (loadingDialog !=null)
                            loadingDialog.dismiss();

                        ToastUtils.showOkToast(mActivity, "提交成功");
                        if(mCommentCallBack != null){
                            mCommentCallBack.commentCallBack(bean.getCommentNumber()) ;
                        }

                        dismiss() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog !=null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog !=null)
                            loadingDialog.dismiss();
                    }
                });
    }
}
