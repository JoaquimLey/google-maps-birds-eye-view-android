package com.joaquimley.birdsseyeview.helpers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;
import com.joaquimley.birdsseyeview.utils.LatLngEvaluator;

import java.util.LinkedList;


public class GoogleMapAnimationHelper {

    /**
     * Animates the camera following a route given by param
     * WARNING: Current animation is to fast
     *
     * @param route
     * @param map
     * @param cameraHeadingChangeRate
     * @param marker
     * @param listener
     * @param animatorUpdateListener
     * @return animator where the caller can animator.start()
     */
    public static AnimatorSet animateRoute(LatLng[] route, final GoogleMap map, long cameraHeadingChangeRate,
                                           Marker marker, Animator.AnimatorListener listener, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {

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
            oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    LatLng target = (LatLng) animation.getAnimatedValue();
                    map.moveCamera(CameraUpdateFactory.newLatLng(target));
                }
            });

            // Use the distance of the route segment for the duration.
            double dist = SphericalUtil.computeDistanceBetween(route[i], route[i + 1]);
            oa.setDuration(Math.round(dist) * 10);

            animators.add(oa);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animators);
        animatorSet.addListener(listener);
        return animatorSet;
    }

    public static void animateCameraToRouteBeginning(LatLng[] route, GoogleMap map,
                                                     float cameraObliqueZoom, float cameraObliqueTilt) {

        CameraPosition pos = new CameraPosition.Builder()
                .target(route[0])
                .zoom(cameraObliqueZoom)
                .tilt(cameraObliqueTilt)
                .bearing((float) SphericalUtil.computeHeading(route[0], route[1]))
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    public static void moveCameraToStartPosition(GoogleMap map, LatLng[] route, float cameraObliqueZoom) {
        CameraPosition pos = new CameraPosition.Builder()
                .target(route[0])
                .zoom(cameraObliqueZoom - 2)
                .build();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    /**
     * Zoom Google Map's camera into a location
     *
     * @param map       self explanatory
     * @param location  where the zoom will occur
     * @param zoomValue how "much" zoom into location
     */
    public static void zoomMapIntoLocation(GoogleMap map, LatLng location, float zoomValue) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, zoomValue);
        map.animateCamera(cameraUpdate);
    }
}
