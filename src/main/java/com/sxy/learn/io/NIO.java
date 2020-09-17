package com.sxy.learn.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author sxy
 * @version 1.0
 * @className NIO
 * @date 2020/9/9 19:12
 */

public class NIO {

    private static final Logger log = Logger.getLogger("NIO");

    public static void main(String[] args) {

        List<SocketChannel> list = new ArrayList<>();

        try {
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.bind(new InetSocketAddress(9090));
            socketChannel.configureBlocking(false);

            while(true){
                SocketChannel socket = socketChannel.accept();
                if(socket!=null){
                    socket.configureBlocking(false);
                    list.add(socket);
                }
                ByteBuffer bb = ByteBuffer.allocate(4096);
                for (SocketChannel sc: list) {
                    int len = sc.read(bb);
                    if(len>0){
                        bb.flip();
                        byte[] byt = new byte[bb.limit()];
                        bb.get(byt);
                        String s = new String(byt);
                        System.out.println(s.substring(0, s.length()-1));
                        bb.clear();
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
