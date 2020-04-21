package com.mlingdu.demo.task07;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.lang.invoke.MutableCallSite;
import java.nio.ByteBuffer;

public class AudioCodecActivity extends AppCompatActivity {
    private static final String MIME_TYPE_AUDIO = "audio/x-aac";

    private MediaCodec mAudioCodecEncoder;
    private MediaCodec mAudioCodecDecoder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void startAudioEncoder() {
        if (mAudioCodecEncoder != null) {
            return;
        }

        MediaFormat mediaFormat= MediaFormat.createAudioFormat(MIME_TYPE_AUDIO, 44100, 2);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100);

        try {
            mAudioCodecEncoder = MediaCodec.createEncoderByType(MIME_TYPE_AUDIO);
            mAudioCodecEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mAudioCodecEncoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startAudioDecoder() {
        if (mAudioCodecDecoder != null) {
            return;
        }

        try {
            mAudioCodecDecoder = MediaCodec.createDecoderByType(MIME_TYPE_AUDIO);
            mAudioCodecDecoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void encodeAudioFrame(final ByteBuffer buffer, int length, final long ptsTimeUs) {
        ByteBuffer[] inputBuffers = mAudioCodecEncoder.getInputBuffers();
        int inputBufferIndex = mAudioCodecEncoder.dequeueInputBuffer(10000);

        if (inputBufferIndex >= 0) {
            //set date into the buffer
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            if (buffer != null) {
                inputBuffer.put(buffer);
            }

            if (length < 0) {
                mAudioCodecEncoder.queueInputBuffer(inputBufferIndex, 0, 0, ptsTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                mAudioCodecEncoder.queueInputBuffer(inputBufferIndex, 0, length, ptsTimeUs, 0);
            }
        }

        //get output date
        ByteBuffer[] outputBuffers = mAudioCodecEncoder.getOutputBuffers();
        int codecStatus = 0;
        do {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            codecStatus = mAudioCodecEncoder.dequeueOutputBuffer(bufferInfo, 10000);
            if (codecStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

            } else if (codecStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mAudioCodecEncoder.getOutputBuffers();
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

    private void decodeAudioFrame(final ByteBuffer buffer, int length, final long ptsTimeUs) {
        ByteBuffer[] inputBuffers = mAudioCodecDecoder.getInputBuffers();
        int inputBufferIndex = mAudioCodecDecoder.dequeueInputBuffer(10000);

        if (inputBufferIndex >= 0) {
            //set date into the buffer
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            if (buffer != null) {
                inputBuffer.put(buffer);
            }

            if (length < 0) {
                mAudioCodecDecoder.queueInputBuffer(inputBufferIndex, 0, 0, ptsTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                mAudioCodecDecoder.queueInputBuffer(inputBufferIndex, 0, length, ptsTimeUs, 0);
            }
        }

        //get output date
        ByteBuffer[] outputBuffers = mAudioCodecEncoder.getOutputBuffers();
        int codecStatus = 0;
        do {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            codecStatus = mAudioCodecDecoder.dequeueOutputBuffer(bufferInfo, 10000);
            if (codecStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

            } else if (codecStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mAudioCodecDecoder.getOutputBuffers();
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
