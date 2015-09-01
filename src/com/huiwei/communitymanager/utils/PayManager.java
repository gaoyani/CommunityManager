package com.huiwei.communitymanager.utils;

import android.R.bool;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.task.GetOrderStatusTask;
import com.huiwei.communitymanager.task.GetOrderStatusTimerTask;

public class PayManager {
	
	private Context context;
	private String orderID;
	private Handler parentHandler;
	
	private GetOrderStatusTimerTask task;
	private int taskNum = 0;
	
	public PayManager(Context context, String orderID, Handler handler) {
		this.context = context;
		this.orderID = orderID;
		this.parentHandler = handler;
	}
	
	public void payResult(Intent data) {
		String result = data.getExtras().getString("pay_result");
		/*
		 * /* 处理返回值 * "success" - payment succeed "fail" - payment
		 * failed "cancel" - user canceld "invalid" - payment plugin not
		 * installed
		 */
		if (result.equals("success")) {
			getPayResult();
		} else {
			String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
			String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息

			System.err
					.println("+++++++++++++++++++++++++++++++++ pay_result : "
							+ result
							+ "; error_msg : "
							+ errorMsg
							+ "; extra_msg : " + extraMsg);
			
			if (errorMsg.equals("unionpay_plugin_not_found")) {
				showMsg("", context.getResources().getString(R.string.yl_plugin_not_installed), "");
			} else {
				showMsg(result, errorMsg, extraMsg);
			}
		}
	}
	
//	private void startSyncResultTimer() {
//		task = new GetOrderStatusTimerTask(context, handlerTimer, orderID);
//		task.execute();
//	}
//	
//	private void stopSyncResultTimer() {
//		if (!task.isCancelled()) {
//			task.stopTimer();
//			task.cancel(true);
//			task = null;
//		}
//	}
	
	private void getPayResult() {
		GetOrderStatusTask at = new GetOrderStatusTask(context, handlerTimer, orderID);
		at.execute();
	}

	Handler handlerTimer = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			taskNum++;
			if (msg.what == Constants.SUCCESS) {
				boolean isPaySuccess = msg.arg1 == 1;
				if (isPaySuccess) {
					parentHandler.sendEmptyMessage(0);
					Toast.makeText(context, R.string.pay_success, Toast.LENGTH_SHORT).show();
					taskNum = 0;
				} else {
					if (taskNum >= 10) {
						parentHandler.sendEmptyMessage(0);
						Toast.makeText(context, R.string.pay_fail, Toast.LENGTH_SHORT).show();
						taskNum = 0;
					} else {
						getPayResult();
					}
				}
			} else {
				parentHandler.sendEmptyMessage(0);
				Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
				taskNum = 0;
			}
		}
	};

	public void showMsg(String title, String msg1, String msg2) {
		String str = title;
		if (msg1.length() != 0) {
			str += "\n" + msg1;
		}
		if (msg2.length() != 0) {
			str += "\n" + msg2;
		}
		
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(str);
		builder.setPositiveButton(R.string.ok, null);
		builder.create().show();
	}
}
