package org.zqs.config.bean.assemble.xml;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanAssembleXmlTest {
    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 1.使用构造注入的方式装配
        System.out.println(appContext.getBean("user1"));
        // 2. 使用设值注入方式装配
        System.out.println(appContext.getBean("user2"));
    }
}
