package com.huiwei.communitymanager.info;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailInfo extends OrderInfo{
	
	public String contactName;
	public String contactNumber;
	public String contactAddress;
	public String remark;
	
	public List<GoodsInfo> goodsList = new ArrayList<GoodsInfo>();
}
