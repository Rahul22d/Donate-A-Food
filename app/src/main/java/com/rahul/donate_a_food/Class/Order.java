package com.rahul.donate_a_food.Class;

public class Order {
    private String orderId;
    private String donorId;
    private String receiverId;
    private String productId;
    private String productName;
    private int foodQuantity;
    private String location;
    private String imageUrl;
    private String status;

    public Order() {
    }

    public Order(String orderId, String donorId, String receiverId, String productId, String productName, int foodQuantity, String location, String imageUrl, String status) {
        this.orderId = orderId;
        this.donorId = donorId;
        this.receiverId = receiverId;
        this.productId = productId;
        this.productName = productName;
        this.foodQuantity = foodQuantity;
        this.location = location;
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
}
