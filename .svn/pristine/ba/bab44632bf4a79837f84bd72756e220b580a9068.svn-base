package com.monitor.action.deviceapp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.domain.virtual.SysMap;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.Logger;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.MonitorDataQueryObject;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

@Controller
public class DeviceAppAction {

    @Autowired
    private IMonitorLineService monitorLineService;

    @Autowired
    private ISensorTypeService sensorTypeService;

    @RequestMapping({ "/deviceapp/appstatuslist.htm" })
    public ModelAndView appStatusList(HttpServletRequest request, HttpServletResponse response, String currentPage, 
            String monitorlineid) {
    	User user = Utils.checkLoginedUser(request);
    	if(user == null){
    		return Utils.redirectToLoginForNonLogined(request, response);
    	}

        ModelAndView mv = new JModelAndView("deviceapp/appstatuslist.html", 0, request, response);

        // return query condition
        mv.addObject("monitorlineid", monitorlineid);

        // for condition selecting
        List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj", null, -1, -1);
        mv.addObject("monitorLines", monitorLines);

        // Query object
        MonitorDataQueryObject qo = new MonitorDataQueryObject(currentPage, mv, "name", "ASC");
        qo.setPageSize(Integer.valueOf(12));
        if(null != monitorlineid && !monitorlineid.isEmpty()){
            qo.addQuery("obj.id",
                    new SysMap("monitorlineid", Long.valueOf(Long.parseLong(monitorlineid))), "=");
        }
        IPageList pList = this.monitorLineService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @RequestMapping({ "/deviceapp/thresholdsetlist.htm" })
    public ModelAndView thresholdSet(HttpServletRequest request, HttpServletResponse response, String currentPage,
            String monitorlineid, String sensorTypeName) {
    	User user = Utils.checkLoginedUser(request);
    	if(user == null){
    		return Utils.redirectToLoginForNonLogined(request, response);
    	}

        ModelAndView mv = new JModelAndView("deviceapp/thresholdsetlist.html", 0, request, response);
        mv.addObject(Globals.MV_ATTR_USERROLE, user.getUserRole()); // control the user operation based on role

        // return query condition
        mv.addObject("sensorTypeName", sensorTypeName);
        mv.addObject("monitorlineid", monitorlineid);

        // for condition selecting
        List<SensorType> sensorTypes = this.sensorTypeService.getUsingSensorTypes();
        mv.addObject("sensorTypes", sensorTypes);
        if(null != sensorTypeName && !sensorTypeName.isEmpty()){
            List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
            mv.addObject("monitorLines", monitorLines);
        }

        // Query object
        MonitorDataQueryObject qo = new MonitorDataQueryObject(currentPage, mv, "name", "ASC");
        qo.setPageSize(Integer.valueOf(12));
        if(null != monitorlineid && !monitorlineid.isEmpty()){
            qo.addQuery("obj.id",
                    new SysMap("monitorlineid", Long.valueOf(Long.parseLong(monitorlineid))), "=");
        }
        if(null != sensorTypeName && !sensorTypeName.isEmpty()){
            qo.addQuery("obj.sensorType",
                    new SysMap("sensorType", sensorTypeName), "=");
        }
        IPageList pList = this.monitorLineService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

        mv.addObject("userrole", user.getUserRole());
        return mv;
    }
    
    /**
     * a jquery request
     * @param request
     * @param response
     * @param formString
     */
    @RequestMapping({"/setsinkingthreshold.htm"})
    public void setSinkingThreshold(HttpServletRequest request, HttpServletResponse response, String formString) {
    	User user = Utils.checkLoginedUser(request);
    	if(user == null){
    		//"resultcode:1,resultstring:请登录!"
    		StringBuffer sb = new StringBuffer(Globals.WEB_RESULTCODE);
    		sb.append(":");
    		sb.append(Globals.WEB_RESULTCODE_NOTLOGIN);
    		Utils.setResponse(response, sb.toString());
			return; // not login
    	} else if(!user.getUserRole().equals(Globals.USERROLE_ADMIN)){
    		Utils.setResponse(response, "当前用户没有修改权限!");
			return; // not login
    	}
    	if(null != formString && !formString.isEmpty()){
    		String[] formDataArray = formString.split(";");
    		if(null != formDataArray && formDataArray.length > 0){
    			if(formDataArray.length != 2){
					Utils.setResponse(response, "数据格式错误!");
					return;
    			}
    			String monitorlineid = formDataArray[0];
    			String thresholdDataString = formDataArray[1];
    			if(null == thresholdDataString || thresholdDataString.isEmpty()){
    				Utils.setResponse(response, "数据格式错误!");
					return;
    			}
    			String[] thresholdDatas = thresholdDataString.split(",");
    			if(thresholdDatas.length != 3){
					Utils.setResponse(response, "阈值数据格式错误!");
					return;
    			}
    			double threshold1 = Double.valueOf(thresholdDatas[0].trim());
    			double threshold2 = Double.valueOf(thresholdDatas[1].trim());
    			double threshold3 = Double.valueOf(thresholdDatas[2].trim());
    			long monitorLineIdLong = Long.valueOf(Long.parseLong(monitorlineid));
    			MonitorLine monitorLine = this.monitorLineService.getObjById(monitorLineIdLong);
    			if(null == monitorLine){
					Utils.setResponse(response, "监测线不存在!"); // maybe be deleted before form committing
					return;
    			} else if(monitorLine.getThresholdSetStatus() == Globals.THRESHOLDSTATUS_ONGOING){
    				Utils.setResponse(response, "不能重复下发阈值!");
    				return;
    			}
    			
				monitorLine.setSinkingthresholdl1(threshold1);
				monitorLine.setSinkingthresholdl2(threshold2);
				monitorLine.setSinkingthresholdl3(threshold3);
				monitorLine.setThresholdSetStatus(Globals.THRESHOLDSTATUS_ONGOING);
				this.monitorLineService.update(monitorLine);
    			Utils.setResponse(response, Globals.WEB_RESULTCODE + ":2"); 
    			return;
				/*
    			String resultString = sendThreshold(monitorLine, threshold1, threshold2, threshold3);
    			if(null != resultString){
    				if(resultString.equals(ERRORSTRING_NETWORK)){
    					Utils.setResponse(response, "阈值下发失败，请检查终端网络是否正常!");
    	    			return;
    				}
    				Gson gs = new Gson();
    				Response appDeviceResponse = gs.fromJson(resultString, Response.class);
    				if(appDeviceResponse.getResultCode() == Response.RESULTCODE_SUCCESS){
    					this.monitorLineService.update(monitorLine);
    					Utils.setResponse(response, "阈值下发成功!");
    					return;
    				} else {
    					Utils.setResponse(response, "阈值下发失败!" + appDeviceResponse.getResultString());
    	    			return;
    				}
    			}
    			Utils.setResponse(response, "阈值下发失败!"); 
    			return;
    			*/
    		}
    	}
        
    }

    private static final String ERRORSTRING_NETWORK = "ERRORSTRING_NETWORK";

    private String sendThreshold(MonitorLine monitorLine, double threshold1, double threshold2, double threshold3) {
    	String sendStr = "{\"monitorLineName\":\"" + monitorLine.getName() + "\",\"" + "level1SinkingThreshold" + "\"" + ":" + threshold1 + ","
    		+ "\"" + "level2SinkingThreshold" + "\":" + threshold2 + "," + "\"" + "level3SinkingThreshold" + "\":" + threshold3 + "}";
        byte[] sendBuf = sendStr.getBytes();
        int port = monitorLine.getPort();
    	DatagramSocket client = null;
        try {
        	client = new DatagramSocket();
            InetAddress addr = InetAddress.getByName(monitorLine.getHostName());
            DatagramPacket sendPacket = new DatagramPacket(sendBuf ,sendBuf.length , addr , port);
			client.send(sendPacket);
	        byte[] recvBuf = new byte[1024];
	        DatagramPacket recvPacket = new DatagramPacket(recvBuf , recvBuf.length);
	        client.setSoTimeout(8000); // 8s
	        try {
	        	client.receive(recvPacket);
	        } catch (IOException e) { 
				e.printStackTrace();
				return ERRORSTRING_NETWORK;
			} 
	        String recvStr = new String(recvPacket.getData() , 0 ,recvPacket.getLength());
	        Logger.printlnWithTime("The response of threshold setting for " + monitorLine.getName() + " + is " + recvStr);
	        return recvStr;
		} catch (IOException e) {
			e.printStackTrace(); // return null behind
		} finally {
			if(null != client){
				client.close();
			}
		}
		return null;
    }

}
