package com.yy.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64s {

    public static String encode(String text)
    {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] textByte = text.getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(textByte);
    }


    public static String decode(String encodedText)
    {
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
    }
}
