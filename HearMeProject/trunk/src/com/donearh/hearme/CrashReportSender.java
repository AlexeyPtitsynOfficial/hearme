package com.donearh.hearme;

import java.util.HashMap;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import android.content.Context;

import com.donearh.hearme.library.JSONParser;
import com.donearh.hearme.tags.CommonTags;

public class CrashReportSender implements ReportSender{

	public CrashReportSender(String params){
        // initialize your sender with needed parameters
    }

	@Override
	public void send(Context arg0, CrashReportData data)
			throws ReportSenderException {
		// TODO Auto-generated method stub
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("tag", CommonTags.crash_report);
		params.put("app_version_name", data.get(ReportField.APP_VERSION_NAME));
		params.put("android_version", data.get(ReportField.ANDROID_VERSION));
		params.put("phone_model", data.get(ReportField.PHONE_MODEL));
		params.put("custom_data", data.get(ReportField.CUSTOM_DATA));
		params.put("stack_trace", data.get(ReportField.STACK_TRACE));
		params.put("logcat", data.get(ReportField.LOGCAT));
		params.put("user_crash_date", data.get(ReportField.USER_CRASH_DATE));
		
		params.put("url", Urls.COMMON);
		
		JSONParser parser = new JSONParser(null, null);
		parser.execute(params);
	}

}
