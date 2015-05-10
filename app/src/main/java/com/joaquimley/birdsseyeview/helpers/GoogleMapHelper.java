/*
 * GNU GENERAL PUBLIC LICENSE
 *                 Version 3, 29 June 2007
 *
 *     Copyright (c) 2015 Joaquim Ley <me@joaquimley.com>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.joaquimley.birdsseyeview.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Handles all Google Service's API requests & Map customization
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
     * Creates a marker (MarkerOptions)
     *
     * @param title                title of the marker (shown when user clicks)
     * @param markerTarget         self explanatory
     * @param isFlat               self explanatory
     * @param iconBitmapDescriptor icon of the marker
     * @return markerOptions object with given given specs
     */
    public static MarkerOptions createMarker(String title, LatLng markerTarget, boolean isFlat,
                                             BitmapDescriptor iconBitmapDescriptor) {

        return new MarkerOptions().position(markerTarget)
                .flat(isFlat)
                .icon(iconBitmapDescriptor)
                .title(title);
    }

    /**
     * Overflow method without title
     *
     * @param markerTarget         self explanatory
     * @param isFlat               self explanatory
     * @param iconBitmapDescriptor icon of the marker
     * @return markerOptions object with given given specs
     */
    public static MarkerOptions createMarker(LatLng markerTarget, boolean isFlat,
                                             BitmapDescriptor iconBitmapDescriptor) {

        return new MarkerOptions().position(markerTarget)
                .flat(isFlat)
                .icon(iconBitmapDescriptor);
    }

    /**
     * Creates a Polyline in with styling options
     *
     * @param route the polyline is going to follow/drawn
     * @param color of the polyline
     * @param width of the polyline
     * @return polylineOptions object
     */
    public static PolylineOptions createPolyline(LatLng[] route, int color, float width) {
        return new PolylineOptions()
                .add(route)
                .color(color)
                .width(width);
    }

    /**
     * Create a route object with LatLng positions given by param
     *
     * @param rotePositions undef. number of positions/markers
     * @return the complete route
     */
    public static LatLng[] createMapRoute(LatLng... rotePositions) {
        return rotePositions;
    }

    /**
     * Adds the @param position to the end of the @param route
     *
     * @param route       self explanatory
     * @param newPosition self explanatory
     */
    public static void addPositionToRoute(LatLng[] route, LatLng newPosition) {
        route[route.length + 1] = newPosition;
    }
}
