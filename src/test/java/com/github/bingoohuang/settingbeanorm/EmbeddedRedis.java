package com.github.bingoohuang.settingbeanorm;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import java.net.ServerSocket;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2017/1/8.
 */
public class EmbeddedRedis {
    @SneakyThrows
    public static int getRandomPort() {
        @Cleanup val socket = new ServerSocket(0);
        return socket.getLocalPort();
    }

    public static final int port = getRandomPort();
}