package jp.co.infofarm.dropbox.service;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

@Service
public class DropBoxClientService {
	
	public DbxClientV2 getClient() {
		String ACCESS_TOKEN = "-- ACCESS TOKEN --";
		return this.getClient(ACCESS_TOKEN);
	}
	
	public DbxClientV2 getClient(HttpSession session) {
		if (session == null) return null;
		
		String accessToken = (String)session.getAttribute("access_token");
		if (accessToken == null || accessToken.isEmpty()) return null;
		
		return this.getClient(accessToken);
	}
	
	public DbxClientV2 getClient(String accessToken) {
		DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
		DbxClientV2 client = new DbxClientV2(config, accessToken);
		
		return client;
	}
	

}
