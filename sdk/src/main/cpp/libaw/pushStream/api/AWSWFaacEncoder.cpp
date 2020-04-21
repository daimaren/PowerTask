//
// Created by Administrator on 2018/3/8.
//

#include "AWSWFaacEncoder.h"
#include "AWEncoderManager.h"
#include "../common/aw_log.h"
/**
* 打开FaacEncoder
*/
void AWSWFaacEncoder::open(){
    if(pAudioConfig){
        aw_faac_config *faac_config = &pAudioConfig->faacConfig;
        aw_sw_encoder_open_faac_encoder(faac_config);
    } else{
        ALOGE("pAudioConfig null pointer");
    }
}
/**
 * 关闭FaacEncoder
 */
void AWSWFaacEncoder::close(){
    aw_sw_encoder_close_faac_encoder();
}
/**
 * PCM 数据编码为FLV 数据
 */
aw_flv_audio_tag *AWSWFaacEncoder::encodePCMDataToFlvTag(char *pcmData, int size){
    int timestamp = 0;
    if(pAudioConfig){
        timestamp = aw_sw_faac_encoder_max_input_sample_count() * 1000 / pAudioConfig->faacConfig.sample_rate;
        if(manager){
            manager->timestamp = timestamp;
        }else{
            ALOGE("manager null pointer");
        }
        return aw_sw_encoder_encode_faac_data((int8_t *)pcmData, size, timestamp);
    } else{
        ALOGE("pAudioConfig null pointer");
        return NULL;
    }
};
/**
 * 创建 audio specific config
 */
aw_flv_audio_tag *AWSWFaacEncoder::createAudioSpecificConfigFlvTag(){
    return aw_sw_encoder_create_faac_specific_config_tag();
}

