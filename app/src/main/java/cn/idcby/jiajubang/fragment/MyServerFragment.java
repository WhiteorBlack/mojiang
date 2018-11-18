package cn.idcby.jiajubang.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.MyServerPictureAdapter;
import cn.idcby.jiajubang.adapter.MyServerProcuctAdapter;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;
import idcby.cn.imagepicker.GlideImageLoader;
import idcby.cn.imagepicker.ImageConfig;
import idcby.cn.imagepicker.ImageSelector;
import idcby.cn.imagepicker.ImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 我的发布--服务
 *  Create on 2018/05/24
 *
 *  注意：编辑相册功能在 MyServerPictureEditActivity 里面用到了
 *  ，如果该Fragment修改功能了，可以试着把编辑相册功能剥离出去（如果需求是只要编辑相册）
 */

public class MyServerFragment extends BaseFragment implements View.OnClickListener,EasyPermissions.PermissionCallbacks{
    private ListView mListView;
    private MaterialRefreshLayout mRefreshLay ;

    //header 相关 开始
    private TextView mEditText ;
    private View mPicAddLay ;
    private RecyclerView mPicRv ;

    private MyServerPictureAdapter mImageAdapter;
    private ArrayList<String> mAdapterImageList = new ArrayList<>();
    private ArrayList<String> localImageList = new ArrayList<>();
    private ArrayList<String> imageUploadList = new ArrayList<>();
    private int uploadIndex = 0;
    private LoadingDialog loadingDialog;
    private ImageConfig imageConfig;
    private final static int UPLOAD_PHOTO = 23;
    private final static int SINGLE_MAX_IMAGE_COUNT = 99 ;//一次最多选择99张

    private final static int REQUEST_CODE_PERMI_IMAGE = 101 ;
    private final static int REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO = 499;

    private boolean mIsEdit = false ;
    private boolean mIsRefresh = false ;

    //header 相关 结束

    private List<ImageThumb> mPictureList = new ArrayList<>() ;

    private boolean mIsInstall = true ;
    private MyServerProcuctAdapter mServerAdapter ;

    public static MyServerFragment newInstance(boolean isInstall){
        MyServerFragment fragment = new MyServerFragment() ;
        fragment.mIsInstall = isInstall ;
        return fragment ;
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
                        new GetImageBase64Task(localImageList.get(uploadIndex), localImageList.size()).execute() ;
                    }
                    break;
            }
        }
    };

    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        getServerPictureInfo() ;
    }

    @Override
    protected void initView(View view) {
        loadingDialog = new LoadingDialog(mContext) ;
        loadingDialog.setCancelable(false);
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        mListView = view.findViewById(R.id.lay_refresh_lv_list_lv);
        mRefreshLay = view.findViewById(R.id.lay_refresh_lv_refresh_lay) ;

        initHeader() ;
    }

    private void initHeader(){
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_my_server_top,null) ;
        mEditText = headerView.findViewById(R.id.header_my_server_edit_tv) ;
        mPicRv = headerView.findViewById(R.id.header_my_server_pic_rv) ;
        mPicAddLay = headerView.findViewById(R.id.header_my_server_pic_add_iv) ;

        mListView.addHeaderView(headerView);

        mServerAdapter = new MyServerProcuctAdapter() ;
        mListView.setAdapter(mServerAdapter) ;

        mPicRv.setFocusable(false);
        mPicRv.setNestedScrollingEnabled(false);
        mEditText.setOnClickListener(this);
        mPicAddLay.setOnClickListener(this);

        initPhotoContainer() ;
    }

    private void initPhotoContainer() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(mContext
                ,ResourceUtils.dip2px(mContext ,5),getResources().getColor(R.color.color_trans)) ;
        itemDecoration.setOrientation(RvLinearManagerItemDecoration.HORIZONTAL_LIST) ;
        mPicRv.setLayoutManager(layoutManager);
        mPicRv.addItemDecoration(itemDecoration);

        int itemWidHei = (int) ((ResourceUtils.getScreenWidth(mContext)
            - ResourceUtils.dip2px(mContext ,5) * 6) / 4.5F);

        mPicAddLay.getLayoutParams().width = itemWidHei ;
        mPicAddLay.getLayoutParams().height = itemWidHei ;

        mImageAdapter = new MyServerPictureAdapter(mContext, mAdapterImageList ,itemWidHei, new RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(1 == type){//删除
                    //删除要判断当前位置的图片是否已经上传过了，以此判断 uploadIndex 的加减
                    if(position < uploadIndex){
                        uploadIndex -- ;
                    }

                    mAdapterImageList.remove(position) ;
                    localImageList.remove(position) ;
                    imageUploadList.remove(position) ;
                    mImageAdapter.notifyDataSetChanged() ;
                }else if(2 == type){//原图
                    SkipUtils.toImageShowActivity(mActivity , localImageList ,position);
                }
            }
            @Override
            public void onItemLongClickListener(int type, int position) {
            }
        });
        mPicRv.setAdapter(mImageAdapter);
    }

    private void checkPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mActivity, perms)) {
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
                    .mutiSelectMaxSize(SINGLE_MAX_IMAGE_COUNT)
                    // 拍照后存放的图片路径（默认 /temp/picture）
                    .filePath("/temp")
                    // 开启拍照功能 （默认关闭）
                    .showCamera()
                    .isReloadModel(true)
                    .requestCode(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO)
                    .build();

        ImageSelector.open(this, imageConfig);
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.lay_refresh_lv;
    }

    @Override
    protected void initListener() {
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshAll() ;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.header_my_server_edit_tv == vId){
            changeEditState() ;
        }else if(R.id.header_my_server_pic_add_iv == vId){
            checkPermission();
        }
    }

    /**
     * 切换编辑状态
     * 编辑中不能刷新等操作
     */
    private void changeEditState(){
        mIsEdit = !mIsEdit ;
        mEditText.setText(mIsEdit ? "完成" : "编辑");
        mPicAddLay.setVisibility(mIsEdit ? View.VISIBLE : View.GONE);
        mImageAdapter.setEditState(mIsEdit);

        mRefreshLay.setEnabled(!mIsEdit);

        if(!mIsEdit){
            submitPicEidt() ;
        }
    }

    /**
     * 填充内容
     */
    private void updateDisplay(){
        loadPage.showSuccessPage() ;
        loadingDialog.dismiss();

        int size = mPictureList.size() ;

        localImageList.clear();
        mAdapterImageList.clear();
        imageUploadList.clear();

        for(int x = size - 1 ; x >= 0 ; x--){
            ImageThumb thumb = mPictureList.get(x) ;
            String imgUrl = thumb.getThumbImgUrl() ;

            localImageList.add(imgUrl);
            mAdapterImageList.add(imgUrl);
            imageUploadList.add(imgUrl);
        }

        uploadIndex = localImageList.size() ;
        mImageAdapter.notifyDataSetChanged();

        if(mIsRefresh){
            mRefreshLay.finishRefresh() ;
        }
    }

    /**
     * 刷新整个界面
     */
    public void refreshAll(){
        mIsRefresh = true ;

        getServerPictureInfo();
    }

    /**
     * 提交
     */
    private void submitPicEidt(){
        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mContext) ;
        }
        loadingDialog.setLoadingText("正在提交");
        loadingDialog.show() ;

        StringBuffer imgBuilder = new StringBuffer() ;
        for(String imgUrl : imageUploadList){
            imgBuilder.append(imgUrl).append(",") ;
        }

        String thumbList = imgBuilder.toString() ;
        if(thumbList.length() > 1){
            thumbList = thumbList.substring(0 , thumbList.length() - 1) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ID" ,mIsInstall ? "1" : "2") ;
        paramMap.put("Code" ,thumbList) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_MY_PICTURE_ADD, paramMap
                , new RequestObjectCallBack<String>("submitPicEidt", mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        EventBus.getDefault().post(new BusEvent.ServerPicChangedEvent(true));

                        getServerPictureInfo() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 获取列表
     */
    private void getServerPictureInfo(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ID" ,mIsInstall ? "1" : "2") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_MY_PICTURE_GET, paramMap
                , new RequestListCallBack<ImageThumb>("getServerPictureInfo", mContext ,ImageThumb.class) {
                    @Override
                    public void onSuccessResult(List<ImageThumb> bean) {
                        mPictureList.clear();
                        mPictureList.addAll(bean) ;
                        updateDisplay() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateDisplay() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateDisplay() ;
                    }
                });
    }


    /**
     * 获取图片base64
     */
    private class GetImageBase64Task extends AsyncTask<Void,Void,String> {
        private String imageUrl ;
        private int size ;

        public GetImageBase64Task(String imageUrl, int size) {
            this.imageUrl = imageUrl;
            this.size = size;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(null == loadingDialog){
                loadingDialog = new LoadingDialog(mContext) ;
            }
            loadingDialog.setCancelable(false) ;
            loadingDialog.setLoadingText("正在上传(" + (uploadIndex + 1)
                    + "/" + localImageList.size() + ")");
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return FileUtil.getUploadImageBase64String(imageUrl);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(null == s){
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();

                ToastUtils.showErrorToast(mContext, "图片上传失败");
            }else{
                requestUploadPhoto(s,size) ;
            }
        }
    }

    /***
     * 上传图片
     */
    private void requestUploadPhoto(String base64Image, final int size) {
        LinkedHashMap<String, String> para = new LinkedHashMap<>();
        para.put("base64Image", base64Image);

        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_PHOTO, false ,para,
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
                                mAdapterImageList.add(url);

                                if (uploadIndex == size - 1) {
                                    uploadIndex++;

                                    //上传完成
//                                    mAdapterImageList.clear();
//                                    mAdapterImageList.addAll(imageUploadList);
                                    mImageAdapter.notifyDataSetChanged();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_FOR_FITTING_UPLOAD_PHOTO == requestCode){
            if (resultCode == Activity.RESULT_OK && data != null) {
                List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                localImageList.addAll(pathList);

                handler.sendEmptyMessage(UPLOAD_PHOTO);
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(REQUEST_CODE_PERMI_IMAGE == requestCode){
            goCheckPhoto() ;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtils.showToast(mContext ,"拒绝了相关权限，会导致部分功能失败");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults ,this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDataList");
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }

    }

}
