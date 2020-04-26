package com.mlingdu.sdk.engine;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES30;
import android.os.HandlerThread;
import android.view.Surface;

import com.mlingdu.sdk.utils.OpenGLUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RenderThread extends HandlerThread implements Camera.PreviewCallback{

    // 操作锁
    private final Object mSynOperation = new Object();
    // 更新帧的锁
    private final Object mSyncFrameNum = new Object();

    private boolean isPreviewing = false;       // 是否预览状态
    private boolean isRecording = false;        // 是否录制状态
    private boolean isRecordingPause = false;   // 是否处于暂停录制状态
    // 相机对象
    private Camera mCamera;
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
    // 可用帧
    private int mFrameNum = 0;
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

        mInputTexture = OpenGLUtils.createOESTexture();
        mSurfaceTexture = new SurfaceTexture(mInputTexture);
        openCamera();
    }

    /**
     * Surface改变
     * @param width
     * @param height
     */
    void surfaceChanged(int width, int height) {
        mCamera.startPreview();
        isPreviewing = true;
    }

    /**
     * Surface销毁
     */
    void surfaceDestroyed() {
        mDisplaySurface.makeCurrent();
        releaseCamera();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mDisplaySurface != null) {
            mDisplaySurface.release();
            mDisplaySurface = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
    }

    /**
     * 绘制帧
     */
    void drawFrame() {
        if (mSurfaceTexture == null || mDisplaySurface == null) {
            return;
        }
        // 当记录的请求帧数不为时，更新画面
        while (mFrameNum > 0) {
            // 切换渲染上下文
            mDisplaySurface.makeCurrent();
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mMatrix);
            --mFrameNum;

            // 显示到屏幕
            mDisplaySurface.swapBuffers();

            // 是否处于录制状态
            if (isRecording && !isRecordingPause) {

            }
        }
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

    // --------------------------------- 相机操作逻辑 ----------------------------------------------
    /**
     * 打开相机
     */
    void openCamera() {
        releaseCamera();

        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        if (mCamera == null) {
            throw new RuntimeException("Unable to open camera");
        }
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(1280, 720);
        parameters.setPictureSize(1280, 720);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(this);
    }
    /**
     * 释放相机
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.addCallbackBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }
}
