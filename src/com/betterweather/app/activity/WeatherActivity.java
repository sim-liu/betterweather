package com.betterweather.app.activity;

import com.betterweather.app.R;
import com.betterweather.app.service.AutoUpdateService;
import com.betterweather.app.util.HttpCallbackListener;
import com.betterweather.app.util.HttpUitl;
import com.betterweather.app.util.Utility;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	
	private LinearLayout weatherInfoLayout;
	
	//��ʾ������
	private TextView cityNameText;
	//������ʾ����ʱ��
	private TextView publishText;
	//������ʾ��������
	private TextView weatherDespText;
	//������ʾ����1
	private TextView temp1Text;
	//������ʾ����2
	private TextView temp2Text;
	//������ʾ��ǰ����
	private TextView currentDateText;
	
	//�л����а�ť
	private Button switchCity;
	//����������ť
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//��ʼ�����ؼ�
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		
		String countryCode = getIntent().getStringExtra("country_code");
		
		if(!TextUtils.isEmpty(countryCode))
		{
			//���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		}
		else
		{
			//û���ؼ�����ʱ����ֱ����ʾ��������
			showWeather();
		}
		
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		
		case R.id.refresh_weather:
			publishText.setText("ͬ����....");
			SharedPreferences prefs  = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode))
			{
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * ��ѯ�ؼ���������Ӧ��������
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
		queryFromServer(address, "countryCode");
	}
	
	/**
	 * ��ѯ������������Ӧ����������
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode)
	{
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address, "weatherCode");
	}

	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 * @param address
	 * @param string
	 */
	private void queryFromServer(String address, final String type) {
		// TODO Auto-generated method stub
		HttpUitl.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if("countryCode".equals(type))
				{
					if(!TextUtils.isEmpty(response))
					{
						//�ӷ��������ص������н�������������
						String[] array = response.split("\\|");
						if(array != null && array.length == 2)
						{
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}
				else if("weatherCode".equals(type))
				{
					//������������ص�������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
					
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				publishText.setText("ͬ��ʧ��");
			}
		});
	}
	
	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("����"+ prefs.getString("publish_time", "")+ "����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		Log.d("WeatherActivity", "��ʾ����");
		
		//������ʱ����
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
		
	}
}