package io.github.kimmking.gateway.inbound;

import io.github.kimmking.gateway.filter.HttpRequestFilterImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpInboundServer {
    private static Logger logger = LoggerFactory.getLogger(HttpInboundServer.class);

    private int port;
    
    private String proxyHost;
    private int proxyPort;

    public HttpInboundServer(int port, String proxyHost, int proxyPort) {
        this.port=port;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    public void run() throws Exception {
        /**
         * NioEventLoopGroup is a multithreaded event loop that handles I/O operation. Netty provides various
         * EventLoopGroup implementations for different kind of transports. We are implementing a server-side application
         * in this example, and therefore two NioEventLoopGroup will be used. The first one, often called 'boss', accepts
         * an incoming connection. The second one, often called 'worker', handles the traffic of the accepted connection
         * once the boss accepts the connection and registers the accepted connection to the worker. How many Threads are
         * used and how they are mapped to the created Channels depends on the EventLoopGroup implementation and may be
         * even configurable via a constructor.
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);

        try {
            /**
             * ServerBootstrap is a helper class that sets up a server. You can set up the server using a Channel
             * directly. However, please note that this is a tedious process, and you do not need to do that in most cases.
             */
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .option(EpollChannelOption.SO_REUSEPORT, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            b.group(bossGroup, workerGroup)
                    /**
                     * we specify to use the NioServerSocketChannel class which is used to instantiate a new Channel to
                     * accept incoming connections.
                     */
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    /**
                     * The handler specified here will always be evaluated by a newly accepted Channel.
                     * The ChannelInitializer is a special handler that is purposed to help a user configure a new Channel.
                     * It is most likely that you want to configure the ChannelPipeline of the new Channel by adding some
                     * handlers such as DiscardServerHandler to implement your network application. As the application gets
                     * complicated, it is likely that you will add more handlers to the pipeline and extract this anonymous
                     * class into a top-level class eventually.
                     */
                    .childHandler(new HttpInboundInitializer(this.proxyHost, this.proxyPort, new HttpRequestFilterImpl()));

            Channel ch = b.bind(port).sync().channel();
            logger.info("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
