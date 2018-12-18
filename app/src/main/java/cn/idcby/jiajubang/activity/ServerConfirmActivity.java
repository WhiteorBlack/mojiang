package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.DialogDatePicker;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DateCompareUtils;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.Bean.ReceiveAddress;
import cn.idcby.jiajubang.Bean.ServerDetails;
import cn.idcby.jiajubang.Bean.ServerOrderDetails;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import idcby.cn.imagepicker.GlideImageLoader;
import idcby.cn.imagepicker.ImageConfig;
import idcby.cn.imagepicker.ImageSelector;
import idcby.cn.imagepicker.ImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 预约服务
 * Created on 2018/4/11.
 * <p>
 * 2018-06-15 13:43:47
 * 改：服务地址信息，改为每次手动填写 ；可以编辑服务订单
 * 2018-07-03 16:17:22
 * 金额不允许修改
 */

public class ServerConfirmActivity extends BaseActivity {
    private TextView mAddressNameTv;
    private TextView mAddressPhoneTv;
    private TextView mAddressTv;
    private View mAddressChooseLay;
    private View mAddressLay;

    private EditText mServerDescEv;
    private TextView mServerTimeTv;
    private EditText mServerMoneyEv;

    private String mServerOrderId;
    private ServerOrderDetails mDetails;

    private String mServerUserId;
    private String mServerUserName;
    private int mServerType = SERVER_INSTALL_TYPE;
    //    private String mChooseAddressId ;
    private ReceiveAddress mChooseAddress;

    private String mTimeStr = "";

    private LoadingDialog mLoadDialog;
    private DialogDatePicker dialogDatePicker;//选择年月日

    private static final int REQUEST_CODE_CHOOSE_ADDRESS = 1000;
    private static final int REQUEST_CODE_PAY_MONEY = 1001;

    public static final int SERVER_INSTALL_TYPE = 1;
    public static final int SERVER_SERVER_TYPE = 2;

    private int isNeedBanYun = 1;
    private int isHasDianTi = 1;
    private int isNeedLaChe = 1;
    private int isTrunck = 1;

    private String mProvinceId;
    private String mProvinceName;
    private String mCityId;
    private String mCityName;
    private String mAreaId;

    private EditText mNameEv;
    private EditText mPhoneEv;
    private EditText mAddressEv;
    private TextView mAreaTv;
    private EditText etBanYun;

    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private int uploadIndex = 0;
    private RecyclerView mRvPhoto;
    private final static int UPLOAD_PHOTO = 23;
    private final static int MAX_IMAGE_COUNT = 3;
    private final static int REQUEST_CODE_PERMI_IMAGE = 101;
    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;
    private ImageConfig imageConfig;
    private ServerDetails details;

    public static void launch(Activity context, String serverUserId, String servicerName, boolean isInstall, int requestCode) {
        Intent toStIt = new Intent(context, ServerConfirmActivity.class);
        toStIt.putExtra(SkipUtils.INTENT_SERVER_USER_ID, serverUserId);
        toStIt.putExtra(SkipUtils.INTENT_SERVER_IS_INSTALL, isInstall);
        toStIt.putExtra(SkipUtils.INTENT_SERVER_USER_ID, servicerName);
        context.startActivityForResult(toStIt, requestCode);
    }

    public static void launch(Activity context, String serverUserId, boolean isInstall, int requestCode) {
        Intent toStIt = new Intent(context, ServerConfirmActivity.class);
        toStIt.putExtra(SkipUtils.INTENT_SERVER_USER_ID, serverUserId);
        toStIt.putExtra(SkipUtils.INTENT_SERVER_IS_INSTALL, isInstall);
        context.startActivityForResult(toStIt, requestCode);
    }


    public static void launch(Activity context, ServerDetails details, int requestCode) {
        Intent toStIt = new Intent(context, ServerConfirmActivity.class);
        toStIt.putExtra(SkipUtils.SERVICE_DETIALS, details);
        context.startActivityForResult(toStIt, requestCode);
    }

    public static void launch(Context context, String orderId) {
        Intent toStIt = new Intent(context, ServerConfirmActivity.class);
        toStIt.putExtra(SkipUtils.INTENT_ORDER_ID, orderId);
        context.startActivity(toStIt);
    }

    public static void launch(Activity context, String orderId, int requestCode) {
        Intent toStIt = new Intent(context, ServerConfirmActivity.class);
        toStIt.putExtra(SkipUtils.INTENT_ORDER_ID, orderId);
        context.startActivityForResult(toStIt, requestCode);
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_server_confirm;
    }

    @Override
    public void initView() {
        details = (ServerDetails) getIntent().getSerializableExtra(SkipUtils.SERVICE_DETIALS);
        mServerOrderId = getIntent().getStringExtra(SkipUtils.INTENT_ORDER_ID);
        mServerUserName = getIntent().getStringExtra(SkipUtils.INTENT_SERVER_USER_NAME);
        mServerUserId = getIntent().getStringExtra(SkipUtils.INTENT_SERVER_USER_ID);
        boolean isInstall = getIntent().getBooleanExtra(SkipUtils.INTENT_SERVER_IS_INSTALL, false);
        mServerType = isInstall ? SERVER_INSTALL_TYPE : SERVER_SERVER_TYPE;
        etBanYun = findViewById(R.id.et_banyun);
        mAddressNameTv = findViewById(R.id.acti_server_confirm_address_name_tv);
        mAddressPhoneTv = findViewById(R.id.acti_server_confirm_address_phone_tv);
        mAddressTv = findViewById(R.id.acti_server_confirm_address_address_tv);
        mAddressChooseLay = findViewById(R.id.acti_server_confirm_address_add_lay);
        mAddressLay = findViewById(R.id.acti_server_confirm_address_lay);
        mServerDescEv = findViewById(R.id.acti_server_confirm_desc_ev);
        mServerMoneyEv = findViewById(R.id.acti_server_confirm_money_ev);
        mServerTimeTv = findViewById(R.id.acti_server_confirm_time_tv);
        TextView mSubTv = findViewById(R.id.acti_server_confirm_submit_tv);
        TextView mTitleTv = findViewById(R.id.acti_server_confirm_title_tv);
        mNameEv = findViewById(R.id.acti_my_address_edit_name_ev);
        mPhoneEv = findViewById(R.id.acti_my_address_edit_phone_ev);
        mAddressEv = findViewById(R.id.acti_my_address_edit_address_ev);
        mAreaTv = findViewById(R.id.acti_my_address_edit_area_tv);
        mAddressChooseLay.setOnClickListener(this);
        mAddressLay.setOnClickListener(this);
        mServerTimeTv.setOnClickListener(this);
        mSubTv.setOnClickListener(this);

        mLoadDialog = new LoadingDialog(mContext);

        if (!"".equals(StringUtils.convertNull(mServerOrderId))) {
            mTitleTv.setText("修改服务订单");
            mSubTv.setText("修改");
            mServerMoneyEv.setEnabled(false);

            mLoadDialog.show();
            getOrderDetails();
        }
        initSelectPic();
    }

    private void initPhotoContainer() {
        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext, 15) * 2
                - ResourceUtils.dip2px(mContext, 5) * 4) / 5;
        mRvPhoto = findViewById(R.id.acti_send_circle_add_rv);
        mRvPhoto.setLayoutManager(new GridLayoutManager(this, 5));
        imageSelectorResultAdapter = new ImageSelectorResultAdapter(this, mAdapterImageList
                , itemWidHei, itemWidHei, MAX_IMAGE_COUNT, new RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if (0 == type) {//删除
                    //删除要判断当前位置的图片是否已经上传过了，以此判断 uploadIndex 的加减
                    if (position < uploadIndex) {
                        uploadIndex--;
                    }

                    mAdapterImageList.remove(position);
                    localImageList.remove(position);
                    imageUploadList.remove(position);
                    imageSelectorResultAdapter.notifyDataSetChanged();
                } else if (1 == type) {//原图
                    if (position < MAX_IMAGE_COUNT && position == mAdapterImageList.size() - 1) {
                        //选择图片
                        checkPermission();
                    } else {
                        SkipUtils.toImageShowActivity(mActivity, localImageList, position);
                    }
                }
            }

            @Override
            public void onItemLongClickListener(int type, int position) {
            }
        });
        mRvPhoto.setAdapter(imageSelectorResultAdapter);
    }

    private void initSelectPic() {
        mAdapterImageList.add(null);//添加图片的那个图标
        mLoadDialog = new LoadingDialog(mContext);
        initPhotoContainer();
    }

    private void checkPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            goCheckPhoto();
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMI_IMAGE, perms);
        }
    }

    private void goCheckPhoto() {
        if (imageConfig == null)
            imageConfig = new ImageConfig.Builder(new GlideImageLoader())
                    .steepToolBarColor(Color.BLACK)
                    .titleBgColor(Color.BLACK)
                    .titleSubmitTextColor(getResources().getColor(R.color.white))
                    .titleTextColor(getResources().getColor(R.color.white))
                    // 开启多选   （默认为多选）
                    .mutiSelectMaxSize(MAX_IMAGE_COUNT)
                    // 拍照后存放的图片路径（默认 /temp/picture）
                    .filePath("/temp")
                    // 开启拍照功能 （默认关闭）
                    .showCamera()
                    .isReloadModel(true)
                    .requestCode(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO)
                    .build();

        imageConfig.setMaxSize(MAX_IMAGE_COUNT - localImageList.size());
        ImageSelector.open(this, imageConfig);
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId();

        if (R.id.acti_server_confirm_address_add_lay == vId
                || R.id.acti_server_confirm_address_lay == vId) {

            MyAddressEditActivity.launch(mActivity, mChooseAddress, REQUEST_CODE_CHOOSE_ADDRESS);

//            Intent toCtIt = new Intent(mContext ,ChooseAddressActivity.class) ;
//            mActivity.startActivityForResult(toCtIt ,REQUEST_CODE_CHOOSE_ADDRESS);
        } else if (R.id.acti_server_confirm_time_tv == vId) {
            datePicker("选择日期", mServerTimeTv);
        } else if (R.id.acti_server_confirm_submit_tv == vId) {
            submitServerConfirm();
        } else if (R.id.ll_location == vId) {
            SelectedProvinceActivity.launch(mActivity, 1003);
        }
    }

    //日期选择器
    private void datePicker(String str, final TextView view) {
        view.setEnabled(false);
        dialogDatePicker = new DialogDatePicker(this, false);
        dialogDatePicker.setTitle(str);
        dialogDatePicker.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(true);
                dialogDatePicker.dismiss();
            }
        });
        dialogDatePicker.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //格式化时间
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sDateFormat.format(new java.util.Date());

                if (DateCompareUtils.compareDay(dialogDatePicker.getDate(), currentDate)) {
                    view.setEnabled(true);
                    view.setText(dialogDatePicker.getDate());
                    mTimeStr = dialogDatePicker.getDate();
                    dialogDatePicker.dismiss();
                } else {
                    ToastUtils.showErrorToast(mContext, "日期不能小于当前时间");
                }
            }
        });

        dialogDatePicker.show();
    }

    /**
     * 填充地址布局
     */
    private void updateAddressDisplay(ReceiveAddress chooseAddress) {
        if (chooseAddress != null) {
//        if(mChooseAddress != null && !"".equals(StringUtils.convertNull(mChooseAddress.getAddressId()))){
//            mChooseAddressId = mChooseAddress.getAddressId() ;

            mChooseAddress = chooseAddress;

            mAddressChooseLay.setVisibility(View.GONE);
            mAddressLay.setVisibility(View.VISIBLE);

            String province = chooseAddress.getProvinceName();
            String city = chooseAddress.getCityName();
            String area = chooseAddress.getCountyName();
            String address = chooseAddress.getAddress();

            mAddressNameTv.setText(chooseAddress.getContacts());
            mAddressPhoneTv.setText(chooseAddress.getAccount());
            mAddressTv.setText(province + city + area + address);
        } else {
//            mChooseAddressId = null ;
            mChooseAddress = null;

            mAddressChooseLay.setVisibility(View.VISIBLE);
            mAddressLay.setVisibility(View.GONE);
        }
    }

    private void updateOrderDisplay() {
        mLoadDialog.dismiss();

        if (null == mDetails) {
            DialogUtils.showCustomViewDialog(mContext, "订单信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
            return;
        }

        String provinceId = mDetails.getProvinceId();
        String provinceName = mDetails.getProvinceName();
        String cityId = mDetails.getCityId();
        String cityName = mDetails.getCityName();
        String address = mDetails.getServiceAddress();
        String name = mDetails.getContacts();
        String phone = mDetails.getContactsPhone();

        mServerUserId = mDetails.getServiceUserId();
        String money = mDetails.getServiceAmount();
        mTimeStr = mDetails.getServiceTime();
        mServerType = mDetails.getOrderType();

        mChooseAddress = new ReceiveAddress(name, phone, provinceId, provinceName, cityId, cityName, address);
        updateAddressDisplay(mChooseAddress);

        mServerMoneyEv.setText(money);
        mServerDescEv.setText(mDetails.getServiceintroduce());
        mServerTimeTv.setText(mTimeStr);
    }

    /**
     * 获取信息
     */
    private void getOrderDetails() {
        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("Code", StringUtils.convertNull(mServerOrderId));

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_ORDER_DETAILS, paramMap
                , new RequestObjectCallBack<ServerOrderDetails>("getDetails", mContext, ServerOrderDetails.class) {
                    @Override
                    public void onSuccessResult(ServerOrderDetails bean) {
                        mDetails = bean;
                        updateOrderDisplay();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        updateOrderDisplay();
                    }

                    @Override
                    public void onFail(Exception e) {
                        updateOrderDisplay();
                    }
                });
    }

    /**
     * 获取默认地址
     */
    private void getDefaultAddress() {
        mLoadDialog.show();

        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_ADDRESS_DEFAULT_GET, paramMap
                , new RequestObjectCallBack<ReceiveAddress>("getDefaultAddress", mContext, ReceiveAddress.class) {
                    @Override
                    public void onSuccessResult(ReceiveAddress bean) {
                        if (mLoadDialog != null) {
                            mLoadDialog.dismiss();
                        }

                        updateAddressDisplay(bean);
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (mLoadDialog != null) {
                            mLoadDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (mLoadDialog != null) {
                            mLoadDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 提交服务
     */
    private void submitServerConfirm() {
        if (null == mChooseAddress) {
            ToastUtils.showToast(mContext, "请先填写联系方式");
            return;
        }

        String desc = mServerDescEv.getText().toString().trim();
        if ("".equals(desc)) {
            ToastUtils.showToast(mContext, "请输入服务介绍");
            mServerDescEv.setText("");
            mServerDescEv.requestFocus();
            return;
        }

        if ("".equals(StringUtils.convertNull(mTimeStr))) {
            ToastUtils.showToast(mContext, "请选择服务时间");
            return;
        }

        final String money = mServerMoneyEv.getText().toString();
        if (StringUtils.convertString2Float(money.trim()) <= 0) {
            ToastUtils.showToast(mContext, "请输入正确的金额");
            mServerMoneyEv.requestFocus();
            mServerMoneyEv.setSelection(money.length());
            return;
        }


        String name = mNameEv.getText().toString();
        String phone = mPhoneEv.getText().toString();
        if ("".equals(StringUtils.convertNull(name))) {
            ToastUtils.showToast(mContext, "请填写名称");
            return;
        }

        if ("".equals(StringUtils.convertNull(phone))) {
            ToastUtils.showToast(mContext, "请填写联系电话");
            return;
        }
        mLoadDialog.show();
        StringBuffer imgBuilder = new StringBuffer();
        for (String imgUrl : imageUploadList) {
            imgBuilder.append(imgUrl).append(",");
        }

        String thumbList = imgBuilder.toString();
        if (thumbList.length() > 1) {
            thumbList = thumbList.substring(0, thumbList.length() - 1);
        }

        String CarryInstruction = etBanYun.getText().toString();
        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("ProvinceId", mProvinceId);
        paramMap.put("ProvinceName", mProvinceName);
        paramMap.put("CityId", mCityId);
        paramMap.put("CityName", mCityName);
        paramMap.put("Contacts", name);
        paramMap.put("ContactsPhone", phone);
        paramMap.put("OrderNO", "");
        paramMap.put("OrderStatus", "");
        paramMap.put("OrderStatusName", "");
        paramMap.put("ServiceAddress", "");
        paramMap.put("ServiceIntroduce", desc);
        paramMap.put("ServiceUserAccount", details.getHxName());
        paramMap.put("ServiceUserHeadIcon", details.getHeadIcon());
        paramMap.put("ServiceUserRealName", details.getNickName());
        paramMap.put("CreateDate", "");
        paramMap.put("CreateUserId", "");
        paramMap.put("CreateUserName", "");
        paramMap.put("ServiceTime", mTimeStr);
        paramMap.put("ServiceAmount", details.getPayMoney());
        paramMap.put("ServiceUserId", StringUtils.convertNull(mServerUserId));
        paramMap.put("ServiceOrderId", StringUtils.convertNull(mServerOrderId));
        paramMap.put("ServiceUserNickName", StringUtils.convertNull(mServerUserName));
        paramMap.put("OrderType", "" + mServerType);
        paramMap.put("IsCarry", isNeedBanYun + "");
        paramMap.put("IsHavePhone", "" + isHasDianTi);
        paramMap.put("CarryInstruction", CarryInstruction);
        paramMap.put("IsPullCart", "" + isNeedLaChe);
        paramMap.put("TruckNeed", "" + isTrunck);
        paramMap.put("ServiceAlbums", thumbList);

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_CONFIRM, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("submitServerConfirm", mContext, NeedsBondPayResult.class) {
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        mLoadDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true));

                        if (!"".equals(StringUtils.convertNull(mServerOrderId))) {//编辑订单
                            ToastUtils.showToast(mContext, "修改成功");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            if (bean != null) {
                                String moneys = bean.PayableAmount;
                                if ("".equals(StringUtils.convertNull(moneys))) {
                                    moneys = money;
                                }
                                //跳转到付款界面
                                Intent toPyIt = new Intent(mContext, ActivityBondPay.class);
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY, moneys);
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID, bean.OrderID);
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE, bean.OrderCode);
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE, "" + SkipUtils.PAY_ORDER_TYPE_ORDER_SERVICE);
                                startActivityForResult(toPyIt, REQUEST_CODE_PAY_MONEY);
                            } else {
                                ToastUtils.showErrorToast(mContext, "预约成功，需要付款");
                            }
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mLoadDialog.dismiss();

                    }

                    @Override
                    public void onFail(Exception e) {
                        mLoadDialog.dismiss();

                        ToastUtils.showErrorToast(mContext, "预约失败");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE_CHOOSE_ADDRESS == requestCode) {
            if (RESULT_OK == resultCode && data != null) {
                ReceiveAddress mChooseAddress = (ReceiveAddress) data.getSerializableExtra(SkipUtils.INTENT_ADDRESS_INFO);
                updateAddressDisplay(mChooseAddress);
            }
        } else if (REQUEST_CODE_PAY_MONEY == requestCode) {
            if (RESULT_OK == resultCode) {
                setResult(RESULT_OK);
            }
            finish();
        } else if (1003 == requestCode) {
            if (resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                mProvinceId = data.getStringExtra("provinceId");
                mCityId = data.getStringExtra("cityId");
                mAreaId = data.getStringExtra("areaId");
                mProvinceName = StringUtils.convertNull(data.getStringExtra("provinceName"));
                mCityName = StringUtils.convertNull(data.getStringExtra("cityName"));
                String areaName = StringUtils.convertNull(data.getStringExtra("areaName"));

                mAreaTv.setText(mProvinceName + mCityName + areaName);
            }
        }
        if (REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO == requestCode) {
            if (resultCode == RESULT_OK && data != null) {
                List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                localImageList.addAll(pathList);
                int index = mAdapterImageList.size() - 1;
                if (index < 0) {
                    index = 0;
                }
                mAdapterImageList.addAll(index, pathList);

                handler.sendEmptyMessage(UPLOAD_PHOTO);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_PHOTO:
                    //上传图片
                    if (imageUploadList != null && localImageList.size() > 0) {
                        new GetImageBase64Task(localImageList.get(uploadIndex), localImageList.size()).execute();
                    }
                    break;

            }
        }
    };

    /**
     * 获取图片base64
     */
    private class GetImageBase64Task extends AsyncTask<Void, Void, String> {
        private String imageUrl;
        private int size;

        public GetImageBase64Task(String imageUrl, int size) {
            this.imageUrl = imageUrl;
            this.size = size;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mLoadDialog.setCancelable(false);
            mLoadDialog.setLoadingText("正在上传(" + (uploadIndex + 1)
                    + "/" + localImageList.size() + ")");
            mLoadDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return FileUtil.getUploadImageBase64String(imageUrl);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (null == s) {
                if (mLoadDialog != null && mLoadDialog.isShowing())
                    mLoadDialog.dismiss();

                ToastUtils.showErrorToast(mContext, "图片上传失败");
            } else {
                requestUploadPhoto(s, size);
            }
        }
    }

    /***
     * 上传图片
     */
    private void requestUploadPhoto(String base64Image, final int size) {
        LinkedHashMap<String, String> para = new LinkedHashMap<>();
        para.put("base64Image", base64Image);

        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, false, para,
                new StringCallback() {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (mLoadDialog != null && mLoadDialog.isShowing())
                            mLoadDialog.dismiss();
                        ToastUtils.showNetErrorToast(mContext);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.showLog("上传图片成功json>>>" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.optInt("type");
                            if (code == 1) {
                                String url = jsonObject.optString("resultdata");
                                imageUploadList.add(url);
                                if (uploadIndex == size - 1) {
                                    uploadIndex++;

                                    //上传完成
                                    imageSelectorResultAdapter.notifyDataSetChanged();
                                    if (mLoadDialog != null && mLoadDialog.isShowing())
                                        mLoadDialog.dismiss();
                                } else {
                                    uploadIndex++;
                                    mLoadDialog.setLoadingText("正在上传(" + (uploadIndex + 1)
                                            + "/" + localImageList.size() + ")");
                                    handler.sendEmptyMessage(UPLOAD_PHOTO);
                                }
                            } else {
                                ToastUtils.showErrorToast(mContext, "图片上传失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        NetUtils.cancelTag("getDefaultAddress");

    }
}
