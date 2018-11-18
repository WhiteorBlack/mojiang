package cn.idcby.jiajubang.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.MD5Util;
import cn.idcby.jiajubang.BuildConfig;
import cn.idcby.jiajubang.application.MyApplication;

public class FileUtil {

    /**
     * 获取file的uri
     * @param file file
     * @return uri
     *
     * 需要适配 android N
     */
    public static Uri getFileUri(Context context ,File file){
        if(null == file || null == context){
            return null ;
        }

        Uri uri ;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        }else{
            uri = Uri.fromFile(file) ;
        }

        return uri ;
    }

    /**
     * 删除SD卡或者手机的缓存图片和目录
     * @param islive 是否保留目录
     * @param filePath 文件夹路径
     */
    public static void deleteFile(boolean islive ,String filePath) {
        try {
            File dirFile = new File(filePath);
            if(!dirFile.exists()){
                return;
            }
            if (dirFile.isDirectory()) {
                String[] children = dirFile.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dirFile, children[i]).delete();
                }
            }
            if(!islive){
                dirFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除SD卡或者手机的缓存图片和目录
     * @param islive 是否保留目录
     * @param file 文件夹
     */
    public static void deleteFile(boolean islive ,File file) {
        try {
            if(!file.exists()){
                return;
            }
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    new File(file, children[i]).delete();
                }
            }
            if(!islive){
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUploadImageCachePath(String imgPath){
        String ts = ".jpg" ;
        if(imgPath.contains(".")){
            ts = imgPath.substring(imgPath.lastIndexOf(".") ,imgPath.length()) ;
        }

        return getUploadCachePath() + MD5Util.MD5Encode(imgPath) + ts ;
    }

    public static String getUploadCachePath(){
        String path = MyApplication.getInstance().getFilesDir().getAbsolutePath() + "/upload/";
        File path1 = new File(path);
        if (!path1.exists()) {
            //若不存在，创建目录，可以在应用启动的时候创建
            path1.mkdirs();
        }
        return path ;
    }

    /**
     * 图片上传获取base64
     * @param filePath path
     * @return base64
     */
    public static String getUploadImageBase64String(String filePath){

        LogUtils.showLog("testCompress" ,"filePath=" + filePath) ;


        //设置参数
        BitmapFactory.Options options = new BitmapFactory.Options() ;
        options.inJustDecodeBounds = true ;
        BitmapFactory.decodeFile(filePath , options) ;

        int bitmapWidth = options.outWidth ;
        int bitmapHeight = options.outHeight ;

        int inSampleSize ;//压缩比例

        float widthRatio = (float) bitmapWidth / MyApplication.getScreenWidth() ;
        float heightRatio = (float) bitmapHeight / MyApplication.getScreenHeight() ;

        float radio = Math.min(widthRatio , heightRatio) ;
        inSampleSize = (int) radio ;

        options.inJustDecodeBounds = false ;
        options.inSampleSize = inSampleSize ;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        LogUtils.showLog("testCompress" ,"widthRatio=" + widthRatio
                + "，heightRatio=" + widthRatio
                + ",inSampleSize=" + inSampleSize) ;

        Bitmap lastBit = BitmapFactory.decodeFile(filePath , options) ;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int bOps = 100;
        // Store the bitmap into output stream(no compress)
        lastBit.compress(Bitmap.CompressFormat.JPEG, bOps, os);

        LogUtils.showLog("testCompress" ,"压缩前的length=" + os.toByteArray().length / 1024 + "kb") ;

        //没发现啥规律，根据测试的数据，暂时做部分处理
        int totalLength = os.toByteArray().length / 1024 ;//KB
        if(totalLength > 11*1024){//大于11M的，直接30起步
            bOps = 30 ;
        }else if(totalLength > 10*1024){//大于10M的，50起步
            bOps = 50 ;
        }else if(totalLength > 5*1024){//大于5M的，60起步
            bOps = 60 ;
        }

        if(bOps < 100){
            os.reset();
            lastBit.compress(Bitmap.CompressFormat.JPEG, bOps, os);
        }

        // Compress by loop
        while ( os.toByteArray().length / 1024 > 700) {

            LogUtils.showLog("testCompress" ,"循环--压缩率=" + bOps) ;

            // Clean up os
            os.reset();
            // interval 10
            bOps -= 10;
            if(bOps < 0){
                bOps = 5 ;
            }
            lastBit.compress(Bitmap.CompressFormat.JPEG, bOps, os);
            if(1 == bOps){
                break;
            }
        }

        LogUtils.showLog("testCompress" ,"最终压缩率=" + bOps) ;

        byte[] resultByte = os.toByteArray() ;

        LogUtils.showLog("testCompress" ,"压缩后的length=" + resultByte.length / 1024 + "kb") ;

        String outPath = getUploadImageCachePath(filePath) ;

        try {
            // Generate compressed image file
            FileOutputStream fos = new FileOutputStream(outPath);
            fos.write(resultByte);
            fos.flush();
            fos.close();

            File file = new File(outPath);
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer,Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null ;
    }

    //  4.4以上  content://com.android.providers.media.documents/document/image:3952
//  4.4以下  content://media/external/images/media/3951
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    //Android 4.4以下版本自动使用该方法
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
