package com.rahul.donate_a_food.Class;

public class OrderBooked {
    private String donorId;
    private String donorName;
    private String foodName;
    private int foodQuantity;
    private String location;
    private String imageUrl;
    private String number;
    private String receiverId;
    private String status;

    public OrderBooked() {
        // Empty constructor required for Firebase
    }

    public OrderBooked(String donorId, String donorName, String foodName, int foodQuantity, String location, String imageUrl, String number, String receiverId, String status) {
        this.donorId = donorId;
        this.donorName = donorName;
        this.foodName = foodName;
        this.foodQuantity = foodQuantity;
        this.location = location;
        this.imageUrl = imageUrl;
        this.number = number;
        this.receiverId = receiverId;
        this.status = status;
    }

    public String getDonorId() { return donorId; }
    public String getDonorName() { return donorName; }
    public String getFoodName() { return foodName; }
    public int getFoodQuantity() { return foodQuantity; }
    public String getImageUrl() { return imageUrl; }
    public String getNumber() { return number; }
    public String getReceiverId() { return receiverId; }
    public String getStatus() { return status; }
    public String getLocation() {return location;}
}
