package com.easing.commons.android.media_codec;

import android.hardware.Camera;

import com.easing.commons.android.code.Console;
import com.easing.commons.android.struct.Collections;

import java.util.List;

//摄像头管理类
@SuppressWarnings("all")
public class Cameras {

    //打卡正面相机
    public static Camera openFrontCamera() {
        try {
            int cameraCount = Camera.getNumberOfCameras();
            for (int i = 0; i < cameraCount; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) return Camera.open(i);
            }
        } catch (Exception e) {
            Console.error(e);
        }
        return null;
    }

    //打卡背面相机
    public static Camera openBackCamera() {
        try {
            int cameraCount = Camera.getNumberOfCameras();
            for (int i = 0; i < cameraCount; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) return Camera.open(i);
            }
        } catch (Exception e) {
            Console.error(e);
        }
        return null;
    }

    //获取摄像头支持的最小尺寸
    public static Camera.Size getMinSupportedVideoSize(Camera camera, int minSize) {
        List<Camera.Size> supportedVideoSizes = camera.getParameters().getSupportedVideoSizes();
        Collections.sort(supportedVideoSizes, (l, r) -> l.width * l.height - r.width * r.height);
        for (Camera.Size size : supportedVideoSizes)
            if (size.width * size.height >= minSize)
                return size;
        return null;
    }
}
