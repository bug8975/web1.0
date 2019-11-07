
package com.monitor.foundation.service;

import java.util.List;
import java.util.Map;

import com.monitor.foundation.domain.SysConfig;

public abstract interface ISysConfigService {
    public abstract boolean save(SysConfig sysConfig);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(SysConfig paramUser);

    public abstract SysConfig getObjById(Long paramLong);

    public abstract SysConfig getObjByName(String name);

    public abstract SysConfig getObjByProperty(String paramString1, String paramString2);

    public abstract List<SysConfig> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}