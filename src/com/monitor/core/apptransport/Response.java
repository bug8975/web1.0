package com.monitor.core.apptransport;

public class Response {
	public final static int RESULTCODE_SUCCESS = 1;
	public final static int RESULTCODE_OTHERERROR = 100;
	public final static int RESULTCODE_FORMATERROR = 200;
	public final static int RESULTCODE_DATAERROR = 300;
	public final static int RESULTCODE_DATAERROR_EXISTEDDEVICE = 301;
	public final static int RESULTCODE_DATAERROR_NOTEXISTEDDEVICE = 302;
	public final static int RESULTCODE_DATAERROR_NOBASEPOINTDATA = 303;

	private int resultCode;
	private String resultString;

	public Response(){
	}

	public Response(int resultCode, String resultString){
		this.resultCode = resultCode;
		this.resultString = resultString;
	}
	
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultString() {
		return resultString;
	}
	public void setResultString(String resultString) {
		this.resultString = resultString;
	}
	
	@Override
	public String toString() {
		return "Response [resultCode=" + resultCode + ", resultString="
				+ resultString + "]";
	}
}
