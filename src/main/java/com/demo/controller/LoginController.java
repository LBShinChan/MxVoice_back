package com.demo.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.fastjson.JSONObject;
import com.demo.entity.Response;
import com.demo.service.LoginService;
import com.demo.service.UserService;
import com.demo.utils.CommonUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/login")
public class LoginController {

    @Autowired
    LoginService loginService;

    @Autowired
    UserService userService;

    @RequestMapping(path = "/getQrCodeImg", method = RequestMethod.GET)
    public String createQrCodeImg(Model model) {

        String uuid = loginService.createQrImg();
        String qrCode = Base64.encodeBase64String(QrCodeUtil.generatePng("http://192.168.10.61:8080/login/scan?uuid=" + uuid, 300, 300));

        model.addAttribute("uuid", uuid);
        model.addAttribute("QrCode", qrCode);

        return "login";
    }

    @RequestMapping(path = "/getQrCodeStatus", method = RequestMethod.GET)
    @ResponseBody
    public Response getQrCodeStatus(@RequestParam String uuid, @RequestParam int currentStatus) throws InterruptedException {
        JSONObject data = loginService.getQrCodeStatus(uuid, currentStatus);
        return Response.createResponse(null, data);
    }

    /*
    以下两个方法用来模拟手机端的操作
     */
    @RequestMapping(path = "/scan", method = RequestMethod.GET)
    @ResponseBody
    public String scanQrCodeImg(String uuid) {
        System.out.println("扫码成功！");
        JSONObject data = loginService.scanQrCodeImg(uuid);
        if (data.getBoolean("valid")) {
            return CommonUtil.getJSONString(200, "扫码成功", data);
//            return Response.createResponse("扫码成功", data);
        }
        return CommonUtil.getJSONString(405, "二维码已失效");
//        return Response.createErrorResponse("二维码已失效");
    }

    @RequestMapping(path = "/confirm", method = RequestMethod.GET)
    @ResponseBody
    public String confirmLogin(String uuid) {
        boolean logged = loginService.confirmLogin(uuid);
        String msg = logged ? "登录成功!" : "二维码已过期!";
        int code = logged? 200:400;
        return CommonUtil.getJSONString(code, msg);
//        return Response.createResponse(msg, logged);
    }
}
