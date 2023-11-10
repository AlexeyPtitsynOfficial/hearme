package com.donearh.hearme.account;


public class AccountGeneral {
	public static final String ACCOUNT_TYPE = "com.donearh.hearme";

	public static final String ACCOUNT_NAME = "HearMe";
	public static final String KEY_HM_ACC_TYPE = "account_type";
	public static final String KEY_SOCIAL_TYPE = "social_type";
	public static final String KEY_ACC_POS = "acc_pos";
	public static final String KEY_DESC = "desc_text";
	public static final String KEY_DISPLAY_NAME = "display_name";
	public static final String KEY_IMAGE_URL = "image_url";

	public static final String KEY_ACCESS_TOKEN = "access_token";
	public static final String KEY_REFRESH_TOKEN = "refresh_token";
	
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an HearMe account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an HearMe account";

	public static final ServerAuthenticate ServerAuth = new ServerAuth();
}
