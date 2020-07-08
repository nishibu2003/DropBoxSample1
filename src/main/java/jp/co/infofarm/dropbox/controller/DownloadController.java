package jp.co.infofarm.dropbox.controller;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import jp.co.infofarm.dropbox.service.DropBoxClientService;

@RestController
public class DownloadController {
	
	@Autowired
	private DropBoxClientService dropboxClientService;
	
	
	@RequestMapping(value = "/files/*/{name}" ,method =  RequestMethod.GET)
	public String download(HttpServletRequest request ,HttpServletResponse response ,@PathVariable("name") String name) throws Exception{
		DbxClientV2 client = dropboxClientService.getClient(request.getSession(false));
		
		String uri = request.getRequestURI();
		String path = URLDecoder.decode(uri.replaceAll("^/files", "").replaceAll("/$", ""), "UTF-8");;
		System.out.println(path);
		
		DbxDownloader<FileMetadata> downloader = client.files().download(path);
		
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=" + new String(name.getBytes("UTF-8") ,"ISO-8859-1") + ";filename*=UTF-8''" + URLEncoder.encode(name, "UTF-8") +"'");
		OutputStream out = response.getOutputStream();
		
		downloader.download(out);
		out.flush();

		return null;
	}
}
