package chat.service.Impl;

import chat.bean.User;
import chat.mapper.UserMapper;
import chat.service.UserService;
import chat.utils.C3p0Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServiceImpl implements UserService {

    //对数据库的一些查询
    private UserMapper userMapper = new UserMapper();

    @Override
    public User getUserByName(String name) {
        User user = userMapper.getUserByName(name);
        return user;
    }

    @Override
    public boolean isExits(String name, String pwd) {
        boolean result = userMapper.isExits(name,pwd);
        return result;
    }

    @Override
    public String getPwd(String name, String email) {
        String pwd = userMapper.getPwd(name,email);
        return pwd;
    }

    @Override
    public boolean doRegister(String name, String pwd, String email) {
        boolean result = false;
        //判断数据库中是否存在同名的邮箱
        User use = userMapper.getUserByEmail(email);
        User user = userMapper.getUserByName(name);
        if (use == null){
            if (user == null){
                //如果数据库中没有同名的邮箱和用户名，将数据到插入到user表中
                int i = userMapper.insertRegisterByUser(name,pwd,email);
                if (i == 1){
                    result = true;
                }
            }else {
                //如果重名
                System.out.println("用户名重复");
            }
        }else {
            System.out.println("注册失败");
        }
        System.out.println("user:"+name+", pwd:"+pwd+", result:"+result);
        return result;
    }

    //修改密码
    @Override
    public boolean doModifyPwd(String name, String oldPwd, String newPwd) {
        boolean res = false;
        boolean isModify = userMapper.updateModifyPwdByName(name, oldPwd, newPwd);

        if (isModify){
            res = true;
        }
        System.out.println("旧的密码为:"+oldPwd+", 新的密码为："+newPwd+", result:"+res);
        return res;
    }

    //用户状态置为在线
    @Override
    public void doStateOnline(String name) {
        userMapper.updateUserStateToOnline(name);
    }

    //获取用户状态
    @Override
    public int getUserState(String name) {
        int state = userMapper.getUserStateByName(name);
        return state;
    }

    //获取状态ID
    @Override
    public int getStateIdByStateName(String name) {
        int id = 0;//userMapper.searchStateIdByName(name);
        return id;
    }

    //设置用户状态为离线
    @Override
    public void doStateOffline(String name) {
        userMapper.updateUserStateOffline(name);
    }

    //判断状态是否是离线
    @Override
    public boolean isStateOffLine(int userState) {
        String name = userMapper.getStateNameById(userState);
        if (name.equals("离线")){
            return true;
        }
        return false;
    }
}
