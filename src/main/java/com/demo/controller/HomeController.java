package com.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.cache.CacheStore;
import com.demo.entity.LoginForm;
import com.demo.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@CrossOrigin
public class HomeController {

    @Autowired
    private CacheStore cacheStore;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String home() {
        return "index";
    }

    @RequestMapping(path = "/test", method = RequestMethod.POST)
    @ResponseBody
    public String test(@RequestBody JSONObject jsonParam, HttpServletResponse response) {
        System.out.println(jsonParam);
        Cookie cookie = new Cookie("access_token", "b7c9744a47614abea00b5aa50cc68021");
//        设置cookie的作用域：为”/“时，以在webapp文件夹下的所有应用共享cookie
        cookie.setPath("/");
        response.addCookie(cookie);
        return CommonUtil.getJSONString(200,"b7c9744a47614abea00b5aa50cc68021");
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

}
