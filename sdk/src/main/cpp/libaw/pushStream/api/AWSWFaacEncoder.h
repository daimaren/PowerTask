//
// Created by Administrator on 2018/3/8.
//

#ifndef NATIVE_LIBRARY_AWSWFAACENCODER_H
#define NATIVE_LIBRARY_AWSWFAACENCODER_H

extern "C"{
#include "../encoder/aw_sw_faac_encoder.h"
};
#include "../base/AWAudioEncoder.h"

class AWSWFaacEncoder: public AWAudioEncoder{

public:
    /**
    * 开始
    */
    void open();
    /**
     * 结束
     */
    void close();
    /**
     * PCM 数据编码为FLV 数据
     */
    aw_flv_audio_tag *encodePCMDataToFlvTag(char *pcmData, int size);
    /**
     * 创建 audio specific config
     */
    aw_flv_audio_tag *createAudioSpecificConfigFlvTag();
};

#endif //NATIVE_LIBRARY_AWSWFAACENCODER_H
