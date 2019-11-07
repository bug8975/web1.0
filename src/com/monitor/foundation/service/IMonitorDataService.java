package com.monitor.foundation.service;

import java.util.List;
import java.util.Map;

import com.monitor.core.query.support.IPageList;
import com.monitor.core.query.support.IQueryObject;
import com.monitor.foundation.domain.MonitorData;

public interface IMonitorDataService {
    public abstract boolean save(MonitorData paramMonitorData);
    
    public abstract boolean save(MonitorData[] paramMonitorDatas);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(MonitorData paramMonitorData);
    
    public abstract boolean update(MonitorData[] paramMonitorDatas);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract MonitorData getObjById(Long paramLong);

    public abstract MonitorData getObjByProperty(String paramString1, String paramString2);

    public abstract List<MonitorData> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
    
    public abstract List<Object> queryTime(String paramString, Map paramMap, int paramInt1, int paramInt2);
    
    public double getSinkingOffsetStat(long id, long beginTime, long endTime);
}
