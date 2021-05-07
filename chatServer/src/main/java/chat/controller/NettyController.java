package chat.controller;

import chat.bean.User;
import chat.constant.EnMsgType;
import chat.service.UserService;
import chat.service.Impl.UserServiceImpl;
import chat.utils.CacheUtil;
import chat.utils.EmailUtils;
import chat.utils.JsonUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;

import java.util.ArrayList;


public class NettyController {

    private static UserService userService = new UserServiceImpl();

    //和客户端建立channel的实例
    private Channel channel;

    /**
     * netty发送的业务请求全部在该方法处理
     * @param msg
     * @return
     */
    public  String process(String msg, Channel channel){
        this.channel = channel;

        //解析客户端发送的消息
        ObjectNode objectNode = JsonUtils.getObjectNode(msg);
        String msgtype = objectNode.get("msgtype").asText();

        if (EnMsgType.EN_MSG_LOGIN.toString().equals(msgtype)) {
            //登录操作
            return doLogin(objectNode);

        }else if (EnMsgType.EN_MSG_REGISTER.toString().equals(msg)) {
            //注册操作
            return doRegister(objectNode);

        } else if (EnMsgType.EN_MSG_FORGET_PWD.toString().equals(msgtype)) {
            //忘记密码功能
            return forgetPwd(objectNode);

        }else if (EnMsgType.EN_MSG_MODIFY_PWD.toString().equals(msgtype)){
            //修改密码功能
            return modifyPwd(objectNode);

        }else if (EnMsgType.EN_MSG_CHAT.toString().equals(msgtype)) {
            //单聊功能
            return singleChat(objectNode);
        }else if (EnMsgType.EN_MSG_OFFLINE.toString().equals(msgtype)){
            //用户下线操作
            return doUserOffLine(objectNode);
        }else if (EnMsgType.EN_MSG_GET_ALL_USERS.toString().equals(msgtype)){
            //查询所有用户在线
            return getAllUsers(objectNode);
        }

        return "";
    }

    private String getAllUsers(ObjectNode objectNode) {
        ObjectNode jsonNodes = JsonUtils.getObjectNode();
        jsonNodes.put("msgtype",EnMsgType.EN_MSG_ACK.toString());
        jsonNodes.put("srctype",EnMsgType.EN_MSG_GET_ALL_USERS.toString());

        ArrayList list = CacheUtil.getUsers();

        if (list != null){
            jsonNodes.put("users",list.toString());
            jsonNodes.put("code",200);
        }else {
            //有错
            jsonNodes.put("code",400);
        }
        return jsonNodes.toString();

    }

    /**
     * 用户下线操作
     * @param objectNode
     * @return
     */
    private String doUserOffLine(ObjectNode objectNode) {
        //解析参数
        String name = objectNode.get("name").asText();

        //封装返回数据
        ObjectNode jsonNodes = JsonUtils.getObjectNode();
        jsonNodes.put("msgtype",EnMsgType.EN_MSG_ACK.toString());
        jsonNodes.put("srctype",EnMsgType.EN_MSG_OFFLINE.toString());

        //删除在线channel
        CacheUtil.del(name);

        if (!CacheUtil.getCacheCS().containsKey(name)){
            //code 200
            jsonNodes.put("code",200);
        }else {
            //失败
            jsonNodes.put("code",400);
        }
        return jsonNodes.toString();

//        //用户状态设置为离线
//        userService.doStateOffline(name);
//        //暂时不做返回客户端消息
    }


    /**
     * 注册操作
     * @param objectNode
     * @return
     */
    private String doRegister(ObjectNode objectNode) {
        //解析参数
        String name = objectNode.get("name").asText();
        String pwd = objectNode.get("pwd").asText();
        String email = objectNode.get("email").asText();

        //判断用户是否能够注册成功
        boolean isRegister = userService.doRegister(name,pwd,email);

        //封装返回消息
        ObjectNode jsonNodes = JsonUtils.getObjectNode();
        jsonNodes.put("msgtype",EnMsgType.EN_MSG_ACK.toString());
        jsonNodes.put("srctype",EnMsgType.EN_MSG_REGISTER.toString());

        if (isRegister){
            //注册成功  200表示注册成功
            jsonNodes.put("code",200);
        }else {
            //用户已经存在 300表示注册失败
            jsonNodes.put("code",300);
        }
        //将结果返回到客户端
        return jsonNodes.toString();
    }

    /**
     * 登录操作
     * @param objectNode
     * @return
     */
    private  String doLogin(ObjectNode objectNode) {
        //解析数据
        String name = objectNode.get("name").asText();
        String pwd = objectNode.get("pwd").asText();
        System.out.println("登录操作参数：name:"+name+";pwd:"+pwd);

        //数据库进行数据验证，判断用户是否存在
        boolean exits = userService.isExits(name, pwd);

        //封装返回信息
        ObjectNode jsonNodes = JsonUtils.getObjectNode();
        jsonNodes.put("msgtype",EnMsgType.EN_MSG_ACK.toString());
        jsonNodes.put("srctype",EnMsgType.EN_MSG_LOGIN.toString());
        jsonNodes.put("code",300);

        if (exits){
            //用户合法
            jsonNodes.put("code",200);

            //添加用户在线信息
            CacheUtil.put(name,channel);
            //将用户状态设置为在线
            userService.doStateOnline(name);

        }else {
            //用户不存在
            objectNode.put("code",300);
        }
        //将结果返回给用户端
        return jsonNodes.toString();
    }

    /**
     * 忘记密码功能
     */
    private  String forgetPwd(ObjectNode obj){
        //解析JSON数据
        String name = obj.get("name").asText();
        String email = obj.get("email").asText();

        //查询数据库获取密码
        String pwd = userService.getPwd(name, email);

        //封装返回数据
        ObjectNode jsonNodes = JsonUtils.getObjectNode();
        jsonNodes.put("msgtype",EnMsgType.EN_MSG_ACK.toString());
        jsonNodes.put("srctype",EnMsgType.EN_MSG_FORGET_PWD.toString());

        if (pwd == null || "".equals(pwd)){
            //没有查到
            jsonNodes.put("code",300);
        }else {
            //查到用户
            jsonNodes.put("code",200);
            try {
                EmailUtils.toEmail(email,"密码:"+pwd);
            } catch (Exception e) {
                //301邮件发送异常
                jsonNodes.put("code",301);
                e.printStackTrace();
            }
        }
        return jsonNodes.toString();
    }

    /**
     * 修改密码功能
     * @param objectNode
     * @return
     */
    private String modifyPwd(ObjectNode objectNode) {
        //解析参数
        String name=objectNode.get("name").asText();
        String oldPwd = objectNode.get("oldPwd").asText();
        String newPwd = objectNode.get("newPwd").asText();

        //判断修改密码是否成功
        boolean isSuccessModifyPwd = userService.doModifyPwd(name,oldPwd,newPwd);

        //封装返回信息
        ObjectNode jsonNodes = JsonUtils.getObjectNode();
        jsonNodes.put("msgtype",EnMsgType.EN_MSG_ACK.toString());
        jsonNodes.put("srctype",EnMsgType.EN_MSG_FORGET_PWD.toString());

        if (isSuccessModifyPwd){
            //修改密码成功
            jsonNodes.put("code",200);
        }else {
            //修改密码失败
            jsonNodes.put("code",300);
        }

        return jsonNodes.toString();
    }

    /**
     *  单聊功能  一对一聊天
     * @param obj
     * @return
     */
    private  String singleChat(ObjectNode obj){
        /**
         * 解析JSON数据  接收方的名称
         *
         * 查证用户的合法性
         *
         * 判断用户是否在线 cacheUtil
         *
         * 在线：通过channel进行转发
         * 不在线：将消息存储，用户上线在做推送（自行开发）
         */
        String toName = obj.get("toname").asText();
        String fromName = obj.get("fromname").asText();
        String msg = obj.get("msg").asText();
        int msgtype = obj.get("msgtype").asInt();

        //封装给发送方JSON消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype",EnMsgType.EN_MSG_ACK.toString());
        objectNode.put("srctype",EnMsgType.EN_MSG_CHAT.toString());

        //判断用户是否存在
        User user = userService.getUserByName(toName);
        if (user == null){
            //用户不合法
            System.out.println(toName+"用户不存在");
            objectNode.put("code",300);
            return objectNode.toString();
        }

//        //存在后，判断用户是否在线
        Channel channel = CacheUtil.get(toName);
        if (channel == null ){
            //用户不在线
            objectNode.put("code",301);

            //消息存储到数据库???

        }else {
            //用户在线
            //消息转发
            channel.writeAndFlush(obj.toString());
            objectNode.put("code",200);
            System.out.println("用户在线，可以发送消息");

        }


        return objectNode.toString();
    }




}
