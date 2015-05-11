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

package com.joaquimley.birdsseyeview.utils;

import android.graphics.Color;

/**
 * Simple class that holds all the values used for testing
 */

public class TestValues {
    public static final float POLYLINE_WIDTH = 8;
    public static final int POLYLINE_HUE = 360; // 0-360
    public static final float POLYLINE_SATURATION = 1; // 0-1
    public static final float POLYLINE_VALUE = 1; // 0-1
    public static final int POLYLINE_ALPHA = 128; // 0-255
    public static final int POLYLINE_FINAL_COLOR = Color.HSVToColor(POLYLINE_ALPHA,
            new float[]{POLYLINE_HUE, POLYLINE_SATURATION, POLYLINE_VALUE});

    public static final float CAMERA_ZOOM = 16;
    public static final float CAMERA_OBLIQUE_ZOOM = 18;
    public static final float CAMERA_OBLIQUE_TILT = 60;
    public static final long CAMERA_HEADING_CHANGE_RATE = 5;

    public static final float MAXIMUM_CAMERA_ZOOM = 19;

}
