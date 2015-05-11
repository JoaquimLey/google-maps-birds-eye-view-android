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

package com.joaquimley.birdsseyeview.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.joaquimley.birdsseyeview.R;
import com.joaquimley.birdsseyeview.helpers.GoogleMapAnimationHelper;
import com.joaquimley.birdsseyeview.helpers.GoogleMapHelper;
import com.joaquimley.birdsseyeview.utils.TestValues;

/**
 * Launcher activity with Google Map fragment
 */

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapLoadedCallback,
        Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private Marker mMarker;
    private AnimatorSet mAnimatorSet;
    private Menu mMenu;
    private LatLng[] mRouteExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        if (GoogleMapHelper.googleServicesAvailability(this) == null) {
            setUpMapIfNeeded();
        }
    }

    /**
     * Initialize UI elements, listeners etc.
     */
    private void init() {
        mRouteExample = GoogleMapHelper.createMapRoute(new LatLng(37.783986, -122.408059),
                new LatLng(37.785716, -122.40587), new LatLng(37.785731, -122.406267), new LatLng(37.799446, -122.408989));
    }

    /**
     * Creates a map instance if there isn't one already created
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap(false, false, false);
            }
        }
    }

    /**
     * Creation and customization of the map
     *
     * @param isIndoorEnabled      self explanatory
     * @param isAllGesturesEnabled self explanatory
     * @param isZoomControlEnabled self explanatory
     */
    private void setUpMap(boolean isIndoorEnabled, boolean isAllGesturesEnabled, boolean isZoomControlEnabled) {
        mMap.setIndoorEnabled(isIndoorEnabled);

        // Disable gestures & controls since ideal results (pause Animator) is
        // not easy to show in a simplified example.
        mMap.getUiSettings().setAllGesturesEnabled(isAllGesturesEnabled);
        mMap.getUiSettings().setZoomControlsEnabled(isZoomControlEnabled);

        // Create a marker to represent the user on the route.
        mMarker = mMap.addMarker(GoogleMapHelper.createMarker(mRouteExample[0], false,
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

//        mMap.addMarker(GoogleMapHelper.createMarker(mRouteExample[0], false,
//                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Create a polyline for the route.
        mMap.addPolyline(GoogleMapHelper.createPolyline(mRouteExample, TestValues.POLYLINE_FINAL_COLOR,
                TestValues.POLYLINE_WIDTH));

        // Once the map is ready, zoom to the beginning of the route start the
        // animation.
        mMap.setOnMapLoadedCallback(this);

        // Move the camera over the start position.
        CameraPosition pos = GoogleMapAnimationHelper.createCameraPosition(mRouteExample,
                TestValues.CAMERA_OBLIQUE_ZOOM);

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        mMap.setOnMapClickListener(this);
    }

    /**
     * When the map has finished loading all it's components (listener), calls the
     * GoogleMapsAnimationHelper.createRouteAnimatorSet() and starts animation (via callAnimateRoute()) method
     */
    @Override
    public void onMapLoaded() {
        // Once the camera has moved to the beginning of the route,
        // start the animation.
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                mMap.setOnCameraChangeListener(null);
                callAnimateRoute();
            }
        });

        // Animate the camera to the beginning of the route.
        CameraPosition pos = GoogleMapAnimationHelper.createCameraPosition(mRouteExample,
                TestValues.CAMERA_OBLIQUE_ZOOM, TestValues.CAMERA_OBLIQUE_TILT);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    /**
     * Calls the createRouteAnimatorSet, here to use the MapsActivity.this as the listener(s)
     * starts the animation.
     */
    private void callAnimateRoute() {

        mAnimatorSet = GoogleMapAnimationHelper.createRouteAnimatorSet(mRouteExample, mMap,
                TestValues.CAMERA_HEADING_CHANGE_RATE, mMarker, this, this, 0, 0);
        mAnimatorSet.start();
    }

    /**
     * Google Map animation listener mAnimatorSet
     */
    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        mMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(CameraPosition.builder(mMap.getCameraPosition())
                        .bearing((Float) valueAnimator.getAnimatedValue())
                        .build()));
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        Toast.makeText(getApplicationContext(), "Animation Cancel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        Toast.makeText(getApplicationContext(), "Welcome to the end", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        Toast.makeText(getApplicationContext(), "Animation Repeat", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAnimationStart(Animator animation) {
        Toast.makeText(getApplicationContext(), "Animation Start", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        mMenu = menu; // Keep the menu for later use (swapping icons).
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMap == null) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.action_marker:
                mMarker.setVisible(!mMarker.isVisible());
                return true;

            case R.id.action_buildings:
                mMap.setBuildingsEnabled(!mMap.isBuildingsEnabled());
                return true;

            case R.id.action_animation:
                if (mAnimatorSet.isRunning()) {
                    mAnimatorSet.cancel();
                } else {
                    mAnimatorSet.start();
                }
                return true;

            case R.id.action_perspective:

                CameraPosition currentPosition = mMap.getCameraPosition();
                CameraPosition newPosition;
                if (currentPosition.zoom == TestValues.CAMERA_OBLIQUE_ZOOM
                        && currentPosition.tilt == TestValues.CAMERA_OBLIQUE_TILT) {
                    newPosition = new CameraPosition.Builder()
                            .tilt(GoogleMapAnimationHelper.getMaximumTilt(19))
                            .zoom(19)
                            .bearing(currentPosition.bearing)
                            .target(currentPosition.target).build();
                } else {
                    newPosition = new CameraPosition.Builder()
                            .tilt(TestValues.CAMERA_OBLIQUE_TILT)
                            .zoom(TestValues.CAMERA_OBLIQUE_ZOOM)
                            .bearing(currentPosition.bearing)
                            .target(currentPosition.target).build();
                }
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(newPosition));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        GoogleMapAnimationHelper.animateLiftOff(mMap, 2);
    }
}