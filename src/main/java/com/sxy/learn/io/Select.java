package com.sxy.learn.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author sxy
 * @version 1.0
 * @className Select
 * @date 2020/9/11 9:39
 */

public class Select {

    private ServerSocketChannel server;
    private Selector selector;
    private static final int port = 9090;

    public void init(){
        try {
            //server 约等于 listen状态的 fd4
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));

            //在 epoll 中等价于 epoll_create -> fd3
            selector = Selector.open();

            /*
               select poll 中 jvm开辟一个数组放fd4进去
               epoll 等价于 epoll_ctl(fd3,ADD,fd4,EPOLLIN)
             */
            server.register(selector, SelectionKey.OP_ACCEPT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void start(){
        init();
        try{
            while (true){
                //System.out.println("keys: "+selector.keys().size());
                /*
                select() 在 select、poll中相当于select(fd4) poll(fd4)
                         在 epoll中相当于 epoll_wait(fd4)
                 */
                if (selector.select() > 0){
                    /*
                    询问select注册的server或client中
                    有没有interest的事件，返回事件的数量
                     */
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                    while(keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if(key.isAcceptable()){
                            acceptHandle(key);
                        }
                        else if(key.isReadable()){
                            readHandle(key);
                        }
                        /*
                        此处必须移除处理完的请求，移除的是selection中的set
                        否则会导致重复执行。而且
                        最重要的是：新的请求来临后，在set中会被判断为已存在，
                        不会被select出来导致select失效
                         */
                        keyIterator.remove();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void readHandle(SelectionKey key) {
        try{
            SocketChannel client = (SocketChannel)key.channel();
            client.configureBlocking(false);
            ByteBuffer bb = ByteBuffer.allocate(1);
            client.read(bb);
            bb.flip();
            byte[] b = new byte[bb.limit()];
            bb.get(b);
            System.out.println("Read: " + new String(b));

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void acceptHandle(SelectionKey key) {
        try{
            ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
            SocketChannel client = ssc.accept();
            client.configureBlocking(false);

            //标记为对读敏感，可以触发 isReadable()
            client.register(selector, SelectionKey.OP_READ);

            System.out.println("----------------------------------");
            System.out.println("client: "+client.getRemoteAddress());
            System.out.println("----------------------------------");

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Select().start();
    }
}
