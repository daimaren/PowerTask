//
// Created by Administrator on 2018/3/8.
//

#include "AWVideoEncoder.h"
#include "../common/aw_log.h"
/**
 * 旋转
 */
char* AWVideoEncoder::rotateNV12Data(char *nv12Data) {
    if(nv12Data){

    }else{
        ALOGD("nv12Data null pointer");
    }
    //使用libyuv前，yuv数据已经是有padding的数据了（没有padding会计算错误）。
    //因此旋转的时候，需要根据具体情况调整 stride。
    //旋转前 width 有padding，旋转后 width变成了高应该去掉padding，否则会有绿边。
    //旋转后 height 有padding，需要设置正确的stride增加padding。
    return NULL;
}