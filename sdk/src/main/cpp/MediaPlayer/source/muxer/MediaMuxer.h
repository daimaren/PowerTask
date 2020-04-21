//
// Created by Administrator on 2019/6/29.
//

#ifndef POWERRECORDER_MEDIAMUXER_H
#define POWERRECORDER_MEDIAMUXER_H

#include <Mutex.h>
#include <Condition.h>
#include <Thread.h>

#include <common/FFmpegUtils.h>
extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/mem.h>
#include <libavutil/rational.h>
#include <libavutil/time.h>
#include <libswresample/swresample.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
#include <libavutil/avstring.h>
};

class MediaMuxer : public Runnable{

public:
    MediaMuxer();

    virtual ~MediaMuxer();

    void start();

    void stop();


protected:
    void run() override;

private:

private:

};


#endif //POWERRECORDER_MEDIAMUXER_H
