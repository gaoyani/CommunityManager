package com.huiwei.communitymanager.info;

public class GoodsInfo {
	public String iconUrl;
	public String id;
	public String name;
	public String price;
	
	public int orderNum = 0;
	
	public void copy(GoodsInfo info) {
		this.iconUrl = info.iconUrl;
		this.id = info.id;
		this.name = info.name;
		this.price = info.price;
		this.orderNum = info.orderNum;
	}
}
