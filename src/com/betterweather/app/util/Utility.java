package com.betterweather.app.util;

import android.text.TextUtils;

import com.betterweather.app.db.BetterWeatherDB;
import com.betterweather.app.model.City;
import com.betterweather.app.model.Country;
import com.betterweather.app.model.Province;

public class Utility {
	
	/**
	 * 解析和处理服务器返回的省级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(BetterWeatherDB betterWeatherDB, String response)
	{
		if(!TextUtils.isEmpty(response))
		{
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0)
			{
				for(String p : allProvinces)
				{
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据存储到Province表
					betterWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(BetterWeatherDB betterWeatherDB, String response, int provinceId)
	{
		if(!TextUtils.isEmpty(response))
		{
			String[] allCities = response.split(",");
			if(allCities != null && allCities.length > 0)
			{
				for(String p : allCities)
				{
					String[] array = p.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存储到City表
					betterWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCountriesResponse(BetterWeatherDB betterWeatherDB, String response, int cityId)
	{
		if(!TextUtils.isEmpty(response))
		{
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0)
			{
				for(String p : allCounties)
				{
					String[] array = p.split("\\|");
					Country country = new Country();
					
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setCityId(cityId);
					
					//将解析出来的数据存储到Country表
					betterWeatherDB.saveCounry(country);
				}
				return true;
			}
		}
		
		return false;
	}
}
