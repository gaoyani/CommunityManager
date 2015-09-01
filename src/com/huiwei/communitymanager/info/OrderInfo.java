package com.huiwei.communitymanager.info;

public class OrderInfo {
	
	public static final int ORDER_STATE_NEW = 0;
	public static final int ORDER_STATE_CONFIRM = 1;
	public static final int ORDER_STATE_FINISH = 2;
	public static final int ORDER_STATE_CANCEL = 3;
	public static final int ORDER_STATE_SHOP_CANCEL = 4;
	public static final int ORDER_STATE_PAY = 5;

	public String id;
	public String sn;
	public String time;
	public String totalPrice;
	public int statusID;
	public String status;
}
