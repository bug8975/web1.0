package com.monitor.action.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.IUserService;
import com.monitor.mv.JModelAndView;

@Controller
public class ModifyPwd {
    @Autowired
    private IUserService userService;

    @RequestMapping({ "/user/modifypwd.htm" })
    public ModelAndView modifyPwd(HttpServletRequest request, HttpServletResponse response) {
    	User user = Utils.checkLoginedUser(request);
    	if(user == null){
    		return Utils.redirectToLoginForNonLogined(request, response);
    	}
        return new JModelAndView("user/modifypwd.html", 0, request, response);
    }

    @RequestMapping({ "/user/modifypwdfinish.htm" })
    public ModelAndView modifyPwdFinish(HttpServletRequest request, HttpServletResponse response, String username,
    		String passwrod) {
    	User user = Utils.checkLoginedUser(request);
    	if(user == null){
    		return Utils.redirectToLoginForNonLogined(request, response);
    	}
    	return new JModelAndView("user/modifypwd.html", 0, request, response);
    }
}
