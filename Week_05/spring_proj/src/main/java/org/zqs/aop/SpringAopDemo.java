package org.zqs.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringAopDemo {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Boy boy1 = (Boy) context.getBean("boy1");
        System.out.println(boy1.toString());
        Boy boy2 = (Boy) context.getBean("boy2");
        System.out.println(boy2.toString());

        Kclass clazz1 = context.getBean(Kclass.class);
        System.out.println(clazz1);
        clazz1.dong();
    }
}
