package org.zqs.config.bean.assemble.anno;

import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoImpl implements UserDao {

    @Override
    public void save() {
        System.out.println("save data for user");
    }
}
