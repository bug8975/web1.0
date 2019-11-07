package com.monitor.foundation.service;

import java.util.List;
import java.util.Map;

import com.monitor.core.query.support.IPageList;
import com.monitor.core.query.support.IQueryObject;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorDataView;

public interface IMonitorDataViewService {
    public abstract boolean save(MonitorDataView paramMonitorData);
    
    public abstract boolean save(MonitorDataView[] paramMonitorDatas);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(MonitorDataView paramMonitorData);
    
    public abstract boolean update(MonitorDataView[] paramMonitorDatas);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract MonitorDataView getObjById(Long paramLong);

    public abstract MonitorDataView getObjByProperty(String paramString1, String paramString2);

    public abstract List<MonitorDataView> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
    
    public abstract List<Object> queryTime(String paramString, Map paramMap, int paramInt1, int paramInt2);
    
    public double getSinkingOffsetStat(long id, long beginTime, long endTime);
}
