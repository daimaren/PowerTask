package com.mlingdu.sdk.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mlingdu.sdk.R;
import com.mlingdu.sdk.fdkaac.AACDecoder;
import com.mlingdu.sdk.fdkaac.AACEncoder;

public class FdkAACActivity extends AppCompatActivity {
    static {
        System.loadLibrary("aac");
    }

    private Button aacEncodeBtn;
    private Button aacDecodeBtn;

    private final String ENCODE_PCM_FILE_PATH = "/mnt/sdcard/a_songstudio/encode_pcm.pcm";
    private final String ENCODE_MONO_PCM_FILE_PATH = "/mnt/sdcard/a_songstudio/encode_mono_pcm.pcm";
    private final String ENCODE_AAC_FILE_PATH = "/mnt/sdcard/a_songstudio/encode_lc_aac.aac";
//    private final String DECODE_AAC_FILE_PATH = "/mnt/sdcard/a_songstudio/decode_aac.aac";
    private final String DECODE_AAC_FILE_PATH = "/mnt/sdcard/a_songstudio/test.aac";
    private final String DECODE_PCM_FILE_PATH = "/mnt/sdcard/a_songstudio/decode_pcm.pcm";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdk_aac);

        aacEncodeBtn = (Button) findViewById(R.id.aac_encode);
        aacDecodeBtn = (Button) findViewById(R.id.aac_decode);
        aacEncodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AACEncoder encoder = new AACEncoder();
//                int wasteTimeMills = encoder.encodeAndDecodeTest(ENCODE_PCM_FILE_PATH, DECODE_PCM_FILE_PATH);
                int wasteTimeMills = encoder.encode(ENCODE_MONO_PCM_FILE_PATH, ENCODE_AAC_FILE_PATH);
                Toast.makeText(FdkAACActivity.this, "AAC Encoder Test waste " + wasteTimeMills + "ms", Toast.LENGTH_LONG).show();
            }
        });
        aacDecodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AACDecoder decoder = new AACDecoder();
                int wasteTimeMills = decoder.decode(DECODE_AAC_FILE_PATH, DECODE_PCM_FILE_PATH);
//                int wasteTimeMills = decoder.decode(ENCODE_AAC_FILE_PATH, DECODE_PCM_FILE_PATH);
                Toast.makeText(FdkAACActivity.this, "AAC Decoder Test waste " + wasteTimeMills + "ms", Toast.LENGTH_LONG).show();
            }
        });
    }
}
