package chat.netty;

import chat.service.SendService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
    public static void start(String ip,int port){
    //创建事件循环组
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
        //创建启动辅助类实例
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new ClientHandler());
                    }
                });

            //同步启动客户端
            Channel channel = bootstrap.connect(ip, port).sync().channel();
            System.out.println("客户端启动啦");

            //通过channel和服务端交互
            SendService sendService = new SendService(channel);
            sendService.send();

            //关闭通信
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            loopGroup.shutdownGracefully();
        }
    }
}
