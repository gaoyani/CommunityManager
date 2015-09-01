/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * OrderFragment.java
 * 创建人：高亚妮
 * 日     期：2014-6-20
 * 描     述：订单页面显示及操作文件
 * 版     本：v6.0
 *****************************************************/
package com.huiwei.communitymanager.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.adapter.OrderItemAdapter;
import com.huiwei.communitymanager.info.OrderInfo;
import com.huiwei.communitymanager.task.ChangeOrderStatusTask;
import com.huiwei.communitymanager.task.CommunityOrderListTask;
import com.huiwei.communitymanager.task.PaymentTask;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.PayManager;
import com.huiwei.communitymanager.utils.UrlConstants;
import com.huiwei.communitymanager.view.LoadingView;
import com.huiwei.communitymanager.view.OrderPaymentView;

public class OrderManagerActivity extends Activity implements OnClickListener {
	
	private ListView communityOrderListView;
	private OrderItemAdapter adapter;
	private LoadingView loadingView;
	
	private List<OrderInfo> communityOrderList = new ArrayList<OrderInfo>();
	
	private AlertDialog paymentDialog;
	private OrderPaymentView orderPaymentView;
	
	private PayManager payManager;
	private OrderInfo orderInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_manager);

		((ImageButton)findViewById(R.id.btn_return)).setOnClickListener(this);
		
		loadingView = (LoadingView)findViewById(R.id.loading_view);
		
		communityOrderListView = (ListView)findViewById(R.id.lv_order);
		adapter = new OrderItemAdapter(this, communityOrderList, adapterHandler);
		communityOrderListView.setAdapter(adapter);
		communityOrderListView.setOnItemClickListener(itemClickListener);
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			OrderInfo info = communityOrderList.get(position);
			Intent intent = new Intent();
			intent.setClass(OrderManagerActivity.this, OrderDetailActivity.class);
			intent.putExtra("order_id", info.id);
			startActivity(intent);
		}
	};
	
	@Override
	public void onResume() {
		reloadList();
		
		super.onResume();
	}
	
	private void reloadList() {
		loadingView.setVisibility(View.VISIBLE);
		loadingView.setLoadingText(getResources().getString(R.string.loading_load));
		CommunityOrderListTask task = new CommunityOrderListTask(this, communityOrdersHandler);
		task.execute();
	}
	
	private Handler communityOrdersHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constants.SUCCESS) {
				communityOrderList = (List<OrderInfo>) msg.obj;
				adapter.setOrderList(communityOrderList);
			} else if (msg.what == Constants.DATA_ERROR){
				Toast.makeText(OrderManagerActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(OrderManagerActivity.this, R.string.get_community_order_error,
						Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private Handler adapterHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			loadingView.setLoadingText(getResources().getString(R.string.loading_submit));
			
			orderInfo = (OrderInfo)msg.obj;
			if (orderInfo.statusID == OrderInfo.ORDER_STATE_NEW) {
				loadingView.setVisibility(View.VISIBLE);
				ChangeOrderStatusTask task = new ChangeOrderStatusTask(OrderManagerActivity.this, operationHandler, orderInfo.id);
				task.execute(UrlConstants.CANCEL_ORDER_URL);
			} else if (orderInfo.statusID == OrderInfo.ORDER_STATE_CONFIRM) {
				if (paymentDialog == null) {
					paymentDialog = new AlertDialog.Builder(
							OrderManagerActivity.this).create();
					LayoutInflater inflater = LayoutInflater
							.from(OrderManagerActivity.this);
					orderPaymentView = (OrderPaymentView) inflater.inflate(
							R.layout.order_payment_view, null);
					orderPaymentView.setHandler(paymentHandler);
					paymentDialog.setView(orderPaymentView, 0, 0, 0, 0);
				}
				paymentDialog.show();
				
				payManager = new PayManager(OrderManagerActivity.this, orderInfo.id, payResultHandler);
				
			} else if (orderInfo.statusID == OrderInfo.ORDER_STATE_PAY) {
				loadingView.setVisibility(View.VISIBLE);
				ChangeOrderStatusTask task = new ChangeOrderStatusTask(OrderManagerActivity.this, operationHandler, orderInfo.id);
				task.execute(UrlConstants.FINISH_ORDER_URL);
			} 
		};
	};
	
	private Handler paymentHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			loadingView.setVisibility(View.VISIBLE);
			paymentDialog.dismiss();
			
			String channle = (String)msg.obj;
			new PaymentTask(OrderManagerActivity.this, handler, orderInfo.id).execute(channle);
		}
	};
	
	private Handler handler = new Handler() {
		public void dispatchMessage(Message msg) {
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private Handler operationHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constants.SUCCESS) {
				reloadList();
			} else if (msg.what == Constants.DATA_ERROR){
				Toast.makeText(OrderManagerActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
				loadingView.setVisibility(View.GONE);
			} else {
				Toast.makeText(OrderManagerActivity.this, R.string.order_operation_error, Toast.LENGTH_SHORT).show();
				loadingView.setVisibility(View.GONE);
			}
		};
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PaymentTask.REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				payManager.payResult(data);
			}
		}
	}
	
	private Handler payResultHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			reloadList();
		};
	};
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			finish();
			break;

		default:
			break;
		}
		
	}
}
