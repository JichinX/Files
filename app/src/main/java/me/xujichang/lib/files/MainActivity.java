package me.xujichang.lib.files;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import me.xujichang.lib.files.base.FileObtainCallback;
import me.xujichang.lib.files.media.image.ImageFiles;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageFiles.getImages(this, new FileObtainCallback() {
            @Override
            public void onFilesObtain(List<Uri> pUris) {
                Log.i(TAG, "onFilesObtain: " + pUris);
            }
        });
    }
}