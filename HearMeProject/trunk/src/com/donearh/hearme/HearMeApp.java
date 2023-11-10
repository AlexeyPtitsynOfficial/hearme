package com.donearh.hearme;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;


@ReportsCrashes( // will not be used
				formUri = "",
				customReportContent = { ReportField.APP_VERSION_NAME, 
										ReportField.ANDROID_VERSION, 
										ReportField.PHONE_MODEL, 
										ReportField.CUSTOM_DATA, 
										ReportField.STACK_TRACE, 
										ReportField.LOGCAT,
										ReportField.USER_CRASH_DATE},
				formUriBasicAuthLogin = "yourlogin", // optional
				formUriBasicAuthPassword = "y0uRpa$$w0rd", // optional
				httpMethod = org.acra.sender.HttpSender.Method.POST,
				mode = ReportingInteractionMode.TOAST,
				resToastText = R.string.crash_toast_text)

public class HearMeApp extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		/*ACRA.init(this);
		
		CrashReportSender sender = new CrashReportSender("");
		ACRA.getErrorReporter().setReportSender(sender);*/
		super.onCreate();
	}

	
}
