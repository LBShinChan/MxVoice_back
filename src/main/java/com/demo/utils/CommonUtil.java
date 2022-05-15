package com.demo.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.UUID;

public class CommonUtil {

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String buildTicketKey(String uuid) {
        return LoginConstant.PREFIX_TICKET + LoginConstant.SPLIT + uuid;
    }

    public static String buildUserKey(String userId) {
        return LoginConstant.PREFIX_USER + LoginConstant.SPLIT + userId;
    }

    public static String buildAccessTokenKey(String token) {
        return LoginConstant.ACCESS_TOKEN__PREFIX + token;
    }

    public static String buildOnceTokenKey(String token) {
        return LoginConstant.ONCE_TOKEN__PREFIX + token;
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map!=null){
            for(String key:map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

}
