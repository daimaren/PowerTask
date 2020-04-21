package com.mlingdu.cameralib.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mlingdu.cameralib.R;
import com.mlingdu.cameralib.engine.camera.CameraParam;
import com.mlingdu.cameralib.engine.listener.OnCameraCallback;
import com.mlingdu.cameralib.engine.listener.OnFpsListener;
import com.mlingdu.cameralib.engine.listener.OnRecordListener;
import com.mlingdu.cameralib.engine.recorder.PreviewRecorder;
import com.mlingdu.cameralib.engine.recorder.VideoCombiner;
import com.mlingdu.cameralib.engine.render.PreviewRenderer;
import com.mlingdu.cameralib.listener.OnPageOperationListener;
import com.mlingdu.cameralib.utils.BrightnessUtils;
import com.mlingdu.cameralib.utils.PathConstraints;
import com.mlingdu.cameralib.utils.PermissionUtils;
import com.mlingdu.cameralib.utils.StringUtils;
import com.mlingdu.cameralib.widget.AspectFrameLayout;
import com.mlingdu.cameralib.widget.HorizontalIndicatorView;
import com.mlingdu.cameralib.widget.PopupSettingView;
import com.mlingdu.cameralib.widget.RatioImageView;
import com.mlingdu.cameralib.widget.ShutterButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraPreviewFragment extends Fragment implements View.OnClickListener,
        HorizontalIndicatorView.OnIndicatorListener {

    private static final String TAG = "CameraPreviewFragment";
    private static final boolean VERBOSE = true;

    private static final String FRAGMENT_DIALOG = "dialog";

    // 对焦大小
    private static final int FocusSize = 100;

    // 相机权限使能标志
    private boolean mCameraEnable = false;
    // 存储权限使能标志
    private boolean mStorageWriteEnable = false;
    // 是否需要等待录制完成再跳转
    private boolean mNeedToWaitStop = false;
    // 显示贴纸页面
    private boolean isShowingStickers = false;
    // 显示滤镜页面
    private boolean isShowingFilters = false;
    // 当前索引
    private int mFilterIndex = 0;

    // 处于延时拍照状态
    private boolean mDelayTaking = false;

    // 预览参数
    private CameraParam mCameraParam;

    // Fragment主页面
    private View mContentView;
    // 预览部分
    private AspectFrameLayout mAspectLayout;
    private SurfaceView mCameraSurfaceView;
    // fps显示
    private TextView mFpsView;
    // 对比按钮
    private Button mBtnCompare;
    // 顶部Button
    private Button mBtnSetting;
    private Button mBtnViewPhoto;
    private Button mBtnSwitch;
    // 预览尺寸切换
    private RatioImageView mRatioView;
    // 设置的PopupView
    private PopupSettingView mSettingView;
    // 倒计时
    private TextView mCountDownView;
    // 贴纸按钮
    private Button mBtnStickers;
    // 快门按钮
    private ShutterButton mBtnShutter;
    // 滤镜按钮
    private Button mBtnEffect;
    // 视频删除按钮
    private Button mBtnRecordDelete;
    // 视频预览按钮
    private Button mBtnRecordPreview;
    // 相机类型指示器
    private HorizontalIndicatorView mBottomIndicator;
    // 相机类型指示文字
    private List<String> mIndicatorText = new ArrayList<String>();
    // 合并对话框
    private CombineVideoDialogFragment mCombineDialog;
    // 主线程Handler
    private Handler mMainHandler;
    // 持有该Fragment的Activity，onAttach/onDetach中绑定/解绑，主要用于解决getActivity() = null的情况
    private Activity mActivity;
    // 页面跳转监听器
    private OnPageOperationListener mPageListener;
    // 贴纸资源页面
    //todo private PreviewResourceFragment mResourcesFragment;
    // 滤镜页面
    //todo private PreviewEffectFragment mEffectFragment;

    public CameraPreviewFragment() {
        mCameraParam = CameraParam.getInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        int currentMode = BrightnessUtils.getSystemBrightnessMode(mActivity);
        if (currentMode == 1) {
            mCameraParam.brightness = -1;
        } else {
            mCameraParam.brightness = BrightnessUtils.getSystemBrightness(mActivity);
        }
        mMainHandler = new Handler(context.getMainLooper());
        mCameraEnable = PermissionUtils.permissionChecking(mActivity, Manifest.permission.CAMERA);
        mStorageWriteEnable = PermissionUtils.permissionChecking(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        mCameraParam.audioPermitted = PermissionUtils.permissionChecking(mActivity, Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreviewRenderer.getInstance()
                .setCameraCallback(mCameraCallback)
                .setCaptureFrameCallback(null)
                .setFpsCallback(mFpsListener)
                .initRenderer(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_camera_preview, container, false);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mCameraEnable) {
            initView(mContentView);
        } else {
            requestCameraPermission();
        }
    }

    /**
     * 初始化页面
     * @param view
     */
    private void initView(View view) {
        mAspectLayout = (AspectFrameLayout) view.findViewById(R.id.layout_aspect);
        mAspectLayout.setAspectRatio(mCameraParam.currentRatio);
        mCameraSurfaceView = new SurfaceView(mActivity);
        mAspectLayout.addView(mCameraSurfaceView);
        mAspectLayout.requestLayout();
        // 绑定需要渲染的SurfaceView
        PreviewRenderer.getInstance().setSurfaceView(mCameraSurfaceView);

        mFpsView = (TextView) view.findViewById(R.id.tv_fps);
        mBtnCompare = (Button) view.findViewById(R.id.btn_compare);
        mBtnCompare.setVisibility(View.GONE);
        mBtnCompare.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //PreviewRenderer.getInstance().enableCompare(true);
                        mBtnCompare.setBackgroundResource(R.drawable.ic_camera_compare_pressed);
                        break;

                    default:
                        //PreviewRenderer.getInstance().enableCompare(false);
                        mBtnCompare.setBackgroundResource(R.drawable.ic_camera_compare_normal);
                        break;
                }
                return true;
            }
        });
        mBtnSetting = (Button)view.findViewById(R.id.btn_setting);
        mBtnSetting.setOnClickListener(this);
        mBtnViewPhoto = (Button) view.findViewById(R.id.btn_view_photo);
        mBtnViewPhoto.setOnClickListener(this);
        mBtnSwitch = (Button) view.findViewById(R.id.btn_switch);
        mBtnSwitch.setOnClickListener(this);
        mRatioView = (RatioImageView) view.findViewById(R.id.iv_ratio);
        mRatioView.setRatioType(mCameraParam.aspectRatio);
        //mRatioView.addRatioChangedListener(mRatioChangedListener);

        mCountDownView = (TextView) view.findViewById(R.id.tv_countdown);
        mBtnStickers = (Button) view.findViewById(R.id.btn_stickers);
        mBtnStickers.setOnClickListener(this);
        mBtnEffect = (Button) view.findViewById(R.id.btn_effects);
        mBtnEffect.setOnClickListener(this);
        //mBottomIndicator = (HorizontalIndicatorView) view.findViewById(R.id.bottom_indicator);
        //String[] galleryIndicator = getResources().getStringArray(R.array.gallery_indicator);
        //mIndicatorText.addAll(Arrays.asList(galleryIndicator));
        //mBottomIndicator.setIndicators(mIndicatorText);
        //mBottomIndicator.addIndicatorListener(this);

        mBtnShutter = (ShutterButton) view.findViewById(R.id.btn_shutter);
        mBtnShutter.setOnShutterListener(mShutterListener);
        mBtnShutter.setOnClickListener(this);

        mBtnRecordDelete = (Button) view.findViewById(R.id.btn_record_delete);
        mBtnRecordDelete.setOnClickListener(this);
        mBtnRecordPreview = (Button) view.findViewById(R.id.btn_record_preview);
        mBtnRecordPreview.setOnClickListener(this);

        adjustBottomView();
    }

    private void adjustBottomView() {
        boolean result = mCameraParam.currentRatio < CameraParam.Ratio_4_3;
        mBtnStickers.setBackgroundResource(result ? R.drawable.ic_camera_sticker_light : R.drawable.ic_camera_sticker_dark);
        mBtnEffect.setBackgroundResource(result ? R.drawable.ic_camera_effect_light : R.drawable.ic_camera_effect_dark);
        mBtnRecordDelete.setBackgroundResource(result ? R.drawable.ic_camera_record_delete_light : R.drawable.ic_camera_record_delete_dark);
        mBtnRecordPreview.setBackgroundResource(result ? R.drawable.ic_camera_record_done_light : R.drawable.ic_camera_record_done_dark);
        mBtnShutter.setOuterBackgroundColor(result ? R.color.shutter_gray_light : R.color.shutter_gray_dark);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBtnShutter.setEnableOpened(true);
        mBtnShutter.setIsRecorder(true);
    }

    private void enhancementBrightness() {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContentView = null;
    }

    @Override
    public void onDestroy() {
        PreviewRenderer.getInstance().destroyRenderer();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onIndicatorChanged(int currentIndex) {

    }

    private void openGallery() {

    }

    private void switchCamera() {

    }

    private void showSettingPopView() {

    }

    private void showStickers() {

    }

    private void showEffectView() {

    }

    private void hideStickerView() {

    }

    private void hideEffectView() {

    }

    private void hideBottomLayout() {

    }

    private void resetBottomLayout() {

    }

    private void takePicture() {

    }

    private OnCameraCallback mCameraCallback = new OnCameraCallback() {
        @Override
        public void onCameraOpened() {

        }

        @Override
        public void onPreviewCallback(byte[] data) {
            if (mBtnShutter != null && !mBtnShutter.isEnableOpened()) {
                mBtnShutter.setEnableOpened(true);
            }
            requestRender();
        }
    };

    /**
     * 请求渲染
     */
    private void requestRender() {
        PreviewRenderer.getInstance().requestRender();
    }


    // -------------------------------------- fps回调 -------------------------------------------
    private OnFpsListener mFpsListener = new OnFpsListener() {
        @Override
        public void onFpsCallback(final float fps) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mCameraParam.showFps) {
                        mFpsView.setText("fps = " + fps);
                        mFpsView.setVisibility(View.VISIBLE);
                    } else {
                        mFpsView.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    // ------------------------------------ 录制回调 -------------------------------------------
    private ShutterButton.OnShutterListener mShutterListener = new ShutterButton.OnShutterListener() {

        @Override
        public void onStartRecord() {

            mBtnShutter.setProgressMax((int) PreviewRecorder.getInstance().getMaxMilliSeconds());
            // 添加分割线
            mBtnShutter.addSplitView();

            // 是否允许录制音频
            boolean enableAudio = true;

            // 计算输入纹理的大小
            int width = mCameraParam.previewWidth;
            int height = mCameraParam.previewHeight;
            if (mCameraParam.orientation == 90 || mCameraParam.orientation == 270) {
                width = mCameraParam.previewHeight;
                height = mCameraParam.previewWidth;
            }
            // 开始录制
            PreviewRecorder.getInstance()
                    .setRecordType(PreviewRecorder.RecordType.Video)
                    .setOutputPath(PathConstraints.getVideoCachePath(mActivity))
                    .enableAudio(enableAudio)
                    .setRecordSize(width, height)
                    .setOnRecordListener(mRecordListener)
                    .startRecord();
        }

        @Override
        public void onStopRecord() {
            PreviewRecorder.getInstance().stopRecord();
        }

        @Override
        public void onProgressOver() {
            // 如果最后一秒内点击停止录制，则仅仅关闭录制按钮，因为前面已经停止过了，不做跳转
            // 如果最后一秒内没有停止录制，否则停止录制并跳转至预览页面
            if (PreviewRecorder.getInstance().isLastSecondStop()) {
                // 关闭录制按钮
                mBtnShutter.closeButton();
            } else {
                stopRecordOrPreviewVideo();
            }
        }
    };

    /**
     * 录制监听器
     */
    private OnRecordListener mRecordListener = new OnRecordListener() {

        @Override
        public void onRecordStarted() {
            // 编码器已经进入录制状态，则快门按钮可用
            mBtnShutter.setEnableEncoder(true);
        }

        @Override
        public void onRecordProgressChanged(final long duration) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // 设置进度
                    mBtnShutter.setProgress(duration);
                    // 设置时间
                    mCountDownView.setText(StringUtils.generateMillisTime((int) duration));
                }
            });
        }

        @Override
        public void onRecordFinish() {
            // 编码器已经完全释放，则快门按钮可用
            mBtnShutter.setEnableEncoder(true);
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // 处于录制状态点击了预览按钮，则需要等待完成再跳转， 或者是处于录制GIF状态
                    if (true) {
                        // 开始预览
                        stopRecordOrPreviewVideo();
                    }
                    mBtnRecordPreview.setVisibility(View.VISIBLE);
                    mBtnRecordDelete.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    /**
     * 停止录制或者预览视频
     */
    private void stopRecordOrPreviewVideo() {
        if (PreviewRecorder.getInstance().isRecording()) {
            mNeedToWaitStop = true;
            PreviewRecorder.getInstance().stopRecord(false);
        } else {
            mNeedToWaitStop = false;
            // 销毁录制线程
            PreviewRecorder.getInstance().destroyRecorder();
            combinePath = PathConstraints.getVideoCachePath(mActivity);
            PreviewRecorder.getInstance().combineVideo(combinePath, mCombineListener);
        }
    }


    // -------------------------------------- 短视频合成监听器 ---------------------------------
    // 合成输出路径
    private String combinePath;
    // 合成监听器
    private VideoCombiner.CombineListener mCombineListener = new VideoCombiner.CombineListener() {
        @Override
        public void onCombineStart() {
            if (VERBOSE) {
                Log.d(TAG, "开始合并");
            }
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mCombineDialog != null) {
                        mCombineDialog.dismiss();
                        mCombineDialog = null;
                    }
                    mCombineDialog = CombineVideoDialogFragment.newInstance(mActivity.getString(R.string.combine_video_message));
                    mCombineDialog.show(getChildFragmentManager(), FRAGMENT_DIALOG);
                }
            });
        }

        @Override
        public void onCombineProcessing(final int current, final int sum) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mCombineDialog != null && mCombineDialog.getShowsDialog()) {
                        mCombineDialog.setProgressMessage(mActivity.getString(R.string.combine_video_message));
                    }
                }
            });
        }

        @Override
        public void onCombineFinished(final boolean success) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mCombineDialog != null) {
                        mCombineDialog.dismiss();
                        mCombineDialog = null;
                    }
                }
            });
            if (mPageListener != null)  {
                mPageListener.onOpenVideoEditPage(combinePath);
            }
        }
    };

    /**
     * 请求相机权限
     */
    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            PermissionConfirmDialogFragment.newInstance(getString(R.string.request_camera_permission), PermissionUtils.REQUEST_CAMERA_PERMISSION, true)
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{ Manifest.permission.CAMERA},
                    PermissionUtils.REQUEST_CAMERA_PERMISSION);
        }
    }
}
