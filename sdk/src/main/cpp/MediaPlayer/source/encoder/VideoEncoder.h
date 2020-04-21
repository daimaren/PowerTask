//
// Created by cain on 2018/12/26.
//

#ifndef VIDEOENCODER_H
#define VIDEOENCODER_H


#include <Encoder/MediaEncoder.h>
#include <player/PlayerState.h>
#include <sync/MediaClock.h>

class VideoEncoder : public MediaEncoder {
public:
    VideoEncoder(AVFormatContext *pFormatCtx, AVCodecContext *avctx,
                 AVStream *stream, int streamIndex, PlayerState *playerState);

    virtual ~VideoEncoder();

    void setMasterClock(MediaClock *masterClock);

    void start() override;

    void stop() override;

    void flush() override;

    int getFrameSize();

    int getRotate();

    FrameQueue *getFrameQueue();

    AVFormatContext *getFormatContext();

    void run() override;

private:
    // encode视频帧
    int encodeVideo();

private:
    AVFormatContext *pFormatCtx;    // 解复用上下文
    FrameQueue *frameQueue;         // 帧队列
    int mRotate;                    // 旋转角度

    bool mExit;                     // 退出标志
    Thread *encodeThread;           // encode线程
    MediaClock *masterClock;        // 主时钟
};


#endif //VIDEOEncoder_H
