package com.donearh.hearme;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.donearh.hearme.library.JSONParser;

public class UserReportActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_report);
		
		final EditText content = (EditText)findViewById(R.id.content);
		Button btn_send = (Button)findViewById(R.id.btn_confirm);
		btn_send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String report_text = null;
				if(content.getText().toString().trim().length() != 0){
					try{
						report_text= URLEncoder.encode(content.getText().toString(),"UTF-8");
					}catch (UnsupportedEncodingException e) {
					       e.printStackTrace();
					} 
					
					HashMap<String, String> params =  new HashMap<String, String>();
					
					params.put("tag", "user_report");
					params.put("text", report_text);
					
					params.put("url", Urls.COMMON);
					JSONParser parser = new JSONParser(null, null);
					parser.execute(params);
					finish();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.user_report, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
