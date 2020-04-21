//
// Created by Administrator on 2018/3/8.
//

#include "AWAVConfig.h"
#include "../common/aw_log.h"
/**
 *
 */
AWAudioConfig::AWAudioConfig() {
    bitrate = 100000;
    channelCount = 1;
    sampleSize = 16;
    sampleRate = 44100;
}

aw_faac_config *AWAudioConfig::getAwFaacConfig() {
    aw_faac_config *faac_config = new aw_faac_config();
    if(faac_config){
        faac_config->bitrate = (int32_t)bitrate;
        faac_config->channel_count = (int32_t)channelCount;
        faac_config->sample_rate = (int32_t)sampleRate;
        faac_config->sample_size = (int32_t)sampleSize;
        return faac_config;
    }else{
        ALOGE("faac_config null pointer");
        return NULL;
    }

}
/**
 *
 */
AWAudioConfig* AWAudioConfig::copyConfig() {
    AWAudioConfig *audioConfig = new AWAudioConfig;
    if(audioConfig){
        audioConfig->bitrate = bitrate;
        audioConfig->channelCount = channelCount;
        audioConfig->sampleRate = sampleRate;
        audioConfig->sampleSize = sampleSize;
        return audioConfig;
    }
    else{
        ALOGE("audioConfig null pointer");
        return NULL;
    }
}
/**
 *
 */
AWVideoConfig::AWVideoConfig() {
    width = 540;
    height = 960;
    bitrate = 1000000;
    fps = 20;
    dataFormat = X264_CSP_NV12;
}
/**
 *
 */
aw_x264_config *AWVideoConfig::getAwX264Config() {
    aw_x264_config *pX264_config = alloc_aw_x264_config();
    if(pX264_config){
        pX264_config->width = (int32_t)pushStreamWidth;
        pX264_config->height = (int32_t)pushStreamHeight;
        pX264_config->bitrate = (int32_t)bitrate;
        pX264_config->fps = (int32_t)fps;
        pX264_config->input_data_format = (int32_t)dataFormat;
        return pX264_config;
    }else{
        ALOGE("pX264_config null pointer");
        return NULL;
    };
}
/**
 *
 */
AWVideoConfig* AWVideoConfig::copyConfig() {
    AWVideoConfig *videoConfig = new AWVideoConfig();
    if(videoConfig){
        videoConfig->bitrate = bitrate;
        videoConfig->fps = fps;
        videoConfig->dataFormat = dataFormat;
        videoConfig->orientation = orientation;
        videoConfig->width = width;
        videoConfig->height = height;
        return videoConfig;
    }
    else{
        ALOGE("videoConfig null pointer");
        return NULL;
    }
}