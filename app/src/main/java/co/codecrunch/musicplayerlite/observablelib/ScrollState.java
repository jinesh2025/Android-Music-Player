/*
 * This is the source code of Music Player Lite for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @CodeCrunch Team, 2016-2017.
 */
package co.codecrunch.musicplayerlite.observablelib;

/**
 * Constants that indicates the scroll state of the Scrollable widgets.
 */
public enum ScrollState {
    /**
     * Widget is stopped.
     * This state does not always mean that this widget have never been scrolled.
     */
    STOP,

    /**
     * Widget is scrolled up by swiping it down.
     */
    UP,

    /**
     * Widget is scrolled down by swiping it up.
     */
    DOWN,
}
