package com.doubtech.vr.windowtoolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.doubtech.vr.windowtoolkit.utils.ShaderHelper;
import com.google.vrtoolkit.cardboard.EyeTransform;

import javax.microedition.khronos.egl.EGLConfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by a1.jackson on 7/21/14.
 */
public class VirtualScreen {
    private static final String TAG = "VR::VirtualScreen";

    private static final boolean DEBUG_POSITION = false;

    static final float[] CUBE_COORDS = new float[] {
            // Front face
            -1.0f, 1.0f, 1.0f,  // Top Triangle::Top Left
            -1.0f, -1.0f, 1.0f, // Top Triangle::Bottom Left
            1.0f, 1.0f, 1.0f,   // Top Triangle::Top Right
            -1.0f, -1.0f, 1.0f, // Bottom Triangle::Bottom Left
            1.0f, -1.0f, 1.0f,  // Bottom Triangle::Bottom Right
            1.0f, 1.0f, 1.0f,   // Bottom Triangle::Top Right

            // Right face
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,

            // Back face
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            // Left face
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,

            // Top face
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,

            // Bottom face
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
    };

    // S, T (or X, Y)
    final float[] CUBE_TEXTURE_COORDS = {
            // Front face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Right face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Back face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Left face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Top face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Bottom face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    static final float[] CUBE_NORMALS = new float[] {
            // Front face
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            // Right face
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,

            // Back face
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,

            // Left face
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,

            // Top face
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            // Bottom face
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f
    };

    static final float[] CUBE_COLORS = new float[] {
            // front, green
            /*0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,*/
            // front, green
            1f, 1f, 1f, .5f,
            1f, 1f, 1f, .5f,
            1f, 1f, 1f, .5f,
            1f, 1f, 1f, .5f,
            1f, 1f, 1f, .5f,
            1f, 1f, 1f, .5f,

            // right, blue
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,

            // back, also green
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,

            // left, also blue
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,

            // top, red
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,

            // bottom, also red
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
    };

    static final float[] CUBE_FOUND_COLORS = new float[] {
            // front, green
            1f, 1f, 1f, 1f,
            1f, 1f, 1f, 1f,
            1f, 1f, 1f, 1f,
            1f, 1f, 1f, 1f,
            1f, 1f, 1f, 1f,
            1f, 1f, 1f, 1f,

            // right, blue
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,

            // back, also green
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,

            // left, also blue
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,

            // top, red
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,

            // bottom, also red
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
            0.8359375f,  0.17578125f,  0.125f, 1.0f,
    };

    private static final int COORDS_PER_VERTEX = 3;

    private final GLModel mModel;
    private final Context mContext;

    private FloatBuffer mCubeVertices;
    private FloatBuffer mCubeColors;
    private FloatBuffer mCubeFoundColors;
    private FloatBuffer mCubeNormals;
    private FloatBuffer mCubeTextureCoordinates;
    private float[] mModelCube;
    private float[] mCubeCoords;

    private float mObjectDistance = 3f;

    private int mTextureHandle = 0;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private int mTextureCoordinateDataSize = 2;

    public VirtualScreen(Context context, GLModel model) {
        mContext = context;
        mModel = model;
        mModelCube = new float[16];
        mCubeCoords = new float[CUBE_COORDS.length];
        setAspectRatio(1920, 1080);
    }

    public void setAspectRatio(int width, int height) {
        float ratio = width / (float) height;

        for(int i = 0; i < CUBE_COORDS.length; i++) {
            if(i % 3 == 0) {
                mCubeCoords[i] = CUBE_COORDS[i] * ratio;
            } else {
                mCubeCoords[i] = CUBE_COORDS[i];
            }
        }
        resetCube();
    }

    public void setDistance(float distance) {
        mObjectDistance = distance;
    }

    public void onSurfaceCreated(EGLConfig config) {
        resetCube();
    }

    private void loadTextures() {
        if(0 != mTextureHandle) {
            Log.w(TAG, "Texture handle is already bound.");
            return;
        }

        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if(textureHandle[0] != 0) {
            mTextureHandle = textureHandle[0];
        }
    }

    public void updateBitmapBuffer(Bitmap bitmap) {
        setAspectRatio(bitmap.getWidth(), bitmap.getHeight());
        if(0 == mTextureHandle) {
            // Attempt to load textures again
            loadTextures();
            if(0 == mTextureHandle) {
                return;
            }
        }
        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        VRGLUtils.checkGLError("updateBitmapBuffer()");
        Log.d(TAG, "Bitmap buffer updated (" + mTextureHandle + ").");
    }

    public void resetCube() {
        ByteBuffer bbVertices = ByteBuffer.allocateDirect(CUBE_COORDS.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        mCubeVertices = bbVertices.asFloatBuffer();
        mCubeVertices.put(mCubeCoords);
        mCubeVertices.position(0);

        ByteBuffer bbColors = ByteBuffer.allocateDirect(CUBE_COLORS.length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        mCubeColors = bbColors.asFloatBuffer();
        mCubeColors.put(CUBE_COLORS);
        mCubeColors.position(0);

        ByteBuffer bbFoundColors = ByteBuffer.allocateDirect(CUBE_FOUND_COLORS.length * 4);
        bbFoundColors.order(ByteOrder.nativeOrder());
        mCubeFoundColors = bbFoundColors.asFloatBuffer();
        mCubeFoundColors.put(CUBE_FOUND_COLORS);
        mCubeFoundColors.position(0);

        ByteBuffer bbNormals = ByteBuffer.allocateDirect(CUBE_NORMALS.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        mCubeNormals = bbNormals.asFloatBuffer();
        mCubeNormals.put(CUBE_NORMALS);
        mCubeNormals.position(0);

        ByteBuffer bbTextureCoords = ByteBuffer.allocateDirect(CUBE_TEXTURE_COORDS.length * 4);
        bbTextureCoords.order(ByteOrder.nativeOrder());
        mCubeTextureCoordinates = bbTextureCoords.asFloatBuffer();
        mCubeTextureCoordinates.put(CUBE_TEXTURE_COORDS);
        mCubeTextureCoordinates.position(0);

        // Object first appears directly in front of user
        Matrix.setIdentityM(mModelCube, 0);
        Matrix.translateM(mModelCube, 0, 0, 0, -mObjectDistance);
    }


    public void draw(EyeTransform transform) {
        Matrix.multiplyMM(mModel.getModelView(), 0, mModel.getView(), 0, mModelCube, 0);
        Matrix.multiplyMM(mModel.getModelViewProjection(),
                0, transform.getPerspective(), 0, mModel.getModelView(), 0);
        drawCube();
    }

    /**
     * Draw the cube. We've set all of our transformation matrices. Now we simply pass them into
     * the shader.
     */
    public void drawCube() {
        // This is not the floor!
        GLES20.glUniform1f(mModel.getIsFloorParam(), 0f);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(mModel.getModelParam(), 1, false, mModelCube, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(mModel.getModelViewParam(), 1, false, mModel.getModelView(), 0);

        // Set the position of the cube
        GLES20.glVertexAttribPointer(mModel.getPositionParam(), COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, mCubeVertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(mModel.getModelViewProjectionParam(), 1, false, mModel.getModelViewProjection(), 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(mModel.getNormalParam(), 3, GLES20.GL_FLOAT,
                false, 0, mCubeNormals);



        if (isLookingAtObject()) {
            GLES20.glVertexAttribPointer(mModel.getColorParam(), 4, GLES20.GL_FLOAT, false,
                    0, mCubeFoundColors);
        } else {
            GLES20.glVertexAttribPointer(mModel.getColorParam(), 4, GLES20.GL_FLOAT, false,
                    0, mCubeColors);
        }


        if(0 != mTextureHandle) {
            mTextureUniformHandle = GLES20.glGetUniformLocation(mModel.getProgramHandle(), "u_Texture");
            mTextureCoordinateHandle = GLES20.glGetAttribLocation(mModel.getProgramHandle(), "a_TexCoordinate");

            mCubeTextureCoordinates.position(0);
            GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                            0, mCubeTextureCoordinates);
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

            // Set the active texture unit to texture unit 0.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);

            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
            GLES20.glUniform1i(mTextureUniformHandle, 0);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        VRGLUtils.checkGLError("drawCube()");
    }

    /**
     * Check if user is looking at object by calculating where the object is in eye-space.
     * @return
     */
    public boolean isLookingAtObject() {
        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(mModel.getModelView(), 0, mModel.getHeadView(), 0, mModelCube, 0);
        Matrix.multiplyMV(objPositionVec, 0, mModel.getModelView(), 0, initVec, 0);

        float pitch = (float)Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float)Math.atan2(objPositionVec[0], -objPositionVec[2]);

        if(DEBUG_POSITION) {
            Log.i(TAG, "Object position: X: " + objPositionVec[0]
                    + "  Y: " + objPositionVec[1] + " Z: " + objPositionVec[2]);
            Log.i(TAG, "Object Pitch: " + pitch + "  Yaw: " + yaw);
        }

        return (Math.abs(pitch) < Constants.PITCH_LIMIT) && (Math.abs(yaw) < Constants.YAW_LIMIT);
    }

}
