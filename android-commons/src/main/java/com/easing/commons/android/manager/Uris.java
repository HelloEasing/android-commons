package com.easing.commons.android.manager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

import androidx.core.content.FileProvider;

import com.easing.commons.android.app.CommonApplication;

import lombok.SneakyThrows;

public class Uris {

    //默认使用CommonApplication的包名作为FileProvider的authority
    //Manifest中FileProvider的authority必须和这个值一致
    public static String AUTHORITY_FILE_PROVIDER = CommonApplication.ctx.getPackageName();

    //绑定自己的Context，而不是CommonApplication
    public static void init(Context ctx) {
        AUTHORITY_FILE_PROVIDER = ctx.getPackageName();
    }

    //获取res.raw下的资源URI
    public static Uri fromResource(String resource) {
        return Uri.parse("android.resource://" + CommonApplication.ctx.getPackageName() + "/raw/" + resource);
    }

    //获取文件的资源URI
    public static Uri fromFile(Context context, String path, String authority) {
        return FileProvider.getUriForFile(context, authority, new File(path));
    }

    //获取文件的资源URI
    public static Uri fromFile(String path) {
        return FileProvider.getUriForFile(CommonApplication.ctx, Uris.AUTHORITY_FILE_PROVIDER, new File(path));
    }

    //通过文件URI解析文件地址
    public static String uriToPath(Context context, Uri uri) {
        if (uri == null)
            return null;

        String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme))
            data = uri.getPath();
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1)
                        data = cursor.getString(index);
                }
                cursor.close();
            }
            if (data == null)
                data = imageUriToPath(context, uri);
        }

        return data;
    }

    //通过图片URI解析图片地址
    private static String imageUriToPath(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;

        // Document
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type))
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return queryFilePathFromMediaDb(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return queryFilePathFromMediaDb(context, contentUri, selection, selectionArgs);
            }
        }

        // MediaStore
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            if (isGooglePhoto(imageUri))
                return imageUri.getLastPathSegment();
            return queryFilePathFromMediaDb(context, imageUri, null, null);
        }

        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme()))
            return imageUri.getPath();

        return null;
    }

    //通过文件URI查询文件路径
    @SneakyThrows
    private static String queryFilePathFromMediaDb(Context context, Uri uri, String selection, String[] args) {
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, args, null);
        if (cursor != null)
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                String path = cursor.getString(index);
                cursor.close();
                return path;
            }
        return null;
    }

    private static boolean isStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhoto(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
