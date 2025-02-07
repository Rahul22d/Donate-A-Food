package com.rahul.donate_a_food;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {
    private final MutableLiveData<Double> latitude = new MutableLiveData<>();
    private final MutableLiveData<Double> longitude = new MutableLiveData<>();

    public void setLocationCoor(double lat, double lon) {
        latitude.setValue(lat);
        longitude.setValue(lon);
    }

    public LiveData<Double> getLatitude() {
        return latitude;
    }

    public LiveData<Double> getLongitude() {
        return longitude;
    }

    private final MutableLiveData<Double[]> location = new MutableLiveData<>();

    public LiveData<Double[]> getLocation() {
        return location;
    }

    public void setLocation(double lat, double lng) {
        location.setValue(new Double[]{lat, lng});
    }
}
