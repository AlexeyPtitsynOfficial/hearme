package com.donearh.hearme;

import java.io.Serializable;

public class HMUser implements Serializable{
	
	public String accessRoken;
	private String login;
	private String image_url;
	
	public String getLogin(){
		return login;
	}
	
	public String getImageUrl(){
		return image_url;
	}
}
