package jp.co.infofarm.dropbox.controller;

import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import jp.co.infofarm.dropbox.service.DropBoxClientService;

@Controller
public class UploadController {
	
	@Autowired
	private DropBoxClientService dropboxClientService;
	
	
	@RequestMapping(value = "/files/*/upload" ,method =  RequestMethod.POST)
	public String upload(HttpServletRequest request ,HttpServletResponse response ,@RequestParam("upload_file") MultipartFile multipartFile) throws Exception{
		DbxClientV2 client = dropboxClientService.getClient(request.getSession(false));
		
		String uri = request.getRequestURI();
		String path = URLDecoder.decode(uri.replaceAll("^/files", "").replaceAll("/upload$", ""), "UTF-8");;
		System.out.println(path);
		
		String originalFileName = multipartFile.getOriginalFilename().replace("\\", "/");
		String fileName = originalFileName.substring(originalFileName.lastIndexOf("/") + 1);
		System.out.println(fileName);
		String filePath = path + "/" + fileName;
		System.out.println(filePath);
		
		InputStream in = null;
		try {
			in = multipartFile.getInputStream();
			FileMetadata metadata = client.files().uploadBuilder(filePath).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
			System.out.println(metadata.toString());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e){
					// Ignore
				}
			}
		}
		
		return "redirect:.";
	}
}
