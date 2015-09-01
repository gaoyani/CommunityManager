package com.huiwei.communitymanager.utils;

public class UrlConstants {
	
	
//	public static final String URL_DOMAIN_NAME = "http://192.168.22.248/shequ2/api.php";
	public static final String URL_DOMAIN_NAME = "http://125.35.14.247:8000/shequ/api.php";
	
	
	public static final String LOGIN_URL =  URL_DOMAIN_NAME + "/User/login";
	public static final String REGIST_URL =  URL_DOMAIN_NAME + "/User/register";
	public static final String LOGOUT_URL =  URL_DOMAIN_NAME + "/User/loginout";
	
	public static final String GET_AUTH_CODE = URL_DOMAIN_NAME + "/User/verifycode";
	
	public static final String COMMUNITY_GOODS_URL =  URL_DOMAIN_NAME + "/Shop/lists";
	public static final String COMMUNITY_LIST_URL =  URL_DOMAIN_NAME + "/User/groupList";
	public static final String COMMUNITY_ORDER_LIST_URL =  URL_DOMAIN_NAME + "/Shop/orderList";
	public static final String ORDER_DETAIL_URL =  URL_DOMAIN_NAME + "/Shop/orderDetail";
	
	public static final String ADD_ORDER_URL = URL_DOMAIN_NAME + "/Shop/submitOrder";
	public static final String CANCEL_ORDER_URL = URL_DOMAIN_NAME + "/Shop/cancelOrder";
	public static final String FINISH_ORDER_URL = URL_DOMAIN_NAME + "/Shop/findishOrder";
	 
	
	public static final String PAYMENT_URL = URL_DOMAIN_NAME + "/Pay/shopPay";
	public static final String GET_PAY_RESULT_URL = URL_DOMAIN_NAME + "/Pay/getPayStatus";
	
//	public static final String PICTURE_URL = "http://192.168.22.248/shequ2/Public/Uploads/tongbu/gouwu/";
	public static final String PICTURE_URL = "http://125.35.14.247:8000/shequ/Public/Uploads/tongbu/gouwu/";

}
