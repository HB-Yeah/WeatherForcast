package com.bin.weatherforcast.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bin.weatherforcast.R;
import com.bin.weatherforcast.SystemBarTintManager;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSystemBar(this);
    }

    public static void initSystemBar(Activity activity) {


        SystemBarTintManager tintManager = new SystemBarTintManager(activity);

        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);

// 使用颜色资源

        tintManager.setStatusBarTintResource(R.color.colorAccent);
    }
}
