package cn.idcby.jiajubang.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ActivityBondPay;
import cn.idcby.jiajubang.activity.ActivityShowWeb;
import cn.idcby.jiajubang.activity.CategoryGoodListActivity;
import cn.idcby.jiajubang.activity.CircleSendActivity;
import cn.idcby.jiajubang.activity.FeedBackActivity;
import cn.idcby.jiajubang.activity.GetTypeByCodeActivity;
import cn.idcby.jiajubang.activity.GoodDetailActivity;
import cn.idcby.jiajubang.activity.JobDetailActivity;
import cn.idcby.jiajubang.activity.LoginActivity;
import cn.idcby.jiajubang.activity.MessageCenterActivity;
import cn.idcby.jiajubang.activity.MessageChatActivity;
import cn.idcby.jiajubang.activity.MyApplyInfoActivity;
import cn.idcby.jiajubang.activity.MyMultiImgShowActivity;
import cn.idcby.jiajubang.activity.MyOrderAfterSaleActivity;
import cn.idcby.jiajubang.activity.NewsDetailActivity;
import cn.idcby.jiajubang.activity.NewsDetailVideoActivity;
import cn.idcby.jiajubang.activity.SearchGoodListActivity;
import cn.idcby.jiajubang.activity.SearchNomalActivity;
import cn.idcby.jiajubang.activity.StoreIndexActivity;
import cn.idcby.jiajubang.activity.UserIndexActivity;

/**
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2018/2/10.
 */

public class SkipUtils {

    public static final String INTENT_CHANGE_PHONE_TYPE = "changePhoneInfoType";

    public static final String INTENT_WEB_TITLE = "webTitle";
    public static final String INTENT_WEB_HREF = "webHref";
    public static final String INTENT_WEB_SHARE = "webIsShowShare";
    public static final String INTENT_WEB_SHARE_IMAGE = "webShareThumb";

    public static final String INTENT_PAY_MONEY = "payMoney";
    public static final String INTENT_PAY_ORDER_ID = "payOrderId";
    public static final String INTENT_PAY_ORDER_CODE = "payOrderCode";
    public static final String INTENT_PAY_ORDER_TYPE = "payOrderType";

    public static final String INTENT_SUBCLASS = "subscripteclass";
    public static final String LOCATION_ISCITYSTOP = "iscitystop";//选择到城市停止

    public static final int PAY_ORDER_TYPE_MALL = 1;//商城订单
    public static final int PAY_ORDER_TYPE_RECHARGE = 2;//充值
    public static final int PAY_ORDER_TYPE_RESUME = 3;//购买简历
    public static final int PAY_ORDER_TYPE_BOND_STORE = 4;//店铺保证金
    public static final int PAY_ORDER_TYPE_BOND_INSTALL = 5;//安装保证金
    public static final int PAY_ORDER_TYPE_BOND_SERVICE = 6;//服务保证金
    public static final int PAY_ORDER_TYPE_BOND_BID = 7;//招标保证金
    public static final int PAY_ORDER_TYPE_ORDER_NEED = 8;//需求订单
    public static final int PAY_ORDER_TYPE_ORDER_SERVICE = 9;//服务订单
    public static final int PAY_ORDER_TYPE_BOND_REQ_BID = 10;//招标--报价--保证金

    //认证状态
    public static final String APPLY_TYPE_PERSON_NO = "0" ;
    public static final String APPLY_TYPE_PERSON = "1" ;
    public static final String APPLY_TYPE_FACTORY = "2" ;
    public static final String APPLY_TYPE_COMPANY = "3" ;
    public static final String APPLY_TYPE_INSTALL = "4" ;
    public static final String APPLY_TYPE_SERVER = "5" ;
    public static final String APPLY_TYPE_STORE = "6" ;
    public static final String APPLY_TYPE_CAR = "7" ;//有货车

    //售后服务状态
    public static final String AFTER_SERVER_TYPE_MONEY = "1" ;//仅退款
    public static final String AFTER_SERVER_TYPE_GOOD = "2" ;//退货退款



    public static final String INTENT_USER_ID = "userId";
    public static final String INTENT_USER_HX_ID = "userHxId";
    public static final String INTENT_USER_INFO = "userInfo";

    public static final String INTENT_ARTICLE_ID = "articleID";
    public static final String INTENT_ARTICLE_IS_TOPIC = "articleIsTopic";
    public static final String INTENT_JOB_ID = "jobID";
    public static final String INTENT_JOB_TYPE = "jobType";
    public static final String INTENT_JOB_POST = "jobPost";
    public static final String INTENT_WORK_EXP = "workExp";

    public static final String INTENT_RESUME_INFO = "resumeInfo";
    public static final String INTENT_RESUME_ID = "resumeId";
    public static final String INTENT_RESUME_TYPE = "resumeType";

    public static final String INTENT_NEEDS_OFFER_ID = "needsOfferId";
    public static final String INTENT_NEEDS_ID = "needsId";
    public static final String INTENT_NEEDS_TYPE = "needsType";
    public static final String INTENT_NEEDS_CATEGORY_INFO = "needsCategoryInfo";
    public static final String INTENT_NEEDS_CATEGORY_TYPE = "needsCategoryType";
    public static final String INTENT_NEEDS_IS_SELF = "isSelfNeed";
    public static final String INTENT_NEEDS_IS_BONDED = "isNeedBonded";
    public static final String INTENT_NEEDS_IS_CHOOSE = "isNeedChooseThis";

    public static final String INTENT_SEARCH_KEY = "searchKey";
    public static final String INTENT_SEARCH_FROM = "searchFrom";
    public static final String SEARCH_FROM_INDEX = "searchFromIndex";
    public static final String INTENT_FOLLOWTYPE = "followandfuns";


    public static final String INTENT_CATEGOTY_ID = "categoryId";
    public static final String INTENT_CATEGOTY_NAME = "categoryName";
    public static final String INTENT_CATEGOTY_CHILD_ID = "categoryChildId";
    public static final String INTENT_CATEGOTY_CHILD_NAME = "categoryChildName";

    public static final String INTENT_ADDRESS_ID = "addressId";
    public static final String INTENT_ADDRESS_INFO = "addressInfo";
    public static final String INTENT_GOOD_SEND_NAME = "goodSendName";
    public static final String INTENT_GOOD_SEND_PHONE = "goodSendPhone";
    public static final String INTENT_RECEIVER_NAME = "receiverName";
    public static final String INTENT_RECEIVER_PHONE = "receiverPhone";

    public static final String INTENT_WELFARE_INFO = "welfareInfo";

    //字典项相关
    public static final String INTENT_EDIT_STATE = "editState";
    public static final String INTENT_WORD_TYPE_MORE = "wordTypeMoreCheck";
    public static final String INTENT_WORD_TYPE_CODE = "wordType";
    public static final String INTENT_WORD_TYPE_INFO = "wordTypeInfo";
    public static final String INTENT_TITLE = "titleStr";
    public static final String WORD_TYPE_WORK_YEAR = "WorkLife";//工作年限
    public static final String WORD_TYPE_WORK_WELFARE = "WorkWelfare";//工作福利
    public static final String WORD_TYPE_EDUCATION = "Education";//学历
    public static final String WORD_TYPE_SERVER_PROMISE = "ServerPromise";//服务承诺
    public static final String WORD_TYPE_AFTER_SALE_SERVICE = "AFServerType";//售后服务类型
    public static final String WORD_TYPE_AFTER_SALE_REASON = "RefundsReason";//售后退款原因
    public static final String WORD_TYPE_DELIVERY_TYPE = "DeliveryType";//配送方式
    public static final String WORD_TYPE_PERSON_WORK = "DeliveryType";//个人资料--职位

    public static final String INTENT_SERVER_ORDER_INDEX = "serverOrderIndex";
    public static final String INTENT_SERVER_TYPE = "serverType";
    public static final String INTENT_SERVER_IS_INSTALL = "serverIsInstall";
    public static final String INTENT_SERVER_IS_MORE = "serverIsMore";
    public static final String INTENT_SERVER_ID = "serverId";
    public static final String INTENT_SERVER_USER_ID = "serverUserId";

    public static final String INTENT_CATEGORY_INFO = "categoryInfo";

    public static final String INTENT_VP_INDEX = "viewPagerIndex";
    public static final String INTENT_ORDER_TYPE = "orderType";

    public static final String INTENT_QUESTION_ID = "questionId";
    public static final String INTENT_QUESTION_CATEGORY_INFO = "questionCategoryInfo";
    public static final String INTENT_QUESTION_USER_ID = "questionUserId";

    public static final String INTENT_UNUSE_ID = "unuseId";
    public static final String INTENT_UNUSE_TYPE_SPEC = "isUnuseSpec";
    public static final String INTENT_UNUSE_CATEGORY_IS_MORE = "unuseIsMore";
    public static final String INTENT_UNUSE_CATEGORY_IS_CHILD = "unuseIsHasChild";
    public static final String INTENT_UNUSE_CATEGORY_INFO = "unuseCategoryInfo";
    public static final String INTENT_GOOD_ORDER_INDEX = "goodOrderIndex";
    public static final String INTENT_GOOD_FROM_STORE = "isFromStore";
    public static final String INTENT_GOOD_ID = "goodId";
    public static final String INTENT_GOOD_IMAGE = "goodImageUrl";
    public static final String INTENT_GOOD_SKU_ID = "goodSkuId";
    public static final String INTENT_GOOD_QUANTITY = "goodQuantity";
    public static final String INTENT_IS_UNUSE = "isUnuse";
    public static final String INTENT_IS_CART = "isCart";
    public static final String INTENT_CART_ID = "cartId";

    public static final String INTENT_STORE_ID = "storeId";
    public static final String INTENT_STORE_INFO = "storeInfo";

    public static final String INTENT_ORDER_ID = "orderId";
    public static final String INTENT_ORDER_AFTER_SALE_ID = "orderAfterSaleId";
    public static final String INTENT_ORDER_GOOD_INFO = "orderGoodInfo";

    //我的收藏 1.服务 2.安装 3.需求 4.资讯 5.招聘 6.闲置 7.商品（直供）
    public static final int COLLECTION_TYPE_SERVICE = 1;//服务
    public static final int COLLECTION_TYPE_INSTALL = 2;//安装
    public static final int COLLECTION_TYPE_NEEDS = 3;//需求
    public static final int COLLECTION_TYPE_NEWS = 4;//资讯
    public static final int COLLECTION_TYPE_JOBS = 5;//招聘
    public static final int COLLECTION_TYPE_USED = 6;//闲置
    public static final int COLLECTION_TYPE_GOOD = 7;//商品（直供）


    public static final int APPLY_ACCESS_STATE = 5;//认证通过的状态
    public static final int APPLY_PAY_BOND_STATE = 4;//待缴纳保证金的状态

    //定位相关切换
    public static final String LOCATION_TYPE = "locationType";
    public static final String LOCATION_CONTENT_ID = "locationId";
    public static final String LOCATION_CONTENT_NAME = "locationContent";
    public static final int LOCATION_TYPE_ALL = 0;//全国
    public static final int LOCATION_TYPE_PROVINCE = 1;//省---注意：type最好跟接口要求一致，省去计算
    public static final int LOCATION_TYPE_CITY = 2;//市
    public static final int LOCATION_TYPE_AREA = 3;//区

    //支付密码
    public static final int PAY_PASSWORD_RIGHT = 0x7f000000;//密码通过
    public static final int PAY_PASSWORD_RECALL = 0x7f000001;//找回密码

    //订单相关
    public static final String ORDER_TYPE_GOOD = "1" ;
    public static final String ORDER_TYPE_UNUSE = "2" ;
    public static final String ORDER_TYPE_SERVER = "3" ;

    @Deprecated
    public static final String ORDER_TYPE_NEED = "6" ;//需求订单不存在了


    //需求类型相关
    public static final int NEED_STYLE_TYPE_GOOD = 1 ;
    public static final int NEED_STYLE_TYPE_UNUSE = 2 ;
    public static final int NEED_STYLE_TYPE_INSTALL = 3 ;
    public static final int NEED_STYLE_TYPE_SERVER = 4 ;


    public static void toApplyActivity(Context context) {
        Intent intent = new Intent(context, MyApplyInfoActivity.class);
        context.startActivity(intent);

    }

    public static void toNewsDetailActivity(Context context, String articleID) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("articleID", articleID);
        context.startActivity(intent);
    }
    public static void toNewsDetailVideoActivity(Context context, String articleID, String videoPath) {
        Intent intent = new Intent(context, NewsDetailVideoActivity.class);
        intent.putExtra("articleID", articleID);
        intent.putExtra("videoPath", videoPath);
        context.startActivity(intent);
    }


    public static void toStoreIndexActivity(Context context, String storeId) {
        Intent toOtIt = new Intent(context, StoreIndexActivity.class);
        toOtIt.putExtra(SkipUtils.INTENT_STORE_ID, storeId);
        context.startActivity(toOtIt);
    }

    public static void toOtherUserInfoActivity(Context context, String userId) {
        toOtherUserInfoActivity(context ,userId ,false) ;
    }

    public static void toOtherUserInfoActivity(Context context, String userId,boolean isHx) {
        Intent toOtIt = new Intent(context, UserIndexActivity.class);
        toOtIt.putExtra(isHx ? SkipUtils.INTENT_USER_HX_ID : SkipUtils.INTENT_USER_ID, userId);
        context.startActivity(toOtIt);
    }

    public static void toShowWebActivity(Context context, String title, String hrefUrl) {
        Intent toWbIt = new Intent(context, ActivityShowWeb.class);
        toWbIt.putExtra(SkipUtils.INTENT_WEB_TITLE, title);
        toWbIt.putExtra(SkipUtils.INTENT_WEB_HREF, hrefUrl);
        context.startActivity(toWbIt);
    }

    public static void toShowWebShareActivity(Context context, String title, String hrefUrl, String thumb) {
        Intent toWbIt = new Intent(context, ActivityShowWeb.class);
        toWbIt.putExtra(SkipUtils.INTENT_WEB_TITLE, title);
        toWbIt.putExtra(SkipUtils.INTENT_WEB_HREF, hrefUrl);
        toWbIt.putExtra(SkipUtils.INTENT_WEB_SHARE, true);
        toWbIt.putExtra(SkipUtils.INTENT_WEB_SHARE_IMAGE, thumb);
        context.startActivity(toWbIt);
    }

    /**
     * 跳转到投诉界面
     * @param context context
     * @param bundle bundle
     */
    public static void toRequestActivity(Context context , Bundle bundle){

        //2018-07-04 17:50:28 暂时跳转到反馈界面
        Intent toRiIt = new Intent(context , FeedBackActivity.class) ;
        context.startActivity(toRiIt) ;

    }

    /********************商品列表相关**********************/

    /**
     * 跳转到商品搜索结果
     * @param context context
     * @param key key
     * @param storeId storeId
     */
    public static void toSearchGoodList(Context context ,String key,String storeId){
        Intent toLiIt = new Intent(context , SearchGoodListActivity.class) ;
        toLiIt.putExtra(SkipUtils.INTENT_STORE_ID,storeId) ;
        toLiIt.putExtra(SkipUtils.INTENT_SEARCH_KEY,key) ;
        context.startActivity(toLiIt) ;
    }

    public static void toSearchGoodList(Context context ,String key){
        toSearchGoodList(context ,key,"") ;
    }


    public static void toNomalGoodList(Context context ,String parentCateId,String parentCateName
            ,String childCateName,String childCateId,String mStoreId){
        Intent toLiIt = new Intent(context , CategoryGoodListActivity.class) ;
        toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID,parentCateId) ;
        toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_NAME,parentCateName) ;
        toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_CHILD_NAME,childCateName) ;
        toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_CHILD_ID,childCateId) ;
        toLiIt.putExtra(SkipUtils.INTENT_STORE_ID,mStoreId) ;
        context.startActivity(toLiIt) ;
    }

    public static void toNomalGoodList(Context context ,String parentCateId,String parentCateName,String storeId){
        toNomalGoodList(context ,parentCateId ,parentCateName ,"" ,"" ,storeId) ;
    }

    public static void toNomalGoodList(Context context ,String parentCateId,String parentCateName){
        toNomalGoodList(context ,parentCateId ,parentCateName ,"" ,"" ,"") ;
    }




    /*********************商品列表相关********************/



    public static void toLoginActivity(Context context) {

        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("isGoMain", false);
        context.startActivity(intent);

    }

    public static void toLoginActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("isGoMain", false);
        context.startActivityForResult(intent, requestCode);
    }

    public static void toLoginActivityForResult(Fragment context, int requestCode) {
        Intent intent = new Intent(context.getContext(), LoginActivity.class);
        intent.putExtra("isGoMain", false);
        context.startActivityForResult(intent, requestCode);
    }


    public static void toSendCircleActivity(Context context) {
        Intent intent = new Intent(context, CircleSendActivity.class);
        context.startActivity(intent);
    }

    public static void toSendCircleActivity(Context context,String categoryId,String categoryName) {
        Intent intent = new Intent(context, CircleSendActivity.class);
        intent.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,categoryId) ;
        intent.putExtra(SkipUtils.INTENT_CATEGOTY_NAME ,categoryName) ;
        context.startActivity(intent);

    }

    public static void toSendCircleActivity(Activity context, int requestCode) {
        Intent intent = new Intent(context, CircleSendActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void toSendCircleActivity(Activity context,String categoryId,String categoryName , int requestCode) {
        Intent intent = new Intent(context, CircleSendActivity.class);
        intent.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,categoryId) ;
        intent.putExtra(SkipUtils.INTENT_CATEGOTY_NAME ,categoryName) ;
        context.startActivityForResult(intent, requestCode);
    }

    public static void toSearchNomalActivity(Activity context,String mSearchKey, int requestCode) {
        Intent toShIt = new Intent(context ,SearchNomalActivity.class) ;
        toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
        context.startActivityForResult(toShIt , requestCode) ;
    }

    public static void toJobDetailActivity(Context context, String jobId) {

        Intent intent = new Intent(context, JobDetailActivity.class);
        intent.putExtra(SkipUtils.INTENT_JOB_ID, jobId);
        context.startActivity(intent);

    }

    public static void toGoodDetailsActivity(Context context, String goodId, String skuId,boolean isFromStore) {
        Intent intent = new Intent(context, GoodDetailActivity.class);
        intent.putExtra(SkipUtils.INTENT_GOOD_ID, goodId);
        intent.putExtra(SkipUtils.INTENT_GOOD_SKU_ID, skuId);
        intent.putExtra(SkipUtils.INTENT_GOOD_FROM_STORE, isFromStore);
        context.startActivity(intent);
    }

    public static void toGoodDetailsActivity(Context context, String goodId, String skuId) {
        toGoodDetailsActivity(context,goodId ,skuId ,false) ;
    }
    public static void toGoodDetailsActivity(Context context, String goodId,boolean isFromStore) {
        toGoodDetailsActivity(context,goodId ,null ,isFromStore) ;
    }
    public static void toGoodDetailsActivity(Context context, String goodId) {
        toGoodDetailsActivity(context,goodId ,null ,false) ;
    }

    public static void toImageShowActivityWithThumb(Activity mContext, List<ImageThumb> thumbImageList, int position) {
        ArrayList<String> imageList = new ArrayList<>();
        for (ImageThumb thumb : thumbImageList) {
            imageList.add(thumb.getOriginalImgUrl());
        }

        Intent intentToImgShow = new Intent(mContext, MyMultiImgShowActivity.class);
        intentToImgShow.putStringArrayListExtra("photos", imageList);
        intentToImgShow.putExtra("position", position);
        mContext.startActivity(intentToImgShow);
        mContext.overridePendingTransition(R.anim.zoom_in, 0);
    }

    public static void toImageShowActivity(Activity mContext, String localImage, int position) {
        List<String> localImageList = new ArrayList<>();
        localImageList.add(localImage);
        Intent intentToImgShow = new Intent(mContext, MyMultiImgShowActivity.class);
        intentToImgShow.putStringArrayListExtra("photos", (ArrayList<String>) localImageList);
        intentToImgShow.putExtra("position", position);
        mContext.startActivity(intentToImgShow);
        mContext.overridePendingTransition(R.anim.zoom_in, 0);
    }

    public static void toImageShowActivity(Activity mContext, List<String> localImageList, int position) {
        Intent intentToImgShow = new Intent(mContext, MyMultiImgShowActivity.class);
        intentToImgShow.putStringArrayListExtra("photos", (ArrayList<String>) localImageList);
        intentToImgShow.putExtra("position", position);
        mContext.startActivity(intentToImgShow);
        mContext.overridePendingTransition(R.anim.zoom_in, 0);
    }

    /**
     * 根据CODE选择相关的item项，用于 Urls.GET_TYPE_BY_CODE 接口
     * @param context ac
     * @param code code
     * @param requestCode requestCode
     */
    public static void toWordTypeActivity(Activity context, String code, int requestCode) {
        toWordTypeMoreActivity(context, code, false, null, requestCode);
    }

    /**
     * 根据CODE选择相关的item项，用于 Urls.GET_TYPE_BY_CODE 接口
     * @param context ac
     * @param code code
     * @param selectedData selectedData
     * @param requestCode requestCode
     */
    public static void toWordTypeActivity(Activity context, String code, ArrayList<WordType> selectedData, int requestCode) {
        toWordTypeMoreActivity(context, code, false, selectedData, requestCode);
    }

    /**
     * 根据CODE选择相关的item项，用于 Urls.GET_TYPE_BY_CODE 接口
     * @param context ac
     * @param code code
     * @param requestCode requestCode
     */
    public static void toWordTypeMoreActivity(Activity context
            , String code, ArrayList<WordType> selectedData
            , int requestCode) {
        toWordTypeMoreActivity(context, code, true, selectedData, requestCode);
    }

    /**
     * 根据CODE选择相关的item项，用于 Urls.GET_TYPE_BY_CODE 接口
     * @param context ac
     * @param code code
     * @param isMoreCheck isMoreCheck
     * @param requestCode requestCode
     */
    public static void toWordTypeMoreActivity(Activity context
            , String code, boolean isMoreCheck, ArrayList<WordType> selectedData
            , int requestCode) {

        String title = "请选择";
        if (SkipUtils.WORD_TYPE_SERVER_PROMISE.equals(code)) {
            title = "服务承诺";
        } else if (SkipUtils.WORD_TYPE_WORK_YEAR.equals(code)) {
            title = "工作年限";
        } else if (SkipUtils.WORD_TYPE_EDUCATION.equals(code)) {
            title = "学历";
        }

        Intent intent = new Intent(context, GetTypeByCodeActivity.class);
        intent.putExtra(SkipUtils.INTENT_TITLE, title);
        intent.putExtra(SkipUtils.INTENT_WORD_TYPE_CODE, code);
        intent.putExtra(SkipUtils.INTENT_WORD_TYPE_MORE, isMoreCheck);
        if (selectedData != null) {
            intent.putExtra(SkipUtils.INTENT_WORD_TYPE_INFO, selectedData);
        }
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到 订单售后界面
     * @param context context
     * @param orderType 1 直供 2闲置 3服务 ；
     * @param orderId id
     * @param itemId itemId
     * @param name name
     * @param img img
     * @param amount money
     * @param isReceive true 是买的订单 ；false 是卖的订单
     */
    public static void toOrderAfterSaleActivity(Context context,String orderType , String orderId , String itemId
            , String name , String img, String amount,boolean isReceive){
        toOrderAfterSaleActivity(context,true,null ,orderType ,orderId ,itemId,name ,img ,amount,isReceive);
    }

    /**
     * 跳转到 订单售后界面
     * @param context context
     * @param isEdit isEdit
     * @param afterSaleId afterSaleId
     */
    public static void toOrderAfterSaleActivity(Context context,boolean isEdit  , String afterSaleId,boolean isReceive){
        toOrderAfterSaleActivity(context,isEdit,afterSaleId ,null ,null ,null,null ,null ,null,isReceive);
    }

    /**
     * 跳转到 订单售后界面
     */
    public static void toOrderAfterSaleActivity(Context context,boolean isEdit , String afterSaleId, String orderType , String orderId , String itemId
            , String name , String img, String amount,boolean isReceive){
        MyOrderAfterSaleActivity.launch(context,isEdit,afterSaleId ,orderType ,orderId ,itemId,name ,img ,amount,isReceive);
    }

    /**
     * 跳转到付款界面
     * @param context con
     * @param money money
     * @param orderId orderId
     * @param orderCode code
     * @param orderType type
     */
    public static void toPayBondActivity(Context context, String money, String orderId
            , String orderCode, int orderType) {
        Intent toPyIt = new Intent(context, ActivityBondPay.class);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY, "" + money);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID, orderId);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE, orderCode);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE, "" + orderType);
        context.startActivity(toPyIt);
    }

    /**
     * 跳转到付款界面
     * @param context con
     * @param requestCode requestCode
     * @param money money
     * @param orderId orderId
     * @param orderCode code
     * @param orderType type
     */
    public static void toPayBondActivity(Activity context, int requestCode, String money, String orderId
            , String orderCode, int orderType) {
        Intent toPyIt = new Intent(context, ActivityBondPay.class);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY, "" + money);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID, orderId);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE, orderCode);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE, "" + orderType);
        context.startActivityForResult(toPyIt, requestCode);
    }

    /**
     * 跳转到付款界面
     * @param fragment con
     * @param requestCode requestCode
     * @param money money
     * @param orderId orderId
     * @param orderCode code
     * @param orderType type
     */
    public static void toPayBondActivity(Fragment fragment, int requestCode, String money, String orderId
            , String orderCode, int orderType) {
        Intent toPyIt = new Intent(fragment.getContext(), ActivityBondPay.class);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY, "" + money);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID, orderId);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE, orderCode);
        toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE, "" + orderType);
        fragment.startActivityForResult(toPyIt, requestCode);
    }


    /**
     * 根据广告位跳转到指定界面
     * @param context context
     * @param advInfo info
     */
    public static void intentToOtherByAdvId(Context context, AdvBanner advInfo) {
        if (advInfo != null) {
            boolean isInner = advInfo.isInnerResouce();

            if (isInner) {
                String articleId = advInfo.getLinkInResourceID();
                toNewsDetailActivity(context, articleId);
            } else {
                String title = advInfo.getTitle();
                String outUrl = advInfo.getLinkOutResourceUrl();
                toShowWebActivity(context, title, outUrl);
            }
        }
    }

    /**
     * 跳转到消息列表
     * @param context con
     */
    public static void toMessageCenterActivity(Context context) {
        Intent toMsIt = new Intent(context, MessageCenterActivity.class);
        context.startActivity(toMsIt);
    }

    /**
     * 跳转到聊天界面
     * @param activity ac
     * @param userHxId hxid
     */
    public static void toMessageChatActivity(Activity activity, String userHxId) {
        Intent toCtIt = new Intent(activity, MessageChatActivity.class);
        toCtIt.putExtra(SkipUtils.INTENT_USER_HX_ID, userHxId);
        activity.startActivity(toCtIt);
    }


    /**
     * 对androidN支持
     * @param context c
     * @param intent i
     * @param uri u
     */
    public static void grantUriPermissionForAndrondN(Context context, Intent intent, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent
                    , PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
    }

    /**
     * 拨打电话
     * @param context context
     * @param phone phone
     */
    public static void toCallPhoneAutoActivity(Context context, String phone) {
        //用intent启动拨打电话
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    /**
     * 拨打电话
     * @param context context
     * @param phone phone
     */
    public static void toCallPhoneDialActivity(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }


}
