package com.donearh.hearme.account;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static com.donearh.hearme.account.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
import static com.donearh.hearme.account.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
import static com.donearh.hearme.account.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY;
import static com.donearh.hearme.account.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
import static com.donearh.hearme.account.AccountGeneral.ServerAuth;

import java.util.HashMap;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.donearh.hearme.LoginActivity;

public class HMAuthenticator extends AbstractAccountAuthenticator{

	private final Context mContext;
	public HMAuthenticator(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		
		final Intent intent = new Intent(mContext, LoginActivity.class);
	    intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, accountType);
	    intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
	    intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
	    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
	    final Bundle bundle = new Bundle();
	    bundle.putParcelable(AccountManager.KEY_INTENT, intent);
	    return bundle;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {

		 if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)) {
	            final Bundle result = new Bundle();
	            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
	            return result;
	        }

	        // Extract the username and password from the Account Manager, and ask
	        // the server for an appropriate AuthToken.
	        final AccountManager am = AccountManager.get(mContext);
	        HashMap<String, String> token_data = new HashMap<String, String>();
	        String authToken = am.peekAuthToken(account, authTokenType);
	        
	        String refresh_token = am.getUserData(account, AccountGeneral.KEY_REFRESH_TOKEN);
	        // Lets give another try to authenticate the user
	        if (TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(refresh_token)) {
                try {
                    token_data = ServerAuth.getRefreshAccessToken(refresh_token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
	        }else{
	        	try {
					boolean is_access = ServerAuth.checkAccess(authToken);
					if(!is_access)
						token_data = ServerAuth.getRefreshAccessToken(refresh_token);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		
		if (!token_data.isEmpty()) {
			
			am.setUserData(account, AccountGeneral.KEY_REFRESH_TOKEN, token_data.get(AccountGeneral.KEY_REFRESH_TOKEN));
			am.setAuthToken(account, authTokenType, token_data.get(AccountManager.KEY_AUTHTOKEN));
			
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token_data.get(AccountManager.KEY_AUTHTOKEN));
            result.putString(AccountGeneral.KEY_REFRESH_TOKEN, token_data.get(AccountGeneral.KEY_REFRESH_TOKEN));
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
        else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
        else
            return authTokenType + " (Label)";
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		 final Bundle result = new Bundle();
	        result.putBoolean(KEY_BOOLEAN_RESULT, false);
	        return result;
	}

}
