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
		//���ܹ㲥�������ٴ���������
		Intent i = new Intent(context, AutoUpdateService.class);
		context.startService(i);
		
		Log.d("AutoUpdateReceiver", "��һ��������ʱ����");
	}

}
