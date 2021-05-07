package chat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {
    public static void start(int port){
        //创建两个事件循环组，boss，worker事件循环组
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup(5);

        try {
        //创建启动辅助类
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boss,worker)
                //指定父事件循环组的通道类型
                .channel(NioServerSocketChannel.class)
                //指定子事件循环组的Handler信息
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //获取pipeline容器
                        ChannelPipeline pipeline = ch.pipeline();

                        //添加channelHandler
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new ServerHandler());
                    }
                });
        //同步启动服务端

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("服务端启动啦");
            //同步关闭资源
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //关闭事件循环组
            if (boss != null) {
                boss.shutdownGracefully();
            }
            if (worker != null) {
                worker.shutdownGracefully();
            }
        }
    }
}
