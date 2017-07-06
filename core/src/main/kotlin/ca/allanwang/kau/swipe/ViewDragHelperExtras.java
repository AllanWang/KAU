package ca.allanwang.kau.swipe;

import android.content.Context;

/**
 * Created by Allan Wang on 2017-07-05.
 * <p>
 * Collection of addition methods added into ViewDragHelper that weren't in the original
 */

interface ViewDragHelperExtras {

    /**
     * Sets the new size of a given edge
     * Any touch within this range will be defined to have started from an edge
     *
     * @param size the new size in px
     */
    void setEdgeSize(int size);

    void setMaxVelocity(float maxVel);

    float getMaxVelocity();

    void setSensitivity(Context context, float sensitivity);

    float getSensitivity();

}
