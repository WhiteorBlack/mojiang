package cn.idcby.jiajubang.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.CartList;
import cn.idcby.jiajubang.Bean.ShopCartBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ShopCartAdapter;
import cn.idcby.jiajubang.interf.RvMoreItemClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created on 2018-04-24.
 */

public class ShoppingCartActivity extends BaseActivity {
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mEditTv;//编辑
    private RecyclerView mRecycleview;//列表
    private TextView mTotlemoneyTv ;//总金额
    private View mMoneyLay ;
    private TextView goBuytv;//结算
    private TextView mDeleteTv;//删除
    private ImageView mAllCheckIv ;//全选imageView

    private ShopCartAdapter mShopCartAdapter;
    private List<ShopCartBean> mAllCartList = new ArrayList<>();

    private LoadingDialog mDialog ;

    private List<String> mCheckedCartList = new ArrayList<>() ;//选中集合

    private boolean mIsEdit = false ;//是否是编辑状态

    private Dialog mCountDialog ;
    private EditText mCountEv ;
    private int mParentPosition ;
    private int mPosition ;


    private static final int REQUEST_CODE_CONFIRM = 1000 ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_shoppingcart;
    }

    @Override
    public void initView() {

//        StatusBarUtil.setColorAndTrans(mActivity,getResources().getColor(R.color.activity_bg_common));

        mDialog = new LoadingDialog(mContext) ;
        mDialog.setOnCancelListener(new CancelListener());

        mEditTv = findViewById(R.id.shop_edited_tv);
        mRefreshLay = findViewById(R.id.acti_cart_refresh_lay);
        mRecycleview = findViewById(R.id.shopcart_recycleview);
        mTotlemoneyTv = findViewById(R.id.id_tv_totalPrice);
        mMoneyLay = findViewById(R.id.acti_cart_money_lay);
        goBuytv = findViewById(R.id.id_tv_totalCount_jiesuan);
        mDeleteTv = findViewById(R.id.acti_cart_delete_tv);
        mAllCheckIv = findViewById(R.id.acti_cart_all_check_iv);
        View allCheckLay = findViewById(R.id.acti_cart_all_check_lay) ;
        allCheckLay.setOnClickListener(this);

        mShopCartAdapter = new ShopCartAdapter(mContext, mAllCartList, new RvMoreItemClickListener() {
            @Override
            public void onItemClickListener(int type, int... position) {
                if(ShopCartAdapter.TYPE_STORE_ALL == type){
                    changeStoreAllCheck(position[0]) ;
                }else if(ShopCartAdapter.TYPE_STORE_LAY == type){
                    toStoreIndexActivity(position[0]) ;
                }else if(ShopCartAdapter.TYPE_GOOD_ALL == type){
                    changeGoodAllCheck(position[0],position[1]) ;
                }else if(ShopCartAdapter.TYPE_GOOD_LAY == type){
                    toGoodDetailActivity(position[0],position[1]) ;
                }else if(ShopCartAdapter.TYPE_GOOD_ADD == type){
                    addOrReduceCount(true ,position[0],position[1]) ;
                }else if(ShopCartAdapter.TYPE_GOOD_REDUCE == type){
                    addOrReduceCount(false ,position[0],position[1]) ;
                }else if(ShopCartAdapter.TYPE_GOOD_COUNT == type){
                    showCartCountDialog(position[0],position[1]) ;
                }
            }
        }) ;
        mRecycleview.setLayoutManager(new LinearLayoutManager(mContext));
        mRecycleview.setAdapter(mShopCartAdapter) ;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        mEditTv.setOnClickListener(this);
        goBuytv.setOnClickListener(this);
        mDeleteTv.setOnClickListener(this);

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                getCartList(true) ;
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_cart_all_check_lay == vId){//全选
            allCheckChanged() ;
        }else if(R.id.shop_edited_tv == vId){//编辑
            changeEditState() ;
        }else if(R.id.dialog_change_cart_ok_tv == vId){//修改数量--确定
            changeCartCountFromDialog() ;
        }else if(R.id.dialog_change_cart_cancel_tv == vId){//修改数量--取消
            mCountDialog.dismiss();
        }else if(R.id.id_tv_totalCount_jiesuan == vId){//结算
            toConfirmOrder() ;
        }else if(R.id.acti_cart_delete_tv == vId){//删除
            toDeleteCartGood() ;
        }
    }

    /**
     * 去结算
     */
    private void toConfirmOrder(){
        if(mCheckedCartList.size() == 0){
            ToastUtils.showToast(mContext ,"请选择商品");
            return ;
        }

        String cartIds = "" ;
        for(String ids : mCheckedCartList){
            cartIds += (ids + ",") ;
        }
        if(cartIds.length() > 1){
            cartIds = cartIds.substring(0 ,cartIds.length() - 1) ;
        }

        GoodOrderConfirmActivity.launchCart(mActivity ,cartIds ,REQUEST_CODE_CONFIRM);
    }

    /**
     * 删除购物车
     */
    private void toDeleteCartGood(){
        if(mCheckedCartList.size() == 0){
            ToastUtils.showToast(mContext ,"请选择商品");
            return ;
        }

        DialogUtils.showCustomViewDialog(mContext, "删除", "删除这些商品？", null
                , "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                deleteCart() ;
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    /**
     * 修改数量
     */
    private void changeCartCountFromDialog(){
        int count = StringUtils.convertString2Count(mCountEv.getText().toString().trim()) ;
        if(count < 1){
            ToastUtils.showToast(mContext ,"请输入正确的数量");
            return ;
        }

        mCountDialog.dismiss() ;

        addOrReduceCartCount(count +"" ,mParentPosition ,mPosition) ;
    }

    /**
     * 修改数量dialog
     * @param parentPosition parent
     * @param position position
     */
    private void showCartCountDialog(final int parentPosition ,final int position){
        mParentPosition = parentPosition ;
        mPosition = position ;

        if(null == mCountDialog){
            mCountDialog = new Dialog(mContext ,R.style.my_custom_dialog) ;
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_change_cart_count,null) ;
            mCountDialog.setContentView(view);
            mCountDialog.setCancelable(false);

            mCountEv = view.findViewById(R.id.dialog_change_cart_count_ev) ;
            TextView cancelTv = view.findViewById(R.id.dialog_change_cart_cancel_tv) ;
            TextView okTv = view.findViewById(R.id.dialog_change_cart_ok_tv) ;

            okTv.setOnClickListener(this);
            cancelTv.setOnClickListener(this);

            view.getLayoutParams().width = (int) (ResourceUtils.getScreenWidth(mContext) * 0.8F) ;

            mCountDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    DialogUtils.hideKeyBoard(mCountEv);
                }
            });
            mCountDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    DialogUtils.showKeyBoard(mCountEv) ;
                }
            });
        }

        int count = StringUtils.convertString2Count(mAllCartList.get(parentPosition)
                .getCartGoodList().get(position).getQuantity()) ;
        if(count < 1){
            count = 1 ;
        }

        mCountEv.setText(count+"");
        mCountEv.setSelection(mCountEv.getText().length());

        mCountDialog.show() ;
    }

    /**
     * 跳到商品详细
     * @param position position
     */
    private void toGoodDetailActivity(int position,int goodPosition){
        ShopCartBean cartBean = mAllCartList.get(position) ;
        SkipUtils.toGoodDetailsActivity(mContext
                ,cartBean.getCartGoodList().get(goodPosition).getProductID()
                ,cartBean.getCartGoodList().get(goodPosition).getSkuID()) ;
    }

    /**
     * 跳转到商铺主页
     * @param position position
     */
    private void toStoreIndexActivity(int position){
        ShopCartBean cartBean = mAllCartList.get(position) ;
        Intent toDtIt = new Intent(mContext ,StoreIndexActivity.class) ;
        toDtIt.putExtra(SkipUtils.INTENT_STORE_ID,cartBean.getStoreId()) ;
        startActivity(toDtIt) ;
    }

    /**
     * 添加
     * @param isAdd true add false reduce
     * @param parentPosition true add false reduce
     * @param position true add false reduce
     */
    private void addOrReduceCount(boolean isAdd,int parentPosition ,int position){
        int count = StringUtils.convertString2Count(mAllCartList.get(parentPosition)
                .getCartGoodList().get(position).getQuantity()) ;

        if(isAdd){
            count = count + 1 ;
            addOrReduceCartCount(""+count ,parentPosition ,position) ;
        }else{
            count = count - 1 ;
            if(count > 0){
                addOrReduceCartCount(""+count ,parentPosition ,position) ;
            }
        }
    }

    /**
     * 切换编辑状态
     */
    private void changeEditState(){
        mIsEdit = !mIsEdit ;
        mEditTv.setText(mIsEdit ? "完成" : "管理");

        //2018-07-02 14:04:55 跟ios对齐，暂时不对金额做显示隐藏
//        mMoneyLay.setVisibility(mIsEdit ? View.INVISIBLE : View.VISIBLE);
        mDeleteTv.setVisibility(mIsEdit ? View.VISIBLE : View.GONE);
        goBuytv.setVisibility(mIsEdit ? View.GONE : View.VISIBLE);

        updateMoneyShow() ;
    }

    /**
     * 全选
     */
    private void allCheckChanged(){

        boolean isAll = true ;
        for(ShopCartBean cartBean : mAllCartList){
            if(!cartBean.isSelected()){
                isAll = false ;
                break;
            }
        }

        for(ShopCartBean cartBean : mAllCartList){
            cartBean.setSelected(!isAll);
            List<CartList> goodList = cartBean.getCartGoodList() ;
            for(CartList good : goodList){
                good.setSelected(!isAll);
                String cartId = good.getCartID() ;

                if(!mCheckedCartList.contains(cartId)){
                    mCheckedCartList.add(cartId) ;
                }
            }
        }

        mShopCartAdapter.notifyDataSetChanged();

        mAllCheckIv.setImageDrawable(getResources().getDrawable(!isAll
                ? R.mipmap.ic_check_checked_cart
                : R.mipmap.ic_check_nomal_cart));

        updateMoneyShow() ;
    }

    /**
     * 商品选中
     * @param parentPosition parent
     * @param position position
     */
    private void changeGoodAllCheck(int parentPosition ,int position){
        ShopCartBean shopCartBean = mAllCartList.get(parentPosition) ;
        List<CartList> goodList = shopCartBean.getCartGoodList() ;
        CartList cartBean = goodList.get(position) ;

        boolean isChecked = cartBean.isSelected() ;
        String cartId = cartBean.getCartID() ;
        cartBean.setSelected(!isChecked);

        if(!isChecked){
            if(!mCheckedCartList.contains(cartId)){
                mCheckedCartList.add(cartId) ;
            }
        }else{
            if(mCheckedCartList.contains(cartId)){
                mCheckedCartList.remove(cartId) ;
            }
        }

        boolean isAll = true ;
        for(CartList cartList : goodList){
            if(!cartList.isSelected()){
                isAll = false ;
                break;
            }
        }
        shopCartBean.setSelected(isAll);
        mShopCartAdapter.notifyDataSetChanged() ;

        //更新数量和金额的显示
        updateMoneyShow() ;
    }

    /**
     * 店铺全选
     * @param position position
     */
    private void changeStoreAllCheck(int position){
        ShopCartBean shopCartBean = mAllCartList.get(position) ;
        boolean isAll = shopCartBean.isSelected() ;
        for(CartList cartBean : shopCartBean.getCartGoodList()){
            cartBean.setSelected(!isAll);
            String cartId = cartBean.getCartID() ;
            if(!isAll){
                if(!mCheckedCartList.contains(cartId)){
                    mCheckedCartList.add(cartId) ;
                }
            }else{
                if(mCheckedCartList.contains(cartId)){
                    mCheckedCartList.remove(cartId) ;
                }
            }
        }
        shopCartBean.setSelected(!isAll);
        mShopCartAdapter.notifyDataSetChanged() ;

        //更新数量和金额的显示
        updateMoneyShow() ;
    }

    /**
     * 更新金额显示
     */
    private void updateMoneyShow(){
        boolean isAll = true ;
        int allCount = 0 ;
        float allMoney = 0F ;
        for(ShopCartBean shopCartBean : mAllCartList){
            List<CartList> cartLists = shopCartBean.getCartGoodList() ;
            if(!shopCartBean.isSelected()){
                if(isAll){
                    isAll = false ;
                }
            }

            for(CartList cartList : cartLists){
                if(cartList.isSelected()){
                    int count = StringUtils.convertString2Count(cartList.getQuantity()) ;
                    float price = StringUtils.convertString2Float(cartList.getSalePrice()) ;

                    allMoney += (count * price) ;
                }
            }
        }

        mTotlemoneyTv.setText("¥" + StringUtils.convertStringNoPoint(StringUtils.convertString2Float(allMoney +"") +""));
        mAllCheckIv.setImageDrawable(getResources().getDrawable(isAll
                ? R.mipmap.ic_check_checked_cart
                : R.mipmap.ic_check_nomal_cart));
    }

    private void getCartList(){
        getCartList(false) ;
    }

    /**
     * 获取购物车数据
     */
    private void getCartList(boolean isRefresh){
        if(!isRefresh){
            mDialog.setCancelable(true);
            mDialog.show();
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_CART_LIST, paramMap
                , new RequestListCallBack<CartList>("getCartList",mContext,CartList.class) {
                    @Override
                    public void onSuccessResult(List<CartList> bean) {
                        mDialog.dismiss();
                        mRefreshLay.finishRefresh();

                        convertCartToGroup(bean) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                        mRefreshLay.finishRefresh();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        mRefreshLay.finishRefresh();
                    }
                });
    }

    /**
     * 修改数量
     */
    private void addOrReduceCartCount(final String count ,final int parentPosition ,final int position){
        mDialog.setCancelable(false);
        mDialog.show();

        final ShopCartBean parentCart = mAllCartList.get(parentPosition) ;
        final CartList childCart = parentCart.getCartGoodList().get(position) ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("CartId" ,childCart.getCartID()) ;
        paramMap.put("Quantity" ,count) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_CART_CHANGE_COUNT, paramMap
                , new RequestListCallBack<CartList>("addOrReduceCount",mContext,CartList.class) {
                    @Override
                    public void onSuccessResult(List<CartList> bean) {
                        mDialog.dismiss();

                        childCart.setQuantity(count) ;
                        mShopCartAdapter.notifyDataSetChanged() ;

                        if(childCart.isSelected()){
                            updateMoneyShow() ;
                        }
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

    /**
     * 删除
     */
    private void deleteCart(){
        mDialog.setCancelable(false);
        mDialog.show();

        String cartIds = "" ;
        for(String ids : mCheckedCartList){
            cartIds += (ids + ",") ;
        }
        if(cartIds.length() > 1){
            cartIds = cartIds.substring(0 ,cartIds.length() - 1) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("CartId" ,cartIds) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_CART_DELETE, paramMap
                , new RequestObjectCallBack<String>("deleteCart",mContext,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext,"删除成功") ;
                        mCheckedCartList.clear() ;

                        changeEditState() ;
                        getCartList() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        ToastUtils.showToast(mContext,"删除失败") ;
                    }
                });
    }

    /**
     * dialog cancel
     */
    private class CancelListener implements Dialog.OnCancelListener{
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            NetUtils.cancelTag("getCartList") ;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mIsEdit){
            getCartList() ;
        }

    }

    /**
     * 把购物车数据分组
     * @param allCartInfo 购物车里的数据
     */
    private void convertCartToGroup(List<CartList> allCartInfo){
        mAllCartList.clear() ;
        mShopCartAdapter.notifyDataSetChanged() ;

        List<String> groupIdList = new ArrayList<>() ;
        for(CartList cartList : allCartInfo){
            if(mCheckedCartList.contains(cartList.getCartID())){
                cartList.setSelected(true) ;
            }

            String storeId = cartList.getMerchantID() ;
            if(!"".equals(storeId) && !groupIdList.contains(storeId)){
                groupIdList.add(storeId) ;
            }
        }

        for(String storeId : groupIdList){
            ShopCartBean cartBean = new ShopCartBean(storeId) ;
            String storeName = "" ;
            List<CartList> cartGood = new ArrayList<>() ;

            for(CartList cartList : allCartInfo){
                String groupId = cartList.getMerchantID() ;

                if(storeId.equals(groupId)){
                    cartGood.add(cartList) ;
                    if("".equals(storeName)){
                        storeName = cartList.getName() ;
                    }
                }
            }

            cartBean.setStoreName(storeName);
            cartBean.setCartGoodList(cartGood);

            mAllCartList.add(cartBean) ;
        }

        //判断store全选状态
        for(ShopCartBean shopCartBean : mAllCartList){
            List<CartList> goodList = shopCartBean.getCartGoodList() ;
            boolean isAll = true ;
            for(CartList cartList : goodList){
                if(!cartList.isSelected()){
                    isAll = false ;
                    break;
                }
            }
            shopCartBean.setSelected(isAll) ;
        }

        //更新购物车
        mShopCartAdapter.notifyDataSetChanged() ;
        updateMoneyShow();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_CONFIRM == requestCode){
            if(RESULT_OK == resultCode){
                //不清楚和onResume谁先执行，所以直接在此更新一下
                mCheckedCartList.clear() ;
                getCartList() ;
            }
        }
    }
}
