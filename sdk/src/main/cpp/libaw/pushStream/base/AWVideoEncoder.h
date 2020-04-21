//
// Created by Administrator on 2018/3/8.
//

#ifndef NATIVE_LIBRARY_AWVIDEOENCODER_H
#define NATIVE_LIBRARY_AWVIDEOENCODER_H

extern "C"{
#include "../flv/aw_encode_flv.h"
};

#include "../api/AWAVConfig.h"
#include "AWEncoder.h"
class AWVideoEncoder: public AWEncoder{
public:
    AWVideoConfig *pVideoConfig = NULL;
public:
    virtual ~AWVideoEncoder(){};
    /**
    * 开始
    */
    virtual void open(){}
    /**
     * 结束
     */
    virtual void close(){}
    /**
     * 旋转
     */
    char *rotateNV12Data(char *nv12Data);
    /**
     * 编码
     */
    virtual aw_flv_video_tag *encodeYUVDataToFlvTag(char *yuvData, int size){
        return NULL;
    };
    /**
     * 根据flv，h264，aac协议，提供首帧需要发送的tag
     * 创建sps pps
     */
    virtual aw_flv_video_tag *createSpsPpsFlvTag(){
        return NULL;
    };
};

#endif //NATIVE_LIBRARY_AWVIDEOENCODER_H
