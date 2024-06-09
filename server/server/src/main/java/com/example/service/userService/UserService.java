package com.example.service.userService;

import com.example.models.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserService extends Remote {
    boolean registerUser(User user) throws RemoteException;
    User loginUser(String login, String password) throws RemoteException;
}
