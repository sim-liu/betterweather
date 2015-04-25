package com.betterweather.app.model;

public class Province {
	private int id;
	private String provinceName;
	private String provinceCode;
	public synchronized int getId() {
		return id;
	}
	public synchronized void setId(int id) {
		this.id = id;
	}
	public synchronized String getProvinceName() {
		return provinceName;
	}
	public synchronized void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public synchronized String getProvinceCode() {
		return provinceCode;
	}
	public synchronized void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
}
