package io.github.kimmking.gateway.helper;

import com.sun.javafx.binding.StringFormatter;

public class BackendInfo {
    private final String host;
    private final int port;

    public BackendInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String transferToUrl() {
        return String.format("http://%s:%d", host, port);
    }
}
