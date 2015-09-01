package com.huiwei.communitymanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.adapter.SelGoodsItemAdapter;
import com.huiwei.communitymanager.info.OrderDetailInfo;
import com.huiwei.communitymanager.info.OrderInfo;
import com.huiwei.communitymanager.task.ChangeOrderStatusTask;
import com.huiwei.communitymanager.task.OrderDetailTask;
import com.huiwei.communitymanager.task.PaymentTask;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.PayManager;
import com.huiwei.communitymanager.utils.UrlConstants;
import com.huiwei.communitymanager.view.LoadingView;
import com.huiwei.communitymanager.view.OrderPaymentView;

public class OrderDetailActivity extends Activity implements OnClickListener {
	
	private ListView listView;
	private SelGoodsItemAdapter adapter;
	private LoadingView loadingView;
	private Button operation;
	
	private OrderDetailInfo orderDetailInfo;
	
	private AlertDialog paymentDialog;
	private OrderPaymentView orderPaymentView;
	
	private PayManager payManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		
		operation = (Button)findViewById(R.id.button_operation);
		operation.setOnClickListener(this);
		((ImageButton)findViewById(R.id.btn_return)).setOnClickListener(this);
		
		loadingView = (LoadingView)findViewById(R.id.view_loading);
		loadingView.setLoadingText(getResources().getString(R.string.loading_load));
		
		listView = (ListView)findViewById(R.id.lv_goods_sel);
		
		getOrdeInfo();
	}
	
	private void getOrdeInfo() {
		OrderDetailTask task = new OrderDetailTask(this, 
				getOrderHandler, getIntent().getStringExtra("order_id"));
		task.execute();
	}
	
	private Handler getOrderHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constants.SUCCESS) {
				orderDetailInfo = (OrderDetailInfo) msg.obj;
				adapter = new SelGoodsItemAdapter(OrderDetailActivity.this, orderDetailInfo.goodsList);
				listView.setAdapter(adapter);
				setDetailInfo();
			} else if (msg.what == Constants.DATA_ERROR){
				Toast.makeText(OrderDetailActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(OrderDetailActivity.this, R.string.get_order_detail_error, Toast.LENGTH_SHORT).show();
			}
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void setDetailInfo() {
		TextView totalPrice = (TextView)findViewById(R.id.tv_total_price);
		totalPrice.setText(getResources().getString(R.string.order_total_price) + 
				String.valueOf(orderDetailInfo.totalPrice));
		
		TextView detialInfo = (TextView)findViewById(R.id.tv_detail_info);
		detialInfo.setText(String.format("%s%s\n\n%s%s\n\n%s%s\n\n%s%s", 
				getResources().getString(R.string.contact_name), orderDetailInfo.contactName, 
				getResources().getString(R.string.contact_number), orderDetailInfo.contactNumber, 
				getResources().getString(R.string.contact_address), orderDetailInfo.contactAddress, 
				getResources().getString(R.string.order_remark), 
				orderDetailInfo.remark.equals("") ? getResources().getString(R.string.wu) : orderDetailInfo.remark));
		
		if (orderDetailInfo.statusID == OrderInfo.ORDER_STATE_NEW) {
			operation.setText(R.string.operation_cancel);
		} else if (orderDetailInfo.statusID == OrderInfo.ORDER_STATE_CONFIRM) {
			operation.setText(R.string.operation_pay);
		} else if (orderDetailInfo.statusID == OrderInfo.ORDER_STATE_PAY) {
			operation.setText(R.string.operation_finish);
		} else {
			operation.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			finish();
			break;
			
		case R.id.button_operation: {
			loadingView.setLoadingText(getResources().getString(R.string.loading_submit));
			if (orderDetailInfo.statusID == OrderInfo.ORDER_STATE_NEW) {
				loadingView.setVisibility(View.VISIBLE);
				ChangeOrderStatusTask task = new ChangeOrderStatusTask(OrderDetailActivity.this, operationHandler, orderDetailInfo.id);
				task.execute(UrlConstants.CANCEL_ORDER_URL);
			} else if (orderDetailInfo.statusID == OrderInfo.ORDER_STATE_CONFIRM) {
				if (paymentDialog == null) {
					paymentDialog = new AlertDialog.Builder(
							OrderDetailActivity.this).create();
					LayoutInflater inflater = LayoutInflater
							.from(OrderDetailActivity.this);
					orderPaymentView = (OrderPaymentView) inflater.inflate(
							R.layout.order_payment_view, null);
					orderPaymentView.setHandler(paymentHandler);
					paymentDialog.setView(orderPaymentView, 0, 0, 0, 0);
				}
				paymentDialog.show();
				
				payManager = new PayManager(OrderDetailActivity.this, orderDetailInfo.id, payResultHandler);
				
			} else if (orderDetailInfo.statusID == OrderInfo.ORDER_STATE_PAY) {
				loadingView.setVisibility(View.VISIBLE);
				ChangeOrderStatusTask task = new ChangeOrderStatusTask(OrderDetailActivity.this, operationHandler, orderDetailInfo.id);
				task.execute(UrlConstants.FINISH_ORDER_URL);
			} 
		}
			break;
			
		

		default:
			break;
		}
	}

	private Handler paymentHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			loadingView.setVisibility(View.VISIBLE);
			paymentDialog.dismiss();
			
			String channle = (String)msg.obj;
			new PaymentTask(OrderDetailActivity.this, handler, orderDetailInfo.id).execute(channle);
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
				finish();
			} else if (msg.what == Constants.DATA_ERROR){
				Toast.makeText(OrderDetailActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(OrderDetailActivity.this, R.string.order_operation_error, Toast.LENGTH_SHORT).show();
			}
			loadingView.setVisibility(View.GONE);
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
			finish();
		};
	};
}
