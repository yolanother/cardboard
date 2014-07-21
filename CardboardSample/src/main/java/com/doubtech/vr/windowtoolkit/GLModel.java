package com.doubtech.vr.windowtoolkit;

/**
 * Created by a1.jackson on 7/21/14.
 */
public interface GLModel {
    float[] getModelView();
    float[] getModelViewProjection();
    float[] getView();
    float[] getHeadView();

    int getIsFloorParam();
    int getModelParam();
    int getModelViewParam();
    int getPositionParam();
    int getModelViewProjectionParam();
    int getNormalParam();
    int getColorParam();

    int getProgramHandle();
}
