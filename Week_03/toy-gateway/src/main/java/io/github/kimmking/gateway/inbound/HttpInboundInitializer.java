package io.github.kimmking.gateway.inbound;

import io.github.kimmking.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {
	
	private String proxyHost;
	private int proxyPort;
	private HttpRequestFilter filter;
	public HttpInboundInitializer(String proxyHost, int proxyPort, HttpRequestFilter filter) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.filter = filter;
	}
	
	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
//		if (sslCtx != null) {
//			p.addLast(sslCtx.newHandler(ch.alloc()));
//		}
		p.addLast(new HttpServerCodec());
		//p.addLast(new HttpServerExpectContinueHandler());
		p.addLast(new HttpObjectAggregator(1024 * 1024));
		p.addLast(new HttpInboundHandler(this.proxyHost, this.proxyPort, this.filter));
	}
}
