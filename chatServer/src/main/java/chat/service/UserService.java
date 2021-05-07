package chat.service;

import chat.bean.User;

public interface UserService {
    /**
     * 根据用户名查找用户是否存在
     * @param name
     * @return
     */
    User getUserByName(String name);

    /**
     * 判读用户是否存在
     * @param name
     * @param pwd
     * @return
     */
    boolean isExits(String name,String pwd);

    /**
     * 根据用户名和邮箱获取用户密码
     * @param name
     * @param email
     * @return
     */
    String getPwd(String name,String email);

    /**
     * 判断用户能否注册
     * @param name
     * @param pwd
     * @param email
     * @return
     */
    boolean doRegister(String name, String pwd, String email);

    /**
     * 判断密码是否修改成功
     * @param name
     * @param oldPwd
     * @param newPwd
     * @return
     */
    boolean doModifyPwd(String name, String oldPwd, String newPwd);

    /**
     * 将用户的状态置为在线
     * @param name
     */
    void doStateOnline(String name);

    /**
     * 获取用户状态
     * @param name
     * @return
     */
    int getUserState(String name);

    /**
     * 根据状态名获取状态ID
     * @param name
     * @return
     */
    int getStateIdByStateName(String name);

    /**
     * 用户状态设置为离线状态
     * @param name
     */
    void doStateOffline(String name);

    /**
     * 根据状态id判断是否是离线状态
     * @param userState
     */
    boolean isStateOffLine(int userState);
}
