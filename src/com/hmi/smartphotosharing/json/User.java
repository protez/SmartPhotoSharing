package com.hmi.smartphotosharing.json;

public class User {
	
	public String uid;
	public String active;
	public String uname;
	public String rname;
	public String pword;
	public String email;
	public String fb_id;
	public String fb_oauth;
	public String picture;
	public String thumb;
	
	public int groups;
	public int photos;
	public int following;
	public int followers;
	
	public long getId() {
		return Long.parseLong(uid);
	}
	
	public String getName() {
		return rname;
	}
}
