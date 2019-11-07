package com.monitor.action.user;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.Md5Encrypt;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.IUserService;
import com.monitor.mv.JModelAndView;

@Controller
public class LoginAction {
    
    @Autowired
    private IUserService userService;

//    @Autowired
//    private IMonitorLineService monitorLineService;
//    
//    @Autowired
//    private ISensorService sensorService;

    @RequestMapping({ "/login.htm" })
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response, String usernameMonitor,
    		String passwordMonitor) {
        ModelAndView mv = new JModelAndView("login.html",0, request, response);
        return mv;
    }

    @RequestMapping({ "/loginFinish.htm" })
    public ModelAndView loginFinish(HttpServletRequest request, HttpServletResponse response, String usernameMonitor,
    		String passwordMonitor) {
    	if(null == usernameMonitor || usernameMonitor.isEmpty() 
    			|| null == passwordMonitor || passwordMonitor.isEmpty()){
            ModelAndView mv = new JModelAndView("login.html",0, request, response);
            mv.addObject("errorString", "用户名和密码不能为空!");
            return mv;
    	}
    	User loginedUser = Utils.checkLoginedUser(request);
    	if(loginedUser != null){
    		if(loginedUser.getUserName().equals(usernameMonitor)) {
    			return redirectToFirstViewPage(request, response);
    		} else {
    			request.getSession().setAttribute(Globals.SESSION_ATTR_USER, null);//remove user from session
    		}
    	}
        Map<String, String> params = new HashMap<String, String>();
        params.put("userName", usernameMonitor);
    	List<User> users = this.userService.query("select obj from User obj where obj.userName=:userName", params, -1, -1);
    	if(null == users || users.isEmpty()){
            ModelAndView mv = new JModelAndView("login.html", 0, request, response);
            mv.addObject("errorString", "用户不存在!");
            return mv;
    	} else if(null != passwordMonitor && !passwordMonitor.isEmpty()) {
    		User user = users.get(0);
    		String encryptedPwd = Md5Encrypt.md5(passwordMonitor);
    		if(encryptedPwd.equalsIgnoreCase(user.getPassword())){
    			request.getSession().setAttribute(Globals.SESSION_ATTR_USER, user);//登录成功，将用户数据放入到Session中
    			return redirectToFirstViewPage(request, response);
    		} else {
                ModelAndView mv = new JModelAndView("login.html",0, request, response);
                mv.addObject("errorString", "密码错误!");
                return mv;
    		}
    	} else {
            ModelAndView mv = new JModelAndView("login.html", 0, request, response);
            mv.addObject("errorString", "密码不能为空!");
            return mv;
    	}
    }


	private ModelAndView redirectToFirstViewPage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("redirect:/home.htm");
		/*
//		ModelAndView mv = new JModelAndView("monitordata/monitordatalist.html", 0, request, response);
		// for condition selecting
		List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj", null, -1, -1);
		mv.addObject("monitorLines", monitorLines);

		// Query object
		SensorQueryObject qo = new SensorQueryObject("1", mv, "name", "ASC");
		qo.setPageSize(Integer.valueOf(15));
		IPageList pList = this.sensorService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		*/
		return mv;
	}

	@RequestMapping({ "/logout.htm" })
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response){
    	User loginedUser = Utils.checkLoginedUser(request);
    	if(loginedUser != null){
    		request.getSession().setAttribute(Globals.SESSION_ATTR_USER, null);//remove user from session
    	}
        return new JModelAndView("login.html", 0, request, response);
        //       return new ModelAndView("redirect:http://114.55.59.23:8888/sub/");
	}
	   
    @RequestMapping({"/verify.htm"})
    public void verify(HttpServletRequest request, HttpServletResponse response, String name) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        HttpSession session = request.getSession(false);

        int width = 73;
        int height = 27;
        BufferedImage image = new BufferedImage(width, height, 1);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);

        g.setFont(new Font("Times New Roman", 0, 24));

        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; ++i) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        String sRand = "";
        for (int i = 0; i < 4; ++i) {
            String rand = CommUtil.randomInt(1).toUpperCase();
            sRand = sRand + rand;

            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 24);
        }
//TODO
//        if (CommUtil.null2String(name).equals(""))
//            session.setAttribute("verify_code", sRand);
//        else {
//            session.setAttribute(name, sRand);
//        }

        g.dispose();
        ServletOutputStream responseOutputStream = response.getOutputStream();

        ImageIO.write(image, "JPEG", responseOutputStream);

        responseOutputStream.flush();
        responseOutputStream.close();
    }

    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}
