package com.joaquimley.birdsseyeview.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Handles all Google Service's API events / requests
 */

public class GoogleMapHelper {

    private static final String TAG = GoogleMapHelper.class.getSimpleName();

    /**
     * Ensures the device has required GoogleServices installed, returns error dialog if
     * device is not GoogleServices enabled, null otherwise.
     *
     * @param activity current activity
     * @return error dialog, null if service IS present
     */
    public static Dialog googleServicesAvailability(final Activity activity) {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode == ConnectionResult.SUCCESS) {
            Log.i(TAG, "User has GooglePlayServices");
            return null;
        }

        return GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE);
    }

    /**
     * Creates a marker on the map given by parameter and zoom into it.
     *
     * @param map                  self explanatory
     * @param title                self explanatory
     * @param iconBitmapDescriptor marker icon
     */
    public static void addMarker(GoogleMap map, String title, LatLng markerPosition, boolean isFlat,
                                 BitmapDescriptor iconBitmapDescriptor) {
        map.addMarker(new MarkerOptions().position(markerPosition)
                .flat(isFlat)
                .icon(iconBitmapDescriptor)
                .title(title));
    }

    /**
     * Creates a Polyline in the with styling options
     *
     * @param map   on which the polyline will be created
     * @param route the polyline is going to follow
     * @param color of the polyline
     * @param width of the polyline
     */
    public static void createPolyline(GoogleMap map, LatLng[] route, int color, float width) {
        map.addPolyline(new PolylineOptions()
                .add(route)
                .color(color)
                .width(width));
    }

    /**
     * Create a route object with LatLng positions given by param
     *
     * @param rotePositions undef number of positions/markers
     * @return the complete route
     */
    public static LatLng[] createMapRoute(LatLng... rotePositions) {
//        LatLng[] route = new LatLng[rotePositions.length];
//
//        for (int i = 0; i < rotePositions.length; i++) {
//            route[i] = rotePositions[i];
//        }
        return rotePositions;
    }
}
