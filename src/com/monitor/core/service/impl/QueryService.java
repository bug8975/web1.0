package com.monitor.core.service.impl;

import com.monitor.core.base.GenericEntityDao;
import com.monitor.core.service.IQueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class QueryService implements IQueryService {

    @Autowired
    @Qualifier("genericEntityDao")
    private GenericEntityDao geDao;

    public GenericEntityDao getGeDao() {
        return this.geDao;
    }

    public void setGeDao(GenericEntityDao geDao) {
        this.geDao = geDao;
    }

    @SuppressWarnings("rawtypes")
    public List query(String scope, Map params, int page, int pageSize) {
        return this.geDao.query(scope, params, page, pageSize);
    }
}