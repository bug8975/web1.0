package com.monitor.action.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.tools.CommUtil;
import com.monitor.foundation.domain.SysConfig;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISysConfigService;
import com.monitor.mv.JModelAndView;

@Controller
public class CommonViewAction {

    @Autowired
    private ISensorService sensorService;

    @Autowired
    private ISysConfigService sysConfigService;

    @RequestMapping({"/index.htm"})
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("login.html",
                1, request, response);
        return mv;
    }

    // @SecurityMapping(title = "菜单导航", value = "/common/menu_nav.htm*", rtype = "common", rname = "公用菜单", rcode = "menu_nav", rgroup = "公用菜单")
    @RequestMapping({"/common/menu_nav.htm"})
    public ModelAndView nav(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("common/menu_nav.html",
                0, request, response);
        String op = CommUtil.null2String(request.getAttribute("op"));
        mv.addObject("op", op);
        User user = Utils.checkLoginedUser(request);
        if(null != user){
            mv.addObject(Globals.MV_ATTR_USERNAME, user.getUserName());
            mv.addObject(Globals.MV_ATTR_USERROLE, user.getUserRole());
        }
        return mv;
    }

    // @SecurityMapping(title = "页头导航", value = "/common/head.htm*", rtype = "common", rname = "公用页头", rcode = "head", rgroup = "公用页头")
    @RequestMapping({"/common/head.htm"})
    public ModelAndView head(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("common/head.html",
                0, request, response);

        String menu = CommUtil.null2String(request.getAttribute("menu"));
        mv.addObject("menu", menu);

        return mv;
    }

    
    @RequestMapping({"/common/top.htm"})
    public ModelAndView top(HttpServletRequest request,
                            HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("common/top.html",
                1, request, response);
        long sensorCount = this.sensorService.getTotal();
        mv.addObject("sensorCount", sensorCount);
        long sensorCountL1Alarm = this.sensorService.getTotalWithLevel1Alarm();
        mv.addObject("sensorCountL1Alarm", sensorCountL1Alarm);
        long sensorCountL2Alarm = this.sensorService.getTotalWithLevel2Alarm();
        mv.addObject("sensorCountL2Alarm", sensorCountL2Alarm);
        long sensorCountL3Alarm = this.sensorService.getTotalWithLevel3Alarm();
        mv.addObject("sensorCountL3Alarm", sensorCountL3Alarm);
        List<SysConfig> systitle = this.sysConfigService.query("select obj from SysConfig obj", null, -1, -1);
        mv.addObject(Globals.SYSCONF_WBESITE_NAME, systitle.get(0).getValue());
        return ((ModelAndView) mv);
    }
    
    @RequestMapping({"/common/bottom.htm"})
    public ModelAndView bottom(HttpServletRequest request,
                               HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("common/bottom.html",
                1, request, response);
//        mv.addObject("navTools", this.navTools);
        return mv;
    }

    @RequestMapping({"/common/getsensorcount.htm"})
    public void getsensorcount(HttpServletRequest request,
                            HttpServletResponse response) {
        Map map = new HashMap();
        long sensorCount = this.sensorService.getTotal();
        map.put("sensorCount", sensorCount);
        long sensorCountL1Alarm = this.sensorService.getTotalWithLevel1Alarm();
        map.put("sensorCountL1Alarm", sensorCountL1Alarm);
        long sensorCountL2Alarm = this.sensorService.getTotalWithLevel2Alarm();
        map.put("sensorCountL2Alarm", sensorCountL2Alarm);
        long sensorCountL3Alarm = this.sensorService.getTotalWithLevel3Alarm();
        map.put("sensorCountL3Alarm", sensorCountL3Alarm);
        Gson gs = new Gson();
        String jsonString = gs.toJson(map);

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @RequestMapping({"/common/getSysTitle.htm"})
    public void getSysTitle(HttpServletRequest request,
                            HttpServletResponse response) {
        Map map = new HashMap();
        List<SysConfig> systitle = this.sysConfigService.query("select obj from SysConfig obj", null, -1, -1);
        map.put(Globals.SYSCONF_WBESITE_NAME, systitle.get(0).getValue());
        Gson gs = new Gson();
        String jsonString = gs.toJson(map);

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
