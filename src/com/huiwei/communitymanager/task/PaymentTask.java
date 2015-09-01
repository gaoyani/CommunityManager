package com.huiwei.communitymanager.task;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huiwei.commonlib.MD5;
import com.huiwei.communitymanager.info.GoodsInfo;
import com.huiwei.communitymanager.info.MemberInfo;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.Data;
import com.huiwei.communitymanager.utils.UrlConstants;
import com.pingplusplus.android.PaymentActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class PaymentTask extends AsyncTask<String, Void, String> {
	public static final int REQUEST_CODE_PAYMENT = 1;

	Context context = null;
	Message message = null;
	String orderID;
	Handler handler;

	public PaymentTask(Context context, Handler handler, String orderID) {
		this.context = context;
		this.handler = handler;
		this.orderID = orderID;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected String doInBackground(String... params) {

		String data = null;
		String json = String.format(
				"{\"id\":\"%s\",\"oid\":\"%s\",\"channel\":\"%s\"}",
				Data.memberInfo.userID, orderID, params[0]);
		try {
			// 向Your Ping++ Server SDK请求数据
			data = postJson(UrlConstants.PAYMENT_URL, json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("----------------------- data : " + data);

		return data;
	}

	private String postJson(String url, String json) throws IOException {
		MediaType type = MediaType.parse("application/json; charset=utf-8");
		System.out.println("------------- json : " + json);
		RequestBody body = RequestBody.create(type, json);
		Request request = new Request.Builder().url(url).post(body).build();
		OkHttpClient client = new OkHttpClient();
		Response response = client.newCall(request).execute();

		return response.body().string();
	}

	@Override
	protected void onPostExecute(String data) {
		Log.d("charge", data);

		JSONObject dataJson = null;
		JSONObject chargeJson = null;
		try {
			dataJson = new JSONObject(data);
			chargeJson = dataJson.getJSONObject("charge");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (chargeJson != null) {
			Intent intent = new Intent();
			String packageName = context.getPackageName();
			System.out.println("=================== packageName : "
					+ packageName);
			ComponentName componentName = new ComponentName(packageName,
					packageName + ".wxapi.WXPayEntryActivity");
			intent.setComponent(componentName);
			intent.putExtra(PaymentActivity.EXTRA_CHARGE, chargeJson.toString());
			((Activity) context).startActivityForResult(intent,
					REQUEST_CODE_PAYMENT);

			// UPPayAssistEx.startPayByJAR(MainActivity.this,PayActivity.class,null,null,data,"01");
			// UPPayAssistEx.startPay(MainActivity.this,null,null,data,"01");
		} else {
			Toast.makeText(context, "charge data error", Toast.LENGTH_SHORT)
					.show();
		}
		
		handler.sendEmptyMessage(0);
	}

}
