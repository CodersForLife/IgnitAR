/*==============================================================================
            Copyright (c) 2012-2013 QUALCOMM Austria Research Center GmbH.
            All Rights Reserved.
            Qualcomm Confidential and Proprietary

This  Vuforia(TM) sample application in source code form ("Sample Code") for the
Vuforia Software Development Kit and/or Vuforia Extension for Unity
(collectively, the "Vuforia SDK") may in all cases only be used in conjunction
with use of the Vuforia SDK, and is subject in all respects to all of the terms
and conditions of the Vuforia SDK License Agreement, which may be found at
https://developer.vuforia.com/legal/license.

By retaining or using the Sample Code in any manner, you confirm your agreement
to all the terms and conditions of the Vuforia SDK License Agreement.  If you do
not agree to all the terms and conditions of the Vuforia SDK License Agreement,
then you may not retain or use any of the Sample Code in any manner.


==============================================================================*/

package com.vizy.ignitar.video;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/** Texture is a support class for the QCAR samples applications.
 *
 *  Exposes functionality for loading a texture from the APK.
 *
 * */

public class Texture
{
    public int mWidth;      /// The width of the texture.
    public int mHeight;     /// The height of the texture.
    public int mChannels;   /// The number of channels.
    public boolean mSuccess;/// Whether the texture was succesfully loaded
    public byte[] mData;    /// The pixel data.

    /** Returns the raw data */
    public byte[] getData()
    {
        return mData;
    }


    /** Factory function to load a texture from the APK. */
    public static Texture loadTextureFromApk(String fileName,
                                             AssetManager assets)
    {
        InputStream inputStream = null;
        try
        {
            inputStream = assets.open(fileName, AssetManager.ACCESS_BUFFER);

            BufferedInputStream bufferedStream =
                new BufferedInputStream(inputStream);
            Bitmap bitMap = BitmapFactory.decodeStream(bufferedStream);

            int[] data = new int[bitMap.getWidth() * bitMap.getHeight()];
            bitMap.getPixels(data, 0, bitMap.getWidth(), 0, 0,
                                bitMap.getWidth(), bitMap.getHeight());

            // Convert:
            byte[] dataBytes = new byte[bitMap.getWidth() *
                                       bitMap.getHeight() * 4];
            for (int p = 0; p < bitMap.getWidth() * bitMap.getHeight(); ++p)
            {
                int colour = data[p];
                dataBytes[p * 4]        = (byte)(colour >>> 16);    // R
                dataBytes[p * 4 + 1]    = (byte)(colour >>> 8);     // G
                dataBytes[p * 4 + 2]    = (byte) colour;            // B
                dataBytes[p * 4 + 3]    = (byte)(colour >>> 24);    // A
            }

            Texture texture = new Texture();
            texture.mWidth      = bitMap.getWidth();
            texture.mHeight     = bitMap.getHeight();
            texture.mChannels   = 4;
            texture.mData       = dataBytes;
            texture.mSuccess    = true;

            return texture;
        }
        catch (IOException e)
        {
            DebugLog.LOGE("Failed to log texture '" + fileName + "' from " +
                "APK. Creating a blank texture");
            DebugLog.LOGI(e.getMessage());

            // Create a blank image
            Texture texture = new Texture();
            texture.mWidth      = 1;
            texture.mHeight     = 1;
            texture.mChannels   = 4;

            byte[] dataBytes = new byte[texture.mWidth *
                                        texture.mHeight * texture.mChannels];
            for (int p = 0; p < texture.mWidth * texture.mHeight; ++p)
            {
                dataBytes[p * 4]        = 0;    // R
                dataBytes[p * 4 + 1]    = 0;    // G
                dataBytes[p * 4 + 2]    = 0;    // B
                dataBytes[p * 4 + 3]    = 1;    // A
            }

            texture.mData       = dataBytes;
            texture.mSuccess    = false;

            return texture;
        }
    }
}
