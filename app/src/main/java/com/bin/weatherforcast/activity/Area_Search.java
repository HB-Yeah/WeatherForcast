package com.bin.weatherforcast.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bin.weatherforcast.R;
import com.bin.weatherforcast.listener.myTextWatcher;

public class Area_Search extends BaseActivity {
    BroadcastReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        initView();
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent intent1 = new Intent(Area_Search.this, MainActivity.class);
                startActivity(intent1);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yhb.action.AREASEARCH_DONE");
        intentFilter.setPriority(100);
        this.registerReceiver(myReceiver, intentFilter);
    }

    TextView search_view_top_bar_back_btn;
    EditText search_view_input_edit;
    ListView search_view_tips_list;
    LinearLayout search_view_hot;
    GridView search_view_hot_grid;
    LinearLayout net_error;

    private void initView() {
        search_view_top_bar_back_btn = (TextView) findViewById(R.id.search_view_top_bar_back_btn);
        search_view_input_edit = (EditText) findViewById(R.id.search_view_input_edit);
        search_view_hot = (LinearLayout) findViewById(R.id.search_view_hot);
        search_view_tips_list = (ListView) findViewById(R.id.search_view_tips_list);
        search_view_hot_grid = (GridView) findViewById(R.id.search_view_hot_grid);
        net_error=(LinearLayout)findViewById(R.id.net_error);

        String[] hot_area_name = {"北京", "上海", "广州", "深圳", "珠海", "佛山", "南京", "苏州", "杭州", "济南", "青岛", "郑州", "石家庄", "福州", "厦门"
                , "武汉", "长沙", "成都", "重庆", "太原", "沈阳", "南宁", "西安", "天津"};
        ArrayAdapter<String> grid_adapter = new ArrayAdapter<String>(this, R.layout.hot_grid_item, R.id.hot_grid_string, hot_area_name);
        search_view_hot_grid.setAdapter(grid_adapter);
        search_view_input_edit.addTextChangedListener(new myTextWatcher(this, search_view_tips_list, search_view_hot,net_error));
        search_view_tips_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView list_item_area_name = (TextView) view.findViewById(R.id.search_list_item_area_name);
                TextView list_item_area_id = (TextView) view.findViewById(R.id.search_list_item_area_id);
                SharedPreferences sp = getSharedPreferences("position_list_always", MODE_PRIVATE);
                String s_position_list = sp.getString("list", "");
                String area_name=list_item_area_name.getText().toString();
                String area_id=list_item_area_id.getText().toString();
                if (s_position_list.length() != 0) {
                    JSONArray position_list_JSONArray = JSONArray.parseArray(s_position_list);
                    boolean isExist=false;
                    for(int i=0;i<position_list_JSONArray.size();i++){
                        JSONObject ja_item=position_list_JSONArray.getJSONObject(i);
                        if (ja_item.getString("area_name").equals(area_name)){
                            Intent intent = new Intent();
                            intent.putExtra("current_num", i);
                            intent.setAction("com.yhb.action.AREASEARCH_DONE");
                            sendOrderedBroadcast(intent, null);
                            isExist=true;
                        }
                    }
                    if (!isExist){
                        JSONObject jo = new JSONObject();
                        jo.put("area_name", area_name);
                        jo.put("area_id",area_id );
                        position_list_JSONArray.add(jo);
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putString("list", position_list_JSONArray.toString());
                        ed.commit();
                        Intent intent = new Intent();
                        intent.putExtra("current_num", position_list_JSONArray.size() - 1);
                        intent.setAction("com.yhb.action.AREASEARCH_DONE");
                        sendOrderedBroadcast(intent, null);
                    }

                } else {
                    JSONArray position_list_JSONArray = new JSONArray();
                    JSONObject jo = new JSONObject();
                    jo.put("area_name", area_name);
                    jo.put("area_id", area_id);
                    position_list_JSONArray.add(jo);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("list", position_list_JSONArray.toString());
                    ed.commit();
                    Intent intent = new Intent();
                    intent.putExtra("current_num", 0);
                    intent.setAction("com.yhb.action.AREASEARCH_DONE");
                    sendOrderedBroadcast(intent, null);
                }
//                Intent intent = new Intent();
//                intent.putExtra("list_item_area_name",list_item_area_name.getText().toString());
//                intent.putExtra("list_item_area_id",list_item_area_id.getText().toString());
                setResult(10);
                Area_Search.this.finish();
            }
        });
    }

    public void on_grid_item_click(View v) {
        String btn_area_name = ((Button) v).getText().toString();
        search_view_input_edit.setText(btn_area_name);
        search_view_input_edit.setSelection(btn_area_name.length());
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(myReceiver);
        super.onDestroy();
    }
}
