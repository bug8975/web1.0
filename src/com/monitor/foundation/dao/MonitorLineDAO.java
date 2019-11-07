package com.monitor.foundation.dao;

import org.springframework.stereotype.Repository;

import com.monitor.core.base.GenericDAO;
import com.monitor.foundation.domain.MonitorLine;

@SuppressWarnings("unchecked")
@Repository("monitorLineDAO")
public class MonitorLineDAO extends GenericDAO<MonitorLine> {
}