package com.mlingdu.cameralib.glfilter.beauty;

import android.content.Context;

import com.mlingdu.cameralib.glfilter.base.GLImageFilter;
import com.mlingdu.cameralib.glfilter.beauty.bean.BeautyParam;
import com.mlingdu.cameralib.glfilter.beauty.bean.IBeautify;
import com.mlingdu.cameralib.glfilter.utils.OpenGLUtils;

import java.nio.FloatBuffer;

/**
 * 实时美颜，这里用的是高反差保留磨皮法
 */
public class GLImageBeautyFilter extends GLImageFilter implements IBeautify {

    // 美肤滤镜
    private GLImageBeautyComplexionFilter mComplexionFilter;

    // 缩放
    private float mBlurScale = 0.5f;

    public GLImageBeautyFilter(Context context) {
        this(context, null, null);
    }

    public GLImageBeautyFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
        initFilters();
    }

    private void initFilters() {
        mComplexionFilter = new GLImageBeautyComplexionFilter(mContext);
    }

    @Override
    public void onInputSizeChanged(int width, int height) {
        super.onInputSizeChanged(width, height);
        if (mComplexionFilter != null) {
            mComplexionFilter.onInputSizeChanged(width, height);
        }
    }

    @Override
    public void onDisplaySizeChanged(int width, int height) {
        super.onDisplaySizeChanged(width, height);
        if (mComplexionFilter != null) {
            mComplexionFilter.onDisplaySizeChanged(width, height);
        }
    }

    @Override
    public boolean drawFrame(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        if (textureId == OpenGLUtils.GL_NOT_TEXTURE) {
            return false;
        }
        int currentTexture = textureId;
        mComplexionFilter.drawFrameBuffer(currentTexture, vertexBuffer, textureBuffer);
        return false;
    }

    @Override
    public int drawFrameBuffer(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        if (textureId == OpenGLUtils.GL_NOT_TEXTURE) {
            return textureId;
        }
        int currentTexture = textureId;
        int sourceTexture = mComplexionFilter.drawFrameBuffer(currentTexture, vertexBuffer, textureBuffer);
        currentTexture = sourceTexture;

        return currentTexture;
    }

    @Override
    public void initFrameBuffer(int width, int height) {
        super.initFrameBuffer(width, height);
        if (mComplexionFilter != null) {
            mComplexionFilter.initFrameBuffer(width, height);
        }

    }

    @Override
    public void destroyFrameBuffer() {
        super.destroyFrameBuffer();
        if (mComplexionFilter != null) {
            mComplexionFilter.destroyFrameBuffer();
        }

    }

    @Override
    public void release() {
        super.release();
        if (mComplexionFilter != null) {
            mComplexionFilter.release();
            mComplexionFilter = null;
        }
    }

    @Override
    public void onBeauty(BeautyParam beauty) {
        if (mComplexionFilter != null) {
            mComplexionFilter.setComplexionLevel(beauty.complexionIntensity);
        }
    }
}
