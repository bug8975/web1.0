package com.monitor.foundation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monitor.core.base.GenericEntityDao;
import com.monitor.core.dao.IGenericDAO;
import com.monitor.core.query.GenericPageList;
import com.monitor.core.query.PageObject;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.query.support.IQueryObject;
import com.monitor.core.tools.Logger;
import com.monitor.core.tools.database.DatabaseTools;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.service.IMonitorLineService;

@Service
@Transactional
public class MonitorLineServiceImpl implements IMonitorLineService {
	public static MonitorLineStatusThread monitorLineStatusThread;
	private static final int MONITORTHREAD_PERIOD = 300000; // 5 m
	private static final int TwelveMinutes = 720000; // 12 m
	private static Object[] LOCK = new Object[0];

    @Autowired
    public DatabaseTools databaseTools;

	class MonitorLineStatusThread extends Thread {
		private IMonitorLineService monitorLineService;
		MonitorLineStatusThread(IMonitorLineService monitorLineService){
			this.monitorLineService = monitorLineService;
			this.setName("MonitorLineStatusThread_"+System.currentTimeMillis());
		}
		public void run(){
			Logger.printlnWithTime("Started MonitorLine status monitor thread " + this.getId());
			while (true) {
				try {
					sleep(MONITORTHREAD_PERIOD);
					Logger.printlnWithTime("Calculating MonitorLine status");
			        List<MonitorLine> monitorLines = monitorLineService.query("select obj from MonitorLine obj", null, -1, -1);
					if (null != monitorLines && !monitorLines.isEmpty()) {
			        	for(MonitorLine monitorLine : monitorLines){
			        		long lastHeartBeatTime = monitorLine.getLastHeartBeatTime();
			        		boolean offline = false;
			        		if(lastHeartBeatTime == 0 
			        				|| (lastHeartBeatTime + TwelveMinutes) < System.currentTimeMillis()){
			        			offline = true;
			        		}
			        		if(monitorLine.getAliveStatus() == MonitorLine.ALIVESTATUS_ONLINE 
			        				&& offline){
			        			Logger.printlnWithTime("Change MonitorLine " + monitorLine.getName() + "'s status to offline!");
				        		String sql = "update monitor_line set aliveStatus=" + MonitorLine.ALIVESTATUS_OFFLINE + " where id=" + monitorLine.getId();
				        		//Logger.printlnWithTime("update monitorline status sql=" + sql);
				        		databaseTools.execute(sql);
			        			continue;
			        		}
			        		if(monitorLine.getAliveStatus() == MonitorLine.ALIVESTATUS_OFFLINE 
			        				&& !offline){
			        			Logger.printlnWithTime("Change MonitorLine " + monitorLine.getName() + "'s status to online!");
				        		String sql = "update monitor_line set aliveStatus=" + MonitorLine.ALIVESTATUS_ONLINE + " where id=" + monitorLine.getId();
				        		//Logger.printlnWithTime("update monitorline status sql=" + sql);
				        		databaseTools.execute(sql);
			        			continue;
			        		}
			        	}
			        }
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}

	public MonitorLineServiceImpl(){
		synchronized (LOCK){
			if(null == monitorLineStatusThread){
				monitorLineStatusThread = new MonitorLineStatusThread(this);
				monitorLineStatusThread.start();
			}
		}
	}

    @Resource(name = "monitorLineDAO")
    private IGenericDAO<MonitorLine> monitorLineDAO;

    @Resource(name = "genericEntityDao")
    private GenericEntityDao genericEntityDao;

    public boolean delete(Long id) {
        try {
            this.monitorLineDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

	@Override
	public void cascadingDelete(Long id) {
		this.genericEntityDao.executeNativeSQL("delete from monitor_dataview where monitorLine_id=" + id);
		this.genericEntityDao.executeNativeSQL("delete from monitor_data where monitorLine_id=" + id);
		this.genericEntityDao.executeNativeSQL("delete from sensor where monitorLine_id=" + id);
		this.delete(id);
	}

    public MonitorLine getObjById(Long id) {
        return ((MonitorLine) this.monitorLineDAO.get(id));
    }

    public boolean save(MonitorLine monitorLine) {
        try {
        	monitorLine.setAddTime(System.currentTimeMillis());
        	monitorLine.setLastModifyTime(monitorLine.getAddTime());
            this.monitorLineDAO.save(monitorLine);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public boolean update(MonitorLine monitorLine) {
        try {
			monitorLine.setLastModifyTime(System.currentTimeMillis());
            this.monitorLineDAO.update(monitorLine);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public List<MonitorLine> query(String query, Map params, int begin, int max) {
        return this.monitorLineDAO.query(query, params, begin, max);
    }
    
    public List<MonitorLine> getObjsBySensorTypeName(String sensorTypeName) {
    	return query("select obj from MonitorLine obj where obj.sensorType='" + sensorTypeName + "'", null, -1, -1);
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(MonitorLine.class, query, params, this.monitorLineDAO);
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

    public MonitorLine getObjByProperty(String propertyName, String value) {
        return ((MonitorLine) this.monitorLineDAO.getBy(propertyName, value));
    }

}
