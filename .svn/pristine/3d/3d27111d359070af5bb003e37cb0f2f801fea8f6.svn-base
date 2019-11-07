package com.monitor.foundation.service;

import java.util.List;
import java.util.Map;

import com.monitor.core.query.support.IPageList;
import com.monitor.core.query.support.IQueryObject;
import com.monitor.foundation.domain.MonitorLine;

public interface IMonitorLineService {
    public abstract boolean save(MonitorLine paramMonitorLine);

    public abstract boolean delete(Long paramLong);

    public abstract void cascadingDelete(Long id);

    public abstract boolean update(MonitorLine paramMonitorLine);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract MonitorLine getObjById(Long paramLong);

    public abstract MonitorLine getObjByProperty(String paramString1, String paramString2);
    
    public abstract List<MonitorLine> getObjsBySensorTypeName(String name);

    public abstract List<MonitorLine> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}
