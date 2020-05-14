package me.xujichang.lib.files.media.image;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContentResolverCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.common.collect.Lists;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import me.xujichang.lib.files.base.FileObtainCallback;
import me.xujichang.lib.files.base.FileOperate;
import me.xujichang.lib.files.util.FileUtil;
import me.xujichang.lib.permissions.PermissionResult;

/**
 * @author xujichang on 2020/5/14.
 */
public class ImageFiles {

    private static final String TAG = "ImageFiles";

    public static List<Uri> getImages(AppCompatActivity pActivity) {
        if (FileUtil.checkPermissions(pActivity)) {
            return patchUris(pActivity);
        }
        return Lists.newArrayList();
    }

    public static void getImages(final AppCompatActivity pActivity, final FileObtainCallback pCallback) {
        FileUtil.checkPermissions(pActivity, new Observer<PermissionResult>() {
            @Override
            public void onChanged(PermissionResult pResult) {
                if (pResult.getType() == PermissionResult.Type.ACCEPT) {
                    pCallback.onFilesObtain(patchUris(pActivity));
                }
            }
        });
    }

    public static void getImages(final Fragment pFragment, final FileObtainCallback pCallback) {
        FileUtil.checkPermissions(pFragment, new Observer<PermissionResult>() {
            @Override
            public void onChanged(PermissionResult pResult) {
                if (pResult.getType() == PermissionResult.Type.ACCEPT) {
                    pCallback.onFilesObtain(patchUris(pFragment.requireContext()));
                }
            }
        });
    }

    private static List<Uri> patchUris(Context pContext) {
        List<Uri> vUris = Lists.newArrayList();
        ContentResolver vResolver = pContext.getContentResolver();
        Cursor vCursor = ContentResolverCompat.query(vResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc", null);
        if (null != vCursor) {
            while (vCursor.moveToNext()) {
                long vId = vCursor.getLong(vCursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                Uri vUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, vId);
                vUris.add(vUri);
                Log.i(TAG, "getImages: " + vUri);
            }
            vCursor.close();
        }
        return vUris;
    }

    public static List<Uri> getImages(Fragment pFragment) {
        Context vContext = pFragment.requireContext();
        if (FileUtil.checkPermissions(pFragment)) {
            return patchUris(vContext);
        }
        return Lists.newArrayList();
    }

    public static Bitmap getImageBitmap(Context pContext, Uri pUri) {
        ContentResolver vResolver = pContext.getContentResolver();
        try {
            ParcelFileDescriptor vDescriptor = vResolver.openFileDescriptor(pUri, FileOperate.WRITE);
            if (null != vDescriptor) {
                return BitmapFactory.decodeFileDescriptor(vDescriptor.getFileDescriptor());
            }
        } catch (FileNotFoundException pE) {
            pE.printStackTrace();
        }
        return null;
    }

    public static void addBitmapToAlbum(ContentResolver pResolver, Bitmap pBitmap, String displayName, String mimeType, Bitmap.CompressFormat pFormat) {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        vContentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vContentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        } else {
            vContentValues.put(MediaStore.MediaColumns.DATA,
                    String.format("%s/%s/%s", Environment.getExternalStorageDirectory().getPath(), Environment.DIRECTORY_DCIM, displayName));
        }
        Uri uri = pResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, vContentValues);
        if (uri != null) {
            try {
                OutputStream outputStream = pResolver.openOutputStream(uri);
                if (outputStream != null) {
                    pBitmap.compress(pFormat, 100, outputStream);
                    outputStream.close();
                }
            } catch (IOException pE) {
                pE.printStackTrace();
            }
        }
    }

    public static void writeInputStreamToAlbum(ContentResolver pResolver, InputStream pInputStream, String displayName, String mimeType) {
        ContentValues vContentValues = new ContentValues();
        vContentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        vContentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vContentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        } else {
            vContentValues.put(MediaStore.MediaColumns.DATA,
                    String.format("%s/%s/%s", Environment.getExternalStorageDirectory().getPath(), Environment.DIRECTORY_DCIM, displayName));
        }
        BufferedInputStream bis = new BufferedInputStream(pInputStream);
        Uri uri = pResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, vContentValues);
        try {
            if (uri != null) {
                OutputStream outputStream = pResolver.openOutputStream(uri);
                if (outputStream != null) {
                    BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                    byte[] buffer = new byte[1024];
                    int bytes = bis.read(buffer);
                    while (bytes >= 0) {
                        bos.write(buffer, 0, bytes);
                        bos.flush();
                        bytes = bis.read(buffer);
                    }
                    bos.close();
                }
            }
            bis.close();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }
}
