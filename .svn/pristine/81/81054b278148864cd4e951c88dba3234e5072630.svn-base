package com.monitor.foundation.service;

import java.util.List;
import java.util.Map;

import com.monitor.core.query.support.IPageList;
import com.monitor.core.query.support.IQueryObject;
import com.monitor.foundation.domain.Sensor;

public interface ISensorService {
    public abstract boolean save(Sensor paramSensor);

    public abstract boolean delete(Long paramLong);

    public abstract void cascadingDelete(Long id);

    public abstract boolean update(Sensor paramSensor);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract Sensor getObjById(Long paramLong);

    public abstract Sensor getObjByProperty(String paramString1, String paramString2);

    public abstract List<Sensor> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
    
    public abstract long getTotal();
    public long getTotalWithLevel1Alarm();
    public long getTotalWithLevel2Alarm();
    public long getTotalWithLevel3Alarm();
    

}
