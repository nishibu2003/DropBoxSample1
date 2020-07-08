package jp.co.infofarm.dropbox.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.co.infofarm.dropbox.service.DropBoxClientService;

@RestController
@SessionAttributes("session")
public class IndexController {
	
	@Autowired
	private DropBoxClientService dropboxClientService;
	
	@RequestMapping(value = "/")
	public ModelAndView index(HttpServletRequest request ,HttpServletResponse response ,ModelAndView mav, @RequestParam(name = "access_token" ,required = false) String accessToken) {
		HttpSession session = request.getSession(true);
		if (session != null) {
			session.invalidate();
			session = request.getSession(true);
		}
		
		boolean loginStatus = false;
		if (accessToken != null && !accessToken.isEmpty()) {
			try {
				dropboxClientService.getClient(accessToken);
				loginStatus = true;
			} catch (Exception e) {
				// ignore
			}
		}
		
		if (loginStatus) {
			session.setAttribute("access_token", accessToken);
			mav.setViewName("redirect:./files/");
		} else {
			mav.setViewName("index");
		}
		
		return mav;
	}
}
