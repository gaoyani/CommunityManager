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
import com.huiwei.communitymanager.utils.UrlConstants;
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

public class GoodsItemAdapter extends BaseAdapter {

	private Context mContext;
	protected LayoutInflater mInflater;
	protected Handler handler;
	private List<GoodsInfo> goodsList;
	private SyncImageLoader syncImageLoader;

	public GoodsItemAdapter(Context context, Handler handler,
			List<GoodsInfo> goodsList) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.handler = handler;
		this.goodsList = goodsList;
		syncImageLoader = new SyncImageLoader(mContext);;
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
			convertView = mInflater.inflate(R.layout.goods_item, null);

			viewHolder = new ViewHolder();
			viewHolder.picture = (ImageView) convertView
					.findViewById(R.id.iv_pictrue);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.tv_goods_name);
			viewHolder.price = (TextView) convertView
					.findViewById(R.id.tv_goods_price);
			// viewHolder.split = (ImageView)
			// convertView.findViewById(R.id.iv_split);
			viewHolder.numberView = (NumberView) convertView
					.findViewById(R.id.number_view);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		GoodsInfo goodsInfo = goodsList.get(position);

		viewHolder.name.setText(goodsInfo.name);
		viewHolder.price.setText(mContext.getResources()
				.getString(R.string.rmb) + goodsInfo.price);
		viewHolder.numberView.setData("0", goodsInfo.id, handler);
		
		viewHolder.picture.setTag(goodsInfo.iconUrl);
		loadImage(viewHolder.picture, goodsInfo);

		return convertView;
	}

	private void loadImage(final ImageView iconView, final GoodsInfo info) {

		if (info.iconUrl != null && !info.iconUrl.equals("")) {
			syncImageLoader.loadImage(UrlConstants.PICTURE_URL+info.iconUrl,
					new SyncImageLoader.OnImageLoadListener() {
						@Override
						public void onImageLoad(Bitmap bitmap) {
							if (iconView.getTag() != null
									&& iconView.getTag().equals(info.iconUrl)) {
								if (bitmap == null) {
									iconView.setImageBitmap(null);
									iconView.setBackgroundResource(R.drawable.default_icon);
								} else {
									iconView.setImageBitmap(bitmap);
									iconView.setBackgroundDrawable(null);
								}
							}
						}

						@Override
						public void onError() {
							iconView.setImageBitmap(null);
							iconView.setBackgroundResource(R.drawable.default_icon);
						}
					});

		} else {
			iconView.setImageBitmap(null);
			iconView.setBackgroundResource(R.drawable.default_icon);
		}
	}

	public static class ViewHolder {
		ImageView picture;
		TextView name;
		TextView price;
		NumberView numberView;
		ImageView split;
	}
}
