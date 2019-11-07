package com.monitor.foundation.dao;

import com.monitor.core.base.GenericDAO;
import com.monitor.foundation.domain.User;

import org.springframework.stereotype.Repository;

@SuppressWarnings("unchecked")
@Repository("userDAO")
public class UserDAO extends GenericDAO<User> {
}