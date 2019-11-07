package com.monitor.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.monitor.core.constant.Globals;
import com.monitor.foundation.domain.User;
import com.monitor.mv.JModelAndView;

public class Utils {

	public static User checkLoginedUser(HttpServletRequest request){
		Object user = request.getSession().getAttribute(Globals.SESSION_ATTR_USER);
		if(user != null){
			return (User) user;
		} else {
			return null;
		}
	}
	
	public static ModelAndView redirectToLoginForNonLogined(HttpServletRequest request, HttpServletResponse response){
	    ModelAndView mv = new JModelAndView("login.html", 0, request, response);
	    mv.addObject("errorString", "请登录!");
	    return mv;
	}

	public static void setResponse(HttpServletResponse response, String resultString) {
		response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(resultString);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
