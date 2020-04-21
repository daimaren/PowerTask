package com.mlingdu;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mlingdu.cameralib.activity.CameraActivity;
import com.mlingdu.cameralib.activity.CameraDemoActivity;
import com.mlingdu.demo.task01.ImageActivity;
import com.mlingdu.demo.task12.PreviewCameraActivity;
import com.mlingdu.demo.R;
import com.mlingdu.demo.utils.PermissionUtils;
import com.mlingdu.sdk.activity.AudioHwEncoderActivity;
import com.mlingdu.sdk.activity.FdkAACActivity;

/**
 * 《Android 音视频从入门到提高 —— 任务列表》
 * 1. 在 Android 平台绘制一张图片，使用至少 3 种不同的 API，ImageView，SurfaceView，自定义 View--ok
 * 2. 在 Android 平台使用 AudioRecord 和 AudioTrack API 完成音频 PCM 数据的采集和播放，并实现读写音频 wav 文件--ok
 * 3. 在 Android 平台使用 Camera API 进行视频的采集，分别使用 SurfaceView、TextureView 来预览 Camera 数据，取到 NV21 的数据回调--ok
 * 4. 学习 Android 平台的 MediaExtractor 和 MediaMuxer API，知道如何解析和封装 mp4 文件--ok
 * 5. 学习 Android 平台 OpenGL ES API，了解 OpenGL 开发的基本流程，使用 OpenGL 绘制一个三角形--ok
 * 6. 学习 Android 平台 OpenGL ES API，学习纹理绘制，能够使用 OpenGL 显示一张图片--ok
 * 7. 学习 MediaCodec API，完成音频 AAC 硬编、硬解--ok
 * 8. 学习 MediaCodec API，完成视频 H.264 的硬编、硬解--ok
 * 9. 串联整个音视频录制流程，完成音视频的采集、编码、封包成 mp4 输出--ok
 * 10. 串联整个音视频播放流程，完成 mp4 的解析、音视频的解码、播放和渲染--ok
 * 11. 进一步学习 OpenGL，了解如何实现视频的剪裁、旋转、水印、滤镜，并学习 OpenGL 高级特性，如：VBO，VAO，FBO 等等--todo
 * 12. 学习 Android 图形图像架构，能够使用 GLSurfaceviw 绘制 Camera 预览画面--ok
 * 13. 深入研究音视频相关的网络协议，如 rtmp，hls，以及封包格式，如：flv，mp4--ok
 * 14. 深入学习一些音视频领域的开源项目，如 webrtc，ffmpeg，ijkplayer，librtmp 等等--ok
 * 15. 将 ffmpeg 库移植到 Android 平台，结合上面积累的经验，编写一款简易的音视频播放器--ok
 * 16. 将 x264 库移植到 Android 平台，结合上面积累的经验，完成视频数据 H264 软编功能--ok
 * 17. 将 librtmp 库移植到 Android 平台，结合上面积累的经验，完成 Android RTMP 推流功能--ok
 * 18. 上面积累的经验，做一款短视频 APP，完成如：断点拍摄、添加水印、本地转码、视频剪辑、视频拼接、MV 特效等功--ok
 * 相信我，如果你认真把所有任务都完成了，你一定会成为音视频人才
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        initView();
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
                    }, REQUEST_CODE);
        }
    }

    private void initView() {
        findViewById(R.id.btn_image_task).setOnClickListener(this);
        findViewById(R.id.btn_record_play_task).setOnClickListener(this);
        findViewById(R.id.btn_opensles_play_task).setOnClickListener(this);
        findViewById(R.id.btn_camera_preview_task).setOnClickListener(this);
        findViewById(R.id.btn_aac_hw_encoder_task).setOnClickListener(this);
        findViewById(R.id.btn_aac_ffmepg_encoder_task).setOnClickListener(this);
        findViewById(R.id.btn_libfdk_aac_encoder_task).setOnClickListener(this);
        findViewById(R.id.btn_task12).setOnClickListener(this);
        findViewById(R.id.btn_task17).setOnClickListener(this);
        findViewById(R.id.btn_task18).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_image_task: {
                intent = new Intent(this, ImageActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_record_play_task: {
                break;
            }
            case R.id.btn_opensles_play_task: {
                break;
            }
            case R.id.btn_camera_preview_task: {
                break;
            }
            case R.id.btn_aac_hw_encoder_task: {
                intent = new Intent(this, AudioHwEncoderActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_aac_ffmepg_encoder_task: {
                break;
            }
            case R.id.btn_libfdk_aac_encoder_task: {
                intent = new Intent(this, FdkAACActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_task12: {
                intent = new Intent(this, PreviewCameraActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_task17:
                intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_task18:
                intent = new Intent(this, CameraDemoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
