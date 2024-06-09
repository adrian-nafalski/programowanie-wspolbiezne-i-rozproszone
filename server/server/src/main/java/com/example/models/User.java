package com.example.models;

import com.example.enums.GRANTS;

import java.io.Serializable;

public class User implements Serializable {
    private Long id;
    private String login;
    private String password;
    private GRANTS grants;

    public User(Long id, String login, String password, GRANTS grants) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.grants = grants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GRANTS getGrants() {
        return grants;
    }

    public void setGrants(GRANTS grants) {
        this.grants = grants;
    }
}