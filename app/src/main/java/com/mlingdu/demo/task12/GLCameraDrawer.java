package com.mlingdu.demo.task12;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLCameraDrawer implements GLSurfaceView.Renderer {
    private SurfaceTexture surfaceTexture;

    private BaseFilter mOesFilter;

    private int width, height;
    private int dataWidth, dataHeight;

    private float[] matrix = new float[16];

    public GLCameraDrawer() {
        mOesFilter = new OesFilter();
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }
}
