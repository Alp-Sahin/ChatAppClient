package org.example.app;

import org.example.service.RequestService;
import org.example.service.ThreadFactory;
import org.example.service.WriterHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class AppRun {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;
    private String password;
    private RequestService requestService;
    private boolean isClientRunning = false;
    private String authToken=null;
    private Socket socket;
    private static AppRun apprun;
    private AppRun(){}
    public static AppRun getInstance(){
        if(apprun == null){
            apprun = new AppRun();
        }
        return apprun;
    }
    public void run(){
        try {
            socket = new Socket("localhost",21521);
            requestService= RequestService.getInstance();
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            isClientRunning = true;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't connect to the server");
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Connected to the server: " + socket.getInetAddress());
        System.out.println("1. Login\n2. Register\n3. Exit");
        int option = scanner.nextInt();
        scanner.nextLine();
        ThreadFactory threadFactory=ThreadFactory.getInstance();
        switch(option){
            case 1:
                System.out.println("Please enter your username: ");
                username = scanner.nextLine();
                System.out.println("Please enter your password: ");
                password = scanner.nextLine();
                requestService.createLoginRequest(username,password,out,in);
                threadFactory.getThreadPool().execute(()->WriterHandler.getInstance().write(socket,out, in));
                System.out.println("Commands: '-history' , '-logout' , '-exit'");
                requestService.checkForMessages(in);
                break;
            case 2:
                System.out.println("Please enter your username: ");
                username = scanner.nextLine();
                System.out.println("Please enter your password: ");
                password = scanner.nextLine();
                requestService.createRegisterRequest(username,password,out,in);
                threadFactory.getThreadPool().execute(()->WriterHandler.getInstance().write(socket,out,in));
                System.out.println("Commands: '-history' , '-logout' , '-exit'");
                requestService.checkForMessages(in);
                break;
            case 3:
                terminate();
                System.out.println("Exiting...");
                break;
        }
    }
    public boolean isClientRunning(){
        return isClientRunning;
    }
    public void terminate(){
        isClientRunning = false;
    }
    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }
    public String getAuthToken(){
        return authToken;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public boolean isAuthenticated(){
        return authToken != null;
    }
}
