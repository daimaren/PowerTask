//
// Created by cain on 2018/12/26.
//

#include "VideoEncoder.h"

VideoEncoder::VideoEncoder(AVFormatContext *pFormatCtx, AVCodecContext *avctx,
                           AVStream *stream, int streamIndex, PlayerState *playerState)
        : MediaEncoder(avctx, stream, streamIndex, playerState) {
    this->pFormatCtx = pFormatCtx;
    frameQueue = new FrameQueue(VIDEO_QUEUE_SIZE, 1);
    mExit = true;
    encodeThread = NULL;
    masterClock = NULL;
    // 旋转角度
    AVDictionaryEntry *entry = av_dict_get(stream->metadata, "rotate", NULL, AV_DICT_MATCH_CASE);
    if (entry && entry->value) {
        mRotate = atoi(entry->value);
    } else {
        mRotate = 0;
    }
}

VideoEncoder::~VideoEncoder() {
    mMutex.lock();
    pFormatCtx = NULL;
    if (frameQueue) {
        frameQueue->flush();
        delete frameQueue;
        frameQueue = NULL;
    }
    masterClock = NULL;
    mMutex.unlock();
}

void VideoEncoder::setMasterClock(MediaClock *masterClock) {
    Mutex::Autolock lock(mMutex);
    this->masterClock = masterClock;
}

void VideoEncoder::start() {
    MediaEncoder::start();
    if (frameQueue) {
        frameQueue->start();
    }
    if (!encodeThread) {
        encodeThread = new Thread(this);
        encodeThread->start();
        mExit = false;
    }
}

void VideoEncoder::stop() {
    MediaEncoder::stop();
    if (frameQueue) {
        frameQueue->abort();
    }
    mMutex.lock();
    while (!mExit) {
        mCondition.wait(mMutex);
    }
    mMutex.unlock();
    if (encodeThread) {
        encodeThread->join();
        delete encodeThread;
        encodeThread = NULL;
    }
}

void VideoEncoder::flush() {
    mMutex.lock();
    MediaEncoder::flush();
    if (frameQueue) {
        frameQueue->flush();
    }
    mCondition.signal();
    mMutex.unlock();
}

int VideoEncoder::getFrameSize() {
    Mutex::Autolock lock(mMutex);
    return frameQueue ? frameQueue->getFrameSize() : 0;
}

int VideoEncoder::getRotate() {
    Mutex::Autolock lock(mMutex);
    return mRotate;
}

FrameQueue *VideoEncoder::getFrameQueue() {
    Mutex::Autolock lock(mMutex);
    return frameQueue;
}

AVFormatContext *VideoEncoder::getFormatContext() {
    Mutex::Autolock lock(mMutex);
    return pFormatCtx;
}

void VideoEncoder::run() {
    encodeVideo();
}

/**
 * 编码视频并放入packet队列
 * @return
 */
int VideoEncoder::encodeVideo() {
    Frame *vp;
    int got_packet;
    int ret = 0;

    AVRational tb = pStream->time_base;

    AVPacket *packet = av_packet_alloc();
    if (!packet) {
        mExit = true;
        mCondition.signal();
        return AVERROR(ENOMEM);
    }

    for (;;) {

        if (abortRequest || playerState->abortRequest) {
            ret = -1;
            break;
        }

        if (playerState->seekRequest) {
            continue;
        }
        // 1.从frameQueue里取出一帧
        if (!(vp = frameQueue->currentFrame())) {
            ret = -1;
            break;
        }

        // 2.送去编码
        playerState->mMutex.lock();
        ret = avcodec_send_frame(pCodecCtx, vp->frame);
        if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
            playerState->mMutex.unlock();
            frameQueue->popFrame();
            continue;
        }

        // 3.得到解码帧
        ret = avcodec_receive_packet(pCodecCtx, packet);
        playerState->mMutex.unlock();
        frameQueue->popFrame();

        if (ret < 0 && ret != AVERROR_EOF) {
            av_packet_unref(packet);
            continue;
        } else {
            got_packet = 1;
        }

        if (got_packet) {
            // 4.入队列
            if (packetQueue->pushPacket(packet) < 0) {
                ret = -1;
                break;
            }
        }
    }

    av_packet_free(&packet);
    av_free(packet);
    packet = NULL;

    mExit = true;
    mCondition.signal();

    return ret;
}
