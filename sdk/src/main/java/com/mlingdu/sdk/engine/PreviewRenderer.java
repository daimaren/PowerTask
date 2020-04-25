package com.mlingdu.sdk.engine;

import android.content.Context;
import android.graphics.SurfaceTexture;

/**
 * 预览渲染器
 */
public class PreviewRenderer {
    private PreviewRenderer() {

    }

    private static class RenderHolder {
        private static PreviewRenderer instance = new PreviewRenderer();
    }

    public static PreviewRenderer getInstance() {
        return RenderHolder.instance;
    }

    // 渲染Handler
    private RenderHandler mRenderHandler;
    // 渲染线程
    private RenderThread mPreviewRenderThread;
    // 操作锁
    private final Object mSyncOperation = new Object();
    /**
     * 初始化渲染器
     */
    void initRenderer(Context context) {
        synchronized (mSyncOperation) {
            mPreviewRenderThread = new RenderThread(context, "RenderThread");
            mPreviewRenderThread.start();
            mRenderHandler = new RenderHandler(mPreviewRenderThread);
            mPreviewRenderThread.setRenderHandler(mRenderHandler);
        }
    }

    /**
     * 销毁渲染器
     */
    public void destroyRenderer() {
        synchronized (mSyncOperation) {
            if (mRenderHandler != null) {
                mRenderHandler.removeCallbacksAndMessages(null);
                mRenderHandler = null;
            }
            if (mPreviewRenderThread != null) {
                mPreviewRenderThread.quitSafely();
                try {
                    mPreviewRenderThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mPreviewRenderThread = null;
            }
        }
    }

    /**
     * 绑定SurfaceTexture
     * @param surfaceTexture
     */
    public void bindSurface(SurfaceTexture surfaceTexture) {
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderHandler.MSG_SURFACE_CREATED, surfaceTexture));
        }
    }

    /**
     * 改变预览大小
     * @param width
     * @param height
     */
    public void changePreviewSize(int width, int height) {
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderHandler.MSG_SURFACE_CHANGED, width, height));
        }
    }

    /**
     * 解绑Surface
     */
    public void unbindSurface() {
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderHandler.MSG_SURFACE_DESTROYED));
        }
    }

    /**
     * 开始录制
     */
    public void startRecording() {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSyncOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderHandler.MSG_START_RECORDING));
        }
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSyncOperation) {
            mRenderHandler.sendEmptyMessage(RenderHandler.MSG_STOP_RECORDING);
        }
    }
}
