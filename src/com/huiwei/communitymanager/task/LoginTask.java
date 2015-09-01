package com.huiwei.communitymanager.task;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.commonlib.MD5;
import com.huiwei.communitymanager.info.CommunityInfo;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.Data;
import com.huiwei.communitymanager.utils.UrlConstants;

public class LoginTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	private String userName = null;
	private String passWrod = null;

	int flag = 0;

	public LoginTask(Context context, Handler handler, String username, String password) {
		this.context = context;
		this.handler = handler;
		this.userName = username;
		this.passWrod = password;
	}

	@Override
	protected Integer doInBackground(String... params) {
		flag = Constants.SUCCESS;
		message = new Message();
		String url = UrlConstants.LOGIN_URL;
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("user_name", userName);
			param.put("password", MD5.md5s(passWrod));

			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);

			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("status");
				if (errorCode == 1) {
					JSONObject jsonContent = jsonObject.getJSONObject("list");
					Data.memberInfo.parseJson(jsonContent);
					JSONArray jsonArray = jsonContent.getJSONArray("grouplist");
					Data.communityList.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonCommunity = jsonArray.getJSONObject(i);
						CommunityInfo info = new CommunityInfo();
						info.id = jsonCommunity.getString("gid");
						info.name = jsonCommunity.getString("name");
						Data.communityList.add(info);
					}
				
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
