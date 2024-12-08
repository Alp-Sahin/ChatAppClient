package org.example.dto;

import org.example.enums.RequestType;

import java.io.Serializable;

public class RequestDTO implements Serializable {
    private String history;

    private String username;

    private String authToken;

    private RequestType requestType;

    private Login login;

    private Register register;

    private Message message;

    public RequestDTO(Login login){
        requestType = RequestType.LOGIN;
        this.login = login;
    }
    public RequestDTO(Register register){
        requestType = RequestType.REGISTER;
        this.register = register;
    }
    public RequestDTO(Message message){
        requestType = RequestType.MESSAGE;
        this.message = message;
    }
    public RequestDTO(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public Login getLogin() {
        return login;
    }
    public Register getRegister() {
        return register;
    }
    public Message getMessage() {
        return message;
    }
    public RequestType getRequestType() {
        return requestType;
    }
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
    public String getHistory(){
        return history;
    }
    public void setHistory(String history){
        this.history = history;
    }
}
