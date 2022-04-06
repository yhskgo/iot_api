package com.hancom.ai.iot.db.family;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("family")
public class Family {
	@Id
	private String id;
	private String userid;
	private String name;
	private String familyid;
	List<Families> families;
	
	public Family() {
	}
	
	public Family(String userid, String name, String familyid) {
		this.userid=userid;
		this.name=name;
		this.familyid=familyid;
		
	}
	
	public Family(String userid, List<Families> families) {
		this.userid=userid;
		this.families=families;
	}
	
	public static class Families {
		String name;
		String familyid;
		
		public Families(String name, String familyid) {
			this.name=name;
			this.familyid=familyid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getFamilyid() {
			return familyid;
		}

		public void setFamilyid(String familyid) {
			this.familyid = familyid;
		}
		
		
	}
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFamilyid() {
		return familyid;
	}

	public void setFamilyid(String familyid) {
		this.familyid = familyid;
	}
	
	

	public List<Families> getFamilies() {
		return families;
	}

	public void setFamilies(List<Families> families) {
		this.families = families;
	}

	@Override
	public String toString() {
		return "Family [userid=" + userid + ", name=" + name + ", familyid=" + familyid + "]";
	}
	

}
