package com.mlingdu.demo.task05;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class BaseGLSurfaceView extends GLSurfaceView {
    public BaseGLSurfaceView(Context context) {
        super(context);
        initEGLContext();
    }

    public BaseGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEGLContext();
    }

    private void initEGLContext() {
        setEGLContextClientVersion(2);
    }
}
