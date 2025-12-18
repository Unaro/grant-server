package com.grantserver.model;

import java.util.ArrayList;
import java.util.List;

public class Participant extends User {
    public String firmName;
    public Manager manager;

    private final List<GrantApplication> applications = new ArrayList<>();

    public Participant() {}

    public Participant(String login, String password, String firmName, Manager manager) {
        super(login, password);
        this.firmName = firmName;
        this.manager = manager;
    }

    public void addApplication(GrantApplication application) {
        this.applications.add(application);
    }

    public void removeApplication(Long applicationId) {
        this.applications.removeIf(a -> a.id.equals(applicationId));
    }

    public List<GrantApplication> getApplications() {
        return applications;
    }
}