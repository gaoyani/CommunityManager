/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * OrderFragment.java
 * 创建人：高亚妮
 * 日     期：2014-6-20
 * 描     述：订单页面显示及操作文件
 * 版     本：v6.0
 *****************************************************/
package com.huiwei.communitymanager.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.adapter.GoodsItemAdapter;
import com.huiwei.communitymanager.info.GoodsInfo;
import com.huiwei.communitymanager.task.CommunityGoodsTask;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.Data;
import com.huiwei.communitymanager.view.LoadingView;

public class CommunityShoppingFragment extends BaseFragment implements OnClickListener {
	
	public static final int UNCONFIRM_LIST = 0;
	public static final int CONFIRM_LIST = 1;
	
	private ListView goodsListView;
	private GoodsItemAdapter goodsItemAdapter;
	
	private TextView goodsNum;
	private TextView goodsPrice;
	
	private LoadingView loadingView;
	
	private int totalNum = 0;
	private List<GoodsInfo> goodsList;
	private int curPage = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_community_shopping, container, false);
		
		goodsNum = (TextView)view.findViewById(R.id.tv_goods_num);
		goodsPrice = (TextView)view.findViewById(R.id.tv_goods_price);
		loadingView = (LoadingView)view.findViewById(R.id.loading_view);
		((ImageButton)view.findViewById(R.id.btn_menu)).setOnClickListener(this);
		((Button)view.findViewById(R.id.button_order)).setOnClickListener(this);
		
		goodsList = new ArrayList<GoodsInfo>();
		goodsListView = (ListView)view.findViewById(R.id.lv_goods);
		goodsItemAdapter = new GoodsItemAdapter(getActivity(), handler, goodsList);
		goodsListView.setAdapter(goodsItemAdapter);
		
		getGoodsList();
		
		return view;
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int num = msg.arg1;
			totalNum += num;
			
			String goodsId = (String) msg.obj;
			GoodsInfo info = Data.findSelGoods(goodsId);
			if (num == 1) {
				if (info == null) {
					info = new GoodsInfo();
					GoodsInfo goodsInfo = findGoods(goodsId);
					info.copy(goodsInfo);
					info.orderNum = num;
					Data.selGoodsList.add(info);
				} else {
					info.orderNum = info.orderNum+1;
				}
			} else if (num == -1){
				if (info != null && info.orderNum == 1) {
					Data.selGoodsList.remove(info);
				} else {
					info.orderNum = info.orderNum-1;
				}
			}	
			
			updatePrice();
		};
	};
	
	private GoodsInfo findGoods(String id) {
		for (GoodsInfo info : goodsList) {
			if (info.id.equals(id)) {
				return info;
			}
		}
		
		return null;
	}
	
	private void updatePrice() {
		goodsNum.setText(String.valueOf(totalNum));
		
		float total = 0;
		for (GoodsInfo info : Data.selGoodsList) {
			float price = Float.parseFloat(info.price)*info.orderNum;
			total += price;
		}

		float result = new BigDecimal(total).setScale(2, 
				BigDecimal.ROUND_HALF_UP).floatValue(); 
		
		goodsPrice.setText(getResources().getString(R.string.rmb)+" "+
				String.valueOf(result)+" "+getResources().getString(R.string.yuan));
	}
	
	private void getGoodsList() {
		for (GoodsInfo info : goodsList) {
			info = null;
		}
		goodsList.clear();
		
		CommunityGoodsTask task = new CommunityGoodsTask(getActivity(), goodsHandler, curPage++);
		task.execute();
	}
	
	Handler goodsHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constants.SUCCESS) {
				curPage = msg.arg1;
				goodsList = (List<GoodsInfo>) msg.obj;
				goodsItemAdapter.setGoodsList(goodsList);
			} else if (msg.what == Constants.LOAD_COMPLETE){
				
			} else if (msg.what == Constants.DATA_ERROR){
				Toast.makeText(getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), R.string.login_error, Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	@Override
	public void onResume() {
		if (Data.selGoodsList.size() == 0) {
			goodsItemAdapter.setGoodsList(goodsList);
			totalNum = 0;
			updatePrice();
		}
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		Log.d("OrderFragment", "onHiddenChanged");
		Log.d("hidden", hidden ? "true" : "false");
		super.onHiddenChanged(hidden);
	}
	
	
	//按钮的点击消息监听
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_menu:
			listener.onMenuPop();
			break;
			
		case R.id.button_order:
			if (Data.selGoodsList.size() == 0) {
				Toast.makeText(getActivity(), R.string.please_choose_goods, Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent();
				intent.setClass(getActivity(), SubmitOrderActivity.class);
				startActivity(intent);
			}
			
			break;
		
		default:
			break;
		}
	}
	
	@Override
	public void onDetach() {
		
		super.onDetach();
	}
}
