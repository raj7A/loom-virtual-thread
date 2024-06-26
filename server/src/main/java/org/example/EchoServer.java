package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class EchoServer {

    public static void main(String[] args) throws IOException {
        System.out.println("CPU count : " + Runtime.getRuntime().availableProcessors());
        virtualThread(args);
        //executorThread(args);
    }

    private static void executorThread(String[] args) throws IOException {
        System.out.println("Flow::TP");
        ExecutorService executor = Executors.newFixedThreadPool(5000);
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        while (true) {
            Socket clientSocket = serverSocket.accept();
            executor.submit(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    sleep(Duration.ofSeconds(1));
                    out.println("TP :: For input " + in.readLine() + " ,Thread is " + Thread.currentThread().threadId());
                } catch (IOException | InterruptedException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void virtualThread(String[] args) throws IOException {
        System.out.println("Flow::VT");
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread.ofVirtual().start(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    sleep(Duration.ofSeconds(1));
                    out.println("VT :: For input " + in.readLine() + " ,Thread is " + Thread.currentThread().threadId());
                } catch (IOException | InterruptedException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            });
        }
    }
}