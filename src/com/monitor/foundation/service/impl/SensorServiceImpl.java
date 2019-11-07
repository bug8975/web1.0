package com.monitor.foundation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monitor.core.base.GenericEntityDao;
import com.monitor.core.constant.Globals;
import com.monitor.core.dao.IGenericDAO;
import com.monitor.core.query.GenericPageList;
import com.monitor.core.query.PageObject;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.query.support.IQueryObject;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.service.ISensorService;

@Service
@Transactional
public class SensorServiceImpl implements ISensorService {
    @Resource(name = "sensorDAO")
    private IGenericDAO<Sensor> sensorDAO;
    
    @Resource(name = "genericEntityDao")
    private GenericEntityDao genericEntityDao;

    public boolean delete(Long id) {
        try {
            this.sensorDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

	@Override
	public void cascadingDelete(Long id) {
		this.genericEntityDao.executeNativeSQL("delete from monitor_data where sensor_id=" + id);
		this.genericEntityDao.executeNativeSQL("delete from monitor_dataview where sensor_id=" + id);
		this.delete(id);
	}

    public Sensor getObjById(Long id) {
        return ((Sensor) this.sensorDAO.get(id));
    }

    public boolean save(Sensor sensor) {
        try {
        	sensor.setAddTime(System.currentTimeMillis());
        	sensor.setLastModifyTime(sensor.getAddTime());
            this.sensorDAO.save(sensor);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public boolean update(Sensor sensor) {
        try {
        	sensor.setLastModifyTime(System.currentTimeMillis());
            this.sensorDAO.update(sensor);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public List<Sensor> query(String query, Map params, int begin, int max) {
        return this.sensorDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Sensor.class, query, params, this.sensorDAO);
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

    public Sensor getObjByProperty(String propertyName, String value) {
        return ((Sensor) this.sensorDAO.getBy(propertyName, value));
    }

	@Override
	public long getTotal() {
		List result = this.sensorDAO.query("select count(*) from Sensor obj", new HashMap(), 0, 1);
		return (Long) result.get(0);
	}

	@Override
	public long getTotalWithLevel1Alarm() {
		List result = this.sensorDAO.query("select count(*) from Sensor obj where alarmLevel="+ Globals.ALARMLEVEL_L1, new HashMap(), 0, 1);
		return (Long) result.get(0);
	}

	@Override
	public long getTotalWithLevel2Alarm() {
		List result = this.sensorDAO.query("select count(*) from Sensor obj where alarmLevel="+ Globals.ALARMLEVEL_L2, new HashMap(), 0, 1);
		return (Long) result.get(0);
	}

	@Override
	public long getTotalWithLevel3Alarm() {
		List result = this.sensorDAO.query("select count(*) from Sensor obj where alarmLevel="+ Globals.ALARMLEVEL_L3, new HashMap(), 0, 1);
		return (Long) result.get(0);
	}
	
}
