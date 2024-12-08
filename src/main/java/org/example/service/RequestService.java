package org.example.service;

import org.example.app.AppRun;
import org.example.dto.Login;
import org.example.dto.Message;
import org.example.dto.Register;
import org.example.dto.RequestDTO;
import org.example.enums.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

public class RequestService {

    private static RequestService requestService;
    private AppRun appRun = AppRun.getInstance();
    private Socket socket;
    private RequestService(){}
    public static RequestService getInstance(){
        if (requestService == null) {
            requestService = new RequestService();
        }
        return requestService;
    }
    public void createLoginRequest(String username, String password, ObjectOutputStream out, ObjectInputStream in) {
        Login login = new Login(username, password);
        RequestDTO requestDTO = new RequestDTO(login);
        try {
            out.writeObject(requestDTO);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create login request: " + e.getMessage());
        }
        try {
            checkForResponse((RequestDTO)in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void createRegisterRequest(String username, String password, ObjectOutputStream out, ObjectInputStream in) {
        Register register = new Register(username, password);
        RequestDTO requestDTO = new RequestDTO(register);
        try {
            out.writeObject(requestDTO);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create register request :"+e.getMessage());
        }
        try {
            checkForResponse((RequestDTO)in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to get register response: "+e.getMessage());
        }
    }
    public void createMessageRequest(String username, String msg, ObjectOutputStream out) {
        Message message = new Message(username,msg, LocalDateTime.now());
        RequestDTO requestDTO = new RequestDTO(message);
        requestDTO.setUsername(username);
        requestDTO.setAuthToken(appRun.getAuthToken());
        try {
            out.writeObject(requestDTO);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create message request: "+e.getMessage());
        }

    }
    public void createLogoutRequest(ObjectOutputStream out) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRequestType(RequestType.LOGOUT);
        requestDTO.setUsername(appRun.getUsername());
        requestDTO.setAuthToken(appRun.getAuthToken());
        try {
            out.writeObject(requestDTO);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create logout request: "+e.getMessage());
        }
    }
    public void createHistoryRequest(ObjectOutputStream out) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRequestType(RequestType.HISTORY);
        requestDTO.setUsername(appRun.getUsername());
        requestDTO.setAuthToken(appRun.getAuthToken());
        try {
            out.writeObject(requestDTO);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create history request: "+e.getMessage());
        }

    }
    public void checkForResponse(RequestDTO requestDTO){
        if(requestDTO.getRequestType()==null) {System.out.println("Authentication failed.");}
        else {
            switch (requestDTO.getRequestType()) {
                case REGISTER, LOGIN -> {
                    if (requestDTO.getAuthToken() != null) {
                        appRun.setAuthToken(requestDTO.getAuthToken());
                        System.out.println("Authentication successful.");
                    }
                }
            }
        }
    }
    public void checkForMessages(ObjectInputStream in){
        while(appRun.isClientRunning()){
            while(appRun.isClientRunning() && !appRun.isAuthenticated()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Sleep failed : "+e.getMessage());
                }
            }
            try {
                RequestDTO requestDTO = (RequestDTO) in.readObject();
                if(requestDTO.getRequestType()==RequestType.MESSAGE) {
                    Message message = requestDTO.getMessage();
                    System.out.println(message.username() + ": " + message.message() + " || " + message.date());
                }
                else if (requestDTO.getRequestType()==RequestType.HISTORY) {
                    System.out.println(requestDTO.getHistory());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Logging out...");
                appRun.setAuthToken(null);
                appRun.terminate();
            }
        }

    }
}
