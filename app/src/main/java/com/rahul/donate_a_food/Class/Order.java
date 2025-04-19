package com.rahul.donate_a_food.Class;

public class Order {
    private String orderId;
    private String donorId;
    private String receiverId;
    private String productId;
    private String donorName;
    private String donorNumber;
    private String productName;
    private int foodQuantity;
    private String location;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String status;

    public Order() {
    }

    public Order(String orderId, String donorId, String receiverId, String productId, String donorName, String donorNumber,
                 String productName, int foodQuantity, String location, double latitude, double longitude, String imageUrl, String status) {
        this.orderId = orderId;
        this.donorId = donorId;
        this.receiverId = receiverId;
        this.productId = productId;
        this.donorName = donorName;
        this.donorNumber = donorNumber;
        this.productName = productName;
        this.foodQuantity = foodQuantity;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDonorId() {
        return donorId;
    }

    public String getReceiverId() {
        return receiverId;
    }
    public String getDonorName() {return donorName;}
    public String getDonorNumber() {return donorNumber;}

    public String getProductName() {
        return productName;
    }

    public int getFoodQuantity() {
        return foodQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }
    public String getProductId() {return productId;}
    public String getLocation() {return location;}

    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}


}
