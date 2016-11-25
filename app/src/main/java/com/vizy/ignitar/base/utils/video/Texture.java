package com.vizy.ignitar.base.utils.video;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Texture {

    public int mWidth;      /// The width of the texture.
    public int mHeight;     /// The height of the texture.
    public int mChannels;   /// The number of channels.
    public boolean mSuccess;/// Whether the texture was succesfully loaded
    public byte[] mData;    /// The pixel data.

    public byte[] getData() {
        return mData;
    }

    public static Texture loadTextureFromApk(String fileName, AssetManager assets) {
        InputStream inputStream = null;
        try {
            inputStream = assets.open(fileName, AssetManager.ACCESS_BUFFER);
            BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
            Bitmap bitMap = BitmapFactory.decodeStream(bufferedStream);
            int[] data = new int[bitMap.getWidth() * bitMap.getHeight()];
            bitMap.getPixels(data, 0, bitMap.getWidth(), 0, 0, bitMap.getWidth(), bitMap.getHeight());
            byte[] dataBytes = new byte[bitMap.getWidth() * bitMap.getHeight() * 4];
            for (int p = 0; p < bitMap.getWidth() * bitMap.getHeight(); ++p) {
                int colour = data[p];
                dataBytes[p * 4] = (byte) (colour >>> 16);    // R
                dataBytes[p * 4 + 1] = (byte) (colour >>> 8);     // G
                dataBytes[p * 4 + 2] = (byte) colour;            // B
                dataBytes[p * 4 + 3] = (byte) (colour >>> 24);    // A
            }
            Texture texture = new Texture();
            texture.mWidth = bitMap.getWidth();
            texture.mHeight = bitMap.getHeight();
            texture.mChannels = 4;
            texture.mData = dataBytes;
            texture.mSuccess = true;
            return texture;
        } catch (IOException e) {
            DebugLog.LOGE("Failed to log texture '" + fileName + "' from " + "APK. Creating a blank texture");
            DebugLog.LOGI(e.getMessage());
            // Create a blank image
            Texture texture = new Texture();
            texture.mWidth = 1;
            texture.mHeight = 1;
            texture.mChannels = 4;
            byte[] dataBytes = new byte[texture.mWidth * texture.mHeight * texture.mChannels];
            for (int p = 0; p < texture.mWidth * texture.mHeight; ++p) {
                dataBytes[p * 4] = 0;    // R
                dataBytes[p * 4 + 1] = 0;    // G
                dataBytes[p * 4 + 2] = 0;    // B
                dataBytes[p * 4 + 3] = 1;    // A
            }
            texture.mData = dataBytes;
            texture.mSuccess = false;
            return texture;
        }
    }
}
