package com.monitor.action.monitordata;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.core.domain.virtual.SysMap;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.tools.CommUtil;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.SensorQueryObject;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

@Controller
public class DataViewAction {

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	//    @SecurityMapping(title = "数据查看", value = "/monitordata/monitordatalist.htm*", rtype = "monitordata", rname = "监测数据", rcode = "monitordata", rgroup = "监测数据")
	@RequestMapping({ "/monitordata/monitordatalist.htm" })
	public ModelAndView monitorDataList(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String sensorTypeName, String monitorlineid, String sensorid, String alarmlevel) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("monitordata/monitordatalist.html",
				0, request, response);

		// return query condition
		mv.addObject("sensorTypeName", sensorTypeName);
		mv.addObject("monitorlineid", monitorlineid);
		mv.addObject("sensorid", sensorid);
		mv.addObject("alarmlevel", alarmlevel);

		// for condition selecting
		List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);
		mv.addObject("sensorTypes", sensorTypes);
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
			mv.addObject("monitorLines", monitorLines);
		}
		// for condition selecting
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id="+monitorlineid, null, -1, -1);
			mv.addObject("sensors", sensors);
		}

		// Query object
		SensorQueryObject qo = new SensorQueryObject(currentPage, mv, "name", "ASC");
		qo.setPageSize(Integer.valueOf(15));
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			qo.addQuery("obj.sensorType",
					new SysMap("sensorType", sensorTypeName), "=");
		}
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			qo.addQuery("obj.monitorLine.id",
					new SysMap("monitorlineid", Long.valueOf(Long.parseLong(monitorlineid))), "=");
		}
		if(null != sensorid && !sensorid.isEmpty()){
			qo.addQuery("obj.id",
					new SysMap("sensorid", Long.valueOf(Long.parseLong(sensorid))), "=");
		}
		if(null != alarmlevel && !alarmlevel.isEmpty()){
			qo.addQuery("obj.alarmLevel",
					new SysMap("alarmLevel", Integer.valueOf(Integer.parseInt(alarmlevel))), "=");
		} 

		IPageList pList = this.sensorService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

		mv.addObject("SensorTypes", sensorTypeService.getSensorTypeDisplayNames()); // for display in UI
		mv.addObject("SensorUnits", sensorTypeService.getSensorUnits()); // for display in UI

		return mv;
	}

	@RequestMapping({ "/monitordata/alarmlist.htm" })
	public ModelAndView alarmList(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String monitorlineid, String sensorid, String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView("monitordata/alarmlist.html",
				0, request, response);
		return mv;
	}

	/*
    @RequestMapping({"/load_sensor.htm"})
    public void load_area(HttpServletRequest request, HttpServletResponse response, String pid) {
        Map params = new HashMap();
        params.put("pid", Long.valueOf(Long.parseLong(pid)));
        List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id=:pid", params, -1, -1);
        List list = new ArrayList();
        for (Sensor sensor : sensors) {
            Map map = new HashMap();
            map.put("id", sensor.getId());
            map.put("sensorName", sensor.getName());
            list.add(map);
        }
        String temp = Json.toJson(list, JsonFormat.compact());
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
	 */
}
