package com.demo;

import com.demo.cache.CacheStore;
import com.demo.entity.Visitor;
import com.demo.repository.VisitorRepository;
import com.demo.utils.CommonUtil;
import com.demo.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
class LoginApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private CacheStore cacheStore;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private VisitorRepository visitorRepository;

    @Test
    void insertUser() {
        User user = new User();
        user.setUserId("1");
        user.setUserName("John同学");
        user.setAvatar("/avatar.jpg");
        cacheStore.put("user:1", user);
    }

    @Test
    void loginByPhone() {
        String accessToken = CommonUtil.generateUUID();
        System.out.println(accessToken);
        cacheStore.put(CommonUtil.buildAccessTokenKey(accessToken), "1");
    }

    @Test
    void sendMessage(){
        mailClient.sendMail("1733136292@qq.com", "Test text mail", "Test Success!");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "1733136292@qq.com");
        String content = templateEngine.process("/demo", context);
        System.out.println(content);

        mailClient.sendMail("1733136292@qq.com", "Test Html", content);
    }

    @Test
    public void testJpaInsert(){
        String username = "test2";
        String password = "testtest";
        String clientid = "sdasda54635a64ds4a4d";
        String email = "test@test.com";
        int activation = 0;
        Visitor visitor = new Visitor(username, email, password, clientid, activation, CommonUtil.generateUUID());
        visitorRepository.save(visitor);
    }

    @Test
    public void testJpaUpdate(){
        String username = "test";
        String password = "testtest";
        String clientid = "sdasda54635a64ds4a4d";
        String email = "test@test.com";
        int activation = 0;
        Visitor visitor1 = visitorRepository.findByUsername(username).orElse(null);
        if(visitor1!=null){
            visitor1.setActivation(1);
            visitorRepository.save(visitor1);
        }
        System.out.println(visitor1);
    }
}
