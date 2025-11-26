package com.grantserver.model;

public class Participant {
    public Long id;
    public String firmName;
    public Manager manager;
    public String login;
    public String password; // Храним в БД, но не отдаем в API

    public Participant() {}
}