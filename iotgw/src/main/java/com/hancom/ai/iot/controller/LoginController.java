package com.hancom.ai.iot.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hancom.ai.iot.db.user.User;
import com.hancom.ai.iot.db.user.UserDaoService;


@RestController
@RequestMapping("/api")
public class LoginController {
	private static Logger logger = LogManager.getLogger(LoginController.class);
	@Autowired
	UserDaoService userDaoService;

	@RequestMapping(method = RequestMethod.POST, path = "/login")
	public String postRequestLogin(@RequestParam String id, @RequestParam String pw) {

		logger.debug("IoT LoginController");

		BufferedReader in=null;
		String authCode="auth code nothing";
		String robotId = "jacob@hancom.com";
		String userId="";
		String comPany="goqual";
		User user = null;
		try {
			user = userDaoService.getUserByUserId(id);
			//logger.info(user.getUserid());
			if(user!=null) {
				user.getUserid();
				logger.info(user.getUserid());
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.info(user.getUserid()+" is NULL");
		}

		if(user!=null) {
			logger.info("Already IoT user exist..."+user.toString());
			userId=user.getUserid();
			return "Aleadry IoT User as user id: "+userId;
		} else {
			try {



				System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
				URL url = new URL("https://goqual.io/oauth/login");
				logger.info("SSL setting");
				Map<String, Object> params = new LinkedHashMap<>();
				params.put("vendor", "openapi");

				StringBuilder postData = new StringBuilder();
				for(Map.Entry<String, Object> param:params.entrySet()) {
					if(postData.length()!=0) postData.append('&');
					postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
					postData.append('=');
					postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

				}
				byte[] postDataBytes = postData.toString().getBytes("UTF-8");

				HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();

				conn.setRequestMethod("POST");
				conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
				conn.setRequestProperty("Access-Control-Allow-Origin", "http://localhost:3000");
				conn.setRequestProperty("Connection", "close");
				conn.setRequestProperty("Origin", "http://localhost:3000");
				conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
				String encoded = Base64.getEncoder().encodeToString((id+":"+pw).getBytes(StandardCharsets.UTF_8));
				conn.setRequestProperty("Authorization", "Basic "+encoded);


				conn.setDoOutput(true);

				conn.connect();

				conn.getOutputStream().write(postDataBytes);

				int code = conn.getResponseCode();

				if(code!=HttpURLConnection.HTTP_OK) {
					return "login error with "+code;
				}

				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

				String inputLine;
				while((inputLine = in.readLine())!=null) {
					logger.info(inputLine);
				}

			} catch(Exception e) {
				logger.info("URL exception error CCC");
				logger.info(e.getMessage());
			} finally {
				if(in!=null) {
					try {
						in.close();

						authCode = getAuthCode(id, pw);
						int idx = authCode.indexOf("=");
						String code = authCode.substring(idx+1);

						authCode = code;

						User addUser = new User(robotId, id, pw, authCode, "", comPany);
						userDaoService.insertUser(addUser);

						logger.info("Next: goto authorize="+code);

						String res = getAuthToken(id, pw, "goqual", code);

						if(res.isEmpty()) {
							res="Token Error Occured!";
						}

						return "GOQUAL Token="+res;

						//URL obj = new URL("https://goqual.io/oauth/authorize");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return "GOQUAL IoT Code: "+authCode;
	}

	private String getAuthCode(String id, String pw) {
		BufferedReader in = null;
		String res = "https://iot.hancom-toki.com?code=";

		try {
			URL url = new URL("https://goqual.io/oauth/authorize");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("response_type", "code");
			params.put("client_id", "1530526115fe41929e2ff5c87d3a1dc8");
			params.put("scope", "openapi");
			params.put("redirect_uri", "https://iot.hancom-toki.com");

			StringBuilder getData = new StringBuilder();
			for(Map.Entry<String, Object> param:params.entrySet()) {
				if(getData.length()!=0) getData.append('&');
				getData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				getData.append('=');
				getData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

			}
			byte[] getDataBytes = getData.toString().getBytes("UTF-8");
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Origin", "http://localhost:3000");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("Content-Length", String.valueOf(getDataBytes.length));
			String encoded = Base64.getEncoder().encodeToString((id+":"+pw).getBytes(StandardCharsets.UTF_8));
			conn.setRequestProperty("Authorization", "Basic "+encoded);
			conn.setDoOutput(true);
			conn.getOutputStream().write(getDataBytes);

			int status = conn.getResponseCode();
            logger.info("res status="+status);

            if(status==302) {
            	logger.info("\nGet Response Header By Key ... Location : code");
    			String authcode = conn.getHeaderField("Location");
    			logger.info("\nAuth Token(code) is "+authcode);
    			res = authcode;
            }

            else {
				logger.info("get token res status="+status);
				return "get token error with "+status;
			}

			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			String inputLine;
			while((inputLine = in.readLine())!=null) {
				logger.info(inputLine);
			}

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(in!=null) {
				try {
					in.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	private String getAuthToken(String id, String pw, String company, String code) {
		
		BufferedReader in=null;
		String resAccessToken="";
		String clientId = "";
		String clientSecret = "";
		int status=200;

		logger.info("\n post req code="+code);

		try {
			URL url = new URL("https://goqual.io/oauth/token");

			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("grant_type", "authorization_code");
			params.put("code", code);
			params.put("redirect_uri", "https://iot.hancom-toki.com");
			params.put("client_id", "1530526115fe41929e2ff5c87d3a1dc8");


			StringBuilder getData = new StringBuilder();
			for(Map.Entry<String, Object> param:params.entrySet()) {
				if(getData.length()!=0) getData.append('&');
				getData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				getData.append('=');
				getData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

			}
			byte[] getDataBytes = getData.toString().getBytes("UTF-8");
			conn.setRequestMethod("POST");

			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

			conn.setRequestProperty("Connection", "keep-alive");
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("Content-Length", String.valueOf(getDataBytes.length));
			String encoded = Base64.getEncoder().encodeToString((clientId+":"+clientSecret).getBytes(StandardCharsets.UTF_8));
			conn.setRequestProperty("Authorization", "Basic "+encoded);
			conn.setDoOutput(true);

			conn.getOutputStream().write(getDataBytes);

			status = conn.getResponseCode();
            logger.info("res status="+status);

            if(status==200) {
            	in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

    			String inputLine;
    			StringBuilder sb = new StringBuilder();
    			while((inputLine = in.readLine())!=null) {
    				logger.info(inputLine);
    				sb.append(inputLine);
    			}
    			String result = sb.toString();

    			logger.info("\n Sent result="+result);

    			JSONObject responseJson = new JSONObject(sb.toString());

    			logger.info("\n access_token:"+responseJson.get("access_token").toString());

    			resAccessToken = responseJson.get("access_token").toString();
    			String refreshToken = responseJson.getString("refresh_token").toString();

    			User updateUser = new User("", id, "", resAccessToken, refreshToken, "");
    			userDaoService.updateUserByUserId(updateUser);


            } else {

            	logger.info("\n post get token error="+status);

            	//resToken=Integer.toString(status);
            }




		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}


		return resAccessToken;

	}

	@RequestMapping(method = RequestMethod.POST, path = "/logib")
	public String postRequest(@RequestParam String id, @RequestParam String pw) throws IOException{
		logger.info("iot ap login-----id="+id+"pw="+pw);

		URL url = new URL("https://goqual.io/oauth/login?vendor=openapi");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("vendor", "openapi");

		StringBuilder postData = new StringBuilder();
		for(Map.Entry<String, Object> param:params.entrySet()) {
			if(postData.length()!=0) postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

		}
		byte[] postDataBytes = postData.toString().getBytes("UTF-8");

		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
		conn.setRequestProperty("Access-Control-Allow-Origin", "http://localhost:3000");
		conn.setRequestProperty("Connection", "close");
		conn.setRequestProperty("Origin", "http://localhost:3000");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

		String inputLine;
		while((inputLine = in.readLine())!=null) {
			logger.info(inputLine);
		}


	    return "id:"+id+" pw:"+pw;
	}

}
