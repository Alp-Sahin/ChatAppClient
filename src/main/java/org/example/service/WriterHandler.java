package org.example.service;

import org.example.app.AppRun;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class WriterHandler {
    private RequestService requestService;
    private AppRun appRun=AppRun.getInstance();
    Scanner scanner = new Scanner(System.in);
    private static WriterHandler writerHandler;
    public static WriterHandler getInstance() {
        if (writerHandler == null) {
            writerHandler = new WriterHandler();
        }
        return writerHandler;
    }
    public void write(Socket socket, ObjectOutputStream out, ObjectInputStream in){
        requestService = RequestService.getInstance();
        while(AppRun.getInstance().isClientRunning()){
            while(!appRun.isAuthenticated() && appRun.isClientRunning()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String message = scanner.nextLine();
            switch (message) {
                case "-logout" -> {
                    requestService.createLogoutRequest(out);
                    appRun.setAuthToken(null);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    appRun.terminate();
                    appRun.run();
                }
                case "-exit" -> {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Socket refused to close: " + e.getMessage());
                    }
                }
                case "-history" -> requestService.createHistoryRequest(out);
                default -> requestService.createMessageRequest(AppRun.getInstance().getUsername(), message, out);
            }

        }
    }

}
