package com.rahul.donate_a_food;

public interface LocationPass {
    double latitude = 0, longitude = 0;
    void setLocation(double lat, double log);
//    void setLon(double lon);
    double getLat();
    double getLon();

}
