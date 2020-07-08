package jp.co.infofarm.dropbox.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.infofarm.dropbox.service.DropBoxClientService;

@RestController
public class FilesController {
	
	@Autowired
	private DropBoxClientService dropboxClientService;
	
	@RequestMapping(value = "/files/*/" ,method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request ,ModelAndView mav) throws DbxApiException, DbxException, JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
		// https://github.com/dropbox/dropbox-sdk-java#setup
		
		DbxClientV2 client = dropboxClientService.getClient(request.getSession(false));
		
		// Get current account info
		FullAccount account = client.users().getCurrentAccount();
		
		Map<String ,Object> accountMap = new HashMap<>();
		accountMap.put("ID", account.getAccountId());
		accountMap.put("NAME", account.getName().getDisplayName());
		accountMap.put("EMAIL", account.getEmail());
		
		
		Map<String ,Object> map = new HashMap<String ,Object>();
		map.put("ACCOUNT", accountMap);

		String uri = request.getRequestURI();
		String path = URLDecoder.decode(uri.replaceAll("^/files", "").replaceAll("/$", ""), "UTF-8");;
		System.out.println(path);
		map.put("PATH" ,path);

		ListFolderResult result = client.files().listFolder(path);
		List<Map<? ,?>> folderList = new ArrayList<>(); 
		List<Map<? ,?>> fileList = new ArrayList<>(); 
		ObjectMapper objectMapper = new ObjectMapper();
		for (Metadata metadata : result.getEntries()) {
			System.out.println(metadata.toString());
			Map<?, ?> metadataMap = objectMapper.readValue(metadata.toString(), Map.class);
			if ("folder".equals(metadataMap.get(".tag"))) {
				folderList.add(metadataMap);
			} else if ("file".equals(metadataMap.get(".tag"))) {
				fileList.add(metadataMap);
			}
		}
		map.put("FOLDER_LIST", folderList);
		map.put("FILE_LIST", fileList);
		
		mav.setViewName("files/list");
		mav.addObject("map" ,map);
		return mav;
	}
}
