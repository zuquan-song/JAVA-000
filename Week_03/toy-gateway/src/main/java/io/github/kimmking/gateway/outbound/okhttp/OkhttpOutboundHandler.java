package io.github.kimmking.gateway.outbound.okhttp;

import io.github.kimmking.gateway.helper.BackendInfo;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class OkhttpOutboundHandler {
    private final List<String> backendUrls;
    private final ExecutorService proxyService;
    private final CloseableHttpAsyncClient client;

    public OkhttpOutboundHandler(ArrayList<BackendInfo> backendUrls) {
        this.backendUrls = backendUrls.stream().map((BackendInfo::transferToUrl)).collect(Collectors.toList());
        final int cores = Runtime.getRuntime().availableProcessors() * 2;
        final long keepAliveTime = 1000;
        final int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        this.proxyService = new ThreadPoolExecutor(cores, cores, keepAliveTime, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize), handler);

        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();

        this.client = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(40)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((Response, Context) -> 6000).build();
        this.client.start();
    }

    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
        Random random = new Random();
        int idx = random.nextInt() % this.backendUrls.size();
        final String url = backendUrls.get(idx) + fullRequest.uri();

        this.proxyService.submit(() -> fetchGet(fullRequest, ctx, url));
    }

    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        Iterator<Map.Entry<String, String>> iter = inbound.headers().iteratorAsString();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            httpGet.setHeader(entry.getKey(), entry.getValue());
        }

        this.client.execute(httpGet, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse httpResponse) {
                try {
                    handleResponse(inbound, ctx, httpResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            public void failed(Exception e) {
                httpGet.abort();
                e.printStackTrace();
            }

            @Override
            public void cancelled() {
                httpGet.abort();
            }
        });
    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx,
                                final HttpResponse endpointResponse) throws Exception {
        FullHttpResponse response = null;
        try {
            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            response.headers().set("Content-Type", "application/json");
            response.headers().set("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));
        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}