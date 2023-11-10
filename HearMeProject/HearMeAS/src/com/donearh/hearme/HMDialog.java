package com.donearh.hearme;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class HMDialog extends Dialog{

	public HMDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.hm_dialog);
	    
	    setCancelable(false);
	    Button btn_ok = (Button)findViewById(R.id.btn_ok);
	    btn_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	    TextView title = (TextView)findViewById(R.id.title);
	    title.setText("Здравствуйте!");
	    TextView text = (TextView)findViewById(R.id.text);
	    text.setText("dfkjadlf haldf hdl fhlaeskflkasdhf lkadhlka sdhflkdhf lahdsfl kjadhlfkjadsh lfkjadsh flkjasdh flkajsdhf  jdhflajsdhf laksjdhfl kajsdhflkaj hd");
	}
}
