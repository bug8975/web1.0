
package com.monitor.foundation.domain.query;

import com.monitor.core.query.QueryObject;

import org.springframework.web.servlet.ModelAndView;

public class UserQueryObject extends QueryObject {
    public UserQueryObject() {
    }

    public UserQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType) {
        super(currentPage, mv, orderBy, orderType);
    }
}