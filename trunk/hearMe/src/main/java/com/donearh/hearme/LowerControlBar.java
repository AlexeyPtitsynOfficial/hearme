package com.donearh.hearme;

import java.util.ArrayList;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.donearh.hearme.datatypes.LowerBarData;
import com.donearh.hearme.library.MainDatabaseHandler;

public class LowerControlBar extends Fragment implements AnimationListener,
														OnClickListener
{

	private Animation animMove;
	private LinearLayout mLinearLayout;
	private GridView mGridView;
	private ArrayList<LowerBarData> mLowerBarDatas = new ArrayList<LowerBarData>();
	private ButtonAdapter mButtonAdapter;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_lower_bar, container, false);
		
		mLinearLayout = (LinearLayout)rootView.findViewById(R.id.bar_layout);
		mGridView = (GridView)rootView.findViewById(R.id.lower_bar_grid_view);
		
		mButtonAdapter = new ButtonAdapter();
		mGridView.setAdapter(mButtonAdapter);
		Animation a1 = AnimationUtils.loadAnimation(getActivity(), R.anim.move_dialog);
		mLinearLayout.startAnimation(a1);
			
		MainDatabaseHandler db = new MainDatabaseHandler(getActivity());
		mLowerBarDatas = db.getLowerBarData();
		
		return rootView;
	}
	
	public void closeAnim()
	{
		animMove = AnimationUtils.loadAnimation(getActivity(), R.anim.move_dialog_out);
		animMove.setAnimationListener(this);
		mLinearLayout.startAnimation(animMove);
	}
	
	public void updateBar(){
		MainDatabaseHandler db = new MainDatabaseHandler(getActivity());
		mLowerBarDatas = db.getLowerBarData();
		mButtonAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		// Take any action after completing the animation

		// check for zoom in animation
		if (animation == animMove) 
		{
			((MainControlBarActivity)getActivity()).mLowerControlBar = null;
			((MainControlBarActivity)getActivity()).mLowerBarFrame.setVisibility(View.GONE);
			getActivity().getSupportFragmentManager().beginTransaction().remove(LowerControlBar.this).commit();
		}

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	private class ButtonAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		private class ViewHolder{
			RelativeLayout btn;
			ImageView icon;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if(convertView == null){
				viewHolder = new ViewHolder(); 
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.adapter_lower_buttons, null);
				viewHolder.btn = (RelativeLayout)convertView.findViewById(R.id.bar_btn);
				viewHolder.icon = (ImageView)convertView.findViewById(R.id.bar_img);
				viewHolder.btn.setOnClickListener(LowerControlBar.this);
				
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)convertView.getTag();
			}
			viewHolder.btn.setTag(mLowerBarDatas.get(position).menu_type);
			viewHolder.icon.setImageResource(mLowerBarDatas.get(position).icon_res);
			
			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bar_btn:
			((MainControlBarActivity)getActivity()).selectItem((Integer)v.getTag(),-1);
			break;

		default:
			break;
		}
	}
}
