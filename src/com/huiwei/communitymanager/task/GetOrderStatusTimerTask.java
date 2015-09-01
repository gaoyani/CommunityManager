package com.huiwei.communitymanager.task;

import java.util.Timer;
import java.util.TimerTask;

import com.huiwei.communitymanager.utils.Constants;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class GetOrderStatusTimerTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	String orderID;
	Timer timer;

	public GetOrderStatusTimerTask(Context context, Handler handler, String orderID) {
		this.context = context;
		this.handler = handler;
		this.orderID = orderID;
	}

	@Override
	protected Integer doInBackground(String... params) {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					GetOrderStatusTask at = new GetOrderStatusTask(context, handler, orderID);
					at.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, 1000*1);
		
		return null;
	}

	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
		}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
	}

}
