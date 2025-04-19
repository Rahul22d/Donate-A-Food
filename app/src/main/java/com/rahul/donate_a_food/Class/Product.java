package com.rahul.donate_a_food.Class;

// Product class to represent product data
public class Product {
    private String fullName;
    private String ownerName;
    private int foodQuantity;
    private String foodDescription;
    private String imageUrl;
    private String contactNumber;
    private String location;
    private double latitude;
    private double longitude;
    public float distance;
    private String foodType;
    private String donorId;
    private String productId;

    public Product(String fullName, String ownerName, int foodQuantity, String foodDescription, String location, double latitude, double longitude, float distance, String imageUrl, String contactNumber, String foodType, String donorId, String productId) {
        this.fullName = fullName;
        this.ownerName = ownerName;
        this.foodQuantity = foodQuantity;
        this.foodDescription = foodDescription;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.contactNumber = contactNumber;
        this.distance = distance;
        this.foodType = foodType;
        this.donorId = donorId;
        this.productId = productId;

    }

    public String getFullName() {
        return fullName;
    }

    public int getFoodQuantity() {
        return foodQuantity;
    }
    public  float getDistance(){ return distance;}
    public String getFoodDescription() { return foodDescription; }

    public String getLocation() { return location; }
    public void setLongitude(double longitude) {this.longitude = longitude;}
    public double getLongitude() {return longitude;}

    public void setLatitude(double latitude) {this.latitude = latitude;}
    public double getLatitude() {return latitude;}
    public String getImageUrl() {
        return imageUrl;
    }

    public String getContactNumber() {
        return contactNumber;
    }
    public String getOwnerName() {return ownerName;}
    public String getFoodType() {return foodType;}

    public String getDonorId() {
        return donorId;
    }
    public String getProductId() {return productId;}
}
