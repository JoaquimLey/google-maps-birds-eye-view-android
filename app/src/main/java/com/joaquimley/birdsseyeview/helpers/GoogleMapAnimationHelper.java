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
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;
import com.joaquimley.birdsseyeview.utils.LatLngEvaluator;

import java.util.LinkedList;

/***
 * Helper class that handles all Google Map animation creation and customization.
 */
public class GoogleMapAnimationHelper {

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
     * @return should be .start() from the caller
     */
    public static AnimatorSet createRouteAnimatorSet(LatLng[] route, final GoogleMap map, long cameraHeadingChangeRate,
                                                     Marker marker, Animator.AnimatorListener animatorListener,
                                                     ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
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
            oa.setDuration(Math.round(dist) * 10);

            animators.add(oa);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animators);
        animatorSet.addListener(animatorListener);
        return animatorSet;
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
}
