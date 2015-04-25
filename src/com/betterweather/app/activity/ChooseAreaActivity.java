package com.betterweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.betterweather.app.R;
import com.betterweather.app.db.BetterWeatherDB;
import com.betterweather.app.model.City;
import com.betterweather.app.model.Country;
import com.betterweather.app.model.Province;
import com.betterweather.app.util.HttpCallbackListener;
import com.betterweather.app.util.HttpUitl;
import com.betterweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private BetterWeatherDB betterWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	// 省列表
	private List<Province> provinceList;
	// 市列表
	private List<City> cityList;
	// 县列表
	private List<Country> countryList;

	// 选中的省份
	private Province selectedProvince;
	// 选中的城市
	private City selectedCity;
	// 当前选中的级别
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//读取SharedPreferences是否已经选取过城市，为true，则直接进入天气页面
		if(prefs.getBoolean("city_selected", false))
		{
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return ;
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);

		listView.setAdapter(adapter);

		betterWeatherDB = BetterWeatherDB.getInstance(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int index, long id) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				}
				else if(currentLevel == LEVEL_COUNTRY)
				{
					String countryCode = countryList.get(index).getCountryCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
				}
			}
		});

		queryProvinces();
	}

	/**
	 * 查询全国所有的省，优先从数据查询，如果没有查询到再去服务器上查询。
	 */
	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList = betterWeatherDB.loadProvinces();

		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}

			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 查询选中省内所有的城市，优先从数据库中查询，如果没有查询到再去服务器上查询。
	 */
	protected void queryCities() {
		// TODO Auto-generated method stub
		cityList = betterWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询选中城市内所有的县，优先从数据库中查询，如果没有再去服务器上查询
	 */
	protected void queryCounties() {
		// TODO Auto-generated method stub
		countryList = betterWeatherDB.loadCounties(selectedCity.getId());

		if (countryList.size() > 0) {
			dataList.clear();
			for (Country country : countryList) {
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "country");
		}
	}

	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * 
	 * @param cityCode
	 * @param string
	 */
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;

		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml"; // 全国所有的省名称以及省份代码
		}

		showProgressDialog();

		HttpUitl.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;

				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(betterWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(betterWeatherDB,
							response, selectedProvince.getId());
				} else if ("country".equals(type)) {
					result = Utility.handleCountriesResponse(betterWeatherDB,
							response, selectedCity.getId());
				}

				if (result) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("provice".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("country".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();
						
					}
				});
			}
		});

	}

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog == null)
		{
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载中...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度条对话框
	 */
	private void closeProgressDialog()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获back案件，根据当前的级别判断，此时应该返回市列表、省列表，还是直接退出
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(currentLevel == LEVEL_COUNTRY)
		{
			queryCities();
		}
		else if(currentLevel == LEVEL_CITY)
		{
			queryProvinces();
		}
		else
		{
			finish();
		}
	}
}
