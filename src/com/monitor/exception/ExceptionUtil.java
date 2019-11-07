package com.monitor.exception;


public class ExceptionUtil {

	public static String getInnestExceptionMessage(Throwable e){
        Throwable tempE = e;
        while (null != tempE.getCause()) {
              tempE = tempE.getCause();
        }
        return tempE.getMessage();
	}

}
