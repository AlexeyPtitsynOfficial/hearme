package com.donearh.hearme;

import java.util.ArrayList;
import java.util.List;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.donearh.hearme.tags.CommonTags;

public class CrashReportSender implements ReportSender{

	public CrashReportSender(String params){
        // initialize your sender with needed parameters
    }
	
	@Override
	public void send(CrashReportData data) throws ReportSenderException {
		// TODO Auto-generated method stub
		;
		URLWithParams urlWithParams = new URLWithParams();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", CommonTags.crash_report));
		params.add(new BasicNameValuePair("app_version_name", data.get(ReportField.APP_VERSION_NAME)));
		params.add(new BasicNameValuePair("android_version", data.get(ReportField.ANDROID_VERSION)));
		params.add(new BasicNameValuePair("phone_model", data.get(ReportField.PHONE_MODEL)));
		params.add(new BasicNameValuePair("custom_data", data.get(ReportField.CUSTOM_DATA)));
		params.add(new BasicNameValuePair("stack_trace", data.get(ReportField.STACK_TRACE)));
		params.add(new BasicNameValuePair("logcat", data.get(ReportField.LOGCAT)));
		params.add(new BasicNameValuePair("user_crash_date", data.get(ReportField.USER_CRASH_DATE)));
		
		urlWithParams.nameValuePairs = params;
		urlWithParams.url = Urls.API + Urls.COMMON;
		
		InsertData insertData = new InsertData();
		insertData.execute(urlWithParams);
	}

}
