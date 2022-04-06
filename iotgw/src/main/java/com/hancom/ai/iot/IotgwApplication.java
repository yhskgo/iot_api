package com.hancom.ai.iot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {"com.hancom.ai.iot.db.user"})
public class IotgwApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotgwApplication.class, args);
	}
	
//	@Bean
//	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
//	    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//	    sessionFactory.setDataSource(dataSource);
//
//	    Resource[] res 
//	    = new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mappers/*Mapper.xml");
//	    sessionFactory.setMapperLocations(res);
//
//	    return sessionFactory.getObject();
//	}

}
