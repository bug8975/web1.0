package com.monitor.action.deviceapp;

import java.text.SimpleDateFormat;

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
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.MonitorDataQueryObject;
import com.monitor.foundation.service.IContactService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISysConfigService;
import com.monitor.mv.JModelAndView;

@Controller
public class MSAlarmAction {
	@Autowired
	private IContactService contactService;

	@Autowired
	private ISysConfigService sysConfigService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@RequestMapping({ "/deviceapp/msalarmsetlist.htm" })
	public ModelAndView thresholdSet(HttpServletRequest request, HttpServletResponse response, String currentPage, String displayName) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}

		ModelAndView mv = new JModelAndView("deviceapp/msalarmsetlist.html", 0, request, response);

		// for condition selecting
		mv.addObject("displayName", displayName);

		// Query object
		MonitorDataQueryObject qo = new MonitorDataQueryObject(currentPage, mv, "id", "ASC");
		qo.setPageSize(Integer.valueOf(15));
		if(null != displayName && !displayName.isEmpty()){
			qo.addQuery("obj.displayName",
					new SysMap("displayName", displayName), "=");
		}
		IPageList pList = this.contactService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
