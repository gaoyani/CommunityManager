/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * AddDishesItemAdapter.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-23
 * ��     �����ӲͲ˵��б�������
 * ��     ����v6.0
 *****************************************************/

package com.huiwei.communitymanager.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.info.OrderInfo;

public class OrderItemAdapter extends BaseAdapter {

	private Context mContext;
	protected LayoutInflater mInflater;
	private List<OrderInfo> orderList;
	private Handler parentHandler;

	public OrderItemAdapter(Context context,List<OrderInfo> orderList, Handler handler) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.orderList = orderList;
		this.parentHandler = handler;
	}

	public void setOrderList(List<OrderInfo> orderList) {
		this.orderList = orderList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return orderList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*****************************************************
	 * ��������getView �� �룺int position -- ����ͼ�������������е�λ�� View convertView -- ����ͼ
	 * ViewGroup parent -- ����ͼ���ջᱻ���ӵ��ĸ�����ͼ �� ����View -- ָ��position���б���ͼ ��
	 * ������ȡ�б����ƶ�position��ʾ�ض����ݵ���ͼ �����ˣ������� �� �ڣ�2014-6-23
	 *****************************************************/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.order_item, null);

			viewHolder = new ViewHolder();
			viewHolder.id = (TextView) convertView
					.findViewById(R.id.tv_order_id);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.tv_order_time);
			viewHolder.state = (TextView) convertView
					.findViewById(R.id.tv_state);
			viewHolder.totalPrice = (TextView) convertView
					.findViewById(R.id.tv_price);
			viewHolder.operation = (Button) convertView
					.findViewById(R.id.button_operation);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		OrderInfo info = orderList.get(position);

		viewHolder.id.setText(info.sn);
		viewHolder.time.setText(info.time);
		viewHolder.state.setText(info.status);
		viewHolder.totalPrice.setText(mContext.getResources()
				.getString(R.string.rmb) + info.totalPrice);
		
		viewHolder.operation.setTag(position);
		viewHolder.operation.setOnClickListener(clickListener);
		if (info.statusID == OrderInfo.ORDER_STATE_NEW) {
			viewHolder.operation.setVisibility(View.VISIBLE);
			viewHolder.operation.setText(R.string.operation_cancel);
		} else if (info.statusID == OrderInfo.ORDER_STATE_CONFIRM) {
			viewHolder.operation.setVisibility(View.VISIBLE);
			viewHolder.operation.setText(R.string.operation_pay);
		} else if (info.statusID == OrderInfo.ORDER_STATE_PAY) {
			viewHolder.operation.setVisibility(View.VISIBLE);
			viewHolder.operation.setText(R.string.operation_finish);
		} else {
			viewHolder.operation.setVisibility(View.GONE);
		}

		return convertView;
	}
	
	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			OrderInfo info = orderList.get((Integer) v.getTag());
			Message message = new Message();
			message.obj = info;
			parentHandler.sendMessage(message);
		}
	};

	public static class ViewHolder {
		TextView id;
		TextView time;
		TextView state;
		TextView totalPrice;
		Button operation;
	}
}
