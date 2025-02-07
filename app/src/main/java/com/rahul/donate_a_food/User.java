package com.rahul.donate_a_food;

public class User {
    private String name;
    private String username;
    private String email;
    private String number;
    private double latitude, longitude;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String username, String email, String number, double latitude, double longitude) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
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
    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}

}

// for location save
class UserLocation{
    private double latitude;
    private double longitude;

    UserLocation(){}

    UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}

}

