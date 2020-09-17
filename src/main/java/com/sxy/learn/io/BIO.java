package com.sxy.learn.io;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author sxy
 * @version 1.0
 * @className BIO
 * @date 2020/9/9 9:46
 */

public class BIO {

    private static final Logger log = Logger.getLogger("BIO");

    public static void main(String[] args) {
        log.setLevel(Level.INFO);
        try {
            ServerSocket serverSocket = new ServerSocket(9090, 10);
            ExecutorService singleThreadPool = Executors.newFixedThreadPool(10);
            singleThreadPool.execute(() -> {
                Socket write = null;
                try {
                    log.info("enter");
                    write = new Socket("localhost",9090);
                    PrintWriter pw = new PrintWriter(write.getOutputStream());
                    int i=1;
                    while(true){
                        pw.write("now is "+i++ + "\n");
                        pw.flush();
                        Thread.sleep(1000);
                    }
                } catch (IOException | InterruptedException e) {
                    log.warning("error: "+ e.toString());
                } finally {
                    if (write != null) {
                        try {
                            write.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            while (true) {
                Socket socket = serverSocket.accept();
                singleThreadPool.execute(()->{
                    try {
                        InputStream is = socket.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        while (true) {
                            String line = br.readLine();
                            if (line!=null && !line.equals("")) {
                                log.info("print: " + line);
                            } else {
                                log.info("break");
                                socket.close();
                                break;
                            }
                        }
                    } catch (IOException e) {
                        log.warning("error: " + e.toString());
                    } finally {
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
