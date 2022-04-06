package com.hancom.ai.iot.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hancom.ai.iot.db.family.Family;
import com.hancom.ai.iot.db.family.Family.Families;
import com.hancom.ai.iot.db.family.FamilyRepository;
import com.hancom.ai.iot.db.user.User;
import com.hancom.ai.iot.db.user.UserDaoService;


@RestController
@RequestMapping("/api")
public class FamilyController {
	private static Logger logger = LogManager.getLogger(LoginController.class);

	@Autowired
	UserDaoService userDaoService;
	
	@Autowired
	FamilyRepository familyRepository;

	@RequestMapping(method = RequestMethod.GET, path = "/devices")
	public String getRequestGetDevices(@RequestParam String userId) throws Exception {
		logger.info("getRequestGetDevices");
		BufferedReader in=null;
		User user = null;
		user=userDaoService.getUserByUserId(userId);
		int status=200;
		if(user==null) {
			logger.info("User Id="+userId+" is not exist");
			return userId+" is not exist. Please do register";
		} else {
			URL url=new URL("https://goqual.io/openapi/devices");
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Bearer "+user.getAccesstoken());
			conn.setDoOutput(true);
			
			
			status = conn.getResponseCode();
			if(status==200) {
				
			} else {
				logger.info("getdevice: "+status);
			}
			
			in=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String inputLine;
			while((inputLine=in.readLine())!=null) {
				logger.info(inputLine);
			}
		}
	
		return Integer.toString(status);
	}
	
	@RequestMapping(method=RequestMethod.GET, path="family")
	public String getRequestGetFamily(@RequestParam String userId) throws Exception {
		logger.info("getRequestGetFamily");
		String res = null;
		BufferedReader in = null;
		User user=userDaoService.getUserByUserId(userId);
		int status = 200;
		if(user==null) {
			logger.info("UserId="+userId+"is not exist.");
			return userId+"is not exist";
		} else {
			URL url=new URL("https://goqual.io/openapi/family");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Bearer "+user.getAccesstoken());
			conn.setDoOutput(true);
			status=conn.getResponseCode();
			if(status==200) {
				in=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuilder sb = new StringBuilder();
				while((inputLine=in.readLine())!=null) {
					logger.info(inputLine);
					sb.append(inputLine);
				}
				String result = sb.toString().replace("result", userId);
				JSONObject responseJson = new JSONObject(result);
				JSONArray jsonArray = (JSONArray)responseJson.get(userId);
				String name="";
				String familyid="";
				ArrayList<Families> families = new ArrayList<Family.Families>();
				for(int i=0; i<jsonArray.length(); i++) {
					JSONObject familyObj=(JSONObject)jsonArray.get(i);
					name = familyObj.get("name").toString();
					familyid = familyObj.get("familyId").toString();
					
					logger.info(familyObj.get("name"));
					logger.info(familyObj.get("familyId"));
					families.add(new Family.Families(name, familyid));
				}
				res=result;
				
				Family f = null;
				try {
					f=familyRepository.findByUserid(userId);
				} catch(Exception e) {
					System.out.print(e);
				}
				
				if(f==null) {
					Family family = new Family(userId, families);
					familyRepository.save(family);
				} else {
					for(int i=0; i<f.getFamilies().size(); i++) {
						System.out.print(userId+":name="+f.getFamilies().get(i).getName().toString()+"\n");
						System.out.print(userId+":familyid="+f.getFamilies().get(i).getFamilyid().toString()+"\n");
					}
					Family family = new Family(userId, families);
					family.setId(f.getId());
					familyRepository.save(family);
					
				}
				
				
				
				//Family family = new Family(userId, name, familyid);
				//familyRepository.save(family);
				
				

			}
		}
		
		
		return res;
	}
	

}
