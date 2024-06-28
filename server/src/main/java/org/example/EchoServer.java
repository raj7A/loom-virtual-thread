package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class EchoServer {

    static int sleepTimeInSecs = 1;

    public static void main(String[] args) throws IOException {
        System.out.println("Socket server up with CPU count : " + Runtime.getRuntime().availableProcessors());
        if (Objects.equals(args[1], "TP")) {
            threadPoolFlow(args);
        } else if (Objects.equals(args[1], "VT")) {
            virtualThreadFlow(args);
        }
    }

    private static void threadPoolFlow(String[] args) throws IOException {
        //System.out.println("Flow::ThreadPool");
//        ExecutorService executor = Executors.newFixedThreadPool(5000);
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        while (true) {
            Socket clientSocket = serverSocket.accept();
            executor.submit(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    sleep(Duration.ofSeconds(sleepTimeInSecs));
                    out.println("ThreadPool :: For input " + in.readLine() + " ,Thread is " + Thread.currentThread().threadId());
                } catch (IOException | InterruptedException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void virtualThreadFlow(String[] args) throws IOException {
        //System.out.println("Flow::VirtualThread");
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread.ofVirtual().start(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    sleep(Duration.ofSeconds(sleepTimeInSecs));
                    out.println("VirtualThread :: For input " + in.readLine() + " ,Thread is " + Thread.currentThread().threadId());
                } catch (IOException | InterruptedException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            });
        }
    }
}