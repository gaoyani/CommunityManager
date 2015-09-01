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

import com.huiwei.commonlib.FileManager;
import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.info.GoodsInfo;
import com.huiwei.communitymanager.utils.Data;
import com.huiwei.communitymanager.view.NumberView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelGoodsItemAdapter extends BaseAdapter {

	private Context mContext;
	protected LayoutInflater mInflater;
	private List<GoodsInfo> goodsList;

	public SelGoodsItemAdapter(Context context,List<GoodsInfo> goodsList) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.goodsList = goodsList;
	}

	public void setGoodsList(List<GoodsInfo> goodsList) {
		this.goodsList = goodsList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return goodsList.size();
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
			convertView = mInflater.inflate(R.layout.sel_goods_item, null);

			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.price = (TextView) convertView
					.findViewById(R.id.tv_price);
			viewHolder.number = (TextView) convertView
					.findViewById(R.id.tv_number);
			viewHolder.totalPrice = (TextView) convertView
					.findViewById(R.id.tv_total_price);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		GoodsInfo goodsInfo = goodsList.get(position);

		viewHolder.name.setText(goodsInfo.name);
		viewHolder.price.setText(mContext.getResources()
				.getString(R.string.rmb) + goodsInfo.price);
		viewHolder.number.setText(String.valueOf(goodsInfo.orderNum));
		viewHolder.totalPrice.setText(mContext.getResources()
				.getString(R.string.rmb) + String.valueOf(
						Float.valueOf(goodsInfo.price)*goodsInfo.orderNum));

		return convertView;
	}

	public static class ViewHolder {
		TextView name;
		TextView price;
		TextView number;
		TextView totalPrice;
	}
}
