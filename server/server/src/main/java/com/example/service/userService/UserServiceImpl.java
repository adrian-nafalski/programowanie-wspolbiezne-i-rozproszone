package com.example.service.userService;

import com.example.db.DatabaseManager;
import com.example.models.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    public UserServiceImpl() throws RemoteException {}

    @Override
    public boolean registerUser(User user) throws RemoteException {
        return DatabaseManager.registerUser(user);
    }

    @Override
    public User loginUser(String login, String password) throws RemoteException {
        return DatabaseManager.loginUser(login, password);
    }
}
