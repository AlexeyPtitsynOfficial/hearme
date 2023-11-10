package com.donearh.hearme;

import java.util.ArrayList;
import java.util.List;

import com.donearh.hearme.datatypes.AreaData;
import com.donearh.imageloader.ImageFromRes;
import com.donearh.imageloader.ImageFromResource;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpinnerAreaAdapter extends ArrayAdapter<AreaData>{

	private ImageFromResource mImageFromRes;
	private ArrayList<AreaData> mAreaData = new ArrayList<AreaData>();
	private int mRes;
	
	private Context mContext;
	
	public SpinnerAreaAdapter(Context context, int textViewResourceId,
			ArrayList<AreaData> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		mContext = context;
		mRes = textViewResourceId;
		mAreaData = objects;
		mImageFromRes = new ImageFromResource(mContext);
	}
	
	public void setData(ArrayList<AreaData> data){
		mAreaData = data;
		AreaData ardata = new AreaData();
		ardata.id = -1;
		ardata.name = "Все регионы";
		ardata.icon_res = R.drawable.area_0;
		mAreaData.add(0, ardata);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAreaData.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mAreaData.get(position).id;
	}

	private class ViewHolder{
		TextView text;
		ImageView icon;
	}
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(mRes, null);
			
			
			viewHolder.text = (TextView)convertView.findViewById(R.id.text);
			viewHolder.icon = (RecyclingImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		
		viewHolder.text.setText(mAreaData.get(position).name);
		//viewHolder.icon.setImageDrawable(mAreaData.get(position).icon_res);
		//new ImageFromRes(mContext, viewHolder.icon, mAreaData.get(position).icon_res).execute();
		mImageFromRes.loadBitmap(mAreaData.get(position).icon_res, viewHolder.icon);
		return convertView;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		TextView text;
		if(convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_spinner_area_title, null);
			
			text = (TextView)convertView.findViewById(R.id.text);
			convertView.setTag(text);
		}
		else
		{
			
			text = (TextView)convertView.getTag();
		}
		
		text.setText(mAreaData.get(position).name);
		return convertView;
	}

}
