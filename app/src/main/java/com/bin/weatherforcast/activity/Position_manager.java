package com.bin.weatherforcast.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bin.weatherforcast.R;
import com.bin.weatherforcast.adapter.Position_List_Adapter;

import java.util.ArrayList;
import java.util.HashMap;

public class Position_manager extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setContentInsetsRelative(0, 0);

        init();
    }

    ListView position_list;
    ListView position_list_1;
    TextView top_bar_back;
    Button position_plus_button;
    RelativeLayout top_bar_0;
    RelativeLayout top_bar_1;

    //        <include layout="@layout/position_list_item"/>
    SharedPreferences sp;

    private void init() {
        top_bar_0 = (RelativeLayout) findViewById(R.id.top_bar_0);
        top_bar_1 = (RelativeLayout) findViewById(R.id.top_bar_1);
        position_list = (ListView) findViewById(R.id.position_list);
        position_list_1=(ListView)findViewById(R.id.position_list_1);
        top_bar_back = (TextView) findViewById(R.id.top_bar_back);
        top_bar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Position_manager.this.onBackPressed();
            }
        });
        position_plus_button = (Button) findViewById(R.id.position_plus_button);
        position_plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Position_manager.this, Area_Search.class);
                startActivityForResult(intent, 10, null);
            }
        });
        refreshPosition_list();
    }

    ArrayList<HashMap<String, Object>> position_list_data = new ArrayList<HashMap<String, Object>>();
    //暂时改变列表，并将改变的项暂存
    ArrayList<HashMap<String, Object>> position_list_data_1 = new ArrayList<HashMap<String, Object>>();
    private void refreshPosition_list() {
        position_list_data.clear();
        position_list_data_1.clear();
        position_list.setVisibility(View.VISIBLE);
        sp = getSharedPreferences("position_list_always", MODE_PRIVATE);
        String s_list = sp.getString("list", "");
        JSONArray position_JSONArray;
        if (s_list.length() != 0) {
            position_JSONArray = JSONArray.parseArray(s_list);
            for (int i = 0; i < position_JSONArray.size(); i++) {
                JSONObject jo = position_JSONArray.getJSONObject(i);
                HashMap<String, Object> data = new HashMap<>();
                data.put("position_list_item_area_name", jo.getString("area_name"));
                data.put("position_list_item_area_id", jo.getString("area_id"));
                position_list_data.add(data);
                position_list_data_1.add(data);
            }
        }
        if (position_list_data != null) {
            SimpleAdapter position_list_adapter = new SimpleAdapter(this, position_list_data, R.layout.position_list_item
                    , new String[]{"position_list_item_area_name", "position_list_item_area_id"}
                    , new int[]{R.id.position_list_item_area_name, R.id.position_list_item_area_id});
            position_list.setAdapter(position_list_adapter);
            SimpleAdapter position_list_adapter_1 = new SimpleAdapter(this, position_list_data, R.layout.position_list_item_remove_icon_visible
                    , new String[]{"position_list_item_area_name", "position_list_item_area_id"}
                    , new int[]{R.id.position_list_item_area_name_1, R.id.position_list_item_area_id_1});
            position_list_1.setAdapter(position_list_adapter_1);
            position_list_1.setVisibility(View.GONE);
            position_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String areaName=((TextView)(view.findViewById(R.id.position_list_item_area_name))).getText().toString();
//                String areaId=((TextView)(view.findViewById(R.id.position_list_item_area_id))).getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    setResult(0, intent);
                    Position_manager.this.finish();
                }
            });
        }

    }


    public void onPosition_list_item_remove_icon_click(View v) {
        LinearLayout view = (LinearLayout) v.getParent();
//        TextView position_list_item_area_name = (TextView) view.findViewById(R.id.position_list_item_area_name_1);
//        TextView position_list_item_area_id = (TextView) view.findViewById(R.id.position_list_item_area_id_1);
//        JSONObject jo= new JSONObject();
//        jo.put("area_name",position_list_item_area_name.getText().toString());
//        jo.put("area_id", position_list_item_area_id.getText().toString());
//        repository.add(jo);
        int postion = position_list_1.getPositionForView(view);
        position_list_data_1.remove(postion);
        SimpleAdapter position_list_adapter = new SimpleAdapter(this, position_list_data_1, R.layout.position_list_item_remove_icon_visible
                , new String[]{"position_list_item_area_name", "position_list_item_area_id",}
                , new int[]{R.id.position_list_item_area_name_1, R.id.position_list_item_area_id_1});
        position_list_1.setAdapter(position_list_adapter);
        //关于arraylist remove（position）思考
    }
//    private void hide_position_list_item_remove_icon(){
//        int position_list_item_count = position_list.getCount();
//        View[] remove_icon = new View[position_list_item_count];
//        for (int i = 0; i < position_list_item_count; i++) {
//            View view = position_list.getChildAt(i);
//            remove_icon[i] = view.findViewById(R.id.position_list_item_remove_icon);
//            remove_icon[i].setVisibility(View.GONE);
//        }
//    }
//    private void show_position_list_item_remove_icon(){
//        int position_list_item_count = position_list.getCount();
//        View[] remove_icon = new View[position_list_item_count];
//        for (int i = 0; i < position_list_item_count; i++) {
//            View view = position_list.getChildAt(i);
//            remove_icon[i] = view.findViewById(R.id.position_list_item_remove_icon);
//            remove_icon[i].setVisibility(View.VISIBLE);
//        }
//    }为什么会nullpointer呢，有空思考

    public void onTop_bar_edit_button_click(View v) {
        top_bar_0.setVisibility(View.GONE);
        top_bar_1.setVisibility(View.VISIBLE);
        position_plus_button.setVisibility(View.GONE);
        position_list.setVisibility(View.GONE);
        position_list_1.setVisibility(View.VISIBLE);

    }

    public void onTop_bar_cancel_btn_click(View v) {
        top_bar_0.setVisibility(View.VISIBLE);
        top_bar_1.setVisibility(View.GONE);
        position_plus_button.setVisibility(View.VISIBLE);
        refreshPosition_list();
    }

    public void onTop_bar_ensure_button_click(View v) {
        SharedPreferences.Editor editor = sp.edit();
        JSONArray ja = new JSONArray();
        for(int i=0;i<position_list_data_1.size();i++){
            HashMap<String,Object> data = position_list_data_1.get(i);
            JSONObject jo = new JSONObject();
            jo.put("area_name",data.get("position_list_item_area_name"));
            jo.put("area_id",data.get("position_list_item_area_id"));
            ja.add(jo);
        }
        editor.putString("list",ja.toString());
        editor.commit();
        onTop_bar_cancel_btn_click(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 10) {
//            String list_item_area_name = data.getStringExtra("list_item_area_name");
//            String list_item_area_id=data.getStringExtra("list_item_area_id");
            this.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
