//
// Created by Administrator on 2018/3/8.
//

#ifndef NATIVE_LIBRARY_AWAUDIOENCODER_H
#define NATIVE_LIBRARY_AWAUDIOENCODER_H

extern "C"{
#include "../flv/aw_encode_flv.h"
};

#include "../api/AWAVConfig.h"
#include "AWEncoder.h"

class AWAudioEncoder: public AWEncoder{
public:
    AWAudioConfig *pAudioConfig = NULL;
public:
    virtual ~AWAudioEncoder(){};
    /**
     * 开始
     */
    virtual void open(){

    }
    /**
     * 结束
     */
    virtual void close(){

    }
    /**
     * PCM 数据编码为FLV 数据
     */
    virtual aw_flv_audio_tag *encodePCMDataToFlvTag(char *pcmData, int size){
        return NULL;
    };
    /**
     * 创建 audio specific config
     */
    virtual aw_flv_audio_tag *createAudioSpecificConfigFlvTag(){
        return NULL;
    }
};

#endif //NATIVE_LIBRARY_AWAUDIOENCODER_H
