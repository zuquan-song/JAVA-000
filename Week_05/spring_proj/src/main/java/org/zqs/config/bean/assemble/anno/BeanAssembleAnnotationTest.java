package org.zqs.config.bean.assemble.anno;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanAssembleAnnotationTest {


    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");

        UserController userController = (UserController) applicationContext.getBean("userController");
        userController.save();
    }
}
