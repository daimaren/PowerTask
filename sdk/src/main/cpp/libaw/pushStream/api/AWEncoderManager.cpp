//
// Created by Administrator on 2018/3/8.
//

#include "AWEncoderManager.h"
#include "../common/aw_log.h"
AWEncoderManager::AWEncoderManager() {
}

AWEncoderManager::~AWEncoderManager() {
}

/**
 * 获取VideoEncoder
 */
AWVideoEncoder *AWEncoderManager::getVideoEncoder(){
    return mVideoEncoder;
}
/**
 * 获取AudioEncoder
 */
AWAudioEncoder *AWEncoderManager::getAudioEncoder(){
    return mAudioEncoder;
}
/**
 * openWithVideoAudioConfig
 */
void AWEncoderManager::openWithVideoAudioConfig(AWAudioConfig *audioConfig,
                                           AWVideoConfig *videoConfig) {
    ALOGD("openWithVideoAudioConfig");
    switch (audioEncoderType){
        case AWAudioEncoderTypeHWAACLC:
            break;
        case AWAudioEncoderTypeSWFAAC:
            mAudioEncoder = new AWSWFaacEncoder();
            break;
        default:
            ALOGE("please assin for audioEncoderType");
    }

    switch (videoEncoderType){
        case AWVideoEncoderTypeHWH264:
            break;
        case AWVideoEncoderTypeSWX264:
            mVideoEncoder = new AWSWX264Encoder();
            break;
        default:
            ALOGD("please assin for videoEncoderType");
    }
    if(mAudioEncoder){
        mAudioEncoder->manager = this;
        mAudioEncoder->pAudioConfig = audioConfig;
        mAudioEncoder->open();
    }
    else{
        ALOGE("mAudioEncoder null pointer");
    }
    if(mVideoEncoder){
        mVideoEncoder->manager = this;
        mVideoEncoder->pVideoConfig = videoConfig;
        mVideoEncoder->open();
    }
    else{
        ALOGE("mVideoEncoder null pointer");
    }
}
/**
 * close
 */
void AWEncoderManager::close() {
    ALOGD("AWEncoderManager close");
    if(mVideoEncoder && mAudioEncoder){
        mAudioEncoder->close();
        mVideoEncoder->close();
        delete mAudioEncoder;
        mAudioEncoder = NULL;
        delete mVideoEncoder;
        mVideoEncoder = NULL;
    } else{
        ALOGE("mVideoEncoder or mAudioEncoder null pointer");
    }
    timestamp = 0;
    audioEncoderType = AWAudioEncoderTypeNone;
    videoEncoderType = AWVideoEncoderTypeNone;
}


