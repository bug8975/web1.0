package com.monitor.action.monitordata;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.monitor.core.tools.CommUtil;
import com.monitor.foundation.dao.BimVizDao;
import com.monitor.foundation.domain.BimVizProject;
import com.monitor.mv.JModelAndView;

@Controller
public class BimVizRelationAction {

	@Autowired
	private BimVizDao bimVizDao;


	@Autowired
	JdbcTemplate jdbcTemplate;

	//    @SecurityMapping(title = "BIM三维模型", value = "/monitordata/bimViz.htm*")
	@RequestMapping({ "/monitordata/bimViz.htm" })
	public ModelAndView monitorDataList(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("monitordata/bimViz.html",
				0, request, response);
		List<BimVizProject> bimVizProjects = bimVizDao.getBimProject();
		BimVizProjectMsg msg = new BimVizProjectMsg();
		for(BimVizProject data:bimVizProjects){
			msg.setBimProjectId(data.getBim_projectid());
			msg.setProjectId(data.getProject_id());
		}
        String webPath = CommUtil.getURL(request);
        msg.setWebPath(webPath);
        Gson gs = new Gson();
        String BimVizMsgString = gs.toJson(msg);
		mv.addObject("BimVizMsgString", BimVizMsgString);
		return mv;
	}
	
	public class BimVizProjectMsg {
		private String bimProjectId;
		private int projectId;
		private String webPath;
		
		public String getBimProjectId() {
			return bimProjectId;
		}
		public void setBimProjectId(String bimProjectId) {
			this.bimProjectId = bimProjectId;
		}
		public int getProjectId() {
			return projectId;
		}
		public void setProjectId(int projectId) {
			this.projectId = projectId;
		}
		public String getWebPath() {
			return webPath;
		}
		public void setWebPath(String webPath) {
			this.webPath = webPath;
		}
	}

}
