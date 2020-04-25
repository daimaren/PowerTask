package com.mlingdu.sdk.engine;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.lang.ref.WeakReference;

/**
 * 预览渲染Handler
 */
public class RenderHandler extends Handler {

    // Surface创建
    public static final int MSG_SURFACE_CREATED = 0x001;
    // Surface改变
    public static final int MSG_SURFACE_CHANGED = 0x002;
    // Surface销毁
    public static final int MSG_SURFACE_DESTROYED = 0x003;
    // 渲染
    public static final int MSG_RENDER = 0x004;
    // 开始录制
    public static final int MSG_START_RECORDING = 0x006;
    // 停止录制
    public static final int MSG_STOP_RECORDING = 0x008;
    // 重新打开相机
    public static final int MSG_REOPEN_CAMERA = 0x009;
    // 切换相机
    public static final int MSG_SWITCH_CAMERA = 0x010;
    // 预览帧回调
    public static final int MSG_PREVIEW_CALLBACK = 0x011;

    private WeakReference<RenderThread> mWeakRenderThread;

    public RenderHandler(RenderThread thread) {
        super(thread.getLooper());
        mWeakRenderThread = new WeakReference<RenderThread>(thread);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mWeakRenderThread == null || mWeakRenderThread.get() == null)
            return;
        RenderThread thread = mWeakRenderThread.get();
        switch (msg.what) {
            case MSG_SURFACE_CREATED:
                if (msg.obj instanceof SurfaceTexture) {
                    thread.surfaceCreated((SurfaceTexture) msg.obj);
                }
                break;
            case MSG_SURFACE_CHANGED:
                thread.surfaceChanged(msg.arg1, msg.arg2);
                break;
            case MSG_SURFACE_DESTROYED:
                thread.surfaceDestroyed();
                break;
            // 帧可用（考虑同步的问题）
            case MSG_RENDER:
                thread.drawFrame();
                break;
            // 开始录制
            case MSG_START_RECORDING:
                thread.startRecording();
                break;
            // 停止录制
            case MSG_STOP_RECORDING:
                thread.stopRecording();
                break;
            // 切换相机
            case MSG_SWITCH_CAMERA:
                thread.switchCamera();
                break;
            // 预览帧回调
            case MSG_PREVIEW_CALLBACK:
                thread.onPreviewCallback((byte[])msg.obj);
                break;
            default:
                break;
        }
    }
}
