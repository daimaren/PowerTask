//
// Created by Administrator on 2018/3/8.
//

#ifndef NATIVE_LIBRARY_AWAUDIOCONFIG_H
#define NATIVE_LIBRARY_AWAUDIOCONFIG_H

extern "C"{
#include "../encoder/aw_faac.h"
#include "../encoder/aw_x264.h"
};
class AWAudioConfig {
public:
    /**
     * 可自由设置
     */
    int bitrate;
    /**
     * 可选 1 2
     */
    int channelCount;
    /**
     * 可选 44100 22050 11025 5500
     */
    int sampleRate;
    /**
     * 可选 16 8
     */
    int sampleSize;
    /**
     * config
     */
    aw_faac_config faacConfig;
public:
    AWAudioConfig();
    aw_faac_config *getAwFaacConfig();
    AWAudioConfig *copyConfig();
};
class AWVideoConfig {
public:
    /**
     * 可选，系统支持的分辨率，采集分辨率的宽
     */
    int width;
    /**
     * 可选，系统支持的分辨率，采集分辨率的高
     */
    int height;
    /**
     * 自由设置
     */
    int bitrate;
    /**
     * 自由设置
     */
    int fps;
    /**
     * 目前软编码只能是X264_CSP_NV12，硬编码无需设置
     */
    int dataFormat;
    int orientation;
    bool shouldRotate;
    /**
     * 推流分辨率宽高，目前不支持自由设置，只支持旋转。
     */
    int pushStreamWidth;
    int pushStreamHeight;
    /**
     * config
     */
    aw_x264_config x264Config;
public:
    AWVideoConfig();
    aw_x264_config *getAwX264Config();
    AWVideoConfig *copyConfig();
};

#endif //NATIVE_LIBRARY_AWAUDIOCONFIG_H
