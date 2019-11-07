package com.monitor.mv;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.monitor.core.security.support.SecurityUserHolder;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.HttpInclude;

public class JModelAndView extends ModelAndView {
    public JModelAndView(String viewName) {
        super.setViewName(viewName);
    }

    public JModelAndView(String viewName, HttpServletRequest request,
            HttpServletResponse response) {
        String webPath = CommUtil.getURL(request);
        super.setViewName(viewName);
        super.addObject("domainPath", CommUtil.generic_domain(request));
        super.addObject("webPath", webPath);
        super.addObject("user", SecurityUserHolder.getCurrentUser());
        super.addObject("httpInclude", new HttpInclude(request, response));
        String query_url = "";
        if ((request.getQueryString() != null) && (!(request.getQueryString().equals("")))) {
            query_url = "?" + request.getQueryString();
        }
        super.addObject("current_url", request.getRequestURI() + query_url);
        boolean second_domain_view = false;
        super.addObject("second_domain_view", Boolean.valueOf(second_domain_view));
    }

    public JModelAndView(String viewName, int type, HttpServletRequest request,
            HttpServletResponse response) {
    	super.setViewName("WEB-INF/html/" + viewName);
        super.addObject("CommUtil", new CommUtil());
        String webPath = CommUtil.getURL(request);
        super.addObject("domainPath", CommUtil.generic_domain(request));
        super.addObject("webPath", webPath);
        super.addObject("user", SecurityUserHolder.getCurrentUser());
        super.addObject("httpInclude", new HttpInclude(request, response));
        String query_url = "";
        if ((request.getQueryString() != null) && (!(request.getQueryString().equals("")))) {
            query_url = "?" + request.getQueryString();
        }
        super.addObject("current_url", request.getRequestURI() + query_url);
        boolean second_domain_view = false;
        super.addObject("second_domain_view", Boolean.valueOf(second_domain_view));
    }
}