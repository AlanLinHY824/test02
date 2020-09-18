package com.powernode.JDBC;

import com.github.pagehelper.PageInfo;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/9/15
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        User user = new User();
        user.setPwd("123");
        user.setSname("张三");
//        PageInfo<User> pagination = JDBCUtil.pagination(User.class, new UserExample(),1, 1);
//        System.out.println(pagination.getList());
//        JDBCUtil.insert(user);
//        User user1 = new User();
//        user1.setPwd("456");
//        user1.setSname("李四");
//        UserExample userExample = new UserExample();
//        userExample.setSname("张三");
//        JDBCUtil.update(userExample,user1);
        UserExample user2 = new UserExample();
        user2.setSnameLike("a");
        PageInfo<User> pagination = JDBCUtil.pagination(User.class, user2,1, 1);
        System.out.println(pagination.getList());
//        JDBCUtil.delete(user2);
    }
}
