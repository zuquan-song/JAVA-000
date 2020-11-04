package io.github.kimmking.gateway.inbound;

import io.github.kimmking.gateway.filter.HttpRequestFilter;
import io.github.kimmking.gateway.filter.HttpRequestFilterNoOperation;
import io.github.kimmking.gateway.outbound.httpclient4.HttpOutboundHandler;
import io.github.kimmking.gateway.outbound.okhttp.OkhttpOutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.kimmking.gateway.helper.BackendInfo;

import java.util.ArrayList;
import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private final OkhttpOutboundHandler handler;
    private HttpRequestFilter filter;
    
    public HttpInboundHandler(String proxyHost, int proxyPort) {
        ArrayList<BackendInfo> backendServers = new ArrayList<>();
        backendServers.add(new BackendInfo(proxyHost, proxyPort));
        handler = new OkhttpOutboundHandler(backendServers);
    }

    public HttpInboundHandler(String proxyHost, int proxyPort, HttpRequestFilter filter) {
        this(proxyHost, proxyPort);
        this.filter = filter;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest fullRequest = (FullHttpRequest) msg;

            if (this.filter != null) {
                this.filter.filter(fullRequest, ctx);
                logger.info(fullRequest.toString());
            }
            this.handler.handle(fullRequest, ctx);
    
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}
