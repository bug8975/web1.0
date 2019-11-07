package com.monitor.foundation.dao;

import org.springframework.stereotype.Repository;

import com.monitor.core.base.GenericDAO;
import com.monitor.foundation.domain.MonitorData;

@SuppressWarnings("unchecked")
@Repository("monitorDataDAO")
public class MonitorDataDAO extends GenericDAO<MonitorData> {
}