package com.mlingdu.cameralib.glfilter.base;

import android.content.Context;

import com.mlingdu.cameralib.glfilter.utils.OpenGLUtils;

/**
 * 加载一张图片，需要倒过来
 */
public class GLImageInputFilter extends GLImageFilter {

    public GLImageInputFilter(Context context) {
        this(context, VERTEX_SHADER, OpenGLUtils.getShaderFromAssets(context,
                "shader/base/fragment_image_input.glsl"));
    }

    public GLImageInputFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
    }
}
