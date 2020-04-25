package com.mlingdu.sdk.engine;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.os.HandlerThread;
import android.view.Surface;

public class RenderThread extends HandlerThread {

    // 操作锁
    private final Object mSynOperation = new Object();
    // 更新帧的锁
    private final Object mSyncFrameNum = new Object();

    private boolean isPreviewing = false;       // 是否预览状态
    private boolean isRecording = false;        // 是否录制状态
    private boolean isRecordingPause = false;   // 是否处于暂停录制状态

    // EGL共享上下文
    private EglCore mEglCore;
    //预览用的EGLSurface
    private WindowSurface mDisplaySurface;

    private int mInputTexture;
    private int mCurrentTexture;
    private SurfaceTexture mSurfaceTexture;

    // 矩阵
    private final float[] mMatrix = new float[16];
    private int mTextureWidth, mTextureHeight;

    // 渲染Handler回调
    private RenderHandler mRenderHandler;

    public RenderThread(Context context, String name) {
        super(name);
    }

    /**
     * 设置预览Handler回调
     * @param handler
     */
    public void setRenderHandler(RenderHandler handler) {
        mRenderHandler = handler;
    }

    /**
     * Surface创建
     */
    void surfaceCreated(SurfaceTexture surfaceTexture) {
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mDisplaySurface = new WindowSurface(mEglCore, surfaceTexture);
        mDisplaySurface.makeCurrent();

        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        GLES30.glDisable(GLES30.GL_CULL_FACE);


    }

    /**
     * Surface改变
     * @param width
     * @param height
     */
    void surfaceChanged(int width, int height) {

    }

    /**
     * Surface销毁
     */
    void surfaceDestroyed() {

    }

    /**
     * 绘制帧
     */
    void drawFrame() {

    }

    /**
     * 切换相机
     */
    void switchCamera() {

    }

    /**
     * 预览回调
     * @param data
     */
    void onPreviewCallback(byte[] data) {

    }

    /**
     * 开始录制
     */
    void startRecording() {

    }

    /**
     * 停止录制
     */
    void stopRecording() {

    }
}
