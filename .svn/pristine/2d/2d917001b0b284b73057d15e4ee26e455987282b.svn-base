
package com.monitor.foundation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monitor.core.dao.IGenericDAO;
import com.monitor.foundation.domain.SysConfig;
import com.monitor.foundation.service.ISysConfigService;

@Service
@Transactional
public class SysConfigServiceImpl implements ISysConfigService {
	
	// cache sys config constant, we can query it by name from cache
	private boolean hasLoaded = false;
	private ReentrantLock lock = new ReentrantLock();
	private List<SysConfig> sysConfigs;
	private Map<String, SysConfig> sysConfigMap = new HashMap<String, SysConfig>();

    @Resource(name = "sysConfigDAO")
    private IGenericDAO<SysConfig> sysConfigDAO;

    @Override
    public boolean delete(Long id) {
        try {
            this.sysConfigDAO.remove(id);
            // TODO delete from cache
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public SysConfig getObjById(Long id) {
        return ((SysConfig) this.sysConfigDAO.get(id));
    }

	@Override
	public SysConfig getObjByName(String name) {
		if (!hasLoaded) {
			loadSensorType();
		}
		return sysConfigMap.get(name);
	}

    @Override
    public boolean save(SysConfig sysConfig) {
        try {
            this.sysConfigDAO.save(sysConfig);
            this.sysConfigMap.put(sysConfig.getName(), sysConfig);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public boolean update(SysConfig sysConfig) {
        try {
            this.sysConfigDAO.update(sysConfig);
            this.sysConfigMap.put(sysConfig.getName(), sysConfig);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public List<SysConfig> query(String query, Map params, int begin, int max) {
        return this.sysConfigDAO.query(query, params, begin, max);
    }

    @Override
    public SysConfig getObjByProperty(String propertyName, String value) {
        return ((SysConfig) this.sysConfigDAO.getBy(propertyName, value));
    }

	private void loadSensorType() {
		lock.lock();
		try {
			if (!hasLoaded) {
				sysConfigs = this.query("select obj from SysConfig obj", null, -1, -1);
				if(null != sysConfigs && !sysConfigs.isEmpty()){
					for(SysConfig sysConfig : sysConfigs){
						sysConfigMap.put(sysConfig.getName(), sysConfig);
					}
				}
				hasLoaded = true;
			}
		} finally {
			lock.unlock();
		}
	}
}