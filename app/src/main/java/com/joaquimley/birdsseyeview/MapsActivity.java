/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.joaquimley.birdsseyeview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.LinkedList;

public class MapsActivity extends FragmentActivity {
    private static final int POLYLINE_HUE = 360; // 0-360
    private static final float POLYLINE_SATURATION = 1; // 0-1
    private static final float POLYLINE_VALUE = 1; // 0-1
    private static final int POLYLINE_ALPHA = 128; // 0-255
    private static final float POLYLINE_WIDTH = 8;

    private static final float CAMERA_ZOOM = 16;
    private static final float CAMERA_OBLIQUE_ZOOM = 18;
    private static final float CAMERA_OBLIQUE_TILT = 60;

    private static final long CAMERA_HEADING_CHANGE_RATE = 5;

    private static final LatLng[] ROUTE = {
            new LatLng(37.783986, -122.408059),
            new LatLng(37.785716, -122.40587),
            new LatLng(37.785731, -122.406267),
            new LatLng(37.799446, -122.408989)
    };

    private GoogleMap mMap;
    private Marker mMarker;
    private AnimatorSet mAnimatorSet;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();

        if (mMap != null) {
            setUpMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                if (currentPosition.zoom == CAMERA_OBLIQUE_ZOOM
                        && currentPosition.tilt == CAMERA_OBLIQUE_TILT) {
                    newPosition = new CameraPosition.Builder()
                            .tilt(0).zoom(CAMERA_ZOOM)
                            .bearing(currentPosition.bearing)
                            .target(currentPosition.target).build();
                } else {
                    newPosition = new CameraPosition.Builder()
                            .tilt(CAMERA_OBLIQUE_TILT)
                            .zoom(CAMERA_OBLIQUE_ZOOM)
                            .bearing(currentPosition.bearing)
                            .target(currentPosition.target).build();
                }
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(newPosition));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpMap() {
        mMap.setIndoorEnabled(false);

        // Disable gestures & controls since ideal results (pause Animator) is
        // not easy to show in a simplified example.
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Create a marker to represent the user on the route.
        mMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(ROUTE[0]));

        // Create a polyline for the route.
        mMap.addPolyline(new PolylineOptions()
                .add(ROUTE)
                .color(Color.HSVToColor(POLYLINE_ALPHA,
                        new float[] {
                                POLYLINE_HUE, POLYLINE_SATURATION, POLYLINE_VALUE
                        }))
                .width(POLYLINE_WIDTH));

        // Once the map is ready, zoom to the beginning of the route start the
        // animation.
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Once the camera has moved to the beginning of the route,
                // start the animation.
                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        mMap.setOnCameraChangeListener(null);
                        animateRoute();
                    }
                });

                // Animate the camera to the beginning of the route.
                float bearing = (float) SphericalUtil.computeHeading(ROUTE[0], ROUTE[1]);

                CameraPosition pos = new CameraPosition.Builder()
                        .target(ROUTE[0])
                        .zoom(CAMERA_OBLIQUE_ZOOM)
                        .tilt(CAMERA_OBLIQUE_TILT)
                        .bearing(bearing)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
            }
        });

        // Move the camera over the start position.
        CameraPosition pos = new CameraPosition.Builder()
                .target(ROUTE[0])
                .zoom(CAMERA_OBLIQUE_ZOOM - 2)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    public void animateRoute() {
        LinkedList<Animator> animators = new LinkedList<Animator>();

        // For each segment of the route, create one heading adjustment animator
        // and one segment fly-over animator.
        for (int i = 0; i < ROUTE.length - 1; i++) {
            // If it the first segment, ensure the camera is rotated properly.
            float h1;
            if (i == 0) {
                h1 = mMap.getCameraPosition().bearing;
            } else {
                h1 = (float) SphericalUtil.computeHeading(ROUTE[i - 1], ROUTE[i]);
            }

            float h2 = (float) SphericalUtil.computeHeading(ROUTE[i], ROUTE[i + 1]);

            ValueAnimator va = ValueAnimator.ofFloat(h1, h2);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float bearing = (Float) animation.getAnimatedValue();
                    CameraPosition pos = CameraPosition.builder(mMap.getCameraPosition())
                            .bearing(bearing)
                            .build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                }
            });

            // Use the change in degrees of the heading for the animation
            // duration.
            long d = Math.round(Math.abs(h1 - h2));
            va.setDuration(d * CAMERA_HEADING_CHANGE_RATE);
            animators.add(va);

            ObjectAnimator oa = ObjectAnimator.ofObject(mMarker, "position",
                    new LatLngEvaluator(ROUTE[i], ROUTE[i + 1]), ROUTE[i], ROUTE[i + 1]);

            oa.setInterpolator(new LinearInterpolator());
            oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    LatLng target = (LatLng) animation.getAnimatedValue();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(target));
                }
            });

            // Use the distance of the route segment for the duration.
            double dist = SphericalUtil.computeDistanceBetween(ROUTE[i], ROUTE[i + 1]);
            oa.setDuration(Math.round(dist) * 10);

            animators.add(oa);
        }

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playSequentially(animators);
        mAnimatorSet.start();

        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mMenu.findItem(R.id.action_animation).setIcon(R.drawable.ic_action_replay);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMenu.findItem(R.id.action_animation).setIcon(R.drawable.ic_action_replay);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mMenu.findItem(R.id.action_animation).setIcon(R.drawable.ic_action_stop);
            }
        });
    }

    class LatLngEvaluator implements TypeEvaluator<LatLng> {
        double dlat, dlng;

        public LatLngEvaluator(LatLng startValue, LatLng endValue) {
            dlat = endValue.latitude - startValue.latitude;
            dlng = endValue.longitude - startValue.longitude;
        }

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            double lat = dlat * fraction + startValue.latitude;
            double lng = dlng * fraction + startValue.longitude;

            return new LatLng(lat, lng);

            // return SphericalUtil.interpolate(startValue, endValue, fraction);
        }
    }
}


//package com.joaquimley.birdsseyeview;
//
//import android.support.v4.app.FragmentActivity;
//import android.os.Bundle;
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//public class MapsActivity extends FragmentActivity {
//
//    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        setUpMapIfNeeded();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        setUpMapIfNeeded();
//    }
//
//    /**
//     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
//     * installed) and the map has not already been instantiated.. This will ensure that we only ever
//     * call {@link #setUpMap()} once when {@link #mMap} is not null.
//     * <p/>
//     * If it isn't installed {@link SupportMapFragment} (and
//     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
//     * install/update the Google Play services APK on their device.
//     * <p/>
//     * A user can return to this FragmentActivity after following the prompt and correctly
//     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
//     * have been completely destroyed during this process (it is likely that it would only be
//     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
//     * method in {@link #onResume()} to guarantee that it will be called.
//     */
//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                setUpMap();
//            }
//        }
//    }
//
//    /**
//     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
//     * just add a marker near Africa.
//     * <p/>
//     * This should only be called once and when we are sure that {@link #mMap} is not null.
//     */
//    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//    }
//}
