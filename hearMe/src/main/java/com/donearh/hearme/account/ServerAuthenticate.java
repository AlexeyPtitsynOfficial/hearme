package com.donearh.hearme.account;

import java.util.HashMap;

/**
 * User: udinic
 * Date: 3/27/13
 * Time: 2:35 AM
 */
public interface ServerAuthenticate {
    public String userSignUp(final String name, final String email, final String pass, String authType) throws Exception;
    public HashMap<String, String> userSignIn(final String user, final String pass, String authType) throws Exception;
    public HashMap<String, String> getRefreshAccessToken(String refresh_token) throws Exception;
    public boolean checkAccess(String access_token) throws Exception;
}
