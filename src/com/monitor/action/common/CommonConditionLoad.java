package com.monitor.action.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;

@Controller
public class CommonConditionLoad {

    @Autowired
    private ISensorService sensorService;

    @Autowired
    private IMonitorLineService monitorLineService;
    
    @Autowired
    private ISensorTypeService sensorTypeService;

    @RequestMapping({"/load_sensor.htm"})
    public void loadSensor(HttpServletRequest request, HttpServletResponse response, String pid) {
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

    @RequestMapping({"/load_sensor_without_basepoint.htm"})
    public void loadSensorWithoutBasepoint(HttpServletRequest request, HttpServletResponse response, String pid) {
        Map params = new HashMap();
        params.put("pid", Long.valueOf(Long.parseLong(pid)));
        List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.base=false and obj.monitorLine.id=:pid", params, -1, -1);
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


    @RequestMapping({"/load_monitorline.htm"})
    public void loadMonitorLine(HttpServletRequest request, HttpServletResponse response, String sensorTypeName) {
        List<MonitorLine> monitorLines= this.monitorLineService.getObjsBySensorTypeName(sensorTypeName);
        List list = new ArrayList();
        for (MonitorLine monitorLine : monitorLines) {
            Map map = new HashMap();
            map.put("id", monitorLine.getId());
            map.put("monitorLineName", monitorLine.getName());
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
}
