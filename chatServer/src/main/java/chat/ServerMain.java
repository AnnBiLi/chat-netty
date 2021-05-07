package chat;

import chat.netty.NettyServer;

/**
 * Hello world!
 *
 */
public class ServerMain
{
    public static void main( String[] args )
    {
        NettyServer.start(9999);
    }
}
