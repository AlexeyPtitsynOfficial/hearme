package com.donearh.hearme.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HMAuthenticatorService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		
		HMAuthenticator authenticator = new HMAuthenticator(this);
		return authenticator.getIBinder();
	}

}
