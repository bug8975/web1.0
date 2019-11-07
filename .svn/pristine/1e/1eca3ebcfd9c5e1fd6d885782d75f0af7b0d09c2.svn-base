package com.monitor.core.security.support;

import com.monitor.core.tools.CommUtil;
import com.monitor.foundation.domain.User;

import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class SecurityUserHolder {
    public static User getCurrentUser() {
        if ((SecurityContextHolder.getContext().getAuthentication() != null)
                && (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User)) {
            return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }
        User user = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (request != null && request.getSession(false) != null) {
                Object obj = request.getSession(false).getAttribute("user");
                if (obj != null) {
                    user = (User) obj;
                }

                Cookie[] cookies = request.getCookies();
                String id = "";
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("user_session")) {
                            id = CommUtil.null2String(cookie.getValue());
                        }
                    }
                }
                if (id.equals("")) {
                    user = null;
                }
            }
        }

        return user;
    }
}