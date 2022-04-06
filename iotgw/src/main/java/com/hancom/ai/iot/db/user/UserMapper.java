package com.hancom.ai.iot.db.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
	
	public List<User> getAll() throws Exception;
	public User getUserByRobotId(String robotId) throws Exception;
	public User getUserByUserId(String userId) throws Exception;
	public void insertUser(User user) throws Exception;
	public void updateByUserId(String userid) throws Exception;
	public void updateUserByUserId(User user);

}
