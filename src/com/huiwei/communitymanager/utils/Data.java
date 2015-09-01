package com.huiwei.communitymanager.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.info.CommunityInfo;
import com.huiwei.communitymanager.info.GoodsInfo;
import com.huiwei.communitymanager.info.MemberInfo;

public class Data {
	
	public static MemberInfo memberInfo = new MemberInfo();
	public static List<CommunityInfo> communityList = new ArrayList<CommunityInfo>();
	public static List<GoodsInfo> selGoodsList = new ArrayList<GoodsInfo>();
	
	public static List<String> mainMenu = new ArrayList<String>();
	
	public static void fillMainNenu(Context context) {
		mainMenu.add(context.getResources().getString(R.string.menu_user_center));
		mainMenu.add(context.getResources().getString(R.string.menu_community_shopping));
		mainMenu.add(context.getResources().getString(R.string.menu_around_stores));
	}
	
	public static GoodsInfo findSelGoods(String id) {
		for (GoodsInfo info : selGoodsList) {
			if (info.id.equals(id)) {
				return info;
			}
		}
		
		return null;
	}
	
	public static void clearSelGoods() {
		for (GoodsInfo info : selGoodsList) {
			info = null;
		}
		
		selGoodsList.clear();
	}
	
	public static CommunityInfo findCommunityInfo(String id) {
		for (CommunityInfo info : communityList) {
			if (info.id.equals(id)) {
				return info;
			}
		}
		
		return null;
	}
}
