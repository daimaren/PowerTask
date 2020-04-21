//
// Created by cain on 2018/12/26.
//

#ifndef AUDIOENCODER_H
#define AUDIOENCODER_H


#include <Encoder/MediaEncoder.h>
#include <player/PlayerState.h>

class AudioEncoder : public MediaEncoder {
public:
    AudioEncoder(AVCodecContext *avctx, AVStream *stream, int streamIndex, PlayerState *playerState);

    virtual ~AudioEncoder();

    int getAudioPacket(AVPacket *packet);

private:
    AVPacket *packet;
    int64_t next_pts;
    AVRational next_pts_tb;
};


#endif //AUDIOEncoder_H
