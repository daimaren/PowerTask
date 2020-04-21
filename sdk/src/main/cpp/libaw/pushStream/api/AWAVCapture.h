//
// Created by Administrator on 2018/3/8.
//

#ifndef NATIVE_LIBRARY_AWAVCAPTURE_H
#define NATIVE_LIBRARY_AWAVCAPTURE_H

extern "C"{
#include "../flv/aw_encode_flv.h"
#include "../common/aw_log.h"
#include "../rtmp/aw_streamer.h"
#include "../common/aw_rtmp.h"
};
/**
 * 编码管理
 */
#include "AWEncoderManager.h"

class AWAVCapture {
private:
    /**
     * 配置
     */
    AWAudioConfig *pAudioConfig;
    AWVideoConfig *pVideoConfig;
    /**
     * 编码器类型
     */
    AWAudioEncoderType audioEncoderType;
    AWVideoEncoderType videoEncoderType;
    /**
     * 是否将数据发送出去
     */
    bool isCapturing;
    /**
     * 编码器管理
     */
    AWEncoderManager *pEncoderManager;
    bool isSpsPpsAndAudioSpecificConfigSent = false;
public:
    AWEncoderManager *initEncoderManager();
    bool open(const char *rtmpUrl, int width, int height, int bitrate,
        int sampleRate, int channel);
    bool close();
    AWVideoConfig *initVideoConfig(int width, int height, int bitrate);
    AWAudioConfig *initAudioConfig(int sampleRate, int channel);
    void sendVideoYuvData(char *videoData, int size);
    void sendAudioPcmData(char *audioData, int size);
    void sendFlvVideoTag(aw_flv_video_tag *flvVideoTag);
    void sendFlvAudioTag(aw_flv_audio_tag *);
    void sendSpsPpsAndAudioSpecificConfigTag();
    static void aw_rtmp_state_changed_cb_in_oc(aw_rtmp_state old_state, aw_rtmp_state new_state);
};

#endif //NATIVE_LIBRARY_AWAVCAPTURE_H
