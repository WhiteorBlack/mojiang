package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.QuestionCategory;
import cn.idcby.jiajubang.Bean.QuestionDetails;
import cn.idcby.jiajubang.Bean.RewardResult;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.service.LocationService;
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
 * 发布提问
 * Created on 2018/4/19.
 * <p>
 * 2018-05-09 16:37:06
 * 本地验证积分数量
 * <p>
 * 2018-06-27 14:19:52
 * 改：去掉本地积分验证
 */

public class QuestionCreateActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private EditText mTitleEv;
    private EditText mContentEv;
    private RecyclerView mQuestionPicRv;
    private TextView mCategoryTv;
    private EditText mRewardEv;

    private String mCurSheng = "";
    private String mCurShi = "";
    private String mCurQu = "";
    private String mCurLon = "0";
    private String mCurLat = "0";

    private String mQuestionId;
    private QuestionDetails mDetails;

    private String mCategoryId;
    private String mThumbs;

    private LocationService mLocationService;

    private ImageSelectorResultAdapter imageSelectorResultAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private int uploadIndex = 0;
    private LoadingDialog loadingDialog;
    private ImageConfig imageConfig;
    private final static int MAX_IMAGE_COUNT = 9;

    private final static int REQUEST_CODE_PERMI_LOCATION = 100;
    private final static int REQUEST_CODE_PERMI_IMAGE = 101;
    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;
    private final static int REQUEST_CODE_FOR_CATEGORY = 1005;

    private final static int UPLOAD_PHOTO = 23;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_PHOTO:
                    //上传图片
                    if (imageUploadList != null && localImageList.size() > 0) {
                        if (null == loadingDialog) {
                            loadingDialog = new LoadingDialog(mContext);
                        }
                        loadingDialog.setCancelable(false);
                        loadingDialog.setLoadingText("正在上传(" + (uploadIndex + 1)
                                + "/" + localImageList.size() + ")");
                        loadingDialog.show();

                        new GetImageBase64Task(localImageList.get(uploadIndex), localImageList.size()).execute();
                    }
                    break;
            }
        }
    };


    @Override
    public int getLayoutID() {
        return R.layout.activity_question_create;
    }

    @Override
    public void initView() {
        mCurLat = MyApplication.getLatitude();
        mCurLon = MyApplication.getLongitude();

        mQuestionId = getIntent().getStringExtra(SkipUtils.INTENT_QUESTION_ID);

        loadingDialog = new LoadingDialog(mContext);

        mAdapterImageList.add(null);//添加图片的那个图标

        mTitleEv = findViewById(R.id.acti_question_send_title_ev);
        mContentEv = findViewById(R.id.acti_question_send_content_ev);
        mCategoryTv = findViewById(R.id.acti_question_send_category_tv);
        mRewardEv = findViewById(R.id.acti_question_send_reward_tv);
        mQuestionPicRv = findViewById(R.id.acti_question_send_pic_lay);
        TextView mSubTv = findViewById(R.id.acti_question_send_submit_tv);
        mSubTv.setOnClickListener(this);

        mTitleEv.requestFocus();

        initPhotoContainer();

        if (!"".equals(StringUtils.convertNull(mQuestionId))) {
            getQuestionDetails();
        }
    }

    @Override
    public void initListener() {
        mCategoryTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId();

        if (R.id.acti_question_send_category_tv == vId) {
            Intent toCtIt = new Intent(mContext, ChooseQuestionCategoryActivity.class);
            startActivityForResult(toCtIt, REQUEST_CODE_FOR_CATEGORY);
        } else if (R.id.acti_question_send_submit_tv == vId) {
            submitQuestion();
        }
    }


    /**
     * 填充
     */
    private void updateDisplay() {
        loadingDialog.dismiss();

        if (null == mDetails) {
            DialogUtils.showCustomViewDialog(mContext, "问题详情获取失败"
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
            return;
        }

        String title = mDetails.getQuestionTitle();
        String content = mDetails.getQuestionExplain();
        String reward = mDetails.getReward();
        mCategoryId = mDetails.getCategoryId();
        if (!"".equals(mCategoryId)) {
            mCategoryTv.setText(mDetails.getCategoryName());
        }

        mTitleEv.setText(title);
        mContentEv.setText(content);
        mRewardEv.setText(reward);

        List<ImageThumb> thumbs = mDetails.getAlbumsList();
        if (thumbs != null && thumbs.size() > 0) {
            int size = thumbs.size();

            for (int x = size - 1; x >= 0; x--) {
                ImageThumb thumb = thumbs.get(x);
                String imgUrl = thumb.getThumbImgUrl();
                if (!"".equals(StringUtils.convertNull(imgUrl))) {
                    localImageList.add(0, imgUrl);
                    mAdapterImageList.add(0, imgUrl);
                    imageUploadList.add(0, imgUrl);
                }
            }

            StringBuffer imgBuilder = new StringBuffer();
            for (String imgUrl : imageUploadList) {
                imgBuilder.append(imgUrl).append(",");
            }

            mThumbs = imgBuilder.toString();
            if (mThumbs.length() > 1) {
                mThumbs = mThumbs.substring(0, mThumbs.length() - 1);
            }

            uploadIndex = localImageList.size();
            imageSelectorResultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取问题详细
     */
    private void getQuestionDetails() {
        Map<String, String> paramMap = ParaUtils.getParaNece(mContext);
        paramMap.put("Code", mQuestionId);

        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_DETAILS, paramMap
                , new RequestObjectCallBack<QuestionDetails>("getDetails", mContext, QuestionDetails.class) {
                    @Override
                    public void onSuccessResult(QuestionDetails bean) {
                        mDetails = bean;
                        updateDisplay();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        updateDisplay();
                    }

                    @Override
                    public void onFail(Exception e) {
                        updateDisplay();
                    }
                });
    }

    /**
     * 提交
     */
    private void submitQuestion() {
        String title = mTitleEv.getText().toString().trim();
        if ("".equals(title)) {
            ToastUtils.showToast(mContext, "请输入标题");
            mTitleEv.setText("");
            mTitleEv.requestFocus();
            return;
        }

        String content = mContentEv.getText().toString().trim();
        if ("".equals(content)) {
            ToastUtils.showToast(mContext, "请输入问题补充");
            mContentEv.setText("");
            mContentEv.requestFocus();
            return;
        }

        if ("".equals(mCategoryId)) {
            ToastUtils.showToast(mContext, "请选择行业分类");
            return;
        }

        String reward = mRewardEv.getText().toString().trim();
        if (StringUtils.convertString2Count(reward) < 0) {
            ToastUtils.showToast(mContext, "请设置悬赏");
            return;
        }

//        if(!LoginHelper.isHasEnoughIntegral(mContext ,reward)){
//            int jifen = StringUtils.convertString2Count(SPUtils.newIntance(mContext).getUserIntegral()) ;
//            ToastUtils.showToast(mContext,"积分不足，剩余" + jifen + "积分") ;
//            mRewardEv.requestFocus() ;
//            mRewardEv.setSelection(mRewardEv.getText().length()) ;
//            return ;
//        }

        StringBuffer imgBuilder = new StringBuffer();
        for (String imgUrl : imageUploadList) {
            if (!"".equals(StringUtils.convertNull(imgUrl))) {
                imgBuilder.append(imgUrl).append(",");
            }
        }

        String thumbList = imgBuilder.toString();
        if (thumbList.length() > 1) {
            thumbList = thumbList.substring(0, thumbList.length() - 1);
        }

        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("ID", StringUtils.convertNull(mQuestionId));
        paramMap.put("CategoryID", StringUtils.convertNull(mCategoryId));
        paramMap.put("QuestionTitle", title);
        paramMap.put("QuestionExplain", content);
        paramMap.put("Reward", reward);
        paramMap.put("Albums", thumbList);
        paramMap.put("CreateProvinceName", StringUtils.convertNull(mCurSheng));
        paramMap.put("CreateCityName", StringUtils.convertNull(mCurShi));
        paramMap.put("CreateCountyName", StringUtils.convertNull(mCurQu));
        paramMap.put("Longitude", mCurLon);
        paramMap.put("Latitude", mCurLat);

        boolean isNoChange = true;
        if (null == mDetails
                || !mDetails.getCategoryId().equals(mCategoryId)
                || !mDetails.getQuestionTitle().equals(title)
                || !mDetails.getQuestionExplain().equals(content)
                || !mDetails.getReward().equals(reward)
                || !mThumbs.equals(thumbList)) {
            isNoChange = false;
        }

        if (isNoChange) {
            DialogUtils.showCustomViewDialog(mContext, "温馨提示！", "您未做任何修改，是否继续？", null
                    , "继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
            return;
        }

        submitApplyRequest(paramMap);
    }

    private void submitApplyRequest(Map<String, String> paramMap) {
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog(mContext);
            loadingDialog.setCancelable(true);
        }
        NetUtils.getDataFromServerByPost(mContext, Urls.QUESTION_ANSWER_SEND, paramMap,
                new RequestObjectCallBack<String>("questionSend", true, mContext, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        loadingDialog.dismiss();
                        ToastUtils.showToast(mContext, "提交成功");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                        ToastUtils.showErrorToast(mContext, "提交失败");
                    }
                });
    }

    private void initPhotoContainer() {
        int itemWidHei = (ResourceUtils.getScreenWidth(mContext)
                - ResourceUtils.dip2px(mContext, 15) * 4) / 5;

        mQuestionPicRv.setLayoutManager(new GridLayoutManager(this, 5));
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
        mQuestionPicRv.setAdapter(imageSelectorResultAdapter);
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
        ImageSelector.open(mActivity, imageConfig);
    }


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
        protected String doInBackground(Void... voids) {
            return FileUtil.getUploadImageBase64String(imageUrl);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (null == s) {
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();

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
                        if (loadingDialog != null && loadingDialog.isShowing())
                            loadingDialog.dismiss();
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
                                    if (loadingDialog != null && loadingDialog.isShowing())
                                        loadingDialog.dismiss();
                                } else {
                                    uploadIndex++;
                                    loadingDialog.setLoadingText("正在上传(" + (uploadIndex + 1)
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE_FOR_CATEGORY == requestCode) {
            if (RESULT_OK == resultCode && data != null) {
                QuestionCategory mQuestionCategory = (QuestionCategory) data.getSerializableExtra(SkipUtils.INTENT_QUESTION_CATEGORY_INFO);
                mCategoryId = "";
                if (mQuestionCategory != null) {
                    mCategoryId = mQuestionCategory.getIndustryCategoryID();
                    String title = mQuestionCategory.getCategoryTitle();
                    mCategoryTv.setText(title);
                } else {
                    mCategoryTv.setText("请选择");
                }
            }
        } else if (REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO == requestCode) {
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
     * 开始定位
     */
    private void startLocations() {
        String[] permiss = {Manifest.permission.READ_PHONE_STATE
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(mContext, permiss)) {

            EasyPermissions.requestPermissions(mActivity
                    , "应用需要定位权限来获取当前位置，拒绝会导致部分功能异常", REQUEST_CODE_PERMI_LOCATION
                    , permiss);
            return;
        }

        // -----------location config ------------
        mLocationService = ((MyApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        mLocationService.registerListener(mListener);
        //注册监听
        mLocationService.setLocationOption(mLocationService.getDefaultLocationClientOption());
        mLocationService.start();
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                mCurSheng = location.getProvince();
                mCurShi = location.getCity();
                mCurQu = location.getDistrict();
                if (mCurShi != null && mCurSheng != null) {
                    mCurLon = location.getLongitude() + "";
                    mCurLat = location.getLatitude() + "";

                    mLocationService.unregisterListener(mListener); //注销掉监听
                    mLocationService.stop(); //停止定位服务

                    MyApplication.updateCurLocation(location);
                }
            }
        }
    };

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (REQUEST_CODE_PERMI_LOCATION == requestCode) {
            startLocations();
        } else {
            goCheckPhoto();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtils.showToast(mContext, "拒绝了相关权限，会导致部分功能失败");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        if (mLocationService != null) {
            mLocationService.unregisterListener(mListener); //注销掉监听
            mLocationService.stop(); //停止定位服务
        }
    }


}
