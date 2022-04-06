package com.hancom.ai.iot.db.family;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FamilyRepository extends MongoRepository<Family, String>{
	Family findByUserid(String userid);

}
