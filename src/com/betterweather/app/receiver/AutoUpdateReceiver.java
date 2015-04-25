package com.betterweather.app.receiver;

import com.betterweather.app.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//接受广播，并且再次启动服务
		Intent i = new Intent(context, AutoUpdateService.class);
		context.startService(i);
		
		Log.d("AutoUpdateReceiver", "再一次启动定时服务");
	}

}
