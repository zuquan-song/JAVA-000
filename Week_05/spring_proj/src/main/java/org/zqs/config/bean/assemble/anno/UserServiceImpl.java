package org.zqs.config.bean.assemble.anno;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource(name="userDao")
    private UserDao userDao;

    @Override
    public void save() {
        this.userDao.save();
        System.out.println("user uservice save ... DONE!");
    }
}
