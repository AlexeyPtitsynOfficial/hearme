package com.donearh.hearme;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerArrayAdapter extends ArrayAdapter<String>{

	private List<String> mTextArray;
	private int mRes;
	
	private Context mContext;
	
	public SpinnerArrayAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		mContext = context;
		mRes = textViewResourceId;
		mTextArray = objects;
	}
	
	public void setError(View v, CharSequence s){
		TextView name = (TextView) v.findViewById(R.id.text);
		name.setError(s);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView text;
		if(convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(mRes, null);
			
			text = (TextView)convertView.findViewById(R.id.text);
			convertView.setTag(text);
		}
		else
		{
			text = (TextView)convertView.getTag();
		}
		
		text.setText(mTextArray.get(position));
		return convertView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView text;
		if(convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_spinner_title, null);
			
			text = (TextView)convertView.findViewById(R.id.text);
			convertView.setTag(text);
		}
		else
		{
			text = (TextView)convertView.getTag();
		}
		
		if(position == 0)
			text.setTextColor(mContext.getResources().getColor(R.color.light_red));
		text.setText(mTextArray.get(position));
		return convertView;
	}

}
