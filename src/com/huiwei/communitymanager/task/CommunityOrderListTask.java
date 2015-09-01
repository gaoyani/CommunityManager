package com.huiwei.communitymanager.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.huiwei.communitymanager.info.CommunityInfo;
import com.huiwei.communitymanager.info.OrderInfo;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.Data;
import com.huiwei.communitymanager.utils.UrlConstants;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class CommunityOrderListTask extends
		AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;

	public CommunityOrderListTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constants.SUCCESS;
		
		try {
			HttpPost request = new HttpPost(UrlConstants.COMMUNITY_ORDER_LIST_URL);
			JSONObject param = new JSONObject();
			param.put("id", Data.memberInfo.userID);
			
			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("status");
				if (errorCode == 1) {
					JSONArray jsonArray = jsonObject.getJSONArray("list");
					List<OrderInfo> orderList = new ArrayList<OrderInfo>();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonOrder = jsonArray.getJSONObject(i);
						OrderInfo info = new OrderInfo();
						info.id = jsonOrder.getString("oid");
						info.sn = jsonOrder.getString("ids");
						info.time = jsonOrder.getString("subtime");
						info.totalPrice = jsonOrder.getString("tprice");
						info.statusID = Integer.valueOf(jsonOrder.getString("status"));
						info.status = jsonOrder.getString("statusdes");
						orderList.add(info);
					}
					message.obj = orderList;
				} else {
					message.obj = jsonObject.getString("msg");
					flag = Constants.DATA_ERROR;
				}
			} else {
				flag = Constants.NET_ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = Constants.OTHER_ERROR;
		}
		
		return flag;
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (handler != null) {
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
