package me.xujichang.lib.files.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import me.xujichang.lib.permissions.LivePermissions;
import me.xujichang.lib.permissions.PermissionResult;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * @author xujichang on 2020/5/14.
 */
public class FileUtil {
    public static final String[] IMAGE_PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static boolean checkPermissions(Context pContext) {
        for (String vPermission : IMAGE_PERMISSIONS) {
            if (PermissionChecker.checkCallingOrSelfPermission(pContext, vPermission) != PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermissions(AppCompatActivity pActivity) {
        new LivePermissions(pActivity).requestPermissions(IMAGE_PERMISSIONS);
        return checkPermissions((Context) pActivity);
    }

    public static boolean checkPermissions(AppCompatActivity pActivity, Observer<PermissionResult> pObserver) {
        new LivePermissions(pActivity).requestPermissions(IMAGE_PERMISSIONS).observe(pActivity, pObserver);
        return checkPermissions((Context) pActivity);
    }

    public static boolean checkPermissions(Fragment pFragment) {
        Context vContext = pFragment.requireContext();
        new LivePermissions(pFragment).requestPermissions(IMAGE_PERMISSIONS);
        return checkPermissions(vContext);
    }

    public static boolean checkPermissions(Fragment pFragment, Observer<PermissionResult> pObserver) {
        Context vContext = pFragment.requireContext();
        new LivePermissions(pFragment).requestPermissions(IMAGE_PERMISSIONS).observe(pFragment, pObserver);
        return checkPermissions(vContext);
    }


    public static void pickFile(AppCompatActivity pActivity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
//        pActivity.startActivityForResult(intent, PICK_FILE);
    }
}
