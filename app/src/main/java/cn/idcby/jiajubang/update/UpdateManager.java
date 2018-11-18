package cn.idcby.jiajubang.update;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.idcby.commonlibrary.utils.AppManager;
import cn.idcby.commonlibrary.utils.AppUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.Version;
import cn.idcby.jiajubang.Bean.VersionBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.SkipUtils;
import okhttp3.Call;
import pub.devrel.easypermissions.EasyPermissions;

public class UpdateManager
{

    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;

    //下载地址
    private static final String VERSIONCODEURL = "http://appdown.idcby.cn/Admin/AppPros/GetAppVersion?Appid=MoJiang00001";
    // 外存sdcard存放路径
    public static final String FILE_PATH = MyApplication.getInstance()
            .getExternalFilesDir("/mojiang/download/capk/").getAbsolutePath();
    // 下载应用存放全路径
    public static final String FILE_NAME = "mojiang.apk";

    /* 记录进度条数量 */
    private int progress;
    private String progressStr = "";
    /* 是否取消更新 */
    private boolean mCancelUpdate = false;

    private Activity mContext;
    private boolean isShouDong;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private TextView mProgressTv ;
    private Dialog mDownloadDialog;

    private Version mVersionInfo ;

    public static final int REQUEST_CODE_INSTALL_APK = 1100 ;
    public static final int INSTALL_PACKAGES_REQUESTCODE = 1101 ;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            // 正在下载
            case DOWNLOAD:
                // 设置进度条位置
                mProgress.setProgress(progress);
                mProgressTv.setText(progressStr) ;
                
                break;
            case DOWNLOAD_FINISH:
                // 安装文件
                installApk() ;
                break;
            default:
                break;
            }
        };
    };

    public UpdateManager(Activity context , boolean isShouDong)
    {
        this.mContext = context;
        this.isShouDong = isShouDong;
    }

    /**
     * 检测应用更新信息
     */
    public void checkUpdateInfo() {
        OkHttpUtils.get()
                .url(VERSIONCODEURL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (isShouDong) {
                            ToastUtils.showToast(mContext, e.getMessage());
                        }
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.showLog("testVersion" ,"version=" + response);
                        try {
                            VersionBean verBean = JSON.parseObject(response, VersionBean.class);
                            if(verBean != null){
                                Version version = verBean.getJsonData() ;
                                int curVersionCode = AppUtils.getVersionCode(mContext) ;
                                boolean isNeedUpdate = version.getOrderIndex() > curVersionCode ;
                                if(AppUtils.isAppMastUpdate(mContext)){
                                    version.setIsMust("1") ;
                                }

//                                isNeedUpdate = true ;
//                                version.setIsMust("0") ;

                                if (isNeedUpdate) {
                                    showNoticeDialog(version);
                                } else {
                                    if (isShouDong) {
                                        ToastUtils.showToast(mContext, "已是最新版本");
                                    }
                                }
                            }else{
                                if (isShouDong) {
                                    ToastUtils.showToast(mContext, "版本检查失败");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (isShouDong) {
                                ToastUtils.showToast(mContext, e.getMessage());
                            }
                        }
                    }
                }) ;
    }


    /**
     * 显示提示更新对话框
     */
    private void showNoticeDialog(final Version version) {
        mVersionInfo = version ;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage(version.getMemo());
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                checkIsAndroidO();
            }
        });
        if (!version.isMust()) {
            //非强制升级
            builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else{
            //强制升级
            //非强制升级
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppManager.getAppManager().finishAllActivity();
                    System.exit(0);
                }
            });
        }
        builder.create();
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 显示软件下载对话框
     */
    public void showDownloadDialog() {
    	if(null == mDownloadDialog){
    		mDownloadDialog = new Dialog(mContext, R.style.my_custom_dialog) ;
    		View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_update_ok_cancel_lay, null) ;
    		mDownloadDialog.setContentView(v) ;
    		mDownloadDialog.setCancelable(false);

    		mProgress = (ProgressBar) v.findViewById(R.id.dialog_update_progress);
    		mProgressTv = (TextView) v.findViewById(R.id.dialog_update_progress_tv) ;
    		TextView cancelTv = (TextView) v.findViewById(R.id.dialog_update_cancel_tv) ;
    		
    		cancelTv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mDownloadDialog.dismiss() ;
					mCancelUpdate = true;
					EventBus.getDefault().post(new BusEvent.UpdateEvent(false)) ;
				}
			}) ;
    	}

        mDownloadDialog.show();
        // 下载文件
        try {
            downloadApk();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载apk文件
     */
    private void downloadApk()
    {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                //清空内容--以前保留的apk
                FileUtil.deleteFile(true ,FILE_PATH) ;
	            URL url = new URL(mVersionInfo.getUri()) ;
	            // 创建连接
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.connect();
	            // 获取文件大小
	            int length = conn.getContentLength();
	            //转成M--保留两位小数
	            String lengthStr = getFormatSize(length) ;
	            
	            // 创建输入流
	            InputStream is = conn.getInputStream();
	
	            File file = new File(FILE_PATH);
	            // 判断文件目录是否存在
	            if (!file.exists())
	            {
	                file.mkdir();
	            }
	            
	            File apkFile = new File(FILE_PATH ,FILE_NAME) ;
	            FileOutputStream fos = new FileOutputStream(apkFile);
	            int count = 0;
	            // 缓存
	            byte buf[] = new byte[1024];
	            // 写入到文件中
	            do
	            {
	                int numread = is.read(buf);
	                count += numread;
	                // 计算进度条位置
	                progress = (int) (((float) count / length) * 100);
	                progressStr = getFormatSize(count) + "/" + lengthStr + "" ;
	                
	                // 更新进度
	                mHandler.sendEmptyMessage(DOWNLOAD);
	                if (numread <= 0)
	                {
	                    // 下载完成
	                	mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
	                    break;
	                }
	                // 写入文件
	                fos.write(buf, 0, numread);
	            } while (!mCancelUpdate);// 点击取消就停止下载.
	            fos.close();
	            is.close();
            } catch (Exception e)
            {
                e.printStackTrace();
                LogUtils.showLog("testVersion" ,"e=" + e.toString()) ;

                if(!EasyPermissions.hasPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ToastUtils.showToast(mContext ,"没有SD卡存储权限！") ;
                }else{
                    ToastUtils.showToast(mContext ,"下载出错！") ;
                }

                EventBus.getDefault().post(new BusEvent.UpdateEvent(false)) ;
            }
            
             //取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    }

    public void startUploadApk(boolean isOk){
        if(mVersionInfo != null){
            if(isOk){
                if(mVersionInfo.isMust()){//强制升级--显示升级对话框
                    showDownloadDialog() ;
                }else{//选择升级--后台下载
                    Intent toLoIt = new Intent() ;
                    toLoIt.setClass(mContext, UpdateService.class) ;
                    toLoIt.putExtra("VersionInfo" ,mVersionInfo) ;
                    mContext.startService(toLoIt) ;
                    ToastUtils.showToast(mContext, "从任务栏中查看下载进度！");
                }
            }else{
                if(mVersionInfo.isMust()){//强制升级--显示升级对话框
                    EventBus.getDefault().post(new BusEvent.UpdateEvent(false)) ;
                }
            }
        }
    }

    /**
     * 判断是否是8.0系统,是的话需要获取此权限，判断开没开，没开的话处理未知应用来源权限问题,否则直接安装
     * 2018-06-14 10:48:59
     * 貌似不用特别处理这个权限，当安装apk的时候，系统会自动提示
     */
    private void checkIsAndroidO() {
//        if (Build.VERSION.SDK_INT >= 26) {
//            //注意：如果用EasyPermissions检测未知应用来源权限，会一只没有权限，即使设置权限了
//            //替换新版的EasyPermissions之后编译不过，没空处理，暂时用系统的检测功能
//            boolean installAllowed= mContext.getPackageManager().canRequestPackageInstalls();
//            if (installAllowed) {
//                startUploadApk(true);
//            } else {
//                ActivityCompat.requestPermissions(mContext
//                        , new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}
//                        , INSTALL_PACKAGES_REQUESTCODE);
//            }
//        } else {
//            startUploadApk(true) ;
//        }
        startUploadApk(true);
    }
    /**
     * 安装APK文件
     */
    private void installApk(){
        File appFile = new File(FILE_PATH ,FILE_NAME);
        if (!appFile.exists()) {
            return;
        }
        Uri apkUri = FileUtil.getFileUri(mContext ,appFile) ;
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        SkipUtils.grantUriPermissionForAndrondN(mContext , intent , apkUri );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivityForResult(intent,REQUEST_CODE_INSTALL_APK);
    }

    /**
     * 格式化单位
     *
     * @param size size
     * @return size
     */
    public static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
    }
    
}