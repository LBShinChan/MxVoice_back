package com.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.cache.CacheStore;
import com.demo.entity.Visitor;
import com.demo.repository.VisitorRepository;
import com.demo.utils.CommonUtil;
import com.demo.utils.CommunityConstant;
import com.demo.utils.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@CrossOrigin
public class HomeController implements CommunityConstant {

    @Autowired
    private CacheStore cacheStore;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${mxvoice.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String home() {
        return "index";
    }

    @RequestMapping(path = "/test", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestBody JSONObject jsonParam, HttpServletResponse response) {
        System.out.println(jsonParam);
        String username = jsonParam.getString("username");
        Visitor visitor = visitorRepository.findByUsername(username).orElse(null);
        if(visitor==null){
            return CommonUtil.getJSONString(404,"用户不存在，请先注册！");
        }
        String password = jsonParam.getString("password");
        if(!visitor.getPassword().equals(password)){
            return CommonUtil.getJSONString(403,"密码错误，请重试！");
        }
        if(visitor.getActivation()!=1){
            return CommonUtil.getJSONString(405,"当前账号尚未激活！");
        }

        String access_token = CommonUtil.generateUUID();
        Cookie cookie = new Cookie("access_token", access_token);
//        设置cookie的作用域：为”/“时，以在webapp文件夹下的所有应用共享cookie
        cookie.setPath("/");
        response.addCookie(cookie);

        visitor.setAccessToken(access_token);
        visitorRepository.save(visitor);

        cacheStore.put(CommonUtil.buildAccessTokenKey(access_token), "1");

        return CommonUtil.getJSONString(200, access_token);
    }

    @RequestMapping(path = "/ver", method = RequestMethod.POST)
    @ResponseBody
    public String ver(@RequestBody JSONObject jsonParam, HttpServletResponse response) {
        System.out.println(jsonParam);
        Long time = cacheStore.getExpireForSeconds(CommonUtil.buildAccessTokenKey(jsonParam.getString("access_token")));
        System.out.println(time);
        return CommonUtil.getJSONString(200);
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public String logout(@RequestBody JSONObject jsonParam, HttpServletResponse response) {
        System.out.println(jsonParam);
        cacheStore.delete(CommonUtil.buildAccessTokenKey(jsonParam.getString("access_token")));
        return CommonUtil.getJSONString(200);
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(@RequestBody JSONObject jsonParam, HttpServletResponse response) {
        String username = jsonParam.getString("username");
        String password = jsonParam.getString("password");
        String clientid = jsonParam.getString("clientid");
        String email = jsonParam.getString("email");
        int activation = 0;
        Visitor visitor = new Visitor(username, email, password, clientid, activation, CommonUtil.generateUUID());
        visitorRepository.save(visitor);

        Context context = new Context();
        context.setVariable("email", visitor.getEmail());
        // 激活路径
        // http://localhost:8080/community/activation/user-id/code
        // 此id为mybatis自动生成，分别对应mybatis.configuration.useGeneratedKeys=true
        // 和user-mapper中的insert key-properties
        String url = domain + contextPath + "/activation/" + visitor.getId() + "/" + visitor.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/activation", context);
        mailClient.sendMail(visitor.getEmail(), "激活账号", content);

        return CommonUtil.getJSONString(200);
    }

    @RequestMapping(path = "/activation/{id}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("id") int id, @PathVariable("code") String code){
        int result = 0;
        Visitor visitor = visitorRepository.findById(id).orElse(null);
        if(visitor!=null){
            int activationStatus = visitor.getActivation();
            String activationCode = visitor.getActivationCode();

            if(activationStatus==1){
                result = ACTIVATION_REPEAT;
            }else if(activationCode.equals(code)){
                visitor.setActivation(1);
                visitorRepository.save(visitor);
                result = ACTIVATION_SUCCESS;
            }else {
                result = ACTIVATION_FAILURE;
            }

        }

        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，账号已可以正常使用！");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","重复激活！");
        }else {
            model.addAttribute("msg","激活失败，激活码不正确");
        }
        return "/operate-result";
    }

}
