package com.huiwei.communitymanager.view;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.info.OrderDetailInfo;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.UrlConstants;

public class OrderPaymentView extends LinearLayout implements OnClickListener {

	private Context mContext;
	
	private Handler handler;
	
	public OrderPaymentView(Context context) {
		super(context);
		mContext = context;
	}
	
	public OrderPaymentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		if (((Button)findViewById(R.id.button_ali)) != null) {
			((Button)findViewById(R.id.button_ali)).setOnClickListener(this);
			((Button)findViewById(R.id.button_wx)).setOnClickListener(this);
			((Button)findViewById(R.id.button_yl)).setOnClickListener(this);
			((Button)findViewById(R.id.button_baidu)).setOnClickListener(this);
		}
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onClick(View v) {
		Message msg = new Message();
		switch (v.getId()) {
		case R.id.button_ali:
			msg.obj = Constants.CHANNEL_ALIPAY;
			break;
			
		case R.id.button_wx:
			msg.obj = Constants.CHANNEL_WECHAT;
			break;
			
		case R.id.button_yl:
			msg.obj = Constants.CHANNEL_UPMP;
			break;
			
		case R.id.button_baidu:
			msg.obj = Constants.CHANNEL_BFB;
			break;

		default:
			break;
		}
		
		handler.sendMessage(msg);
	}
}
