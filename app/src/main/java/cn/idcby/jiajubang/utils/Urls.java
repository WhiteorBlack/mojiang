package cn.idcby.jiajubang.utils;

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
 * Created by mrrlb on 2018/2/7.
 */

public class Urls {

//    public static String BASE_URL = "http://123.56.158.181:33001";
//    public static String PHOTO_BASE_URL = "http://123.56.158.181:33002";
//    public static String BASE_URL = "http://api.jajubang.com";
//    public static String PHOTO_BASE_URL = "http://webfile.jajubang.com";
    public static String BASE_URL = "http://api.hmmojiang.com";
    public static String PHOTO_BASE_URL = "http://file.hmmojiang.com";

    //帮助中心、关于我们等通用h5功能列表集合
    public static String HELPER_PARENT_URL = BASE_URL + "/MIS/Article/CategoryList" ;
    public static String HELPER_URL = BASE_URL + "/MIS/Article/systemArticleList" ;
    //我要分享--url
    public static String MY_SHARE_URL = BASE_URL + "/MIS/Article/SharingPromotionByCode" ;

    //发送位置
    public static String UPLOAD_LOCATION = BASE_URL + "/PersonalInfo/UpdatePosition";
    //退出登录
    public static String LOGIN_OUT = BASE_URL + "/PersonalInfo/LogOff";

    //上传图片
    public static String UPLOAD_PHOTO = PHOTO_BASE_URL + "/UpLoadImage/Base64ImageSave";
    //获取图形验证码
    public static String GET_PHOTO_CODE = BASE_URL + "/Public/GetImage";
    //登录
    public static String LOGIN = BASE_URL + "/Account/Login";
    //注册
    public static String REGISTER = BASE_URL + "/Account/Register";
    //注册获取验证码
    public static String GET_MSG_CODE_FOR_REGISTER = BASE_URL + "/Sms/Register";
    //修改手机号获取验证码
    public static String GET_MSG_CODE_FOR_CHANGE_PHONE = BASE_URL + "/Sms/UpdateAccount";
    //修改登录密码获取验证码
    public static String GET_MSG_CODE_FOR_CHANGE_LOGIN = BASE_URL + "/Sms/UpdatePassword";
    //修改支付密码获取验证码
    public static String GET_MSG_CODE_FOR_CHANGE_PAY = BASE_URL + "/Sms/UpdatePayPassWord";
    //修改登录帐号
    public static String CHANGE_LOGIN_NUMBER = BASE_URL + "/PersonalInfo/UpdateAccount";
    //修改登录密码
    public static String CHANGE_LOGIN_PASSWORD = BASE_URL + "/Account/UpdatePassword";
    //修改支付密码
    public static String CHANGE_PAY_PASSWORD = BASE_URL + "/PersonalInfo/UpdatePayPassword";
//    //修改支付密码
//    public static String CHANGE_PAY_PASSWORD = BASE_URL + "/Api/Account/ForgetPassWord";

    //充值
    public static String USER_RECHARGE = BASE_URL + "/PersonalInfo/AddRechargeOrder" ;
    //余额提现
    public static String USER_WITHDRAW = BASE_URL + "/PersonalInfo/Withdrawals" ;




    //轮播图片
    public static String SEE_BANNER = BASE_URL + "/Api/Article/AlbumsSlider?code=IndexBanner";
    //6.3.	文章列表
    public static String NEWS_LIST = BASE_URL + "/MIS/Article/ArticleList";
    //6.3.	文章列表--推荐
    public static String NEWS_LIST_HOT = BASE_URL + "/MIS/Article/tjArticleList";
    //6.2.	文章分类
    public static String NEWS_CATEGORY_LIST = BASE_URL + "/MIS/Article/CategoryList";
    //6.1.	文章推荐列表
    public static String NEWS_RECOMMEND_LIST = BASE_URL + "/MIS/Article/IndexRecommendArticleList";
    //5.2.	个人认证信息读取
    public static String GET_PERSON_APPLY_INFO = BASE_URL + "/User/PersonalAuthentication/Get";
    //5.1.	个人认证申请
    public static String PERSON_APPLY = BASE_URL + "/User/PersonalAuthentication/Add";
    //5.1.	用户认证状态读取
    public static String GET_USER_APPLY_INFO = BASE_URL + "/PersonalInfo/GetAuthenticationInfo";


    //1.3.	根据字典分组编码获取字典项
    public static String GET_ENTIRY_BY_CODE = BASE_URL + "/Public/DictEntity";
    //1.3.	根据字典分组编码获取字典项
    public static String GET_TYPE_BY_CODE = BASE_URL + "/Public/DictList";
    //1.4.	获取省列表
    public static String GET_PROVINCE = BASE_URL + "/Public/ProvinceList/";
    //1.5.	根据省获取市列表
    public static String GET_CITY = BASE_URL + "/Public/CityList/";
    //1.6.	根据市获取区县列表
    public static String GET_AREA = BASE_URL + "/Public/CountyList/";


    //5.14.	公司认证申请
    public static String COMPANY_APPLY = BASE_URL + "/User/CompanyAuthentication/Add";
    //5.15.	公司认证信息读取
    public static String GET_COMPANY_APPLY_INFO = BASE_URL + "/User/CompanyAuthentication/Get";
    //5.12.	工厂认证申请
    public static String FACTORY_APPLY = BASE_URL + "/User/FactoryAuthentication/Add";
    //5.15.	公司认证信息读取
    public static String GET_FACTORY_APPLY_INFO = BASE_URL + "/User/FactoryAuthentication/Get";
    public static String SERVER_APPLY = BASE_URL + "/User/ServiceAuthentication/Add";
    public static String GET_SERVER_APPLY_INFO = BASE_URL + "/User/ServiceAuthentication/Get";
    public static String INSTALL_APPLY = BASE_URL + "/User/InstallAuthentication/Add";
    public static String GET_INSTALL_APPLY_INFO = BASE_URL + "/User/InstallAuthentication/Get";
    //店铺认证
    public static String STORE_APPLY = BASE_URL + "/User/ShopAuthentication/Add";
    //店铺认证--信息
    public static String STORE_APPLY_INFO = BASE_URL + "/User/ShopAuthentication/Get";

    //获取其他人认证信息
    public static String STORE_APPLY_INFO_OTHER = BASE_URL + "/Home/GetShopAuthentication";
    public static String FACTORY_APPLY_INFO_OTHER = BASE_URL + "/Home/GetFactoryAuthentication";
    public static String COMPANY_APPLY_INFO_OTHER = BASE_URL + "/Home/GetCompanyAuthentication";
    public static String INSTALL_APPLY_INFO_OTHER = BASE_URL + "/Home/GetInstallAuthentication";
    public static String SERVER_APPLY_INFO_OTHER = BASE_URL + "/Home/GetServiceAuthentication";
    public static String INDUSTRY_APPLY_INFO_OTHER = BASE_URL + "/Home/GetIndustryVAuthentication";
    public static String MASTER_APPLY_INFO_OTHER = BASE_URL + "/Home/GetQAMasterAuthentication";



    //文章详细
    public static String ARTICLE_DETAIL = BASE_URL + "/MIS/Article/ArticleDetail";
    //文章详细--各个协议的contentUrl
//    public static String ARTICLE_DETAIL_CONTENT_H5 = BASE_URL + "/MIS/Article/ContentDetail";
    public static String ARTICLE_DETAIL_BY_CODE = BASE_URL + "/MIS/Article/ArticleDetailByCode";
    //文章评论列表
    public static String ARTICLE_COMMENT_LIST = BASE_URL + "/MIS/Article/ArticleCommentList";
    public static String ADD_COMMENT_ARTICLE_DETAIL = BASE_URL + "/MIS/Article/AddComment";
    public static String ARTICLE_ADMIRE = BASE_URL + "/MIS/Article/AddOrDeleteLike";
    public static String ARTICLE_COLLECT = BASE_URL + "/MIS/Article/AddOrDeleteCollection";

    //广告位
    public static String API_ADVERT = BASE_URL + "/Advert/GetItemsList";

    //首页--热门话题
    public static String API_HOME_HOT_QUESTION = BASE_URL + "/Home/HotPostList";
    //首页--最新需求
    public static String API_HOME_LATEST_NEEDS = BASE_URL + "/Home/NeedsList";
    //首页--热门服务
    public static String API_HOME_HOT_SERVICE = BASE_URL + "/Home/SpecialServiceList";
    //首页--安装大师
    public static String API_HOME_HOT_INSTALL = BASE_URL + "/Home/SpecialInstallServiceList";
    //首页--求职招聘社交数量
    public static String API_HOME_WORK_RESUME_COUNT = BASE_URL + "/Home/Work_ResumeAndRecruitCount";
    //首页--行业社交
    public static String API_HOME_SOCIAL_CONTACT = BASE_URL + "/Home/SocialContact";
    //首页--推荐店铺
    public static String API_HOME_COMMENT_STORE = BASE_URL + "/Home/SpecialShopList";
    //首页--更具城市名字获取id
    public static String API_HOME_GET_CITY_ID_BY_NAME = BASE_URL + "/Home/GetAreaIdByName";
    //首页--热门搜索
    public static String API_HOME_HOT_SEARCH = BASE_URL + "/Search/Search/HotSearch";
    //首页--添加搜索历史到服务器
    public static String API_HOME_SEARCH_TO_SERVER = BASE_URL + "/Search/Search/Search";
    //首页--搜索历史
    public static String API_HOME_SEARCH_HISTORY = BASE_URL + "/Search/Search/SearchList";
    //首页--搜索历史--清除
    public static String API_HOME_SEARCH_HISTORY_CLEAR = BASE_URL + "/PersonalInfo/DeleteSearchList";

    //附近--行业服务
    public static String API_NEAR_SERVER = BASE_URL + "/Nearby/NearbyServiceList";
    //附近--安装服务
    public static String API_NEAR_SERVER_INSTALL = BASE_URL + "/Nearby/NearbyInstallServiceList";
    //附近--工作
    public static String API_NEAR_WORK_LIST = BASE_URL + "/Nearby/NearbyWorkList";
    //附近--店铺
    public static String API_NEAR_SHOP_LIST = BASE_URL + "/Nearby/ShopList";
    //附近--附近的人
    public static String API_NEAR_USER = BASE_URL + "/Nearby/NearbyList";

    //话题列表
    public static String TOPIC_LIST = BASE_URL + "/BBS/Post/TopicList" ;
    //行业大咖
    public static String USER_FOCUS_LIST = BASE_URL + "/BBS/Post/UserVList" ;
    //帖子列表（关注的）
    public static String FOLLOW_USER_POST_LIST = BASE_URL + "/BBS/Post/FollowUserPostList" ;
    //帖子分类
    public static String CIRCLE_CATEGORY_LIST = BASE_URL + "/BBS/Post/CategoryList" ;
    //热门帖子列表
    public static String HOT_POST_LIST = BASE_URL + "/BBS/Post/HotPostList" ;
    //同城帖子列表
    public static String SAME_CITY_POST_LIST = BASE_URL + "/BBS/Post/LocalPostList" ;
    //发帖
    public static String SEND_CIRCLE = BASE_URL + "/BBS/PostUser/Add" ;
    //发帖
    public static String SEND_CIRCLE_TRANSPORT = BASE_URL + "/BBS/PostUser/AddReprint" ;
    //帖子详细
    public static String CIRCLE_DETAILS = BASE_URL + "/BBS/Post/Detail" ;
    //话题详细
    public static String CIRCLE_TOPIC_DETAILS = BASE_URL + "/BBS/Post/TopicDetail" ;
    //帖子评论
    public static String CIRCLE_COMMENT_LIST = BASE_URL + "/BBS/Post/CommentList" ;
    //圈子点赞
    public static String CIRCLE_SUPPORT = BASE_URL + "/BBS/PostUser/AddOrDeleteLike" ;
    //圈子收藏
    public static String CIRCLE_COLLECT = BASE_URL + "/BBS/PostUser/AddOrDeleteCollection" ;
    //圈子回复
    public static String CIRCLE_COMMENT_ADD = BASE_URL + "/BBS/PostUser/AddComment" ;
    //圈子--我发布的--删除
    public static String CIRCLE_MY_DELETE = BASE_URL + "/PersonalInfo/DeletePost" ;

    //关注、取消关注用户（店铺）
    public static String FOCUS_OR_CANCEL_USER = BASE_URL + "/User/UserFollow/AddOrDeleteFollow" ;

    //招聘公司模版
    public static String JOB_COM_NOMAL_INFO = BASE_URL + "/PersonalInfo/GetRecruitCompany" ;
    //招聘公司模版
    public static String JOB_COM_NOMAL_INFO_EDIT = BASE_URL + "/PersonalInfo/ManageRecruitCompany" ;



    //职位列表
    public static String JOB_POST_LIST = BASE_URL + "/Work/WorkPost/WorkPostList" ;
    //推荐企业列表
    public static String RECOMMEND_COMPANY_LIST = BASE_URL + "/Work/WorkRecruit/RecruitSpecialItemList" ;
    //推荐职位列表
    public static String RECOMMEND_JOBS_LIST = BASE_URL + "/Work/WorkPost/SpecialItemList" ;
    //招聘列表
    public static String JOBS_LIST = BASE_URL + "/Work/WorkRecruit/RecruitList" ;
    //招聘列表--推荐职位下的--2018-09-15 10:19:15 跟JOBS_LIST 合并 该接口去掉
    @Deprecated
    public static String JOBS_LIST_BY_CATEGORY = BASE_URL + "/Work/WorkRecruit/SpecialItemRecruit" ;
    //职位详细
    public static String JOBS_DETAILS = BASE_URL + "/Work/WorkRecruit/RecruitDetail" ;
    //职位---删除
    public static String JOBS_DELETE = BASE_URL + "/Work/WorkToken/DeleteRecruit" ;
    //职位点赞
    public static String JOBS_SUPPORT = BASE_URL + "/Work/WorkToken/AddOrDeleteLikeRecruit" ;
    //职位收藏
    public static String JOBS_COLLECTION = BASE_URL + "/Work/WorkToken/AddOrDeleteCollectionRecruit" ;
    //职位发布--发布招聘
    public static String JOBS_CREATE = BASE_URL + "/Work/WorkToken/ReleaseRecruitment" ;
    //简历列表
    public static String RESUME_LIST = BASE_URL + "/Work/WorkResume/ResumeList" ;
    //发布招聘下的简历列表
    public static String RESUME_LIST_JOBS = BASE_URL + "/Work/WorkRecruit/RecruitResumeList" ;
    //简历详细
    public static String RESUME_DETAILS = BASE_URL + "/Work/WorkResume/ResumeDetail" ;
    //新增简历
    public static String RESUME_CREATE = BASE_URL + "/Work/WorkToken/Add" ;
    //简历点赞
    public static String RESUME_SUPPORT = BASE_URL + "/Work/WorkToken/AddOrDeleteLike" ;
    //简历收藏
    public static String RESUME_COLLECTION = BASE_URL + "/Work/WorkToken/AddOrDeleteCollection" ;
    //简历投递
    public static String RESUME_SEND = BASE_URL + "/Work/WorkToken/DeliveryResumes" ;
    //收藏的职位列表
    public static String JOB_COLLECTION_LIST = BASE_URL + "/Work/WorkToken/WorkRecruitCollectionList" ;
    //收藏的简历列表
    public static String RESUME_COLLECTION_LIST = BASE_URL + "/Work/WorkToken/WorkCollectionList" ;
    //简历联系TA
    public static String RESUME_CONTACT_INFO = BASE_URL + "/Work/WorkToken/ContactTA" ;
    //简历购买
    public static String RESUME_BUY = BASE_URL + "/Work/WorkToken/AddBuyResumeOrder" ;
    //可购买简历列表
    public static String RESUME_BUY_LIST = BASE_URL + "/Work/WorkResume/ResumeSetMealList" ;
    //我发布的招聘--刷新
    public static String JOBS_REFRESH = BASE_URL + "/Work/WorkToken/RefreshRecruit" ;
    //我发布的招聘--上下架
    public static String JOBS_UPDOWN = BASE_URL + "/Work/WorkToken/UpdateEnableRecruit" ;
    //我发布的招聘--置顶
    public static String JOBS_TOTOP = BASE_URL + "/Work/WorkToken/UpdateCompanyTop" ;
    //我发布的简历--刷新
    public static String RESUME_REFRESH = BASE_URL + "/Work/WorkToken/RefreshResume" ;
    //我发布的简历--上下架
    public static String RESUME_UPDOWN = BASE_URL + "/Work/WorkToken/UpdateEnableResume" ;

    //个人信息--根据环信id获取，根据userId获取用户信息
    public static String USER_HX_INFO = BASE_URL + "/Home/GetOtherInfo" ;
    //个人信息
    public static String MY_INFO = BASE_URL + "/PersonalInfo/GetInfo" ;
    //个人信息--修改
    public static String MY_INFO_UPDATE = BASE_URL + "/PersonalInfo/ModifyInfo" ;
    //保证金信息
    public static String MY_BONDS = BASE_URL + "/UserInfo/UserBond" ;
    //保证金--退保
    public static String MY_BONDS_CANCEL = BASE_URL + "/PersonalInfo/Surrender" ;
    //我的资金明细
    public static String MY_MONEY_BILL_LIST = BASE_URL + "/PersonalInfo/CapitalDetail" ;
    //我的积分明细
    public static String MY_JIFEN_BILL_LIST = BASE_URL + "/PersonalInfo/IntegralDetail" ;

    //我的地址
    public static String MY_ADDRESS_LIST = BASE_URL + "/User/UserAddressToken/AddressList" ;
    //我的地址--详细
    public static String MY_ADDRESS_DETAILS = BASE_URL + "/User/UserAddressToken/AddressInfo" ;
    //我的地址--编辑
    public static String MY_ADDRESS_EDIT = BASE_URL + "/User/UserAddressToken/SaveAddress" ;
    //我的地址--删除
    public static String MY_ADDRESS_DELETE = BASE_URL + "/User/UserAddressToken/DeleteAddress" ;
    //我的地址--设置默认
    public static String MY_ADDRESS_DEFAULT = BASE_URL + "/User/UserAddressToken/SetAddressDefault" ;
    //我的地址--获取默认
    public static String MY_ADDRESS_DEFAULT_GET = BASE_URL + "/User/UserAddressToken/DefaultAddress" ;

    //我的简历
    public static String MY_RESUME_LIST = BASE_URL + "/Work/WorkToken/ResumeList" ;
    //我购买的简历
    public static String MY_RESUME_BUY_LIST = BASE_URL + "/Work/WorkToken/BuyResumeList" ;
    //我的简历--删除
    public static String MY_RESUME_DELETE = BASE_URL + "/Work/WorkToken/DeleteResume" ;
    //我的收藏
    public static String MY_COLLECTION_LIST_BY_TYPE = BASE_URL + "/PersonalInfo/GetMyCollection" ;

    //我的问答--问题
    public static String MY_QUESTION_LIST = BASE_URL + "/PersonalInfo/GetMyQuestion" ;
    //我的问答--问题--删除
    public static String MY_QUESTION_DELETE = BASE_URL + "/QA/QuestionToken/DeleteQuestion" ;
    //我的问答--问题--刷新
    public static String MY_QUESTION_REFRESH = BASE_URL + "/QA/QuestionToken/RefreshQuestion" ;
    //我的问答--回答
    public static String MY_QUESTION_ANSWER_LIST = BASE_URL + "/PersonalInfo/GetMyAnswer" ;

    //我的报价
    public static String MY_NEEDS_OFFER_LIST = BASE_URL + "/Need/NeedToken/MyNeedsOfferList" ;
    //我的需求订单
    public static String MY_NEEDS_ORDER_LIST = BASE_URL + "/Need/NeedToken/NeedsOrderList" ;
    //我的需求订单--详细
    public static String MY_NEEDS_ORDER_DETAILS = BASE_URL + "/Need/NeedToken/OrderDetail" ;
    //我的需求订单--取消
    public static String MY_NEEDS_ORDER_CANCEL = BASE_URL + "/Need/NeedToken/CancelNeedOrder" ;
    //我的需求订单--完成
    public static String MY_NEEDS_ORDER_FINISH = BASE_URL + "/Need/NeedToken/FinshNeedOrder" ;
    //我的发布
    public static String MY_SEND_LIST = BASE_URL + "/PersonalInfo/MyRelease" ;
    //用户发布列表
    public static String USER_SEND_LIST = BASE_URL + "/Public/OtherUserRelease" ;
    //我的订阅--订阅的栏目
    public static String MY_SUBSCRIPTION_COLUMN = BASE_URL + "/PersonalInfo/UserTakeCoulmnList" ;
    //帮助与反馈--我要反馈
    public static String MY_FEEDBACK = BASE_URL + "/Public/AddOption" ;


    //需求分类
    public static String NEEDS_CATEGORY = BASE_URL + "/Need/Needs/CategoryList" ;
    //需求发布
    public static String NEEDS_SEND = BASE_URL + "/Need/NeedToken/AddNeedInfo" ;
    //需求列表
    public static String NEEDS_LIST = BASE_URL + "/Need/Needs/NeedsList" ;
    //需求详细
    public static String NEEDS_DETAILS = BASE_URL + "/Need/Needs/Detail" ;
    //需求--刷新
    public static String NEEDS_REFRESH = BASE_URL + "/Need/NeedToken/RefreshNeed" ;
    //需求--上下架
    public static String NEEDS_UPDOWN = BASE_URL + "/Need/NeedToken/UpdateEnableNeed" ;
    //需求--删除
    public static String NEEDS_DELETE = BASE_URL + "/Need/NeedToken/DeleteNeed" ;
    //需求--完成
    public static String NEEDS_FINISH = BASE_URL + "/Need/NeedToken/FinshNeed" ;
    //需求点赞
    public static String NEEDS_SUPPORT = BASE_URL + "/Need/NeedToken/AddOrDeleteLike" ;
    //需求收藏
    public static String NEEDS_COLLECTION = BASE_URL + "/Need/NeedToken/AddOrDeleteCollection" ;
    //需求商家列表
    public static String NEEDS_SELLER_LIST = BASE_URL + "/Need/Needs/ParticBussinessList" ;
    //需求评论列表
    public static String NEEDS_COMMENT_LIST = BASE_URL + "/Need/Needs/CommentList" ;
    //需求发布评论
    public static String NEEDS_COMMENT_ADD = BASE_URL + "/Need/NeedToken/AddComment" ;
    //需求招标发布之后，缴纳保证金
    public static String NEEDS_PAY_BOND = BASE_URL + "/Need/NeedToken/PayBond" ;
    //需求发布报价--需求
    public static String NEEDS_MODIFY_BID_NEED = BASE_URL + "/Need/NeedToken/InsertNeedOffer" ;
    //需求发布报价--招标
    public static String NEEDS_MODIFY_BID = BASE_URL + "/Need/NeedToken/ModifyNeedOffer" ;
    //招标报价
    public static String NEEDS_ADD_BID = BASE_URL + "/Need/NeedToken/AddNeedOffer" ;
    //需求报价详细
    public static String NEEDS_BID_DETAILS = BASE_URL + "/Need/NeedToken/BidDetail" ;
    //需求报价选择
    public static String NEEDS_BID_COMFIRM= BASE_URL + "/Need/NeedToken/ConfirmBid" ;

    //我的招标--发起付款
    public static String NEEDS_OFFER_SEND_PAY = BASE_URL + "/Need/NeedToken/AddNeedOrder" ;


    //服务分类
    public static String SERVER_CATEGORY = BASE_URL + "/Service/Services/CategoryList" ;
    //行业服务列表--推荐
    public static String SERVER_SERVER_LIST_RECOMMEND = BASE_URL + "/Service/Services/SpecialServiceList" ;
    //安装服务列表--推荐
    public static String SERVER_INSTALL_LIST_RECOMMEND = BASE_URL + "/Service/Services/SpecialInstallServiceList" ;
    //服务列表--行业服务
    public static String SERVER_LIST_SERVER = BASE_URL + "/Service/Services/ServiceList" ;
    //服务列表--安装服务
    public static String SERVER_LIST_INSTALL = BASE_URL + "/Service/Services/InstallServiceList" ;
    //服务详细
    public static String SERVER_DETAILS = BASE_URL + "/Service/Services/ServerDetail" ;
    //服务详细--安装
    public static String SERVER_DETAILS_INSTALL = BASE_URL + "/Service/Services/InstallDetail" ;
    //服务--评论
    public static String SERVER_COMMENT_LIST = BASE_URL + "/Service/Services/EvaluateList" ;
    //服务--删除
    public static String SERVER_DELETE = BASE_URL + "/Service/ServicesToken/DeleteServiceInfo" ;
    //服务--点赞
    public static String SERVER_SUPPORT = BASE_URL + "/Service/ServicesToken/AddOrDeleteLike" ;
    //服务--收藏
    public static String SERVER_COLLECTION = BASE_URL + "/Service/ServicesToken/AddOrDeleteCollection" ;
    //服务--预约
    public static String SERVER_CONFIRM = BASE_URL + "/Service/ServicesToken/ReserService" ;
    //服务--发布
    public static String SERVER_SEND = BASE_URL + "/Service/ServicesToken/AddServiceInfo" ;
    //服务订单--行业服务--列表
    public static String SERVER_ORDER_LIST = BASE_URL + "/Service/ServicesToken/ServiceOrderList" ;
    //服务订单--安装服务--列表
    public static String SERVER_ORDER_LIST_INSTALL = BASE_URL + "/Service/ServicesToken/InstallServiceOrderList" ;
    //服务订单--我预约的服务（不区分安装和服务）--列表
    public static String SERVER_ORDER_LIST_SUBS = BASE_URL + "/Service/ServicesToken/MyBuyServiceOrderList" ;
    //服务订单--详细
    public static String SERVER_ORDER_DETAILS = BASE_URL + "/Service/ServicesToken/OrderDetail" ;
    //服务订单--开始
    public static String SERVER_ORDER_START = BASE_URL + "/Service/ServicesToken/BeginService" ;
    //服务订单--完成
    public static String SERVER_ORDER_FINISH = BASE_URL + "/Service/ServicesToken/FinishService" ;
    //服务订单--编辑
    public static String SERVER_ORDER_EDIT = BASE_URL + "/Service/ServicesToken/EditAmount" ;
    //服务订单--取消
    public static String SERVER_ORDER_CANCEL = BASE_URL + "/Service/ServicesToken/OrdersCancel" ;
    //服务订单--删除
    public static String SERVER_ORDER_DELETE = BASE_URL + "/Service/ServicesToken/DeleteOrder" ;
    //服务订单--评论
    public static String SERVER_ORDER_COMMENT = BASE_URL + "/Service/ServicesToken/ServiceOrderEvaluate" ;
    //服务--我的--相册获取
    public static String SERVER_MY_PICTURE_GET = BASE_URL + "/PersonalInfo/GetUserAlbums" ;
    //服务--我的--相册添加
    public static String SERVER_MY_PICTURE_ADD = BASE_URL + "/PersonalInfo/AddUserAlbums" ;
    //服务--我的--相册删除
    public static String SERVER_MY_PICTURE_DELETE = BASE_URL + "/PersonalInfo/DeleteUserAlbums" ;

    //bg_index_hywd--分类列表
    public static String QUESTION_CATEGORY = BASE_URL + "/QA/Question/CategoryList" ;
    //bg_index_hywd--问题列表
    public static String QUESTION_LIST = BASE_URL + "/QA/Question/QuestionList" ;
    //bg_index_hywd--问题详细
    public static String QUESTION_DETAILS = BASE_URL + "/QA/Question/QuestionDetail" ;
    //bg_index_hywd--问题回答列表
    public static String QUESTION_COMMENT_LIST = BASE_URL + "/QA/Question/AnswerList" ;
    //bg_index_hywd--问题回答
    public static String QUESTION_COMMENT_SUBMIT = BASE_URL + "/QA/QuestionToken/AddAnswer" ;
    //bg_index_hywd--问题回答--采纳回答
    public static String QUESTION_COMMENT_AGREE = BASE_URL + "/QA/QuestionToken/AdoptAnswer" ;
    //bg_index_hywd--问题回答--赞
    public static String QUESTION_COMMENT_SUPPORT = BASE_URL + "/QA/QuestionToken/AddOrDelereLikeAnswer" ;
    //bg_index_hywd--推荐行业大师
    public static String QUESTION_ANSWER_LIST_HOT = BASE_URL + "/QA/Question/QAMasterList" ;
    //bg_index_hywd--行业大师
    public static String QUESTION_ANSWER_LIST = BASE_URL + "/QA/Question/CategoryQAMasterList" ;
    //bg_index_hywd--问题列表
    public static String QUESTION_ANSWER_SEND = BASE_URL + "/QA/QuestionToken/AddQuestion" ;
    //bg_index_hywd--问答大师认证
    public static String QUESTION_ANSWER_APPLY = BASE_URL + "/User/QAMasterAuthentication/Add" ;
    //bg_index_hywd--问答大师认证--获取
    public static String QUESTION_ANSWER_APPLY_INFO = BASE_URL + "/User/QAMasterAuthentication/Get" ;
    //bg_index_hywd--行业大咖认证
    public static String INDUSTRY_V_APPLY = BASE_URL + "/User/IndustryVAuthentication/Add" ;
    //bg_index_hywd--行业大咖认证--获取
    public static String INDUSTRY_V_APPLY_INFO = BASE_URL + "/User/IndustryVAuthentication/Get" ;


    //闲置,厂家直供的分类（左右滚动的viewpager数据）
    public static String GOOD_CATEGORY_LIST = BASE_URL + "/Product/Product/CategoryList" ;
    //专场列表
    public static String UNUSE_SPEC_LIST = BASE_URL + "/Product/Product/SpecialList" ;
    //专场商品列表
    public static String UNUSE_SPEC_GOOD_LIST = BASE_URL + "/Product/Product/SpecialGoodList" ;
    //专场商品一级分类列表
    public static String UNUSE_SPEC_GOOD_CATEGORY_LIST = BASE_URL + "/Product/Product/SpecialCategoryList" ;
    //闲置--商品列表
    public static String UNUSE_GOOD_LIST = BASE_URL + "/Product/Product/GoodList" ;
    //闲置--商品列表
    public static String UNUSE_GOOD_LIST_SIMPLE = BASE_URL + "/Product/Product/CategoryOldProductList" ;
    //闲置--商品详细
    public static String UNUSE_DETAILS = BASE_URL + "/Product/Product/Detail" ;
    //闲置--删除
    public static String UNUSE_DELETE = BASE_URL + "/Product/ProductToken/DeleteProduct" ;
    //闲置--商品评论列表
    public static String UNUSE_COMMENT_LIST = BASE_URL + "/Product/Product/LeaveList" ;
    //闲置--商品评论添加
    public static String UNUSE_COMMENT_ADD = BASE_URL + "/Product/ProductToken/AddLeave" ;
    //闲置--商品点赞
    public static String UNUSE_SUPPORT = BASE_URL + "/Product/ProductToken/AddOrDeleteLike" ;
    //闲置--我的--刷新
    public static String UNUSE_REFRESH = BASE_URL + "/Product/ProductToken/RefreshOldProduct" ;
    //闲置--我的--上下架
    public static String UNUSE_UPDOWN = BASE_URL + "/Product/ProductToken/UpdateEnableOldProduct" ;

    //直供专场列表
    public static String DIRECT_SPEC_LIST = BASE_URL + "/Product/Product/SpecialSupplyList" ;
    //直供专场商品列表
    public static String DIRECT_SPEC_GOOD_LIST = BASE_URL + "/Product/Product/SpecialSupplyGoodList" ;
    //商品收藏
    public static String UNUSE_GOOD_COLLECTION = BASE_URL + "/Product/ProductToken/AddOrDeleteCollection" ;
    //直供--商品列表
    public static String UNUSE_DIRE_GOOD_LIST = BASE_URL + "/Product/Product/SupplyList" ;
    //直供--商品详细
    public static String UNUSE_GOOD_DETAILS = BASE_URL + "/Product/Product/SupplyDetail" ;
    //直供--商品评价列表
    public static String GOOD_COMMENT_LIST = BASE_URL + "/Product/Product/ProductCommentList" ;
    //直供--商品加入购物车
    public static String UNUSE_GOOD_ADD_CART = BASE_URL + "/Order/OrderToken/AddCart" ;
    //直供--商品--购物车
    public static String UNUSE_GOOD_CART_LIST = BASE_URL + "/Order/OrderToken/CartList" ;
    //直供--商品--购物车--数量修改
    public static String UNUSE_GOOD_CART_CHANGE_COUNT = BASE_URL + "/Order/OrderToken/EditCart" ;
    //直供--商品--购物车--删除
    public static String UNUSE_GOOD_CART_DELETE = BASE_URL + "/Order/OrderToken/DeleteCart" ;
    //直供--商品--筛选参数
    public static String GOOD_PARAM_INFO = BASE_URL + "/Product/Product/ParaList" ;

    //直供--商品--订单详情--我想要/立即购买（Token必传）
    public static String DIRECT_SPEC_GOOD_ORDE_CONFIRMORDER = BASE_URL + "/Order/OrderToken/ConfirmOrder" ;
    //直供--商品--购物车结算（Token必传）
    public static String CONFIRM_GOOD_ORDER_CART = BASE_URL + "/Order/OrderToken/Settlement" ;
    //直供--商品--下单（Token必传）
    public static String GOOD_ORDER_SUBMIT = BASE_URL + "/Order/OrderToken/CreateOrder" ;
    //直供--商品--订单（Token必传）
    public static String GOOD_ORDER_LIST = BASE_URL + "/Order/OrderToken/OrderList" ;
    //直供--商品--订单（Token必传）
    public static String GOOD_ORDER_DETAILS = BASE_URL + "/Order/OrderToken/OrderDetail" ;
    //直供--商品--订单取消（Token必传）
    public static String GOOD_ORDER_CANCEL = BASE_URL + "/Order/OrderToken/OrdersCancel" ;
    //直供--商品--订单确认收货（Token必传）
    public static String GOOD_ORDER_FINISH = BASE_URL + "/Order/OrderToken/ConfirmGoods" ;
    //直供--商品--订单删除（Token必传）
    public static String GOOD_ORDER_DELETE = BASE_URL + "/Order/OrderToken/DeleteOrder" ;
    //直供--商品--订单发货（Token必传）
    public static String GOOD_ORDER_SEND = BASE_URL + "/Order/OrderToken/DeliverGoods" ;
    //直供--商品--订单编辑（Token必传）
    public static String GOOD_ORDER_EDIT = BASE_URL + "/Order/OrderToken/EditOrders" ;
    //直供--商品--订单评价（Token必传）
    public static String GOOD_ORDER_COMMENT = BASE_URL + "/Order/OrderToken/CommentOrders" ;
    //直供--商品--订单评价--追评（Token必传）
    public static String GOOD_ORDER_COMMENT_RESEND = BASE_URL + "/Order/OrderToken/ReviewCommentOrders" ;
    //直供--商品评价列表--我的评价
    public static String GOOD_COMMENT_LIST_MY = BASE_URL + "/Product/ProductToken/ProductCommentList" ;
    //直供--商品评价列表--我的评价--删除
    public static String GOOD_COMMENT_LIST_MY_DELETE = BASE_URL + "/Order/OrderToken/DeleteCommentOrders" ;

    //店铺--店铺主页
    public static String STORE_DETAILS = BASE_URL + "/Product/Product/ShopDetail" ;
    //店铺--商品分类
    public static String STORE_GOOD_CATEGORY = BASE_URL + "/Product/Product/BabyCategoryList" ;

    //系统消息
    public static String MESSAGE_LIST_SYSTEM = BASE_URL + "/PersonalInfo/NoticeList" ;
    //评论消息--需求评论
    public static String MESSAGE_LIST_NEEDS = BASE_URL + "/Need/NeedToken/NeedComment" ;
    //评论消息--互动消息
    public static String MESSAGE_LIST_CIRCLE = BASE_URL + "/BBS/PostUser/InteractionMessage" ;


    public static String PAY_ZFB_URL = BASE_URL + "/Pay/GetAliPayParameter" ;
    public static String PAY_WX_URL = BASE_URL + "/Pay/GetWXPayParameter" ;
    public static String PAY_YE_URL = BASE_URL + "/Pay/BanlancePay" ;


    //我的订阅
    public static String SUB_CATEGORY = BASE_URL + "/PersonalInfo/UserTakeCoulmnList" ;
    //我的订阅列表
    public static String SUB_SUBSCRIBE = BASE_URL + "/PersonalInfo/SubscribeList" ;
    //添加订阅
    public static String SUB_ADDUSERTAKECOULMN = BASE_URL + "/PersonalInfo/AddUserTakeCoulmn" ;
    //取消订阅
    public static String SUB_CANCLECOULMN=BASE_URL+"/PersonalInfo/CancelUserTakeCoulmn";
    //关注的人
    public static String ME_GETMYCOLLECTIONMAN=BASE_URL+"/PersonalInfo/GetMyCollectionMan";
    //我的粉丝
    public static String ME_GETMYFANS=BASE_URL+"/PersonalInfo/GetMyFans";
    //发布闲置的添加
    public static String  UNUSED_ADDPRODUCT  =BASE_URL+"/ProductToken/AddProduct";
    //发布闲置的添加
    public static String  UNUSED_EDITPRODUCT  =BASE_URL+"/ProductToken/EditProduct";


    //我的订单
    public static String  MY_RECEIVE_ORDER_LIST  =BASE_URL+"/PersonalInfo/MyAllOrder";
    //我的订单--数量
    public static String  MY_RECEIVE_ORDER_COUNT  =BASE_URL+"/PersonalInfo/MyOrderCount";

    //售后相关————————start

    //售后订单列表
    public static String  ORDER_AFTER_SALE_LIST = BASE_URL + "/PersonalInfo/AfterSaleList";
    //售后订单详细
    public static String  ORDER_AFTER_SALE_DETAILS = BASE_URL + "/PersonalInfo/AfterSaleDetail";

    //申请售后/修改售后申请
    public static String ORDER_AFTER_SALE_SUBMIT = BASE_URL + "/PersonalInfo/AfterSale" ;
    //售后申请--取消
    public static String ORDER_AFTER_SALE_CANCEL = BASE_URL + "/PersonalInfo/CancelAfterSale" ;
    //售后申请--卖家同意后，提交退货地址，买家发物流
    public static String ORDER_AFTER_SALE_SEND_EXPRESS = BASE_URL + "/PersonalInfo/ReturnExpressInfo" ;

    //售后申请--同意--针对卖家--填写相关信息
    public static String ORDER_AFTER_SALE_AGREE = BASE_URL + "/PersonalInfo/Agree" ;
    //售后申请--不同意--针对卖家
    public static String ORDER_AFTER_SALE_DISAGREE = BASE_URL + "/PersonalInfo/Disagree" ;
    //售后申请--完成整个售后--收到货了，可以退钱给买家了--针对卖家
    public static String ORDER_AFTER_SALE_FINISH = BASE_URL + "/PersonalInfo/CollectGoods" ;

    //售后相关————————end


    //支付信息模板
    public static String PAY_BIND_INFO = BASE_URL + "/PersonalInfo/GetWithdrawInfo" ;
    public static String PAY_BIND_INFO_EDIT = BASE_URL + "/PersonalInfo/ManageWithdrawInfo" ;
    public static String PAY_BIND_INFO_DELETE = BASE_URL + "/PersonalInfo/DeleteWithdrawInfo" ;

}
