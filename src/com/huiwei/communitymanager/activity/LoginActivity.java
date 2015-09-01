package com.huiwei.communitymanager.activity;

import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.task.LoginTask;
import com.huiwei.communitymanager.task.LogoutTask;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.view.EditInputView;
import com.huiwei.communitymanager.view.LoadingView;

import com.huiwei.commonlib.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class LoginActivity extends Activity implements OnClickListener {
	
	private EditInputView username;
	private EditInputView password;
	
	private LoadingView loadingView;
	
	private long mExitTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		initView();
		
		((Button)findViewById(R.id.button_login)).setOnClickListener(this);
		((Button)findViewById(R.id.button_regist)).setOnClickListener(this);
		
		loadingView = (LoadingView)findViewById(R.id.view_loading);
		loadingView.setLoadingText(getResources().getString(R.string.loading_login));
	}
	
	private void initView() {
		username = (EditInputView)findViewById(R.id.view_username);
		password = (EditInputView)findViewById(R.id.view_password);
		
		password.setInfo(getResources().getString(R.string.hint_password),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 
				R.drawable.icon_password);
		
		String saveName = Preferences.GetString(this, Constants.KEY_USER_NAME);
		username.setInfo(getResources().getString(R.string.hint_user_name),
				saveName, InputType.TYPE_CLASS_TEXT, R.drawable.icon_username);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_login: {
			Preferences.SetString(this, Constants.KEY_USER_NAME, username.getText());
			LoginTask task = new LoginTask(LoginActivity.this, handler, username.getText(), password.getText());
			task.execute();
			loadingView.setVisibility(View.VISIBLE);
		}
			break;
			
		case R.id.button_regist:
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, RegistActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	private Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constants.SUCCESS) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else if (msg.what == Constants.DATA_ERROR){
				Toast.makeText(LoginActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - mExitTime > Constants.INTERVAL) {
			Toast.makeText(this, getResources().getString(R.string.exit_app), 
					Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		} else {
			finish();
			System.exit(0);
		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		getMenuInflater().inflate(R.menu.activity_login, menu);
//		return true;
//	}

}
