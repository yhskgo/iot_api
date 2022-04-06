package com.hancom.ai.iot.db.user;

import lombok.Data;

@Data
public class User {
	
	private String robotid;
	private String userid;
	private String password;
	private String accesstoken;
	private String refreshtoken;
	private String company;
	
	public User() {
		
	}
	
	public User(String robotId, String userId, String pw, String accessToken, String refreshToken, String companyName) {
		this.robotid=robotId;
		this.userid=userId;
		this.password=pw;
		this.accesstoken=accessToken;
		this.refreshtoken=refreshToken;
		
		this.company=companyName;
	}

}
