package cn.idcby.jiajubang.viewmodel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.dialog.DialogDatePicker;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DateCompareUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.Bean.ReceiveAddress;
import cn.idcby.jiajubang.Bean.RequestServiceConfirm;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ActivityBondPay;
import cn.idcby.jiajubang.activity.BaseBindActivity;
import cn.idcby.jiajubang.activity.SelectedProvinceActivity;
import cn.idcby.jiajubang.activity.ServiceConfirmActivity;
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

import static android.app.Activity.RESULT_OK;

public class ServiceConfirmViewModel extends BaseObservable implements ViewModel {
    private ServiceConfirmActivity activity;
    private RequestServiceConfirm confirmData = new RequestServiceConfirm();
    private LoadingDialog mLoadDialog;
    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ImageConfig imageConfig;

    public ServiceConfirmViewModel(ServiceConfirmActivity activity) {
        this.activity = activity;
        initData();
        initSelectPic();
    }

    private void initData() {
        RequestServiceConfirm data= (RequestServiceConfirm) activity.getIntent().getSerializableExtra(SkipUtils.SERVICE_DETIALS);
        setConfirmData(data);
    }

    private void initPhotoContainer() {
        int itemWidHei = (ResourceUtils.getScreenWidth(activity)
                - ResourceUtils.dip2px(activity, 15) * 2
                - ResourceUtils.dip2px(activity, 5) * 4) / 5;
        imageSelectorResultAdapter = new ImageSelectorResultAdapter(activity, mAdapterImageList
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
                        SkipUtils.toImageShowActivity(activity, localImageList, position);
                    }
                }
            }

            @Override
            public void onItemLongClickListener(int type, int position) {
            }
        });

        activity.setAdapter(imageSelectorResultAdapter);
        imageSelectorResultAdapter.notifyDataSetChanged();
    }

    private void initSelectPic() {
        mAdapterImageList.add(null);//添加图片的那个图标
        mLoadDialog = new LoadingDialog(activity);
        initPhotoContainer();
    }

    private void goCheckPhoto() {
        if (imageConfig == null)
            imageConfig = new ImageConfig.Builder(new GlideImageLoader())
                    .steepToolBarColor(Color.BLACK)
                    .titleBgColor(Color.BLACK)
                    .titleSubmitTextColor(activity.getResources().getColor(R.color.white))
                    .titleTextColor(activity.getResources().getColor(R.color.white))
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
        ImageSelector.open(activity, imageConfig);
    }

    private void checkPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            goCheckPhoto();
        } else {
            EasyPermissions.requestPermissions(activity, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMI_IMAGE, perms);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView19:
            case R.id.imageView3:
                datePicker("选择日期", view);
                break;
            case R.id.tv_develer_yes:
                confirmData.setIsCarry("1");
                break;
            case R.id.tv_develer_no:
                confirmData.setIsCarry("0");
                break;
            case R.id.tv_dianti_yes:
                confirmData.setIsHavePhone("1");
                break;
            case R.id.tv_dianti_no:
                confirmData.setIsHavePhone("0");
                break;
            case R.id.tv_lache_yes:
                confirmData.setIsPullCart("1");
                break;
            case R.id.tv_lache_no:
                confirmData.setIsPullCart("0");
                break;
            case R.id.tv_trank_yes:

                break;
            case R.id.tv_trank_no:

                break;
            case R.id.btn_commit:
                submitServerConfirm();
                break;
            case R.id.tv_loacation:
                SelectedProvinceActivity.launch(activity, 1003);
                break;
        }
    }

    /**
     * 提交服务
     */
    private void submitServerConfirm() {

        if ("".equals(confirmData.getServiceIntroduce())) {
            ToastUtils.showToast(activity, "请输入服务介绍");
            return;
        }

        if ("".equals(StringUtils.convertNull(confirmData.getCreateDate()))) {
            ToastUtils.showToast(activity, "请选择服务时间");
            return;
        }

        if (StringUtils.convertString2Float(confirmData.getServiceAmount().trim()) <= 0) {
            confirmData.setServiceAmount("0.01");
            ToastUtils.showToast(activity, "请输入正确的金额");
            return;
        }


        if ("".equals(StringUtils.convertNull(confirmData.getContacts()))) {
            ToastUtils.showToast(activity, "请填写名称");
            return;
        }

        if ("".equals(StringUtils.convertNull(confirmData.getContactsPhone()))) {
            ToastUtils.showToast(activity, "请填写联系电话");
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

        Map<String, String> paramMap = ParaUtils.getParaWithToken(activity);
        paramMap.put("ProvinceId", confirmData.getProvinceId());
        paramMap.put("ProvinceName", confirmData.getProvinceName());
        paramMap.put("CityId", confirmData.getCityId());
        paramMap.put("CityName", confirmData.getCityName());
        paramMap.put("Contacts", confirmData.getContacts());
        paramMap.put("ContactsPhone", confirmData.getContactsPhone());
        paramMap.put("OrderNO", confirmData.getOrderNO());
        paramMap.put("OrderStatus", confirmData.getOrderStatus());
        paramMap.put("OrderStatusName", confirmData.getOrderStatusName());
        paramMap.put("ServiceAddress", confirmData.getServiceAddress());
        paramMap.put("ServiceIntroduce", confirmData.getServiceIntroduce());
        paramMap.put("ServiceUserAccount", confirmData.getServiceUserAccount());
        paramMap.put("ServiceUserHeadIcon", confirmData.getServiceUserHeadIcon());
        paramMap.put("ServiceUserRealName", confirmData.getServiceUserRealName());
        paramMap.put("CreateDate", confirmData.getCreateDate());
        paramMap.put("CreateUserId", confirmData.getCreateUserId());
        paramMap.put("CreateUserName", confirmData.getCreateUserName());
        paramMap.put("ServiceTime", confirmData.getServiceTime());
        paramMap.put("ServiceAmount", confirmData.getServiceAmount());
        paramMap.put("ServiceUserId", StringUtils.convertNull(confirmData.getServiceUserId()));
        paramMap.put("ServiceOrderId", StringUtils.convertNull(confirmData.getServiceOrderId()));
        paramMap.put("ServiceUserNickName", StringUtils.convertNull(confirmData.getServiceUserNickName()));
        paramMap.put("OrderType", "" + confirmData.getOrderType());
        paramMap.put("IsCarry", confirmData.getIsCarry());
        paramMap.put("IsHavePhone", "" + confirmData.getIsHavePhone());
        paramMap.put("CarryInstruction", confirmData.getCarryInstruction());
        paramMap.put("IsPullCart", "" + confirmData.getIsPullCart());
        paramMap.put("TruckNeed", "" + confirmData.getIsCarry());
        paramMap.put("ServiceAlbums", thumbList);

        NetUtils.getDataFromServerByPost(activity, Urls.SERVER_CONFIRM, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("submitServerConfirm",true, activity, NeedsBondPayResult.class) {
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        mLoadDialog.dismiss();

                        EventBus.getDefault().post(new BusEvent.ReceiveOrderRefresh(true));

                        if (!"".equals(StringUtils.convertNull(confirmData.getServiceOrderId()))) {//编辑订单
                            ToastUtils.showToast(activity, "修改成功");
                            activity.setResult(RESULT_OK);
                            activity.finish();
                        } else {
                            if (bean != null) {
                                String moneys = bean.PayableAmount;
                                if ("".equals(StringUtils.convertNull(moneys))) {
                                    confirmData.setServiceAmount(moneys);
                                }
                                //跳转到付款界面
                                Intent toPyIt = new Intent(activity, ActivityBondPay.class);
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY, moneys);
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID, bean.OrderID);
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE, bean.OrderCode);
                                toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE, "" + SkipUtils.PAY_ORDER_TYPE_ORDER_SERVICE);
                                activity.startActivityForResult(toPyIt, REQUEST_CODE_PAY_MONEY);
                            } else {
                                ToastUtils.showErrorToast(activity, "预约成功，需要付款");
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

                        ToastUtils.showErrorToast(activity, "预约失败");
                    }
                });
    }


    //日期选择器
    private DialogDatePicker dialogDatePicker;//选择年月日

    private void datePicker(String str, final View view) {
        view.setEnabled(false);
        dialogDatePicker = new DialogDatePicker(activity, false);
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
                    confirmData.setCreateDate(dialogDatePicker.getDate());
                    dialogDatePicker.dismiss();
                } else {
                    ToastUtils.showErrorToast(activity, "日期不能小于当前时间");
                }
            }
        });

        dialogDatePicker.show();
    }


    private static final int REQUEST_CODE_CHOOSE_ADDRESS = 1000;
    private static final int REQUEST_CODE_PAY_MONEY = 1001;
    private final static int UPLOAD_PHOTO = 23;
    private final static int MAX_IMAGE_COUNT = 3;
    private final static int REQUEST_CODE_PERMI_IMAGE = 101;
    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;
    public static final int SERVER_INSTALL_TYPE = 1;
    public static final int SERVER_SERVER_TYPE = 2;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_CHOOSE_ADDRESS == requestCode) {
            if (RESULT_OK == resultCode && data != null) {
                ReceiveAddress mChooseAddress = (ReceiveAddress) data.getSerializableExtra(SkipUtils.INTENT_ADDRESS_INFO);
                updateAddressDisplay(mChooseAddress);
            }
        } else if (REQUEST_CODE_PAY_MONEY == requestCode) {
//            if (RESULT_OK == resultCode) {
//                setResult(RESULT_OK);
//            }
        } else if (1003 == requestCode) {
            if (resultCode == SelectedProvinceActivity.RESULT_CODE_FOR_SELECTED_CITY) {
                confirmData.setProvinceName(StringUtils.convertNull(data.getStringExtra("provinceName")));
                confirmData.setProvinceId(data.getStringExtra("provinceId"));
                confirmData.setCityId(data.getStringExtra("cityId"));
                confirmData.setCityName(StringUtils.convertNull(data.getStringExtra("cityName")));
                confirmData.setServiceAddress(StringUtils.convertNull(data.getStringExtra("provinceName")) + StringUtils.convertNull(data.getStringExtra("cityName")));
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

    /**
     * 填充地址布局
     */
    private void updateAddressDisplay(ReceiveAddress chooseAddress) {
        if (chooseAddress != null) {
            String province = chooseAddress.getProvinceName();
            String city = chooseAddress.getCityName();
            String area = chooseAddress.getCountyName();
            String address = chooseAddress.getAddress();
            confirmData.setProvinceName(chooseAddress.getProvinceName());
            confirmData.setProvinceId(chooseAddress.getProvinceId());
            confirmData.setCityId(chooseAddress.getCityId());
            confirmData.setCityName(chooseAddress.getCityName());
            confirmData.setServiceAddress(chooseAddress.getAddress());
        }
    }

    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private int uploadIndex = 0;
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

                ToastUtils.showErrorToast(activity, "图片上传失败");
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

        NetUtils.getDataFromServerByPost(activity, Urls.UPLOAD_PHOTO, false, para,
                new StringCallback() {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (mLoadDialog != null && mLoadDialog.isShowing())
                            mLoadDialog.dismiss();
                        ToastUtils.showNetErrorToast(activity);
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
                                ToastUtils.showErrorToast(activity, "图片上传失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @Bindable
    public RequestServiceConfirm getConfirmData() {
        return confirmData;
    }

    public void setConfirmData(RequestServiceConfirm confirmData) {
        this.confirmData = confirmData;
        notifyPropertyChanged(BR.confirmData);
    }

    @Override
    public void destory() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        NetUtils.cancelTag("getDefaultAddress");
    }
}
