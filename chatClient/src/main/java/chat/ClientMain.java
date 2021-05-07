package chat;

import chat.netty.NettyClient;

/**
 * Hello world!
 *
 */
public class ClientMain
{
    public static void main( String[] args )
    {
        NettyClient.start("127.0.0.1",9999);
    }
}
