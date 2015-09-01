package com.huiwei.communitymanager.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.huiwei.commonlib.Preferences;
import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.info.CommunityInfo;
import com.huiwei.communitymanager.task.CommunityListTask;
import com.huiwei.communitymanager.task.GetAuthCodeTask;
import com.huiwei.communitymanager.task.RegistTask;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.Data;
import com.huiwei.communitymanager.utils.UrlConstants;
import com.huiwei.communitymanager.view.EditInputView;
import com.huiwei.communitymanager.view.LoadingView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegistActivity extends Activity implements OnClickListener {
	
	private EditInputView username, password, passwordConfirm, realName, 
		phoneNumber, authCode, email, address;
	private TextView communityChoose;
	private LoadingView loadingView;
	
	private Button getAuthCode;
	private boolean isAuthCodeInvalid = false;
	private boolean isCountDown = false;
	private CountDownTimer countDownTimer;
	
	private CommunityInfo communityInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);

		initEditInput();
		
		communityChoose = (TextView)findViewById(R.id.tv_community);
		communityChoose.setOnClickListener(this);
		loadingView = (LoadingView)findViewById(R.id.view_loading);
		loadingView.setLoadingText(getResources().getString(R.string.loading_submit));
		getAuthCode = (Button)findViewById(R.id.btn_get_auth_code);
		getAuthCode.setOnClickListener(this);
		((Button)findViewById(R.id.button_regist)).setOnClickListener(this);
		((ImageButton)findViewById(R.id.btn_return)).setOnClickListener(this);
		
		getCommunityList();
	}
	
	private void initEditInput() {
		username = (EditInputView)findViewById(R.id.view_username);
		password = (EditInputView)findViewById(R.id.view_password);
		passwordConfirm = (EditInputView)findViewById(R.id.view_confirm_password);
		realName = (EditInputView)findViewById(R.id.view_real_name);
		phoneNumber = (EditInputView)findViewById(R.id.view_phone_number);
		authCode = (EditInputView)findViewById(R.id.view_auth_code);
		email = (EditInputView)findViewById(R.id.view_email);
		address = (EditInputView)findViewById(R.id.view_address);
		
		username.setInfo(getResources().getString(R.string.hint_user_name),
				"", InputType.TYPE_CLASS_TEXT, R.drawable.icon_username);
		password.setInfo(getResources().getString(R.string.hint_password),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 
				R.drawable.icon_password);
		passwordConfirm.setInfo(getResources().getString(R.string.hint_password_confirm),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 
				R.drawable.icon_password);
		realName.setInfo(getResources().getString(R.string.hint_real_name),
				"", InputType.TYPE_CLASS_TEXT, R.drawable.icon_real_name);
		phoneNumber.setInfo(getResources().getString(R.string.hint_phone_number),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER, 
				R.drawable.icon_contact);
		authCode.setInfo(getResources().getString(R.string.hint_auth_code),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER, 
				R.drawable.icon_code);
		email.setInfo(getResources().getString(R.string.hint_email),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS, 
				R.drawable.icon_email);
		address.setInfo(getResources().getString(R.string.hint_address),
				"", InputType.TYPE_CLASS_TEXT, R.drawable.icon_address);
	}
	
	private void getCommunityList() {
		CommunityListTask task = new CommunityListTask(this, null);
		task.execute();
	}
	
	@Override
	public void onDestroy() {
		if (countDownTimer != null)
			countDownTimer.cancel();
		
		super.onDestroy();
	}
	
	private boolean checkInput() {
		if (username.getText().toString().length() == 0) {
			Toast.makeText(this, getResources().getString(
					R.string.please_input_login_name), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (realName.getText().toString().length() == 0) {
			Toast.makeText(this, getResources().getString(
					R.string.please_input_username), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (password.getText().toString().length() < 6) {
			Toast.makeText(this, getResources().getString(
					R.string.password_number_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!password.getText().toString().equals(passwordConfirm.getText().toString())) {
			Toast.makeText(this, getResources().getString(
					R.string.password_confirm_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (authCode.getText().toString().length() == 0) {
			Toast.makeText(this, getResources().getString(
					R.string.please_input_auth_code), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (isAuthCodeInvalid) {
			Toast.makeText(this, getResources().getString(
					R.string.auth_code_invalid), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (email.getText().toString().length() == 0) {
			Toast.makeText(this, getResources().getString(
					R.string.please_input_email), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (address.getText().toString().length() == 0) {
			Toast.makeText(this, getResources().getString(
					R.string.please_input_address), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (communityChoose.getText().toString().length() == 0) {
			Toast.makeText(this, getResources().getString(
					R.string.please_choose_community), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			finish();
			break;
			
		case R.id.tv_community: {
			if (Data.communityList.size() == 0) {
				Toast.makeText(this, getResources().getString(
						R.string.no_community_to_choose), Toast.LENGTH_SHORT).show();
			} else {
				communityChoseDialog();
			}
		}
			break;
			
		case R.id.button_regist: {
			if (checkInput()) {
				RegistTask task  = new RegistTask(RegistActivity.this, registHandler, username.getText(), 
						password.getText(), realName.getText(), phoneNumber.getText(), email.getText(),
						address.getText(), communityInfo.id, authCode.getText());
				task.execute();
				loadingView.setVisibility(View.VISIBLE);
			}
		}
			break;
			
		case R.id.btn_get_auth_code: {
			if (isCountDown)
				return;
						
			if (phoneNumber.getText().toString().equals("")) {
				Toast.makeText(this, getResources().getString(
						R.string.please_input_phone_num), Toast.LENGTH_SHORT).show();
			} else {
				getAuthCode.setBackgroundResource(R.drawable.button_invalid);
				isCountDown = true;
				GetAuthCodeTask task = new GetAuthCodeTask(
						this, authCodeHandler, phoneNumber.getText().toString());
				task.execute(UrlConstants.GET_AUTH_CODE);
			}
		}
			break;

		default:
			break;
		}
	}
	
	private Handler registHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constants.SUCCESS) {
				finish();
			} else if (msg.what == Constants.DATA_ERROR) {
				Toast.makeText(RegistActivity.this, (String)(msg.obj), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegistActivity.this, R.string.regist_error, Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void communityChoseDialog() {
		List<String> communities = new ArrayList<String>();
		for (int i=0; i<Data.communityList.size(); i++) {
			communities.add(Data.communityList.get(i).name);
		}
		new AlertDialog.Builder(this)             
		.setSingleChoiceItems((String[]) communities.toArray(
				new String[communities.size()]), 0, 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        communityInfo = Data.communityList.get(which);
		        communityChoose.setText("    "+communityInfo.name);
		     }
		  }
		)
		.show();
	}
	
	private Handler authCodeHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constants.SUCCESS) {
				isAuthCodeInvalid = false;
				countDownTimer = new CountDownTimer(60000, 1000) {
					
					@Override
					public void onTick(long millisUntilFinished) {
						getAuthCode.setText(""+millisUntilFinished/1000+getResources().
								getString(R.string.second_reget));
					}
					
					@Override
					public void onFinish() {
						getAuthCode.setText(getResources().
								getString(R.string.get_auth_code));
						getAuthCode.setBackgroundResource(R.drawable.button_orange_selector);
						isAuthCodeInvalid = true;
						isCountDown = false;
					}
				};
				countDownTimer.start();
			} else if (msg.what == Constants.DATA_ERROR) {
				Toast.makeText(RegistActivity.this, (String)(msg.obj), Toast.LENGTH_SHORT).show();
				isCountDown = false;
				getAuthCode.setBackgroundResource(R.drawable.button_orange_selector);
			} else {
				Toast.makeText(RegistActivity.this, getResources().
						getString(R.string.get_auth_code_fail), Toast.LENGTH_SHORT).show();
				isCountDown = false;
				getAuthCode.setBackgroundResource(R.drawable.button_orange_selector);
			}
		};
	};
}
