package com.rahul.donate_a_food;

public class User {
    private String name;
    private String username;
    private String email;
    private String number;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String username, String email, String number) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.number = number;
    }

    // Getters and setters (if needed)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumber(String number) {this.number = number;}

    public String getNumber() {return number;}
}

