package com.bin.weatherforcast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class WeatherInfoRefreshDone extends BroadcastReceiver {
    Handler handler;
    public WeatherInfoRefreshDone(){}
    public WeatherInfoRefreshDone(Handler handler) {
        this.handler=handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
            boolean isDone = intent.getBooleanExtra("isDone", false);
            if (isDone){
                handler.sendEmptyMessage(0);
            }
            else
            {
                Toast.makeText(context,"更新失败",Toast.LENGTH_SHORT).show();

            }

        /*com.yhb.action.AREASEARCH_DONE*/


    }
}
