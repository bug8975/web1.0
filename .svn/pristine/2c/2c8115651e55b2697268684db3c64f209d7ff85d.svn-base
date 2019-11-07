package com.monitor.foundation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monitor.core.dao.IGenericDAO;
import com.monitor.core.query.GenericPageList;
import com.monitor.core.query.PageObject;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.query.support.IQueryObject;
import com.monitor.core.service.IQueryService;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorDataView;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorDataViewService;

@Service
@Transactional
public class MonitorDataViewServiceImpl implements IMonitorDataViewService {
    @Resource(name = "monitorDataViewDAO")
    private IGenericDAO<MonitorDataView> monitorDataViewDAO;

    @Autowired
    private IQueryService queryService;

    public boolean delete(Long id) {
        try {
            this.monitorDataViewDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public MonitorDataView getObjById(Long id) {
        return ((MonitorDataView) this.monitorDataViewDAO.get(id));
    }

    public boolean save(MonitorDataView monitorData) {
        try {
    		monitorData.setAddTime(System.currentTimeMillis());
            this.monitorDataViewDAO.save(monitorData);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public boolean save(MonitorDataView[] monitorDatas) {
        try {
        	if(null == monitorDatas || monitorDatas.length == 0){
        		return true;
        	}
        	for(MonitorDataView monitorData : monitorDatas){
        		if(null != monitorData){
            		monitorData.setAddTime(System.currentTimeMillis());
                    this.monitorDataViewDAO.save(monitorData);
        		}
        	}
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public boolean update(MonitorDataView monitorData) {
        try {
            this.monitorDataViewDAO.update(monitorData);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public boolean update(MonitorDataView[] monitorDatas) {
        try {
        	if(null == monitorDatas || monitorDatas.length == 0){
        		return true;
        	}
        	for(MonitorDataView monitorData : monitorDatas){
        		if(null != monitorData){
                    this.monitorDataViewDAO.update(monitorData);
        		}
        	}
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public List<MonitorDataView> query(String query, Map params, int begin, int max) {
        return this.monitorDataViewDAO.query(query, params, begin, max);
    }

    @Override
    public double getSinkingOffsetStat(long id, long beginTime, long endTime){
    	Map params = new HashMap();
    	params.put("id", id);
    	params.put("beginTime", beginTime);
    	params.put("endTime", endTime);
    	List list = this.queryService.query("select sum(sinkingOffset) as sinkingstat from MonitorDataView obj where obj.sensor.id=:id and obj.collectingTime>=:beginTime and obj.collectingTime<=:endTime", params, -1, -1);
    	if(null != list && !list.isEmpty()){
    		if(null != list.get(0)){
    			return Double.parseDouble(list.get(0).toString());
    		}
    	}
    	return Sensor.INIT_MONITORVALUE;
    }
    

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(MonitorDataView.class, query, params, this.monitorDataViewDAO);
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

    public MonitorDataView getObjByProperty(String propertyName, String value) {
        return ((MonitorDataView) this.monitorDataViewDAO.getBy(propertyName, value));
    }
    
    @Override
   	public List<Object> queryTime(String query, Map params, int begin, int max) {
           return this.monitorDataViewDAO.query(query, params, begin, max);
   	}

}
