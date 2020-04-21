//
// Created by Administrator on 2018/3/8.
//

#include "AWAVCapture.h"

/**
 * 初始化
 */
bool AWAVCapture::open(const char *rtmpUrl, int width, int height, int bitrate,
    int sampleRate, int channel) {
    //1.check url
    if(!rtmpUrl || strlen(rtmpUrl) < 8){
        ALOGE("rtmpUrl error when start capture");
        return false;
    }
    //2.initVideoAudioConfig
    pVideoConfig = initVideoConfig(width, height, bitrate);
    pAudioConfig = initAudioConfig(sampleRate, channel);
    //3.check video config and audio config
    if(!pVideoConfig || !pAudioConfig){
        ALOGD("one of videoConfig and audioConfig must be NON-NULL");
        return false;
    }
    //3.initEncoderManager
    if(!pEncoderManager){
        pEncoderManager = initEncoderManager();
    }
    //5.open encoder
    if(pEncoderManager){
        pEncoderManager->audioEncoderType = AWAudioEncoderTypeSWFAAC;
        pEncoderManager->videoEncoderType = AWVideoEncoderTypeSWX264;
        pEncoderManager->openWithVideoAudioConfig(pAudioConfig, pVideoConfig);
    }else{
        ALOGE("pEncoderManager null pointer");
        return false;
    }
    //6.open rtmp
    int retCode = aw_streamer_open(rtmpUrl, aw_rtmp_state_changed_cb_in_oc);
    if(retCode){
        isCapturing = true;
        ALOGD("startCapture rtmpOpen success! retcode=%d", retCode);
        return true;
    } else{
        isCapturing = false;
        ALOGE("startCapture rtmpOpen error! retcode=%d", retCode);
        return false;
    }
}
/**
 * 关闭
 */
bool AWAVCapture::close() {
    if(pEncoderManager){
        pEncoderManager->close();
        delete pEncoderManager;
        pEncoderManager = NULL;
    }else{
        ALOGE("pEncoderManager null pointer");
    }

    if(pVideoConfig){
        delete pVideoConfig;
        pVideoConfig = NULL;
    } else{
        ALOGE("pVideoConfig null pointer");
    }

    if(pAudioConfig){
        delete pAudioConfig;
        pAudioConfig = NULL;
    } else{
        ALOGE("pAudioConfig null pointer");
    }
}
/**
 * initEncoderManager
 */
AWEncoderManager* AWAVCapture::initEncoderManager() {
    AWEncoderManager *awEncoderManager = NULL;
    awEncoderManager = new AWEncoderManager();
    if(awEncoderManager){
        awEncoderManager->audioEncoderType = audioEncoderType;
        awEncoderManager->videoEncoderType = videoEncoderType;
        return awEncoderManager;
    } else{
        ALOGE("awEncoderManager null pointer");
        return NULL;
    }
}
/**
 * initVideoConfig
 */
AWVideoConfig *AWAVCapture::initVideoConfig(int width, int height, int bitrate) {
    AWVideoConfig *awVideoConfig = new AWVideoConfig();
    if(awVideoConfig){
        awVideoConfig->x264Config.width = width;
        awVideoConfig->x264Config.height = height;
        awVideoConfig->x264Config.bitrate = bitrate;
        awVideoConfig->x264Config.fps = awVideoConfig->fps;
        awVideoConfig->x264Config.input_data_format = awVideoConfig->dataFormat;
        return awVideoConfig;
    } else{
        ALOGE("awVideoConfig null pointer");
        return NULL;
    }
}
/**
 * initAudioConfig
 */
AWAudioConfig *AWAVCapture::initAudioConfig(int sampleRate, int channel) {
    AWAudioConfig *awAudioConfig = new AWAudioConfig();
    if(awAudioConfig){
        awAudioConfig->faacConfig.sample_rate = sampleRate;
        awAudioConfig->faacConfig.channel_count = channel;
        awAudioConfig->faacConfig.bitrate = awAudioConfig->bitrate;
        awAudioConfig->faacConfig.sample_size = awAudioConfig->sampleSize;
        return awAudioConfig;
    }
    else{
        ALOGE("awVideoConfig null pointer");
        return NULL;
    }

}
/**
 * 发送视频数据
 */
void AWAVCapture::sendVideoYuvData(char *videoData, int size) {
    //是否需要做格式转换
    aw_flv_video_tag *video_tag = NULL;
    if(isCapturing){
        char *rotatedData = NULL;
        //rotatedData
        if(pEncoderManager && videoData && pEncoderManager->getVideoEncoder()){
            video_tag = pEncoderManager->getVideoEncoder()->encodeYUVDataToFlvTag(videoData, size);
            if(video_tag){
                sendFlvVideoTag(video_tag);
            }else{
                ALOGE("video_tag null pointer");
            }
        }else{
            ALOGE("sendVideoYuvData null pointer");
        }
    }else{
        ALOGE("isCapturing false");
    }
}
/**
 * 发送音频数据
 */
void AWAVCapture::sendAudioPcmData(char *audioData, int size) {
    if(!isCapturing){
        ALOGE("isCapturing false");
        return;
    }
    if(pEncoderManager && audioData && pEncoderManager->getAudioEncoder()){
        aw_flv_audio_tag *audio_tag = pEncoderManager->getAudioEncoder()->encodePCMDataToFlvTag(audioData, size);
        if(audio_tag){
            sendFlvAudioTag(audio_tag);
        }else{
            ALOGE("audio_tag null pointer");
        }
    }else{
        ALOGE("sendAudioPcmData null pointer");
    }
}
/**
 * 发送Flv VideoTag
 */
void AWAVCapture::sendFlvVideoTag(aw_flv_video_tag *video_tag) {
    if(!isCapturing){
        ALOGE("isCapturing false");
        free_aw_flv_video_tag(&video_tag);
        return;
    }
    if(!video_tag){
        ALOGE("video_tag null pointer");
        return;;
    }
    if(!isSpsPpsAndAudioSpecificConfigSent){
        sendSpsPpsAndAudioSpecificConfigTag();
        free_aw_flv_video_tag(&video_tag);
    } else{
        aw_streamer_send_video_data(video_tag);
    }
}
/**
 * 发送Flv AudioTag
 */
void AWAVCapture::sendFlvAudioTag(aw_flv_audio_tag *audio_tag) {
    if(!isCapturing){
        ALOGE("[AVCapture] not capture");
        free_aw_flv_audio_tag(&audio_tag);
        return;
    }
    if(!audio_tag){
        ALOGE("audio_tag null pointer");
        return;;
    }
    if(!isSpsPpsAndAudioSpecificConfigSent){
        sendSpsPpsAndAudioSpecificConfigTag();
        free_aw_flv_audio_tag(&audio_tag);
    } else{
        aw_streamer_send_audio_data(audio_tag);
    }
}
/**
 * 根据flv，h264，aac协议，提供首帧需要发送的tag
 */
void AWAVCapture::sendSpsPpsAndAudioSpecificConfigTag() {
    aw_flv_video_tag *spsPpsTag = NULL;
    aw_flv_audio_tag *audioSpecificConfigTag = NULL;
    if(!isCapturing && isSpsPpsAndAudioSpecificConfigSent){
        ALOGE("[AVCapture] already send sps pps or not capture");
        return;
    }
    //create video sps pps tag
    if(pEncoderManager && pEncoderManager->getVideoEncoder()){
        spsPpsTag = pEncoderManager->getVideoEncoder()->createSpsPpsFlvTag();
        if(spsPpsTag) {
            sendFlvVideoTag(spsPpsTag);
            ALOGD("send sps pps success");
        }else{
            ALOGE("spsPpsTag null pointer");
        }
    }
    else{
        ALOGE("pEncoderManager null pointer");
    }
    //audio specific config tag
    if(pEncoderManager && pEncoderManager->getAudioEncoder()){
        audioSpecificConfigTag = pEncoderManager->getAudioEncoder()->createAudioSpecificConfigFlvTag();
        if(audioSpecificConfigTag){
            sendFlvAudioTag(audioSpecificConfigTag);
            ALOGD("send audio Specific Tag success");
        }else{
            ALOGE("audioSpecificConfigTag null pointer");
        }
    }
    else{
        ALOGD("pEncoderManager NULL Pointer");
    }
    isSpsPpsAndAudioSpecificConfigSent = spsPpsTag || audioSpecificConfigTag;
    ALOGD("is sps pps and audio sepcific config sent=%d", isSpsPpsAndAudioSpecificConfigSent);
}
/**
 * RTMP状态变化回调函数
 */
void AWAVCapture::aw_rtmp_state_changed_cb_in_oc(aw_rtmp_state old_state,
                                                 aw_rtmp_state new_state) {
    ALOGD("[AVCapture] rtmp state changed from(%s), to(%s)", aw_rtmp_state_description(old_state), aw_rtmp_state_description(new_state));
}