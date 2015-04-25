package com.betterweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.betterweather.app.model.City;
import com.betterweather.app.model.Country;
import com.betterweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BetterWeatherDB {

	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";

	public static final int VERSION = 1;

	private static BetterWeatherDB betterWeatherDB;
	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化（单例）
	 */
	private BetterWeatherDB(Context context) {
		// TODO Auto-generated constructor stub
		BetterWeatherOpenHelper dbHelper = new BetterWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 获取BetterWeatherDB的实例（单例）
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static BetterWeatherDB getInstance(Context context) {
		if (betterWeatherDB == null) {
			betterWeatherDB = new BetterWeatherDB(context);
		}
		return betterWeatherDB;
	}

	/**
	 * 将Province实例存储到数据库中
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	/**
	 * 从数据库中读取全国所有的省份信息
	 * 
	 * @return
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));

				list.add(province);
			} while (cursor.moveToNext());
		}

		return list;
	}

	/**
	 * 将City实例存储到数据库中
	 * 
	 * @param city
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());

			db.insert("City", null, values);
		}
	}

	/**
	 * 从数据库读取某省下所有的城市信息
	 * 
	 * @param provincedId
	 */
	public List<City> loadCities(int provincedId) {
		List<City> list = new ArrayList<City>();

		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provincedId) }, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provincedId);

				list.add(city);
			} while (cursor.moveToNext());

		}

		return list;
	}

	/**
	 * 将country实例存储到数据库
	 * 
	 * @param counry
	 */
	public void saveCounry(Country country) {
		if (country != null) {
			ContentValues values = new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountryCode());
			values.put("city_id", country.getCityId());

			db.insert("Country", null, values);
		}
	}

	/**
	 * 从数据库中读取某个城市下所有的 县的信息
	 * @param cityId
	 * @return
	 */
	public List<Country> loadCounties(int cityId) {
		List<Country> list = new ArrayList<Country>();

		Cursor cursor = db.query("Country", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Country country = new Country();
				
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCityId(cityId);
				
				list.add(country);
			}while(cursor.moveToNext());
		}
		
		return list;
	}
}
