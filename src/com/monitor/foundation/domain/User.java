package com.monitor.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.monitor.core.domain.IdEntity;

@Entity
@Table(name = "user")
public class User extends IdEntity {
    private static final long serialVersionUID = 8026813053768023527L;

    private String userName;

    private String password;

    private String userRole; // admin/viewer

    private int status;

    private long lastLoginDate;

    private String lastLoginIp;

    public String getLastLoginIp() {
        return this.lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getPassword() {
        return this.password;
    }
//
//    public String getUsername() {
//        return this.userName;
//    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static long getSerialVersionUID() {
        return 8026813053768023527L;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getUserRole() {
        return this.userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

	public long getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(long lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

}