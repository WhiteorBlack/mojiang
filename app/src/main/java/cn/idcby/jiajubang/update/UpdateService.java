package cn.idcby.jiajubang.update;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import java.io.File;

import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.Version;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import pub.devrel.easypermissions.EasyPermissions;

/** 
 * 检测安装更新文件的助手类 
 */  
  
@SuppressLint("NewApi")
public class UpdateService extends Service {

    private Version mVersionInfo ;
    /** 安卓系统下载类 **/  
    DownloadManager manager;
  
    /** 接收下载完的广播 **/  
    DownloadCompleteReceiver receiver;  
  
    /** 初始化下载器 **/  
    private void initDownManager() {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
          
        receiver = new DownloadCompleteReceiver();  
  
        //设置下载地址  
        Request down = new Request(
                Uri.parse(mVersionInfo.getUri()));
          
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以  
        down.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                | Request.NETWORK_WIFI);
          
        // 下载时，通知栏显示
        down.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
        // 显示下载界面  
        down.setVisibleInDownloadsUi(true);  
          
        String sdpath = UpdateManager.FILE_PATH ;

        if(sdpath != null){
            //清空内容--以前保留的apk
            FileUtil.deleteFile(true ,sdpath) ;
            // 设置下载后文件存放的位置
            down.setDestinationInExternalFilesDir(this, sdpath , UpdateManager.FILE_NAME);

            // 将下载请求放入队列
            manager.enqueue(down);

            //注册下载广播
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }else{
            if(!EasyPermissions.hasPermissions(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ToastUtils.showToast(getApplicationContext() ,"没有SD卡存储权限！") ;
            }

            //停止服务并关闭广播
            UpdateService.this.stopSelf();
        }
    }
  
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null){
            mVersionInfo = (Version) intent.getSerializableExtra("VersionInfo");
            if(mVersionInfo != null){
                // 调用下载
                initDownManager();
            }
        }

        return START_NOT_STICKY ;  
    }  
  
    @Override
    public IBinder onBind(Intent intent) {
      
        return null;  
    }  
  
    @Override
    public void onDestroy() {  
  
        // 注销下载广播  
        if (receiver != null)  
            unregisterReceiver(receiver);  
          
        super.onDestroy();  
    }  
  
    // 接受下载完成后的intent  
    class DownloadCompleteReceiver extends BroadcastReceiver {
		@Override
        public void onReceive(Context context, Intent intent) {
  
            //判断是否下载完成的广播  
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                //获取下载的文件id
                long downId = intent.getLongExtra(  
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                  
                //自动安装apk  
                installAPK(downId);
            }  
        }  
  
        /** 
         * 安装apk文件 
         */  
        private void installAPK(long downId) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DEFAULT);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            File apkFile = queryDownloadedApk(downId);
            if(apkFile != null && apkFile.exists()) {
                Uri apkUri = FileUtil.getFileUri(getApplicationContext() ,apkFile) ;
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                SkipUtils.grantUriPermissionForAndrondN(getApplicationContext() , intent , apkUri );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                ToastUtils.showToast(getApplicationContext() ,"安装文件不存在");
            }

            //停止服务并关闭广播  
            UpdateService.this.stopSelf();  
        }  
    }

    public File queryDownloadedApk(long downloadId) {
        File targetApkFile = null;
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = manager.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!"".equals(StringUtils.convertNull(uriString))) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;
    }

}  