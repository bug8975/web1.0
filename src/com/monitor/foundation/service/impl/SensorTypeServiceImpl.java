package com.monitor.foundation.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monitor.core.dao.IGenericDAO;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.service.ISensorTypeService;

@Service
@Transactional
public class SensorTypeServiceImpl implements ISensorTypeService{
	// cache sensor type constant, we can query it by name from cache
	private volatile boolean hasLoaded = false;
	private ReentrantLock lock = new ReentrantLock();
	private List<SensorType> sensorTypes;
	private List<SensorType> usingSensorTypes = new ArrayList<SensorType>();
	private Map<String, SensorType> sensorTypeMap = new HashMap<String, SensorType>();
	private Map<String, String> sensorUnits = new HashMap<String, String>();
	private Map<String, String> sensorTypeDisplayNames = new HashMap<String, String>();

    @Resource(name = "sensorTypeDAO")
    private IGenericDAO<SensorType> sensorTypeDAO;

	@Override
	public List<SensorType> query(String query, Map params) {
        return this.sensorTypeDAO.query(query, params, -1, -1);
	}

	@Override
	public SensorType getObjByName(String sensorTypeName) {
		if (!hasLoaded) {
			loadSensorType();
		}
		return sensorTypeMap.get(sensorTypeName);
	}

	@Override
	public List<SensorType> getObjByUsingStatus(int status) {
		if (!hasLoaded) {
			loadSensorType();
		}
		List<SensorType> result = new ArrayList<SensorType>(16);
		for(SensorType sensorType : sensorTypes){
			if(sensorType.getUsingStatus() == status){
				result.add(sensorType);
			}
		}
		return result;
	}

	@Override
	public List<SensorType> getUsingSensorTypes() {
		if (!hasLoaded) {
			loadSensorType();
		}
		return usingSensorTypes;
	}
	
	

	private void loadSensorType() {
		lock.lock();
		try {
			if (!hasLoaded) {
				sensorTypes = this.query("select obj from SensorType obj", null);
				if(null != sensorTypes && !sensorTypes.isEmpty()){
					for(SensorType sensorType : sensorTypes){
						sensorTypeMap.put(sensorType.getName(), sensorType);
						sensorUnits.put(sensorType.getName(), sensorType.getUnit());
						sensorTypeDisplayNames.put(sensorType.getName(), sensorType.getDisplayName());
						if(SensorType.STATUS_USING == sensorType.getUsingStatus()){
							usingSensorTypes.add(sensorType);
						}
					}
				}
				hasLoaded = true;
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Map<String, String> getSensorUnits() {
		if (!hasLoaded) {
			loadSensorType();
		}
		return sensorUnits;
	}

	@Override
	public Map<String, String> getSensorTypeDisplayNames() {
		if (!hasLoaded) {
			loadSensorType();
		}
		return sensorTypeDisplayNames;
	}

}
