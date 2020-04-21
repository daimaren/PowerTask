package com.mlingdu.demo.task08;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoCodecActivity extends AppCompatActivity {
    private static final String MIME_TYPE_Video = "video/avc";

    private MediaCodec mVideoCodecEncoder;
    private MediaCodec mVideoCodecDecoder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void startVideoEncoder() {
        if (mVideoCodecEncoder != null) {
            return;
        }

        MediaFormat mediaFormat= MediaFormat.createVideoFormat(MIME_TYPE_Video, 1920, 1080);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1920 * 1080 * 3 * 8 * 25 / 256);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);

        try {
            mVideoCodecEncoder = MediaCodec.createEncoderByType(MIME_TYPE_Video);
            mVideoCodecEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mVideoCodecEncoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startVideoDecoder() {
        if (mVideoCodecDecoder != null) {
            return;
        }

        try {
            mVideoCodecDecoder = MediaCodec.createDecoderByType(MIME_TYPE_Video);
            mVideoCodecDecoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void encodeVideoFrame(final ByteBuffer buffer, int length, final long ptsTimeUs) {
        ByteBuffer[] inputBuffers = mVideoCodecEncoder.getInputBuffers();
        int inputBufferIndex = mVideoCodecEncoder.dequeueInputBuffer(10000);

        if (inputBufferIndex >= 0) {
            //set date into the buffer
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            if (buffer != null) {
                inputBuffer.put(buffer);
            }

            if (length < 0) {
                mVideoCodecEncoder.queueInputBuffer(inputBufferIndex, 0, 0, ptsTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                mVideoCodecEncoder.queueInputBuffer(inputBufferIndex, 0, length, ptsTimeUs, 0);
            }
        }

        //get output date
        ByteBuffer[] outputBuffers = mVideoCodecEncoder.getOutputBuffers();
        int codecStatus = 0;
        do {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            codecStatus = mVideoCodecEncoder.dequeueOutputBuffer(bufferInfo, 10000);
            if (codecStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

            } else if (codecStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mVideoCodecEncoder.getOutputBuffers();
            } else if (codecStatus < 0) {

            } else {
                //normal case
                ByteBuffer outputBuffer = outputBuffers[codecStatus];
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0;
                }
                //process data in here
            }
        } while (codecStatus >= 0);
    }

    private void decodeVideoFrame(final ByteBuffer buffer, int length, final long ptsTimeUs) {
        ByteBuffer[] inputBuffers = mVideoCodecDecoder.getInputBuffers();
        int inputBufferIndex = mVideoCodecDecoder.dequeueInputBuffer(10000);

        if (inputBufferIndex >= 0) {
            //set date into the buffer
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            if (buffer != null) {
                inputBuffer.put(buffer);
            }

            if (length < 0) {
                mVideoCodecDecoder.queueInputBuffer(inputBufferIndex, 0, 0, ptsTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                mVideoCodecDecoder.queueInputBuffer(inputBufferIndex, 0, length, ptsTimeUs, 0);
            }
        }

        //get output date
        ByteBuffer[] outputBuffers = mVideoCodecEncoder.getOutputBuffers();
        int codecStatus = 0;
        do {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            codecStatus = mVideoCodecDecoder.dequeueOutputBuffer(bufferInfo, 10000);
            if (codecStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

            } else if (codecStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mVideoCodecDecoder.getOutputBuffers();
            } else if (codecStatus < 0) {

            } else {
                //normal case
                ByteBuffer outputBuffer = outputBuffers[codecStatus];
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0;
                }
                //process data in here
            }
        } while (codecStatus >= 0);
    }
}
