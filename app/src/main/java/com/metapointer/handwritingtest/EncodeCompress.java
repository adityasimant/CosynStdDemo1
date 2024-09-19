package com.metapointer.handwritingtest;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;

public class EncodeCompress {

    void compressString(@NonNull String inputString){
        byte[] input = null;
        try {
            input = inputString.getBytes("UTF-8");
            byte[] output = new byte[100];
            Deflater compresser = new Deflater();
            compresser.setInput(input);
            compresser.finish();
            int compressedDataLength = compresser.deflate(output);
            compresser.end();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
