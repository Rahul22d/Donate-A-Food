package com.rahul.donate_a_food.Class;

public class OrderBooked {
    private String donorId;
    private String donorName;
    private String foodName;
    private int foodQuantity;
    private String location;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String donorNumber;
    private String receiverId;
    private String status;

    public OrderBooked() {
        // Empty constructor required for Firebase
    }

    public OrderBooked(String donorId, String donorName, String foodName, int foodQuantity, String location, double latitude, double longitude, String imageUrl, String donorNumber, String receiverId, String status) {
        this.donorId = donorId;
        this.donorName = donorName;
        this.foodName = foodName;
        this.foodQuantity = foodQuantity;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.donorNumber = donorNumber;
        this.receiverId = receiverId;
        this.status = status;
    }

    public String getDonorId() { return donorId; }
    public String getDonorName() { return donorName; }
    public String getFoodName() { return foodName; }
    public int getFoodQuantity() { return foodQuantity; }
    public String getImageUrl() { return imageUrl; }
    public String getDonorNumber() { return donorNumber; }
    public String getReceiverId() { return receiverId; }
    public String getStatus() { return status; }
    public String getLocation() {return location;}
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
