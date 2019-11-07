
package com.monitor.foundation.service.impl;

import com.monitor.core.dao.IGenericDAO;
import com.monitor.core.query.GenericPageList;
import com.monitor.core.query.PageObject;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.query.support.IQueryObject;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.IUserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Resource(name = "userDAO")
    private IGenericDAO<User> userDAO;

    public boolean delete(Long id) {
        try {
            this.userDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getObjById(Long id) {
        return ((User) this.userDAO.get(id));
    }

    public boolean save(User user) {
        try {
            this.userDAO.save(user);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean update(User user) {
        try {
            this.userDAO.update(user);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public List<User> query(String query, Map params, int begin, int max) {
        return this.userDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(User.class, query, params, this.userDAO);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null)
                pList.doList((pageObj.getCurrentPage() == null) ? 0 : pageObj.getCurrentPage().intValue(),
                        (pageObj.getPageSize() == null) ? 0 : pageObj.getPageSize().intValue());
        } else {
            pList.doList(0, -1);
        }
        return pList;
    }

    public User getObjByProperty(String propertyName, String value) {
        return ((User) this.userDAO.getBy(propertyName, value));
    }
}