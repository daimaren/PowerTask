package com.mlingdu.demo.task01;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.mlingdu.demo.R;

public class ImageActivity extends AppCompatActivity {
    private ImageView mImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acvitity_image);
        initView();
    }

    private void initView() {
        mImageView  = (ImageView)findViewById(R.id.iv_image);
    }
}
