package com.mlingdu.demo.task12;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mlingdu.demo.R;

public class PreviewCameraActivity extends AppCompatActivity {
    private CameraGLSurfaceView mCameraView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_camera);
        initView();
    }

    private void initView() {
        mCameraView = findViewById(R.id.gl_camera_view);
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (mCameraView != null) {
            mCameraView.onResume();
        }
    }

}
