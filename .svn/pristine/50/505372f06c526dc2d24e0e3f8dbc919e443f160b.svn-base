package com.monitor.foundation.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monitor.core.base.GenericEntityDao;
import com.monitor.core.dao.IGenericDAO;
import com.monitor.core.query.GenericPageList;
import com.monitor.core.query.PageObject;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.query.support.IQueryObject;
import com.monitor.foundation.domain.Contact;
import com.monitor.foundation.service.IContactService;

@Service
@Transactional
public class ContactServiceImpl implements IContactService {
    @Resource(name = "contactDAO")
    private IGenericDAO<Contact> contactDAO;

    public boolean delete(Long id) {
        try {
            this.contactDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Contact getObjById(Long id) {
        return ((Contact) this.contactDAO.get(id));
    }

    public boolean save(Contact contact) {
        try {
        	contact.setAddTime(System.currentTimeMillis());
            this.contactDAO.save(contact);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public boolean update(Contact contact) {
        try {
            this.contactDAO.update(contact);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public List<Contact> query(String query, Map params, int begin, int max) {
        return this.contactDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Contact.class, query, params, this.contactDAO);
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

    public Contact getObjByProperty(String propertyName, String value) {
        return ((Contact) this.contactDAO.getBy(propertyName, value));
    }
}
