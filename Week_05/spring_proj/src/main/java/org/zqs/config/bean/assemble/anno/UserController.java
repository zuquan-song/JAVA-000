package org.zqs.config.bean.assemble.anno;

import lombok.Setter;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller("userController")
public class UserController {

    @Setter
    @Resource(name="userService")
    private UserService userService;

    public void save() {
        this.userService.save();
        System.out.println("user controller save ... DONE!");
    }
}
