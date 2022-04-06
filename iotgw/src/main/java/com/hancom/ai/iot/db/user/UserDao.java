package com.hancom.ai.iot.db.user;

import java.util.List;

public interface UserDao {
	
	public List<User> getAll() throws Exception;
	public User getUserByRobotId(String robotId) throws Exception;
	public User getUserByUserId(String userId) throws Exception;
	public void insertUser(User user) throws Exception;
	public void updateByUserId(String userid) throws Exception;
	public void updateUserByUserId(User user) throws Exception;
	

}
