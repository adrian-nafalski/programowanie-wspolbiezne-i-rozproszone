package com.example.client;

import com.example.models.User;
import com.example.service.userService.UserService;

import java.rmi.Naming;

public class UserServiceClient {
    private UserService userService;

    public UserServiceClient() {
        try {
            userService = (UserService) Naming.lookup("//localhost/UserService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(User user) {
        try {
            return userService.registerUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User loginUser(String login, String password) {
        try {
            return userService.loginUser(login, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
