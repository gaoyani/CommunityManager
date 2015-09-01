package com.huiwei.communitymanager.info;

import org.json.JSONException;
import org.json.JSONObject;

public class MemberInfo {
	public String userID;
	public String realName;
	public String userName;
	public String phontNumber;
	public String email;
	public String address;
	public String communityID;
	
	public void parseJson(JSONObject jsonContent) throws JSONException {
		userID = jsonContent.getString("id");
		userName = jsonContent.getString("passort");
		realName = jsonContent.getString("username");
		phontNumber = jsonContent.getString("cellphone");
		email = jsonContent.getString("email");
		address = jsonContent.getString("address");
		communityID = jsonContent.getString("gid");
	}
}
