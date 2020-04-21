//
// Created by Administrator on 2018/3/8.
//

#include "AWSWX264Encoder.h"
#include "AWEncoderManager.h"
#include "../common/aw_log.h"
/**
 * 旋转
 */
char* AWSWX264Encoder::rotateNV12Data(char *nv12Data) {

    //使用libyuv前，yuv数据已经是有padding的数据了（没有padding会计算错误）。
    //因此旋转的时候，需要根据具体情况调整 stride。
    //旋转前 width 有padding，旋转后 width变成了高应该去掉padding，否则会有绿边。
    //旋转后 height 有padding，需要设置正确的stride增加padding。
    return NULL;
}
/**
 * 编码
 */
aw_flv_video_tag* AWSWX264Encoder::encodeYUVDataToFlvTag(char *yuvData, int size) {
    if(manager && yuvData){
        return aw_sw_encoder_encode_x264_data((int8_t *)yuvData, size, manager->timestamp);
    } else{
        ALOGE("manager or yuvData null pointer");
    }
}
/**
 * 根据flv，h264，aac协议，提供首帧需要发送的tag
 * 创建sps pps
 */
aw_flv_video_tag* AWSWX264Encoder::createSpsPpsFlvTag() {
    return aw_sw_encoder_create_x264_sps_pps_tag();
}
/**
 * 打开编码器
 */
void AWSWX264Encoder::open() {
    aw_x264_config *x264_config = NULL;
    if(pVideoConfig){
        x264_config = &pVideoConfig->x264Config;
        aw_sw_encoder_open_x264_encoder(x264_config);
    } else{
        ALOGE("pVideoConfig null pointer");
    }
}
/**
 * 关闭编码器
 */
void AWSWX264Encoder::close() {
    aw_sw_encoder_close_x264_encoder();
}