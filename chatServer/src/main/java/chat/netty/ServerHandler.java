package chat.netty;

import chat.controller.NettyController;
import chat.utils.CacheUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    NettyController controller = new NettyController();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

    }

    //接收数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+":消息："+msg);

        Channel channel = ctx.channel();

        //客户端发送过来的消息，交给controller处理
        String recvMsg = controller.process((String) msg,channel);

        if (recvMsg != null){
            //将消息回写给客户端
            ctx.writeAndFlush(recvMsg);
        }


    }
    //用户上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+":上线啦");

        //用户在线，将用户的状态置为在线
        //在用户登录操作中设置为在线
        //


    }
    //用户下线
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        //删除用户缓存
        Channel channel = ctx.channel();
        CacheUtil.del(channel);

        System.out.println(ctx.channel().remoteAddress()+":下线啦");

        //将用户的状态置为离线



    }
}
