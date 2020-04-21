//
// Created by cain on 2018/12/26.
//

#include "AudioEncoder.h"

AudioEncoder::AudioEncoder(AVCodecContext *avctx, AVStream *stream, int streamIndex, PlayerState *playerState)
        : MediaEncoder(avctx, stream, streamIndex, playerState) {
    packet = av_packet_alloc();
}

AudioEncoder::~AudioEncoder() {
    mMutex.lock();
    if (packet) {
        av_packet_free(&packet);
        av_freep(&packet);
        packet = NULL;
    }
    mMutex.unlock();
}

int AudioEncoder::getAudioPacket(AVPacket *packet) {
    Frame *vp;
    int got_packet = 0;
    int ret = 0;

    if (!packet) {
        return AVERROR(ENOMEM);
    }
    av_packet_unref(packet);

    do {

        if (abortRequest) {
            ret = -1;
            break;
        }

        if (playerState->seekRequest) {
            continue;
        }

        // 1.从frameQueue里取出一帧
        //if (!(vp = frameQueue->currentFrame())) {
            //ret = -1;
            //break;
        //}

        playerState->mMutex.lock();
        // 2.送去编码
        ret = avcodec_send_frame(pCodecCtx, vp->frame);
        if (ret < 0) {
            playerState->mMutex.unlock();
            //frameQueue->popFrame();
            continue;
        }

        // 3.得到解码帧
        ret = avcodec_receive_packet(pCodecCtx, packet);
        playerState->mMutex.unlock();
        //frameQueue->popFrame();
        if (ret < 0) {
            av_packet_unref(packet);
            got_packet = 0;
            continue;
        } else {
            got_packet = 1;
        }

    } while (!got_packet);

    if (ret < 0) {
        return -1;
    }

    return got_packet;
}







