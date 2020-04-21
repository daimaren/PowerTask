package com.mlingdu.cameralib.activity;

import android.Manifest;
import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES30;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.mlingdu.cameralib.R;
import com.mlingdu.cameralib.engine.camera.CameraParam;
import com.mlingdu.cameralib.gles.EglCore;
import com.mlingdu.cameralib.gles.WindowSurface;
import com.mlingdu.cameralib.glfilter.base.GLImageOESInputFilter;
import com.mlingdu.cameralib.glfilter.beauty.GLImageBeautyFilter;
import com.mlingdu.cameralib.glfilter.utils.OpenGLUtils;
import com.mlingdu.cameralib.glfilter.utils.TextureRotationUtils;
import com.mlingdu.cameralib.utils.PermissionUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

public class CameraDemoActivity extends AppCompatActivity implements Camera.PreviewCallback{

    // 坐标缓冲
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.CENTER_CROP;
    private SurfaceView mSurfacView;
    private EglCore mEglCore;
    private WindowSurface mDisplaySurface;
    private int mInputTexture;
    private SurfaceTexture mSurfaceTexture;
    private int mWidth = CameraParam.DEFAULT_16_9_WIDTH;
    private int mHeight = CameraParam.DEFAULT_16_9_HEIGHT;

    private int mTextureWidth = 720;
    private int mTextureHeight = 1280;

    private int mViewWidth = 1080;
    private int mViewHeight = 1920;

    private Camera mCamera;
    // 预览回调
    private byte[] mPreviewBuffer;
    // 矩阵
    private final float[] mMatrix = new float[16];

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    // 用于显示裁剪的纹理顶点缓冲
    private FloatBuffer mDisplayVertexBuffer;
    private FloatBuffer mDisplayTextureBuffer;
    private GLImageOESInputFilter mInputFilter;
    private GLImageBeautyFilter mImageBeautyFilter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acvitity_camera_demo);
        checkPermissions();
        initView();
    }
    private void initView() {
        mSurfacView = findViewById(R.id.sv_camera);
        mSurfacView.getHolder().addCallback(mSurfaceCallback);
    }

    SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
            mDisplaySurface = new WindowSurface(mEglCore, surfaceHolder.getSurface(), false);
            mDisplaySurface.makeCurrent();

            GLES30.glDisable(GLES30.GL_DEPTH_TEST);
            GLES30.glDisable(GLES30.GL_CULL_FACE);

            initFilter();
            mInputTexture = OpenGLUtils.createOESTexture();
            mSurfaceTexture = new SurfaceTexture(mInputTexture);
            // 打开相机
            openCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            mViewWidth = i1;
            mViewHeight = i2;
            adjustCoordinateSize();

            mInputFilter.onInputSizeChanged(mTextureWidth, mTextureHeight);
            mInputFilter.onDisplaySizeChanged(mViewWidth, mViewHeight);
            mImageBeautyFilter.onInputSizeChanged(mTextureWidth, mTextureHeight);
            mImageBeautyFilter.onDisplaySizeChanged(mViewWidth, mViewHeight);

            mCamera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.addCallbackBuffer(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

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
            releaseBuffers();
            mInputFilter.release();
            mImageBeautyFilter.release();
        }
    };

    private void initFilter() {
        mVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices);
        mTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices);
        mDisplayVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices);
        mDisplayTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices);

        mInputFilter = new GLImageOESInputFilter(this);
        mImageBeautyFilter = new GLImageBeautyFilter(this);
    }

    private void openCamera() {
        mCamera = Camera.open(1);
        if (mCamera == null) {
            throw new RuntimeException("Unable to open camera");
        }
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(mWidth, mHeight);
        params.setPictureSize(mWidth, mHeight);
        mCamera.setParameters(params);
        mCamera.setDisplayOrientation(calculateCameraPreviewOrientation(this));
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            mPreviewBuffer = new byte[mWidth * mHeight * 3/ 2];
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.addCallbackBuffer(mPreviewBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        //preview
        drawFrame();
        if (mPreviewBuffer != null) {
            camera.addCallbackBuffer(mPreviewBuffer);
        }
    }

    private void drawFrame() {
        int currentTexture = mInputTexture;
        mSurfaceTexture.updateTexImage();
        // 切换渲染上下文
        mDisplaySurface.makeCurrent();
        mSurfaceTexture.getTransformMatrix(mMatrix);
        mInputFilter.setTextureTransformMatrix(mMatrix);
        // 绘制渲染
        currentTexture = mInputFilter.drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
        currentTexture = mImageBeautyFilter.drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
        mInputFilter.drawFrame(currentTexture, mDisplayVertexBuffer, mDisplayTextureBuffer);

        // 显示到屏幕
        mDisplaySurface.swapBuffers();
    }

    private void checkPermissions() {
        boolean cameraEnable = PermissionUtils.permissionChecking(this,
                Manifest.permission.CAMERA);
        boolean storageWriteEnable = PermissionUtils.permissionChecking(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean recordAudio = PermissionUtils.permissionChecking(this,
                Manifest.permission.RECORD_AUDIO);
        if (!cameraEnable || !storageWriteEnable || !recordAudio) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO
                    }, 0);
        }
    }

    /**
     * 调整由于surface的大小与SurfaceView大小不一致带来的显示问题
     */
    private void adjustCoordinateSize() {
        float[] textureCoord = null;
        float[] vertexCoord = null;
        float[] textureVertices = TextureRotationUtils.TextureVertices;
        float[] vertexVertices = TextureRotationUtils.CubeVertices;
        float ratioMax = Math.max((float) mViewWidth / mTextureWidth,
                (float) mViewHeight / mTextureHeight);
        // 新的宽高
        int imageWidth = Math.round(mTextureWidth * ratioMax);
        int imageHeight = Math.round(mTextureHeight * ratioMax);
        // 获取视图跟texture的宽高比
        float ratioWidth = (float) imageWidth / (float) mViewWidth;
        float ratioHeight = (float) imageHeight / (float) mViewHeight;
        if (mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
            vertexCoord = new float[] {
                    vertexVertices[0] / ratioHeight, vertexVertices[1] / ratioWidth, vertexVertices[2],
                    vertexVertices[3] / ratioHeight, vertexVertices[4] / ratioWidth, vertexVertices[5],
                    vertexVertices[6] / ratioHeight, vertexVertices[7] / ratioWidth, vertexVertices[8],
                    vertexVertices[9] / ratioHeight, vertexVertices[10] / ratioWidth, vertexVertices[11],
            };
        } else if (mScaleType == ImageView.ScaleType.CENTER_CROP) {
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCoord = new float[] {
                    addDistance(textureVertices[0], distVertical), addDistance(textureVertices[1], distHorizontal),
                    addDistance(textureVertices[2], distVertical), addDistance(textureVertices[3], distHorizontal),
                    addDistance(textureVertices[4], distVertical), addDistance(textureVertices[5], distHorizontal),
                    addDistance(textureVertices[6], distVertical), addDistance(textureVertices[7], distHorizontal),
            };
        }
        if (vertexCoord == null) {
            vertexCoord = vertexVertices;
        }
        if (textureCoord == null) {
            textureCoord = textureVertices;
        }
        // 更新VertexBuffer 和 TextureBuffer
        mDisplayVertexBuffer.clear();
        mDisplayVertexBuffer.put(vertexCoord).position(0);
        mDisplayTextureBuffer.clear();
        mDisplayTextureBuffer.put(textureCoord).position(0);
    }

    /**
     * 计算距离
     * @param coordinate
     * @param distance
     * @return
     */
    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    /**
     * 设置预览角度，setDisplayOrientation本身只能改变预览的角度
     * previewFrameCallback以及拍摄出来的照片是不会发生改变的，拍摄出来的照片角度依旧不正常的
     * 拍摄的照片需要自行处理
     * 这里Nexus5X的相机简直没法吐槽，后置摄像头倒置了，切换摄像头之后就出现问题了。
     * @param activity
     */
    private int calculateCameraPreviewOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(1, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * 释放缓冲区
     */
    private void releaseBuffers() {
        if (mVertexBuffer != null) {
            mVertexBuffer.clear();
            mVertexBuffer = null;
        }
        if (mTextureBuffer != null) {
            mTextureBuffer.clear();
            mTextureBuffer = null;
        }
        if (mDisplayVertexBuffer != null) {
            mDisplayVertexBuffer.clear();
            mDisplayVertexBuffer = null;
        }
        if (mDisplayTextureBuffer != null) {
            mDisplayTextureBuffer.clear();
            mDisplayTextureBuffer = null;
        }
    }
}
