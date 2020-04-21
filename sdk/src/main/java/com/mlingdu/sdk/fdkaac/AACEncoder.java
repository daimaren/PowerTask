package com.mlingdu.sdk.fdkaac;

public class AACEncoder {
    public native int encode(String pcmFilePath, String aacFilePath);
    public native int encodeAndDecodeTest(String pcmFilePath, String pcmPath2);
}
