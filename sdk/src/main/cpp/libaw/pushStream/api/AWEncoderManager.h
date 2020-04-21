//
// Created by Administrator on 2018/3/8.
//
#ifndef NATIVE_LIBRARY_AWENCODERMANAGER_H
#define NATIVE_LIBRARY_AWENCODERMANAGER_H

#include "../base/AWAudioEncoder.h"
#include "../base/AWVideoEncoder.h"
#include  "AWSWFaacEncoder.h"
#include  "AWSWX264Encoder.h"
#include "AWAVConfig.h"

typedef enum{
    AWVideoEncoderTypeNone,
    AWVideoEncoderTypeHWH264,
    AWVideoEncoderTypeSWX264,
} AWVideoEncoderType;

typedef enum{
    AWAudioEncoderTypeNone,
    AWAudioEncoderTypeHWAACLC,
    AWAudioEncoderTypeSWFAAC,
} AWAudioEncoderType;

class AWVideoEncoder;
class AWAudioEncoder;
class AWEncoderManager {
private:
    /**
     * 编码器
     */
    AWVideoEncoder *mVideoEncoder;
    AWAudioEncoder *mAudioEncoder;
public:
    /**
    * 编码器类型
    */
    AWAudioEncoderType audioEncoderType;
    AWVideoEncoderType videoEncoderType;
    /**
    * 时间戳
    */
    uint32_t timestamp;
public:
    AWEncoderManager();
    ~AWEncoderManager();
    /**
     * 获取VideoEncoder
     */
    AWVideoEncoder *getVideoEncoder();
    /**
     * 获取AudioEncoder
     */
    AWAudioEncoder *getAudioEncoder();
    /**
     * 初始化音视频编码器
     */
    void openWithVideoAudioConfig(AWAudioConfig * audioConfig, AWVideoConfig * videoConfig);
    /**
     * 关闭
     */
    void close();
};

#endif //NATIVE_LIBRARY_AWENCODERMANAGER_H
