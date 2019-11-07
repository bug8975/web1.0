package com.monitor.foundation.dao;

import org.springframework.stereotype.Repository;

import com.monitor.core.base.GenericDAO;
import com.monitor.foundation.domain.SysConfig;

@SuppressWarnings("unchecked")
@Repository("sysConfigDAO")
public class SysConfigDAO extends GenericDAO<SysConfig> {
}