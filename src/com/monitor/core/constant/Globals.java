package com.monitor.core.constant;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class Globals {
    public static final String DEFAULT_SYSTEM_TITLE = "MonitorSys";
    
    public static final String HTTP_CONTENT_TYPE = "application/json";

    public static final String REPORT_FILE_DIR = "excelreport";
    public static final String FS = File.separator;

    public static final String WEB_RESULTCODE = "resultcode";
    public static final String WEB_RESULTSTRING = "resultstring";
    public static final int WEB_RESULTCODE_SUCCESS = 0; 
    public static final int WEB_RESULTCODE_NOTLOGIN = 1; 
    public static final int WEB_RESULTCODE_NODATA = 10;
    public static final String WEB_RESULTSTRING_NODATA = "没有数据"; 


    public static final String DEVICE_MODIFY_ADD = "ADD";
    public static final String DEVICE_MODIFY_UPDATE = "UPDATE";
    public static final String DEVICE_MODIFY_DEL = "DEL";

    public static final int ALARMLEVEL_NORMAL = 0; 
    public static final int ALARMLEVEL_L1 = 1; 
    public static final int ALARMLEVEL_L2 = 2; 
    public static final int ALARMLEVEL_L3 = 3; 
    
    public static final int THRESHOLDSTATUS_INIT = 0;
    public static final int THRESHOLDSTATUS_SUCCESS = 1;
    public static final int THRESHOLDSTATUS_ONGOING = 2;
    public static final int THRESHOLDSTATUS_OTHER = 100; 
    public static final int THRESHOLDSTATUS_FORMATERROR = 200;
    public static final int THRESHOLDSTATUS_DATAERROR = 300;
    
    public static final String SESSION_ATTR_USER = "logined_user";
    public static final String MV_ATTR_USERNAME = "logined_username";
    public static final String MV_ATTR_USERROLE = "logined_userrole";
    public static final String MV_ATTR_USER = "logined_user";
    public static final String USERROLE_ADMIN = "ADMIN";
    public static final String USERROLE_VIEWER = "VIEW"; // need to change to VIEWER???
    
    public static final String SENSORTYPE_CTRANSDUCER = "C-levelingTransducer";
    public static final String SENSORTYPE_ITRANSDUCER = "I-levelingTransducer";
    public static final String SENSORTYPE_PTRANSDUCER = "P-levelingTransducer";
    public static final Set<String> SET_SENSORTYPE_TRANSDUCER = new HashSet<String>();
    public static final String SENSORTYPE_CONDITION_IN = "('C-levelingTransducer','I-levelingTransducer','P-levelingTransducer')";
    static {
    	SET_SENSORTYPE_TRANSDUCER.add(SENSORTYPE_CTRANSDUCER);
    	SET_SENSORTYPE_TRANSDUCER.add(SENSORTYPE_ITRANSDUCER);
    	SET_SENSORTYPE_TRANSDUCER.add(SENSORTYPE_PTRANSDUCER);
    }
    public static final Set<String> SET_INCLINOMETER = new HashSet<String>();
    static {
    	SET_INCLINOMETER.add("FixedInclinoMeter");
    	SET_INCLINOMETER.add("InclinoMeter");
    }
    
    public static final boolean SSO_SIGN = true;

    public static final String SYSCONF_WBESITE_NAME = "SysTitle";

    public static final String DEFAULT_WBESITE_NAME = "MonitorSys";
    public static final String DEFAULT_CLOSE_REASON = "系统维护中...";

    public static final String SYSTEM_DATA_BACKUP_PATH = "data";

    public static final Boolean SYSTEM_UPDATE = Boolean.valueOf(true);

    public static final boolean SAVE_LOG = false;

    public static final String SECURITY_CODE_TYPE = "normal";

    public static final boolean STORE_ALLOW = true;

    public static final int DEFAULT_COMPLAINT_TIME = 30;

    public static final String DEFAULT_BIND_DOMAIN_CODE = "126A11D4BB76663E85078487393AB64897B9DCE99C5934CD589CFE4E769668CB";
    
    public static final DateFormat DateFormatWITHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    public static final DateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat DateFormatWithHM = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    public static final DateFormat DateFormatYMD = new SimpleDateFormat("yyyy/MM/dd");
    public static final DateFormat DateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat DateTimeFormatDay = new SimpleDateFormat("dd HH:mm:ss");
    public static final DateFormat DateTimeFormat4File = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public static final DateFormat DateTimeFormatLineReport = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

}