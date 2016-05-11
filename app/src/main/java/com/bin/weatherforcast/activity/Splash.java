package com.bin.weatherforcast.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bin.weatherforcast.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("position_list_always", MODE_PRIVATE);
        JSONArray position_JSONArray;
        String s_list=sp.getString("list","");
        if (s_list.length()!=0){
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
        }else{
            Intent intent = new Intent(this,Area_Search.class);
            startActivity(intent);
        }
        this.finish();
    }
}
