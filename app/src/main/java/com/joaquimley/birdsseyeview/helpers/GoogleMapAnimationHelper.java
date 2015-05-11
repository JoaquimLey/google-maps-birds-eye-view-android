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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;
import com.joaquimley.birdsseyeview.utils.LatLngEvaluator;
import com.joaquimley.birdsseyeview.utils.TestValues;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Helper class that handles all Google Map animation creation and customization.
 */
public class GoogleMapAnimationHelper {

    private static final String TAG = GoogleMapAnimationHelper.class.getSimpleName();

    /**
     * Create a routeAnimator set with given @params
     * WARNING: Current animation is to fast
     *
     * @param route                   to be followed
     * @param map                     where the animation is going to occur
     * @param cameraHeadingChangeRate self-explanatory
     * @param marker                  "Avatar"/representation following the route
     * @param animatorListener        self explanatory
     * @param animatorUpdateListener  self explanatory
     * @param animatorSetDuration     set a specific duration for the AnimatorSet (0 for null)
     * @param childDuration           set a specific duration for AnimatorSet's child's (0 for null)
     * @return should be .start() from the caller
     */
    public static AnimatorSet createRouteAnimatorSet(LatLng[] route, final GoogleMap map, long cameraHeadingChangeRate,
                                                     Marker marker, Animator.AnimatorListener animatorListener,
                                                     ValueAnimator.AnimatorUpdateListener animatorUpdateListener,
                                                     long animatorSetDuration, long childDuration) {
        // TODO: Fix movement speed

        LinkedList<Animator> animators = new LinkedList<>();
        // For each segment of the route, create one heading adjustment animator
        // and one segment fly-over animator.
        for (int i = 0; i < route.length - 1; i++) {
            // If it the first segment, ensure the camera is rotated properly.
            float h1;
            if (i == 0) {
                h1 = map.getCameraPosition().bearing;
            } else {
                h1 = (float) SphericalUtil.computeHeading(route[i - 1], route[i]);
            }

            float h2 = (float) SphericalUtil.computeHeading(route[i], route[i + 1]);

            ValueAnimator va = ValueAnimator.ofFloat(h1, h2);
            va.addUpdateListener(animatorUpdateListener);

            // Use the change in degrees of the heading for the animation
            // duration.
            long d = Math.round(Math.abs(h1 - h2));
            va.setDuration(d * cameraHeadingChangeRate);
            animators.add(va);

            ObjectAnimator oa = ObjectAnimator.ofObject(marker, "position",
                    new LatLngEvaluator(route[i], route[i + 1]), route[i], route[i + 1]);

            oa.setInterpolator(new LinearInterpolator());
            // TODO: Move this listener to calling activity
            oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    LatLng target = (LatLng) animation.getAnimatedValue();
                    map.moveCamera(CameraUpdateFactory.newLatLng(target));
                }
            });

            // Use the distance of the route segment for the duration.
            double dist = SphericalUtil.computeDistanceBetween(route[i], route[i + 1]);
            if (childDuration != 0) {
                oa.setDuration(childDuration);
            } else {
                oa.setDuration(Math.round(dist) * 10);
            }
            animators.add(oa);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animators);
        if (animatorSetDuration != 0) {
            animatorSet.setDuration(animatorSetDuration);
        }
        animatorSet.addListener(animatorListener);

        return animatorSet;
    }

//    public static ArrayList<CameraPosition> getLiftOffCameraPositions(CameraPosition currentCameraPosition, CameraPosition finalCameraPosition) {
//        CameraPosition startPosition = new CameraPosition.Builder()
//                .tilt(GoogleMapAnimationHelper.getMaximumTilt(TestValues.MAXIMUM_CAMERA_ZOOM))
//                .zoom(TestValues.MAXIMUM_CAMERA_ZOOM)
//                .bearing(currentCameraPosition.bearing)
//                .target(new LatLng(37.785731, -122.406267)).build();
//
//        CameraPosition finalPosition = new CameraPosition.Builder()
//                .tilt(TestValues.CAMERA_OBLIQUE_TILT)
//                .zoom(TestValues.CAMERA_OBLIQUE_ZOOM)
//                .bearing(finalCameraPosition.bearing)
//                .target(new LatLng(37.799446, -122.408989)).build();
//
//        ArrayList<CameraPosition>  cameraPositions = new ArrayList<>();
//        cameraPositions.add(0, startPosition);
//        cameraPositions.add(1, finalPosition);
//
//        return cameraPositions;
//    }

    /**
     * Creates a sequence of CameraPosition objects to make a smooth transition simulating "Lift-off"
     * by using the Zoom and Tilt parameters
     *
     * @param route             for the camera target(s)
     * @param numberOfPositions to be considered (number of positions in the route @param) for the complete lift-off animation
     * @return an array with CameraPosition objects
     */
    public static ArrayList<CameraPosition> getLiftOffCameraPositions(GoogleMap map, int numberOfPositions) {
//        if (numberOfPositions > route.length) {
//            Log.e(TAG, "Not enough route points to animate! route has: " + route.length + " animate: " + numberOfPositions);
//            return null;
//        }
//
//        if (numberOfPositions >= route.length) {
//            Log.e(TAG, "Animation positions must be < then route.length route has: " + route.length +
//                    " animatePositions: " + numberOfPositions);
//            return null;
//        }

        if (map == null) {
            Log.e(TAG, "map is null");
            return null;
        }

        if (numberOfPositions <= 1) {
            Log.e(TAG, "Not enough animation positions: " + numberOfPositions + " min: 2");
            return null;
        }

        ArrayList<CameraPosition> cameraPositions = new ArrayList<>();
        cameraPositions.add(new CameraPosition.Builder() // First position: MAX Tilt & MAX Zoom
                .zoom(TestValues.MAXIMUM_CAMERA_ZOOM)
                .tilt(GoogleMapAnimationHelper.getMaximumTilt(TestValues.MAXIMUM_CAMERA_ZOOM))
                .target(map.getCameraPosition().target)
                .bearing(map.getCameraPosition().bearing)
                .build());

        for (int i = 1; i < numberOfPositions - 1; i++) { // For each position to be animated, adjust correct zoom and tilt levels
            cameraPositions.add(new CameraPosition.Builder()
                    .zoom(TestValues.MAXIMUM_CAMERA_ZOOM - i)
                    .tilt(GoogleMapAnimationHelper.getMaximumTilt(TestValues.MAXIMUM_CAMERA_ZOOM - i))
                    .target(map.getCameraPosition().target)
                    .bearing(map.getCameraPosition().bearing)
                    .build());
        }
        cameraPositions.add(new CameraPosition.Builder() // Final position with default zoom and tilt values
                .zoom(TestValues.CAMERA_ZOOM)
                .tilt(GoogleMapAnimationHelper.getMaximumTilt(TestValues.CAMERA_ZOOM))
                .target(map.getCameraPosition().target)
                .bearing(map.getCameraPosition().bearing)
                .build());
        return cameraPositions;
    }

    /**
     * For each cameraPosition object present in the cameraPositions animate the camera.
     * WARNING: This is not respecting the MVC since it's animating the map camera, this should be moved to the caller
     * activity
     *
     * @param map                       on which the camera is present
     * @param route                     the camera shall follow
     * @param numberOfAnimatedPositions inside the @param route to be considered for the lift off animation
     */
    public static void animateLiftOff(final GoogleMap map, final int numberOfAnimatedPositions) {
        final ArrayList<CameraPosition> cameraPositions = getLiftOffCameraPositions(map, numberOfAnimatedPositions);
        if (cameraPositions == null) {
            return;
        }

        final int[] i = new int[1];
        i[0] = 0;
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositions.get(i[0])), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                i[0]++;
                if (i[0] <= numberOfAnimatedPositions)
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositions.get(i[0])));
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * Creates a camera position with given @params
     *
     * @param target            of the position
     * @param cameraObliqueZoom self explanatory
     * @param cameraObliqueTilt self explanatory
     * @return the CameraPosition object (already .build())
     */
    public static CameraPosition createCameraPosition(LatLng[] target,
                                                      float cameraObliqueZoom, float cameraObliqueTilt) {
        return new CameraPosition.Builder()
                .target(target[0])
                .zoom(cameraObliqueZoom)
                .tilt(cameraObliqueTilt)
                .bearing((float) SphericalUtil.computeHeading(target[0], target[1]))
                .build();
    }

    /**
     * Creates a camera position with given @params
     * NOTICE: Overflow method without cameraObliqueTilt @param
     *
     * @param cameraObliqueZoom self explanatory
     * @return the CameraPosition object (already .build())
     */
    public static CameraPosition createCameraPosition(LatLng[] target, float cameraObliqueZoom) {
        return new CameraPosition.Builder()
                .target(target[0])
                .zoom(cameraObliqueZoom)
                .bearing((float) SphericalUtil.computeHeading(target[0], target[1]))
                .build();
    }

    /**
     * Adjusts Google Map Camera zoom according with @param height
     *
     * @param height used as reference for giving aprox. real camera zoom
     * @return zoom value to update the camera
     */
    public static float getAdjustedCameraZoom(double height) {

        int zoom = (int) Math.round(Math.log(35200000 / height) / Math.log(2));
        if (zoom < 14) {
            return 14;
        }

        if (zoom > 19) {
            zoom = 19;
        }
        return zoom;
    }

    /**
     * Returns maximum camera tilt with zoom @param
     *
     * @param zoom used as reference
     * @return float title
     */
    public static float getMaximumTilt(float zoom) {
        float tilt = 30.0f;

        if (zoom > 15.5f) {
            tilt = 67.5f;
        } else if (zoom >= 14.0f) {
            tilt = (((zoom - 14.0f) / 1.5f) * (67.5f - 45.0f)) + 45.0f;
        } else if (zoom >= 10.0f) {
            tilt = (((zoom - 10.0f) / 4.0f) * (45.0f - 30.0f)) + 30.0f;
        }

        return tilt;
    }
}
