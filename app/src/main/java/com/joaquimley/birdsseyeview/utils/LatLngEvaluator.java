package com.joaquimley.birdsseyeview.utils;

import android.animation.TypeEvaluator;

import com.google.android.gms.maps.model.LatLng;

public class LatLngEvaluator implements TypeEvaluator<LatLng> {

    double mLatitude;
    double mLongitude;

    public LatLngEvaluator(LatLng startValue, LatLng endValue) {
        mLatitude = endValue.latitude - startValue.latitude;
        mLongitude = endValue.longitude - startValue.longitude;
    }

    @Override
    public LatLng evaluate(float fraction, LatLng startValue, com.google.android.gms.maps.model.LatLng
            endValue) {
        double lat = mLatitude * fraction + startValue.latitude;
        double lng = mLongitude * fraction + startValue.longitude;
        return new LatLng(lat, lng);
    }
}
