package com.monitor.action.picreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;
import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.domain.virtual.SysMap;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.Logger;
import com.monitor.foundation.dao.ManPowerDao;
import com.monitor.foundation.dao.MonitorDao;
import com.monitor.foundation.dao.MonitorLineDAO;
import com.monitor.foundation.domain.ManPowerData;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorDataImport;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.SensorQueryObject;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

import jcifs.dcerpc.msrpc.samr;

@Controller
public class ManPowerDataUploadAction implements ServletConfigAware,ServletContextAware{  

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	public MonitorDao monitorDao;

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private ManPowerDao manPowerDao;

	private ServletContext servletContext;  
	@Override  
	public void setServletContext(ServletContext arg0) {  
		this.servletContext = arg0;  
	}  
	private ServletConfig servletConfig;  
	@Override  
	public void setServletConfig(ServletConfig arg0) {  
		this.servletConfig = arg0;  
	}  

	@RequestMapping({ "/picreport/manpowerdataimport.htm" })
	public ModelAndView manpowerImport(HttpServletRequest request, HttpServletResponse response,String currentPage) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("picreport/manpowerdataimport.html", 0, request, response);

		return mv;
	}

	@RequestMapping(value = "/picreport/manpowerimportData.htm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView manimportdata(HttpServletRequest request, HttpSession session,HttpServletResponse response,String currentPage,
			String collectTime) {
		/*long collectT = 0;
		try {
			Date collectTimeDate = Globals.DateFormatWithHM.parse(collectTime);
			collectT = collectTimeDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(collectTime);
		System.out.println(collectT);*/
		ModelAndView mv = new JModelAndView("picreport/manpowerdataimport.html", 0, request, response);
		List list = new ArrayList();

		try {
			SmartUpload smartupload = new SmartUpload();
			smartupload.initialize(servletConfig, request, response);
			smartupload.upload();
			collectTime = smartupload.getRequest().getParameter("collectTime");
			currentPage = smartupload.getRequest().getParameter("currentPage");
			if(collectTime=="" || collectTime==null){
				return mv;
			}
			if(currentPage=="" || currentPage==null){
				return mv;
			}
			Date collectTimeDate = Globals.DateFormatWithHM.parse(collectTime);
			long collectT = collectTimeDate.getTime();
			ManPowerData data = new ManPowerData();
			data.setUploadTime(collectT);
			data.setObserDirection("后视");
			data.setNodeName("ZD1");
			data.setLevelDegree(0.89460);
			data.setStadiaLength(39.357);
			data.setStadiaDifference(-8.806);
			data.setAccumulatestadiaDifference(-8.7510);
			data.setHighDifference(-0.2500);
			list.add(data);
			ArrayList slist = new ArrayList();
			for(int i = 1;i <= 30;i++){
				Map map = new HashMap();
				map.put("line", i);
				map.put("view", "后视");
				map.put("node", "ZD1");
				map.put("data1", "0.89460");
				map.put("check1", "是");
				map.put("data2", "39.357");
				map.put("check2", "是");
				map.put("data3", "-8.806");
				map.put("check3", "否");
				map.put("data4", "-8.7510");
				map.put("check4", "是");
				map.put("data5", "-0.2500");
				map.put("check5", "是");
				slist.add(map);
			}
			if (list.size() > 0) {            
				mv = new JModelAndView("picreport/manpowerdataimport.html", 0, request, response);
				request.setAttribute("list", list);
				session.setAttribute("list", list);
				SensorQueryObject qo = new SensorQueryObject(currentPage, mv, "name", "ASC");
				qo.setPageSize(Integer.valueOf(15));
				PageUtils pageUtils = new PageUtils(slist, qo.getPageSize(), currentPage);
				mv.addObject("collectTime", collectTime);
				mv.addObject("show", pageUtils.getArrayList());
				mv.addObject("totalPage", new Integer(pageUtils.getPageCount()));
	            mv.addObject("pageSize", Integer.valueOf(pageUtils.getPageSize()));
	            mv.addObject("rows", new Integer(pageUtils.getRowCount()));
				mv.addObject("gotoPageFormHTML",CommUtil.showPageFormHtml(pageUtils.getCurrentPage(),pageUtils.getPageCount()));
	            mv.addObject("currentPage", new Integer(pageUtils.getCurrentPage()));
	            mv.addObject("gotoPageHTML", CommUtil.showPageHtml("", "",pageUtils.getCurrentPage(), pageUtils.getPageCount()));
				return mv;
			}
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SmartUploadException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		/*try {        
			SmartUpload smartupload = new SmartUpload();
			smartupload.initialize(servletConfig, request, response);
			smartupload.setAllowedFilesList("sql,txt,xls,DAT");
			smartupload.upload();

			com.jspsmart.upload.File uploadFile = smartupload.getFiles().getFile(0);

			if (! uploadFile.isMissing()) {
				String extensionName = uploadFile.getFileExt();
				String uploadPostFix = "/upload/docs/data/";
				String fileName = UUID.randomUUID().toString().replace("-", "");
				uploadPostFix = uploadPostFix + fileName + "." + extensionName;
				uploadFile.saveAs(uploadPostFix);

				String realPathFileName = session.getServletContext().getRealPath(uploadPostFix);                

				File newfile = new File(realPathFileName);
				if (newfile.exists()) {
					System.out.println("extension name: " + extensionName);

					if (extensionName.equals("DAT")) {    
						System.out.println("!");
						InputStream is = new FileInputStream(newfile);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}*/

		return mv;
	}




	@RequestMapping({ "/manimportDataOk.htm" })
	public  void  importDataOk(HttpServletRequest request,HttpServletResponse response, HttpSession session) {

		int count_update = 0;
		int count_insert = 0;

		List<ManPowerData> list = (List<ManPowerData>)session.getAttribute("list");

		for (int i=0; i<list.size(); i++) {
			if (manPowerDao.commit(list.get(i)) == 1) {
				count_update++;
			}
			else {
				count_insert++;
			}
		}
		session.removeAttribute("list");    

		List notifylist = new ArrayList();
		Map map = new HashMap();
		map.put("count", "导入数据成功！\r\n" + "总计: "+list.size()+"条\r\n更新: "+count_update+"条\r\n增加: " + count_insert+"条" );
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

	@RequestMapping({ "/manimportDataCancel.htm" })
	public ModelAndView importDataCancel(HttpServletRequest request,HttpServletResponse response, HttpSession session) {
		ModelAndView mv = new JModelAndView("picreport/manpowerdataimport.html", 0, request, response);

		session.removeAttribute("list");        
		return mv;
	}


}
