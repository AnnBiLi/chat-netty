package chat.netty;

import chat.constant.EnMsgType;
import chat.utils.JsonUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.SynchronousQueue;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    //定义一个同步阻塞队列，用户主线程和工作线程通信，将子线程获取的服务端返回交给主线程
    public  static SynchronousQueue<Integer> queue = new SynchronousQueue<>();


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

    }

    //接收服务端返回消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //服务端返回结果
        System.out.println("[recv]:"+msg);

        //解析服务端返回结果
        ObjectNode objectNode = JsonUtils.getObjectNode((String) msg);
        String msgtype = objectNode.get("msgtype").asText();
        if (EnMsgType.EN_MSG_ACK.toString().equals(msgtype)){
            //回复的ACK消息
            String srctype = objectNode.get("srctype").asText();
            if (EnMsgType.EN_MSG_LOGIN.toString().equals(srctype)){
                //登录操作的回复消息
                int code = objectNode.get("code").asInt();

                //通过同步队列交给主线程
                queue.offer(code);
            }else if (EnMsgType.EN_MSG_FORGET_PWD.toString().equals(srctype)){
                //忘记密码的回复消息
                int code = objectNode.get("code").asInt();

                //通过同步队列交给主线程
                queue.offer(code);
            } else if (EnMsgType.EN_MSG_CHAT.toString().equals(srctype)) {
                //发送端的返回消息
                int code = objectNode.get("code").asInt();
                queue.offer(code);
                //将状态码发给父线程处理
//                if (code == 300){
//                    System.out.println("接收方不存在");
//                }else if (code == 400){
//                    System.out.println("接收方不在线，已经成功将消息放入数据库的缓存中");
//                }else {
//                    System.out.println("消息发送成功");
//                }
            }
        }else if (EnMsgType.EN_MSG_CHAT.toString().equals(msgtype)){
            //接收端接收消息

            String fromname = objectNode.get("fromname").asText();
            String msg1 = objectNode.get("msg").asText();
            System.out.println(fromname+"发送消息:"+msg1);
        }

    }
}
