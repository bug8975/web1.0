package com.monitor.foundation.service;

import java.util.List;
import java.util.Map;

import com.monitor.foundation.domain.SensorType;

public interface ISensorTypeService {
//    public abstract boolean save(SensorType sensorType);
//
//    public abstract boolean delete(String sensorTypeName);

//    public abstract void cascadingDelete(String sensorTypeName);

//    public abstract boolean update(SensorType sensorType);

//    public abstract IPageList list(IQueryObject paramIQueryObject);
//
//    public abstract SensorType getObjByName(String sensorTypeName);
//
//    public abstract SensorType getObjByProperty(String paramString1, String paramString2);

    public abstract List<SensorType> query(String query, Map params);
    
//    public abstract long getTotal();

    public abstract SensorType getObjByName(String sensorTypeName);

    public abstract List<SensorType> getObjByUsingStatus(int status);

    public abstract List<SensorType> getUsingSensorTypes();
    
    public abstract Map<String, String> getSensorUnits();
    
    public abstract Map<String, String> getSensorTypeDisplayNames();
}
