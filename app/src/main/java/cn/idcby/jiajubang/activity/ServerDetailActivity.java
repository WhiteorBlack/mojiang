package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.Bean.Admire;
import cn.idcby.jiajubang.Bean.Collect;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.ServerComment;
import cn.idcby.jiajubang.Bean.ServerDetails;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ServerCommentAdapter;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.FlowLayout;
import cn.idcby.jiajubang.view.MyCornerTextView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 服务详细
 * <p>
 * 2018-05-23 16:35:21
 * 改：服务以后针对人，所以要隐藏点赞，修改收藏为 关注
 * 服务详细用userid获取
 * 改：重写界面
 * <p>
 * 2018-07-25 13:59:30
 * 改：添加编辑功能，当服务工是自己的时候，相册右上角显示编辑图标，点击进入服务、安装编辑界面
 * 改：去掉 预订 功能
 * <p>
 * 去除安装工人
 */
public class ServerDetailActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private String mServerUserId; //服务工的userId
    private boolean mIsInstall = true;

    private View mEditIv;

    private Banner mBanner;
    private TextView mTitleTv;
    private TextView mStateTv;
    private FlowLayout mTypeLay;
    private TextView mCountTv;
    private TextView mComScaleTv;
    private TextView mBondMoneyTv;

    private FlowLayout mApplyLay;

    private TextView mServerTimeTv;
    private TextView mSellerAddressTv;
    private TextView mServerAreaTv;
    private TextView mServerDescTv;

    private LinearLayout mPromiseLay;

    private TextView mCommentCountTv;
    private View mCommentMoreTv;
    private View mCommentMoreBotTv;
    private View mCommentNullTv;
    private ListView mCommentLv;

    private TextView mCollectionTv;
    private TextView mSupportTv;
    private TextView mSendServerTv;
    private TextView mConnectTv;
    private ImageView mImgAdmire;
    private ImageView mImgCollect;

    private ServerDetails mServerDetails;
    private LoadingDialog loadingDialog;

    private boolean mIsSelf = false;

    private static final int REQUEST_CODE_COLLECTION = 1001;
    private static final int REQUEST_CODE_SUPPORT = 1002;
    private static final int REQUEST_CODE_CONNECT = 1003;
    private static final int REQUEST_CODE_SUBMIT = 1004;
    private static final int REQUEST_CODE_FOCUS = 1005;
    private static final int REQUEST_CODE_CUSTOMER = 1006;
    private static final int REQUEST_CODE_CART = 1007;
    private static final int REQUEST_CODE_PICTURE_EDIT = 1008;

    //评论相关
    private List<ServerComment> mCommentList = new ArrayList<>();
    private ServerCommentAdapter mCommentAdapter;

    private List<WordType> mPromiseList = new ArrayList<>();
    private Dialog mPromiseDialog;
    private LinearLayout mPromiseDialogLay;

    private List<ImageThumb> mBannerImageList = new ArrayList<>();

    public static void launch(Context context, String optionId, boolean isInstall) {
        Intent toSdIt = new Intent(context, ServerDetailActivity.class);
        toSdIt.putExtra(SkipUtils.INTENT_SERVER_USER_ID, optionId);
        toSdIt.putExtra(SkipUtils.INTENT_SERVER_IS_INSTALL, isInstall);
        context.startActivity(toSdIt);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_server_detail;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);

        mServerUserId = getIntent().getStringExtra(SkipUtils.INTENT_SERVER_USER_ID);
        mIsInstall = getIntent().getBooleanExtra(SkipUtils.INTENT_SERVER_IS_INSTALL, mIsInstall);

        mIsSelf = SPUtils.newIntance(mContext).getUserNumber().equals(mServerUserId);

        StatusBarUtil.setTransparentForImageView(this, null);

        mEditIv = findViewById(R.id.acti_server_dt_edit_iv);
        mEditIv.setOnClickListener(this);
        mEditIv.setVisibility(mIsSelf ? View.VISIBLE : View.GONE);

        mBanner = findViewById(R.id.acti_server_dt_banner);
        mTitleTv = findViewById(R.id.acti_server_dt_title_tv);
        mStateTv = findViewById(R.id.acti_server_dt_state_tv);
        mTypeLay = findViewById(R.id.acti_server_dt_type_lay);
        mCountTv = findViewById(R.id.acti_server_dt_count_tv);
        mComScaleTv = findViewById(R.id.acti_server_dt_commScale_tv);
        mBondMoneyTv = findViewById(R.id.acti_server_dt_bond_tv);

        mApplyLay = findViewById(R.id.acti_server_dt_server_apply_lay);

        mServerTimeTv = findViewById(R.id.acti_server_dt_server_time_tv);
        mSellerAddressTv = findViewById(R.id.acti_server_dt_server_address_tv);
        mServerAreaTv = findViewById(R.id.acti_server_dt_server_area_tv);
        mServerDescTv = findViewById(R.id.acti_server_dt_server_desc_tv);

        mPromiseLay = findViewById(R.id.acti_server_dt_promise_lay);
        TextView mSubsTv = findViewById(R.id.acti_server_dt_subs_tv);
        TextView mBuyTv = findViewById(R.id.acti_server_dt_bt_tv);
        mSubsTv.setOnClickListener(this);
        mBuyTv.setOnClickListener(this);
        mPromiseLay.setOnClickListener(this);

        mCommentCountTv = findViewById(R.id.acti_server_dt_comment_count_tv);
        mCommentMoreTv = findViewById(R.id.acti_server_dt_comment_count_more_tv);
        mCommentMoreBotTv = findViewById(R.id.acti_server_dt_comment_more_tv);
        mCommentNullTv = findViewById(R.id.acti_server_dt_comment_null_tv);
        mCommentLv = findViewById(R.id.acti_server_dt_comment_lv);
        mCommentMoreTv.setOnClickListener(this);
        mCommentMoreBotTv.setOnClickListener(this);
        mCommentLv.setFocusable(false);

        View supportLay = findViewById(R.id.acti_server_dt_support_lay);
        View connectionLay = findViewById(R.id.acti_server_dt_collection_lay);
        View shareLay = findViewById(R.id.acti_server_dt_share_lay);
        View cartLay = findViewById(R.id.acti_server_dt_cart_lay);
        mImgAdmire = findViewById(R.id.acti_server_dt_support_iv);
        mImgCollect = findViewById(R.id.acti_server_dt_collection_iv);
        mCollectionTv = findViewById(R.id.acti_server_dt_collection_tv);
        mSupportTv = findViewById(R.id.acti_server_dt_support_tv);
        mConnectTv = findViewById(R.id.acti_server_dt_connection_tv);
        mSendServerTv = findViewById(R.id.acti_server_dt_send_tv);

        cartLay.setOnClickListener(this);
        supportLay.setOnClickListener(this);
        connectionLay.setOnClickListener(this);
        shareLay.setOnClickListener(this);
        mConnectTv.setOnClickListener(this);
        mSendServerTv.setOnClickListener(this);

        int screenWidth = ResourceUtils.getScreenWidth(mContext);
        int height = (int) (screenWidth / ImageWidthUtils.getServerPicImageRote());
        mBanner.getLayoutParams().height = height;

        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //取消自动轮播
        mBanner.isAutoPlay(false);
        mBanner.setBannerStyle(BannerConfig.NUM_INDICATOR);
        mBanner.setImageLoader(new BannerImageLoader());
        mBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                SkipUtils.toImageShowActivityWithThumb(mActivity, mBannerImageList, position - 1);//注：banner的position默认加了1
            }
        });

        if (mIsSelf) {
            mSendServerTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
            mConnectTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
        }
    }

    @Override
    public void initData() {
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog(mContext);
        }
        loadingDialog.show();

        getServerDetails();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        if (i == R.id.acti_server_dt_support_lay) {
            admire();
        } else if (i == R.id.acti_server_dt_collection_lay) {
            focusUser();
        } else if (i == R.id.acti_server_dt_share_lay) {//分享
            ShareUtils.shareWeb(mActivity, mServerDetails.getRealName()
                    , mServerDetails.getH5Url(), mServerDetails.getHeadIcon(), "");
        } else if (i == R.id.acti_server_dt_cart_lay) {//购物车
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, REQUEST_CODE_CART);
            } else {
                Intent toCtIt = new Intent(mContext, ShoppingCartActivity.class);
                startActivity(toCtIt);
            }
        } else if (i == R.id.acti_server_dt_connection_tv) {//联系TA
            if (!mIsSelf) {
                String phonePer = Manifest.permission.CALL_PHONE;
                if (!EasyPermissions.hasPermissions(mContext, phonePer)) {
                    EasyPermissions.requestPermissions(this, "需要拨打电话权限，禁止会导致该功能异常",
                            100, phonePer);
                } else {
                    if (LoginHelper.isNotLogin(mContext)) {
                        SkipUtils.toLoginActivityForResult(mActivity, REQUEST_CODE_CONNECT);
                    } else {
                        toCallPhone();
                    }
                }
            }
        } else if (i == R.id.acti_server_dt_send_tv) {//客服
            if (!mIsSelf) {
                if (LoginHelper.isNotLogin(mContext)) {
                    SkipUtils.toLoginActivityForResult(mActivity, REQUEST_CODE_CUSTOMER);
                } else {
                    String userHxId = mServerDetails.getHxName();
                    SkipUtils.toMessageChatActivity(mActivity, userHxId);
                }
            }
        } else if (i == R.id.acti_server_dt_subs_tv) {//预定
            if (!mIsSelf) {

            }
        } else if (i == R.id.acti_server_dt_bt_tv) {//买单
            if (!mIsSelf) {
                ServerConfirmActivity.launch(mActivity, mServerUserId
                        , mIsInstall, REQUEST_CODE_SUBMIT);
            }
        } else if (i == R.id.acti_server_dt_comment_count_more_tv
                || i == R.id.acti_server_dt_comment_more_tv) {//更多服务评价

            ServerCommentActivity.launch(mContext, mIsInstall, mServerUserId);

        } else if (i == R.id.acti_server_dt_promise_lay) {//服务承诺
            showPromiseDialog();
        } else if (i == R.id.acti_server_dt_edit_iv) {//编辑相册
            MyServerPictureEditActivity.launch(mActivity, mIsInstall);
        }
    }

    /**
     * 联系TA，打电话
     */
    private void toCallPhone() {
//        SkipUtils.toCallPhoneDialActivity(mContext ,mServerDetails.getUserMobile()) ;
        SkipUtils.toCallPhoneAutoActivity(mContext, mServerDetails.getUserMobile());
    }

    /**
     * 显示服务承诺
     */
    private void showPromiseDialog() {
        updatePromiseDialog(true);
    }

    /**
     * 填充内容
     */
    private void updatePromiseDialog(boolean show) {
        if (null == mPromiseDialog) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_server_promise, null);

            mPromiseDialog = new Dialog(mContext, R.style.my_custom_dialog);
            mPromiseDialog.setContentView(view);

            mPromiseDialogLay = view.findViewById(R.id.dialog_server_promise_content_lay);
            int width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.8F);
            view.getLayoutParams().width = width;
            view.getLayoutParams().height = width;

            View closeView = view.findViewById(R.id.dialog_server_promise_close_iv);
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPromiseDialog.dismiss();
                }
            });
        }

        mPromiseDialogLay.removeAllViews();

        int promiseSize = mPromiseList.size();
        int itemHeight = ResourceUtils.dip2px(mContext, 40);
        int drawPad = ResourceUtils.dip2px(mContext, 8);

        for (int x = 0; x < promiseSize; x++) {
            WordType wordType = mPromiseList.get(x);

            if (wordType != null) {
                TextView tv = new TextView(mContext);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight);
                tv.setLayoutParams(lp);
                tv.setSingleLine(true);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_server_promise);// 找到资源图片
                // 这一步必须要做，否则不会显示。
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
                tv.setCompoundDrawablePadding(drawPad);
                tv.setCompoundDrawables(drawable, null, null, null);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                tv.setTextColor(mContext.getResources().getColor(R.color.color_deep33_text));
                tv.setText(wordType.getItemName());

                mPromiseDialogLay.addView(tv);
            }
        }

        if (show) {
            mPromiseDialog.show();
        }
    }

    /**
     * 关注
     */
    private void focusUser() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mActivity, REQUEST_CODE_FOCUS);
            return;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("FollowType", "1");
        para.put("ResourceId", StringUtils.convertNull(mServerUserId));

        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, para,
                new RequestObjectCallBack<Collect>("focusUser", mContext, Collect.class) {
                    @Override
                    public void onSuccessResult(Collect bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (bean.AddOrDelete == 1) {
                            mImgCollect.setImageResource(R.mipmap.ic_collection_checked);
                        } else {
                            mImgCollect.setImageResource(R.mipmap.ic_collection_nomal);
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 收藏
     */
    private void collect() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mActivity, REQUEST_CODE_COLLECTION);
            return;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("Code", mServerUserId);
        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_COLLECTION, false, para,
                new RequestObjectCallBack<Collect>("collectionServer", mContext, Collect.class) {
                    @Override
                    public void onSuccessResult(Collect bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (bean.AddOrDelete == 1) {
                            mImgCollect.setImageResource(R.mipmap.ic_collection_checked);
                        } else {
                            mImgCollect.setImageResource(R.mipmap.ic_collection_nomal);
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 点赞
     */
    private void admire() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mActivity, REQUEST_CODE_SUPPORT);
            return;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("Code", mServerUserId);
        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_SUPPORT, false, para,
                new RequestObjectCallBack<Admire>("supportServer", mContext, Admire.class) {
                    @Override
                    public void onSuccessResult(Admire bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (bean.AddOrDelete == 1) {
                            mImgAdmire.setImageResource(R.mipmap.ic_support_checked);
                        } else {
                            mImgAdmire.setImageResource(R.mipmap.ic_support_nomal);
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 获取详细
     */
    private void getServerDetails() {
        Map<String, String> para = ParaUtils.getParaNece(mContext);
        para.put("Code", mServerUserId);

        NetUtils.getDataFromServerByPost(mContext, mIsInstall ? Urls.SERVER_DETAILS_INSTALL
                        : Urls.SERVER_DETAILS, para,
                new RequestObjectCallBack<ServerDetails>("getServerDetails", mContext, ServerDetails.class) {
                    @Override
                    public void onSuccessResult(ServerDetails bean) {
                        mServerDetails = bean;
                        updateUI();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        updateUI();
                    }

                    @Override
                    public void onFail(Exception e) {
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        loadingDialog.dismiss();

        if (null == mServerDetails) {
            DialogUtils.showCustomViewDialog(mContext, "信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
            return;
        }

        if (mServerDetails.isLike()) {
            mImgAdmire.setImageResource(R.mipmap.ic_support_checked);
        } else {
            mImgAdmire.setImageResource(R.mipmap.ic_support_nomal);
        }

        if (mServerDetails.isFollow()) {
            mImgCollect.setImageResource(R.mipmap.ic_collection_checked);
        } else {
            mImgCollect.setImageResource(R.mipmap.ic_collection_nomal);
        }

        //图集
        List<ImageThumb> imgList = mServerDetails.getAlbumsList();
        mBannerImageList.clear();
        mBannerImageList.addAll(imgList);

        List<String> imgUrlList = new ArrayList<>(imgList.size());
        for (ImageThumb imageThumb : imgList) {
            imgUrlList.add(imageThumb.getOriginalImgUrl());
        }
        mBanner.update(imgUrlList);

        String name = mServerDetails.getNickName();
        String singleAm = mServerDetails.getSingleAmount();
        String comRote = mServerDetails.getPraiseRate();
        String bondMoney = mServerDetails.getPayMoney();

        String time = mServerDetails.getServiceTime();//暂时隐藏
        String sellerArea = mServerDetails.getBussinessPostion();
        String serverArea = mServerDetails.getPostion();
        String serverDesc = mServerDetails.getServiceDescription();

        String commentCount = mServerDetails.getEvaluateListCount();

        mTitleTv.setText(name);
        mCountTv.setText("月接单：" + singleAm);
        mComScaleTv.setText("好评率：" + comRote);
        mBondMoneyTv.setText("保证金：" + bondMoney);
        mServerTimeTv.setText(time);
        mServerAreaTv.setText(serverArea);
        mSellerAddressTv.setText(sellerArea);
        mServerDescTv.setText(serverDesc);
        mCommentCountTv.setText("评价（" + commentCount + "）");

        mCommentMoreTv.setVisibility(StringUtils.convertString2Count(commentCount) > 0 ? View.VISIBLE : View.INVISIBLE);
        mCommentMoreBotTv.setVisibility(StringUtils.convertString2Count(commentCount) > 0 ? View.VISIBLE : View.INVISIBLE);
        mCommentNullTv.setVisibility(StringUtils.convertString2Count(commentCount) > 0 ? View.GONE : View.VISIBLE);

        //认证信息
        String applyText = mServerDetails.getAuthenticationText();

        if (mApplyLay.getChildCount() > 0) {
            mApplyLay.removeAllViews();
        }

        if (applyText.contains(",")) {
            String[] applyStates = applyText.split(",");
            for (String state : applyStates) {
                if (SkipUtils.APPLY_TYPE_CAR.equals(state) && !mIsInstall) {//不是安装，有货车
                    continue;
                }

                View itemLay = LayoutInflater.from(mContext).inflate(R.layout.dt_apply_item, null);
                ImageView icIv = itemLay.findViewById(R.id.dt_apply_icon_iv);
                TextView nameTv = itemLay.findViewById(R.id.dt_apply_name_tv);

                if (SkipUtils.APPLY_TYPE_PERSON_NO.equals(state)) {
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.geren));
                    nameTv.setText(getResources().getString(R.string.apply_text_person_no));
                } else if (SkipUtils.APPLY_TYPE_PERSON.equals(state)) {
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.geren));
                    nameTv.setText(getResources().getString(R.string.apply_text_person));
                } else if (SkipUtils.APPLY_TYPE_FACTORY.equals(state)) {
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.fangzi));
                    nameTv.setText(getResources().getString(R.string.apply_text_factory));
                } else if (SkipUtils.APPLY_TYPE_COMPANY.equals(state)) {
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.baoxiao));
                    nameTv.setText(getResources().getString(R.string.apply_text_company));
                } else if (SkipUtils.APPLY_TYPE_INSTALL.equals(state)) {
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.anzhuang));
                    nameTv.setText(getResources().getString(R.string.apply_text_install));
                } else if (SkipUtils.APPLY_TYPE_SERVER.equals(state)) {
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.gongsi));
                    nameTv.setText(getResources().getString(R.string.apply_text_server));
                } else if (SkipUtils.APPLY_TYPE_STORE.equals(state)) {
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_apply_store));
                    nameTv.setText(getResources().getString(R.string.apply_text_store));
                } else if (SkipUtils.APPLY_TYPE_CAR.equals(state) && mIsInstall) {//是安装，而且有货车
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_apply_car));
                    nameTv.setText(getResources().getString(R.string.apply_text_car));
                    itemLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ServerCarPictureActivity.launch(mContext, mServerDetails.getCarPositiveImg()
                                    , mServerDetails.getCarSideImg());
                        }
                    });
                }

                mApplyLay.addView(itemLay);
            }
        } else {
            if (SkipUtils.APPLY_TYPE_PERSON.equals(applyText)
                    || SkipUtils.APPLY_TYPE_PERSON_NO.equals(applyText)) {//通过了个人认证
                View perLay = LayoutInflater.from(mContext).inflate(R.layout.dt_apply_item, null);
                ImageView icIv = perLay.findViewById(R.id.dt_apply_icon_iv);
                TextView nameTv = perLay.findViewById(R.id.dt_apply_name_tv);

                icIv.setImageDrawable(getResources().getDrawable(R.mipmap.geren));
                if (SkipUtils.APPLY_TYPE_PERSON_NO.equals(applyText)) {
                    nameTv.setText(getResources().getString(R.string.apply_text_person_no));
                } else {
                    nameTv.setText(getResources().getString(R.string.apply_text_person));
                }
                mApplyLay.addView(perLay);
            }
        }

        //服务类型
        List<WordType> mTypeList = mServerDetails.getTypeList();
        int flTvPadding = ResourceUtils.dip2px(mContext, 2);
        mTypeLay.removeAllViews();
        int typeSize = mTypeList.size();
        for (int x = 0; x < typeSize; x++) {
            WordType wordType = mTypeList.get(x);

            if (wordType != null) {
                MyCornerTextView tv = new MyCornerTextView(mContext);
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setPadding(flTvPadding * 2, flTvPadding / 2, flTvPadding * 2, flTvPadding);
                tv.setfilColor(Color.parseColor(wordType.getColorValue())).setCornerSize(flTvPadding);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                tv.setTextColor(mContext.getResources().getColor(R.color.color_white));
                tv.setText(wordType.getItemName());

                mTypeLay.addView(tv);
            }
        }

        //服务承诺
        mPromiseList.clear();
        mPromiseList.addAll(mServerDetails.getPromiseList());
        mPromiseLay.removeAllViews();
        int promiseSize = mPromiseList.size();
        int rightPad = ResourceUtils.dip2px(mContext, 20);
        int drawPad = ResourceUtils.dip2px(mContext, 8);
        for (int x = 0; x < promiseSize; x++) {
            WordType wordType = mPromiseList.get(x);

            if (wordType != null) {
                TextView tv = new TextView(mContext);
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setPadding(0, 0, rightPad, 0);
                tv.setSingleLine(true);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_server_promise);// 找到资源图片
                // 这一步必须要做，否则不会显示。
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
                tv.setCompoundDrawablePadding(drawPad);
                tv.setCompoundDrawables(drawable, null, null, null);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                tv.setTextColor(mContext.getResources().getColor(R.color.color_deep33_text));
                tv.setText(wordType.getItemName());

                mPromiseLay.addView(tv);
            }
        }

        //dialog的内容填充
        updatePromiseDialog(false);

        mCommentList.clear();
        mCommentList.addAll(mServerDetails.getEvaluateList());
        if (null == mCommentAdapter) {
            mCommentAdapter = new ServerCommentAdapter(mContext, mCommentList);
            mCommentLv.setAdapter(mCommentAdapter);
        } else {
            mCommentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SUPPORT:
                if (RESULT_OK == resultCode) {
                    admire();
                }
                break;
            case REQUEST_CODE_COLLECTION:
                if (RESULT_OK == resultCode) {
                    collect();
                }
                break;
            case REQUEST_CODE_SUBMIT:
                if (RESULT_OK == resultCode) {
                    DialogUtils.showCustomViewDialog(mContext, "预约成功", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                }
                break;
            case REQUEST_CODE_CONNECT:
                if (RESULT_OK == resultCode) {
                    toCallPhone();
                }
                break;
            case REQUEST_CODE_CUSTOMER:
                if (RESULT_OK == resultCode) {
                    String userHxId = mServerDetails.getHxName();
                    SkipUtils.toMessageChatActivity(mActivity, userHxId);
                }
                break;
            case REQUEST_CODE_FOCUS:
                if (RESULT_OK == resultCode) {
                    focusUser();
                }
                break;
            case REQUEST_CODE_CART:
                if (RESULT_OK == resultCode) {
                    Intent toCtIt = new Intent(mContext, ShoppingCartActivity.class);
                    startActivity(toCtIt);
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.ServerPicChangedEvent ev) {
        if (ev.isChanged()) {
            getServerDetails();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        NetUtils.cancelTag("getServerDetails");
        NetUtils.cancelTag("supportServer");
        NetUtils.cancelTag("collectionServer");
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == 100) {
            Toast.makeText(this, "您拒绝了拨打电话所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
