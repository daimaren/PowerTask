//
// Created by Administrator on 2018/3/8.
//
#include <jni.h>
#include <string>

extern "C"{
#include "common/aw_log.h"

#include "pushStream/api/AWAVCapture.h"
AWAVCapture *pAVCapture;

/**
 * 打开
 */
JNIEXPORT jboolean JNICALL Java_com_mlingdu_media_common_jni_AVStreamPush_open(JNIEnv *env, jobject thiz, jstring url,
    jint width, jint height, jint bitrate, jint sampleRate, jint channel){
    ALOGD("打开");
    if(!pAVCapture){
        pAVCapture = new AWAVCapture();
        if(!pAVCapture){
            ALOGD("pAVCapture null pointer");
            return JNI_FALSE;
        }
    }
    const char *rtmp_url = env->GetStringUTFChars(url,0);
    if(pAVCapture){
        pAVCapture->open(rtmp_url, width, height, bitrate, sampleRate, channel);
    } else{
        ALOGD("pAVCapture null pointer");
        return JNI_FALSE;
    }
    env->ReleaseStringUTFChars(url,rtmp_url);
    return JNI_TRUE;
}
/**
 * 关闭
 */
JNIEXPORT jboolean JNICALL Java_com_mlingdu_media_common_jni_AVStreamPush_close(JNIEnv *env, jobject thiz){
    ALOGD("关闭");
    if(pAVCapture){
        pAVCapture->close();
    } else{
        ALOGD("pAVCapture null pointer");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}
/**
 * 发送音频
 */
JNIEXPORT jboolean JNICALL Java_com_mlingdu_media_common_jni_AVStreamPush_pushVideo(JNIEnv *env,jobject thiz,jbyteArray data,jint size){
    char* videoData = (char *) env->GetByteArrayElements(data, 0);
    if(pAVCapture){
        pAVCapture->sendVideoYuvData(videoData, size);
    }else{
        ALOGD("pAVCapture null pointer");
        return JNI_FALSE;
    }
    env->ReleaseByteArrayElements(data, (jbyte *) videoData, 0);
    return JNI_TRUE;
}
/**
 * 发送视频
 */
JNIEXPORT jboolean JNICALL Java_com_mlingdu_media_common_jni_AVStreamPush_pushAudio(JNIEnv *env,jobject thiz,jbyteArray data,jint size){
    char* audioData = (char *) env->GetByteArrayElements(data, 0);
    if(pAVCapture){
        pAVCapture->sendAudioPcmData(audioData, size);
    }else{
        ALOGD("pAVCapture null pointer");
        return JNI_FALSE;
    }
    env->ReleaseByteArrayElements(data, (jbyte *) audioData, 0);
    return JNI_TRUE;
}
}