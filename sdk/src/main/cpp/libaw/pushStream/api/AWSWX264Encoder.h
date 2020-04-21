//
// Created by Administrator on 2018/3/8.
//

#ifndef NATIVE_LIBRARY_AWSWX264ENCODER_H
#define NATIVE_LIBRARY_AWSWX264ENCODER_H

extern "C"{
#include "../encoder/aw_sw_x264_encoder.h"
};
#include "../base/AWVideoEncoder.h"

class AWSWX264Encoder: public AWVideoEncoder{

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
     * 旋转
     */
    /**
     * 旋转
     */
    char *rotateNV12Data(char *nv12Data);
    /**
     * 编码
     */
    aw_flv_video_tag *encodeYUVDataToFlvTag(char *yuvData, int size);
    /**
     * 根据flv，h264，aac协议，提供首帧需要发送的tag
     * 创建sps pps
     */
    aw_flv_video_tag *createSpsPpsFlvTag();
};

#endif //NATIVE_LIBRARY_AWSWX264ENCODER_H
