package chat.service;

import chat.constant.EnMsgType;
import chat.netty.ClientHandler;
import chat.utils.JsonUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;

import java.util.Scanner;

public class SendService {
    //客户端和服务端通信的channel实例
    private Channel channel;
    Scanner scanner = new Scanner(System.in);

    //缓存登录用户信息
    private String localName;


    public SendService(Channel channel){
        this.channel = channel;
    }

    public void send(){
        scanner.useDelimiter("\n");
        while (true){
            loginView();
            System.out.println("请选择:");
            String num = scanner.nextLine();
            if ("1".equals(num)){
                //登录操作
                System.out.println("登录操作：");
                System.out.println("请输入用户名：");
                String name = scanner.nextLine();
                System.out.println("请输入密码：");
                String pwd = scanner.nextLine();

                doLogin(name,pwd);
            }else if ("4".equals(num)){
                //退出
                System.out.println("退出操作：");
                System.exit(1);
                break;
            }else if("3".equals(num)){
                //忘记密码
                System.out.println("忘记密码");
                System.out.println("请输入用户名：");
                String name = scanner.nextLine();
                System.out.println("请输入邮箱：");
                String email = scanner.nextLine();

                //忘记密码
                forgetPwd(name,email);
            }else if ("1".equals(num)){
                //注册操作
                doRegister();
            }else {
                System.out.println("输入有误");
            }
        }


//        System.out.println("请输入用户名：");
//        while (scanner.hasNext()){
//            String msg = scanner.nextLine();
//
//            if (msg != null){
//                //封装给服务端JSON  msg msgType
//                ObjectNode objectNode = JsonUtils.getObjectNode();
//                objectNode.put("msgtype","test");
//                objectNode.put("name",msg);
//
//                //键盘输入的信息发送给服务端
//                channel.writeAndFlush(objectNode.toString());
//            }
//        }

    }

    private void doRegister() {
        System.out.println("请输入用户名：");
        String name = scanner.nextLine();
        System.out.println("请输入密码：");
        String pwd = scanner.nextLine();
        System.out.println("请输入邮箱账号：");
        String email = scanner.nextLine();

        //封装给服务端发送数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype",EnMsgType.EN_MSG_REGISTER.toString());
        objectNode.put("name",name);
        objectNode.put("pwd",pwd);
        objectNode.put("email",email);
        String msg = objectNode.toString();

        //给服务端发送消息
        channel.writeAndFlush(msg);

        //等服务端的返回消息
        int code = 0;

        try {
            code = ClientHandler.queue.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (code == 200){
            System.out.println("注册成功");
            mainMenu();
        }else {
            //返回第一次用户显示界面
            System.out.println("注册失败");

        }

    }

    private void forgetPwd(String name,String email){
        //参数校验
//        if ("".equals(name) || "".equals(email) || !email.endsWith("@qq.com") ||!email.endsWith("@163.com")){
//            System.out.println("输入参数有误。请重新操作!");
//            return;
//        }
        //封装参数
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype",EnMsgType.EN_MSG_FORGET_PWD.toString());
        objectNode.put("name",name);
        objectNode.put("email",email);
        //发送给服务端
        channel.writeAndFlush(objectNode.toString());
        //等待服务端返回结果
        int code = 0;

        try {
            code = ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (code == 200){
            //邮件发送成功
            System.out.println("邮件已发送到你邮箱，请登录邮箱查看你的密码！");
        }else if (code == 301){
            //服务端发送邮件失败
            System.out.println("发送邮件服务不可用，工程。。正在抢修");
        } else{
            //失败
            System.out.println("当前用户和邮箱不匹配，请重试！");
        }
    }

    //登录操作
    private void doLogin(String name,String pwd){
        /**
         * 给服务端发送消息
         * 封装JSON数据  msgtype：name pwd
         * channel 发送给服务端
         *
         * 服务端处理
         * 返回状态码 200：登录成功  ； 300：登陆失败  301参数不合法  302用户名不存在
         *
         * 客户端拿到状态码，成功：进入主菜单页面  失败：进入用户登录页面
         *
         * 注意：服务端返回给客户端的消息在事件循环组中的某一个线程，而登录操作是在主线程中，
         * 即主线程需要在子线程中获取结果，涉及线程间通信BlockingQueue
         *
         */
        //封装JSON消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", EnMsgType.EN_MSG_LOGIN.toString());
        objectNode.put("name",name);
        objectNode.put("pwd",pwd);

        //发送给服务端
        channel.writeAndFlush(objectNode.toString());

        int code = 0;
        try {
            //等待服务端返回结果
            code = ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (code == 200){

            //缓存用户信息
            localName = name;

            //登录成功
            System.out.println("登录成功");
            mainMenu();

        }else if (code == 300){
            System.out.println("尚未注册，请注册");
        }else {
            //登录失败
            System.out.println("登录失败");
        }

    }

    //用户登录页面
    private void loginView(){
        System.out.println("=====================");
        System.out.println("1.登录");
        System.out.println("2.注册");
        System.out.println("3.忘记密码");
        System.out.println("4.退出系统");
        System.out.println("=====================");
    }

    //主菜单
    private void mainMenu(){
        showMainMenu();
        System.out.println("请输入：");
        while (true){

            String msg = scanner.nextLine();

            if (msg == null || " ".equals(msg)){
                System.out.println("输入信息无效，请重新操作！");
                continue;
            }

            if ("quit".equals(msg)){
                System.out.println("用户下线操作");
                userOffLine();

                continue;
            }
            if ("help".equals(msg)){
                System.out.println("查询主菜单");
                showMainMenu();
                continue;
            }
            if ("getallusers" .equals(msg)){
                System.out.println("获取好友列表");
                //获取用户列表
                get_All_Users();
                continue;
            }

            String[] split = msg.split(":");

            if ("modifypwd".equals(split[0])){
                //修改密码
                System.out.println("修改密码操作");
                modifyPwd();

            }else if ("all".equals(split[0])){

                System.out.println("群发消息");

            }else if ("sendfile".equals(split[0])){

                System.out.println("发送文件操作");

            }else {
                //System.out.println("一对一聊天");
                singleChat(split[0],split[1]);

            }


        }
    }

    //获取用户列表
    private void get_All_Users() {
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype",EnMsgType.EN_MSG_GET_ALL_USERS.toString());
        String msg = objectNode.toString();

        //给服务端发送消息
        channel.writeAndFlush(msg);

    }

    //用户下线消息
    private void userOffLine() {
        String name = localName;
        //封装消息给服务端
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype",EnMsgType.EN_MSG_OFFLINE.toString());
        objectNode.put("name",name);
        String msg = objectNode.toString();

        //给服务端发送消息
        channel.writeAndFlush(msg);

        //等待服务端返回消息
        int code = 0;
        try {
            ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (code == 200){
            System.out.println("已下线");
        }else {
            System.out.println("操作失败");
            showMainMenu();
        }

    }

    //修改密码操作
    private void modifyPwd() {
        String name = localName;
        System.out.println("请输入旧密码：");
        String oldPwd = scanner.nextLine();
        System.out.println("请输入新密码");
        String newPwd = scanner.nextLine();

        //封装给服务端发送数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype",EnMsgType.EN_MSG_MODIFY_PWD.toString());
        objectNode.put("name",name);
        objectNode.put("oldPwd",oldPwd);
        objectNode.put("newPwd",newPwd);
        String msg = objectNode.toString();

        //给服务端发消息
        channel.writeAndFlush(msg);

        //等服务端的返回消息
        int code = 0;
        try {
            ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (code == 200){
            System.out.println("修改成功");
        }else {
            System.out.println("修改失败");
            showMainMenu();
        }

    }

    //单聊功能
    private void singleChat(String toname,String msg){
        /**
         * 封装Json消息 type fromname ， toName  msg
         *
         * 通过channel发送
         *
         * 服务端返回 code  200 ：发送服务端成功；300：接收方不合法
         */
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype",EnMsgType.EN_MSG_CHAT.toString());
        objectNode.put("fromname",localName);
        objectNode.put("toname",toname);
        objectNode.put("msg",msg);

        //发送服务端
        channel.writeAndFlush(objectNode.toString());

//        //等待服务端返回
        int code = 0;

        try {
            code = ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (code == 200){
            System.out.println("消息发送成功");
        }else if (code == 300){
            System.out.println("接收方不存在，请查验后再试");
        }else if (code == 301){
            System.out.println("接收方不在线，上线后立马推送");
        }

    }



    //主菜单页面
    private void showMainMenu(){
        System.out.println("====================系统使用说明=====================");
        System.out.println("                              注：输入多个信息用\":\"分割");
        System.out.println("1.输入modifypwd:username 表示该用户要修改密码");
        System.out.println("2.输入getallusers 表示用户要查询所有人员信息");
        System.out.println("3.输入username:xxx 表示一对一聊天");
        System.out.println("4.输入all:xxx 表示发送群聊消息");
        System.out.println("5.输入sendfile:xxx 表示发送文件请求:[sendfile][接收方用户名][发送文件路径]");
        System.out.println("6.输入quit 表示该用户下线，注销当前用户重新登录");
        System.out.println("7.输入help 查看系统菜单");
        System.out.println("======================================================");
    }

}
