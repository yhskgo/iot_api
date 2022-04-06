package com.hancom.ai.iot.db.user;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoService implements UserDao {
	
	private static final Logger logger = LoggerFactory.getLogger(UserDaoService.class);
	
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<User> getAll() throws Exception {
		// TODO Auto-generated method stub
		logger.debug("getAll");
		UserMapper mapper = sqlSession.getMapper(UserMapper.class);
		return mapper.getAll();
	}

	@Override
	public User getUserByRobotId(String robotId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertUser(User user) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("insertUser");
		UserMapper mapper = sqlSession.getMapper(UserMapper.class);
		
		mapper.insertUser(user);
		
	}

	@Override
	public void updateByUserId(String userid) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User getUserByUserId(String id) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("getUserByUserId");
		UserMapper mapper = sqlSession.getMapper(UserMapper.class);
		return mapper.getUserByUserId(id);
	}

	@Override
	public void updateUserByUserId(User user) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("updateUserByUserId");
		UserMapper mapper = sqlSession.getMapper(UserMapper.class);
		mapper.updateUserByUserId(user);
		
	}

}
