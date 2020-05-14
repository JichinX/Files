package me.xujichang.lib.files.base;

import android.net.Uri;

import java.util.List;

/**
 * @author xujichang on 2020/5/14.
 */
public interface FileObtainCallback {

    void onFilesObtain(List<Uri> pUris);
}
