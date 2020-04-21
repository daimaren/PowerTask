package com.mlingdu.demo.task05;

import android.opengl.GLES20;
import android.util.Log;

import javax.microedition.khronos.opengles.GL;

public class BaseGLSL {
    private static final String TAG = "BaseGLSL";
    public static final int COORDS_PER_VERTEX = 3;
    public static final int vertexStride = COORDS_PER_VERTEX * 4;

    public static int loaderShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static int createOpenGLProgram(String vertexSource, String framentSource) {
        int vertex = loaderShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) {
            Log.e(TAG, "loader shader vertex failed");
            return 0;
        }

        int fragment = loaderShader(GLES20.GL_FRAGMENT_SHADER, framentSource);
        if (fragment == 0) {
            Log.e(TAG, "loader shader fragment failed");
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertex);
            GLES20.glAttachShader(program, fragment);
            GLES20.glLinkProgram(program);

            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "could not link program" + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                return 0;
            }
        }
        return program;
    }
}
