package com.donearh.hearme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class AdAddActivity extends AppCompatActivity {

	private SavedData mSavedData;
	private AdAddFragment mAdAddFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_add_frame);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mSavedData = SavedData.getInstance(AdAddActivity.this);
		
		Bundle bundle = new Bundle();
		bundle.putInt("acc_pos", getIntent().getExtras().getInt("acc_pos"));
		bundle.putInt("id_main_area", mSavedData.getMainAreaId());
		
		mAdAddFragment = new AdAddFragment();
		mAdAddFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mAdAddFragment).commit();
	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//getSupportFragmentManager().beginTransaction().remove(mAdAddFragment).commit();
		mAdAddFragment = null;
		super.onDestroy();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.ad_add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == MainControlBarActivity.AD_ADD){
			setResult(RESULT_OK, data);
			finish();
		}
		else{
			mAdAddFragment.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
