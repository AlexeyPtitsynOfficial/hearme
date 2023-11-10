package com.donearh.hearme;

import java.io.Serializable;
import java.util.ArrayList;

public class AdListData implements Serializable{

	public Integer Id;
	public Integer User_id;
	public String user_name;
	public Integer area;
	public Integer state;
	public String Title;
	public Integer Parent_cat_id;
	public Integer Category_id;
	public String Category_name;
	public String Cat_img_id;
	public String cat_left_color;
	public String cat_right_color;
	public String Desc;
	public long add_datetime;
	public String user_image_url;
	public ArrayList<String> files_urls = new ArrayList<String>();
}
