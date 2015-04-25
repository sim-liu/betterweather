package com.betterweather.app.service;

import com.betterweather.app.receiver.AutoUpdateReceiver;
import com.betterweather.app.util.HttpCallbackListener;
import com.betterweather.app.util.HttpUitl;
import com.betterweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();

		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		int anHour = 8 * 60 * 60 * 1000; // 这是8个小时的毫秒数
		int anHour = 8000; // 这是1000毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 更新天气信息（更新到文件SharedPreferences）
	 */
	protected void updateWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");

		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";

		HttpUitl.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				Log.d("AutoUpdateService", response);
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
			}
		});
	}

}
