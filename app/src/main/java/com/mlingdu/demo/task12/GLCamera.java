package com.mlingdu.demo.task12;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GLCamera implements ICamera{

    private Config mConfig;
    private Camera mCamera;
    private CameraSizeComparator sizeComparator;
    private Point mPreSize;
    private Camera.Size picSize;
    private Camera.Size preSize;

    public GLCamera() {
        this.mConfig = new Config();
        mConfig.minPreviewWidth = 720;
        mConfig.minPictureWidth = 720;
        mConfig.rate = 1.778f;
        sizeComparator = new CameraSizeComparator();
    }

    @Override
    public boolean open(int cameraId) {
        mCamera = Camera.open();
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            picSize = getPropPictureSize(parameters.getSupportedPictureSizes(), mConfig.rate, mConfig.minPictureWidth);
            preSize = getPropPreviewSize(parameters.getSupportedPreviewSizes(), mConfig.rate, mConfig.minPreviewWidth);
            parameters.setPictureSize(picSize.width, picSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
            mCamera.setParameters(parameters);
            Camera.Size pre = parameters.getPreviewSize();
            mPreSize = new Point(pre.height, pre.width);
            return true;
        }
        return false;
    }

    @Override
    public boolean preview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
        return true;
    }

    @Override
    public boolean close() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Point getPreviewSize() {
        return null;
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.height == rhs.height) {
                return 0;
            } else if (lhs.height > rhs.height) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }
}
