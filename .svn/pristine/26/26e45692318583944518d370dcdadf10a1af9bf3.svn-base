package com.monitor.core.tools.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.monitor.core.tools.Logger;

@Repository
public class DatabaseTools implements IBackup {

    @Autowired
    private PublicMethod publicMethod;

    @SuppressWarnings("unused")
    public boolean executSqlScript(String filePath) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        List<String> sqlStrList = null;
        boolean ret = true;
        try {
            sqlStrList = this.publicMethod.loadSqlScript(filePath);
            conn = this.publicMethod.getConnection();
            stmt = conn.createStatement();

            conn.setAutoCommit(false);
            for (String sqlStr : sqlStrList) {
                int index = sqlStr.indexOf("INSERT");
                if (-1 == index) {
                    stmt.addBatch(sqlStr);
                }

            }

            stmt.executeBatch();

            for (String sqlStr : sqlStrList) {
                int index = sqlStr.indexOf("INSERT");
                if (-1 != index) {
                    Logger.printlnWithTime(sqlStr);
                    int i = stmt.executeUpdate(sqlStr);
                }
            }

            stmt.executeBatch();
            conn.commit();
        } catch (Exception ex) {
            ret = false;
            Logger.printlnWithTime(ex.getMessage());
            ex.printStackTrace();
            conn.rollback();
            ex.printStackTrace();
        }
        return ret;
    }

    public boolean execute(String sql) {
        Connection conn = null;
        boolean ret = true;
        try {
            conn = this.publicMethod.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (Exception e) {
            ret = false;
            e.printStackTrace();
        } finally {
            this.publicMethod.closeConn();
        }
        return ret;
    }

    @SuppressWarnings("unused")
    public ResultSet query(String sql) {
        Connection conn = null;
        ResultSet rs = null;
        boolean ret = true;
        try {
            conn = this.publicMethod.getConnection();
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (Exception e) {
            ret = false;
            e.printStackTrace();
        } finally {
            this.publicMethod.closeConn();
        }
        return rs;
    }
}