package com.monitor.action.deviceapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.foundation.dao.TrendAnalysisDao;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.TrendAnalysis;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

@Controller
public class TrendAnalysisAction {

	@Autowired
	private ISensorTypeService sensorTypeService;
	
	@Autowired
	private IMonitorLineService monitorLineService;
	
	@Autowired
	public TrendAnalysisDao trendanalysisDao;
	
	@RequestMapping({ "/deviceapp/trendanalysis.htm" })
	public ModelAndView trendanalysis(HttpServletRequest request,HttpServletResponse response,String compareTime,String trendInterval,String monitorlineid){
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("deviceapp/trendanalysis.html", 0, request, response);
		Date compareTimeDate;
		long compareTimeLong = 0;
		long monitorlineidLong;
		if(compareTime != null && compareTime!=""){
			try {
				compareTimeDate = Globals.DateFormat.parse(compareTime);
				compareTimeLong = compareTimeDate.getTime();
		    	long trendIntervalLong = Integer.parseInt(trendInterval) * 60 * 1000;//分钟转换为毫秒级
		    	monitorlineidLong = Long.parseLong(monitorlineid);
				trendanalysisDao.commit(compareTimeLong,trendIntervalLong,monitorlineidLong);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		List<TrendAnalysis>  trendanalysis= trendanalysisDao.getMsg();
		List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='P-levelingTransducer'", null, -1, -1);
		mv.addObject("trendanalysis", trendanalysis);
		mv.addObject("SensorTypes", sensorTypeService.getSensorTypeDisplayNames()); // for display in UI
		mv.addObject("monitorLines", monitorLines);
		return mv;
	}
	
	@RequestMapping({ "/deviceapp/trendDelete.htm" })
	public void timeModify(HttpServletRequest request, HttpServletResponse response,String monitorlineid) {
		long monitorlineidLong;
		int status = 0;
		if(monitorlineid!=null && monitorlineid!=null){
			monitorlineidLong = Long.parseLong(monitorlineid);
			status = trendanalysisDao.trendDelete(monitorlineidLong);
		}
		System.out.println(status);
		List notifylist = new ArrayList();
		Map map = new HashMap();
		map.put("status", status);
		notifylist.add(map);
		String temp = Json.toJson(notifylist, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
