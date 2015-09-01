package com.huiwei.communitymanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.adapter.SelGoodsItemAdapter;
import com.huiwei.communitymanager.info.GoodsInfo;
import com.huiwei.communitymanager.task.AddOrderTask;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.Data;
import com.huiwei.communitymanager.view.EditInputView;
import com.huiwei.communitymanager.view.LoadingView;

public class SubmitOrderActivity extends Activity implements OnClickListener {
	
	private ListView listView;
	private SelGoodsItemAdapter adapter;
	private EditInputView contactName, contactNumber, contactAddress;
	private EditText orderRemark;
	private LoadingView loadingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_order);

		initEditInput();
		
		((Button)findViewById(R.id.button_submit)).setOnClickListener(this);
		((ImageButton)findViewById(R.id.btn_return)).setOnClickListener(this);
		
		loadingView = (LoadingView)findViewById(R.id.view_loading);
		loadingView.setLoadingText(getResources().getString(R.string.loading_submit));
		
		listView = (ListView)findViewById(R.id.lv_goods_sel);
		adapter = new SelGoodsItemAdapter(this, Data.selGoodsList);
		listView.setAdapter(adapter);
		
		updateTotalPrice();
	}
	
	private void initEditInput() {
		contactName = (EditInputView)findViewById(R.id.view_real_name);
		contactNumber = (EditInputView)findViewById(R.id.view_phone_number);
		contactAddress = (EditInputView)findViewById(R.id.view_address);
		orderRemark = (EditText)findViewById(R.id.et_remark);

		contactName.setInfo(getResources().getString(R.string.hint_real_name),
				Data.memberInfo.realName, InputType.TYPE_CLASS_TEXT, -1);
		contactNumber.setInfo(getResources().getString(R.string.hint_phone_number),
				Data.memberInfo.phontNumber, InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER, 
				-1);
		contactAddress.setInfo(getResources().getString(R.string.hint_address),
				Data.memberInfo.address, InputType.TYPE_CLASS_TEXT, -1);
	}
	
	private void updateTotalPrice() {
		TextView totalPrice = (TextView)findViewById(R.id.tv_total_price);
		float price = 0;
		for (GoodsInfo info : Data.selGoodsList) {
			price += Float.valueOf(info.price)*info.orderNum;
		}
		
		totalPrice.setText(getResources().getString(R.string.order_total_price) + 
				String.valueOf(price));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private boolean checkInput() {
		if (contactName.getText().toString().length() == 0 || 
				contactNumber.getText().toString().length() == 0 || 
				contactAddress.getText().toString().length() == 0) {
			Toast.makeText(this, getResources().getString(
					R.string.input_contact_info), Toast.LENGTH_SHORT).show();
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
			
		case R.id.button_submit: {
			if (checkInput()) {
				loadingView.setVisibility(View.VISIBLE);
				AddOrderTask task = new AddOrderTask(SubmitOrderActivity.this, submitHandler,
						contactName.getText(), contactNumber.getText(), contactAddress.getText(), 
						orderRemark.getText().toString());
				task.execute();
			}
		}
			break;
			
		

		default:
			break;
		}
	}
	
	private Handler submitHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constants.SUCCESS) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SubmitOrderActivity.this);
				builder.setMessage(R.string.message_order_submit);
				builder.setPositiveButton(getResources().getString(R.string.ok),  new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						Data.clearSelGoods();
					}
				});
				builder.create().show();
				
			} else if (msg.what == Constants.DATA_ERROR){
				Toast.makeText(SubmitOrderActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(SubmitOrderActivity.this, R.string.regist_error, Toast.LENGTH_SHORT).show();
			}
			loadingView.setVisibility(View.GONE);
		};
	};

}
