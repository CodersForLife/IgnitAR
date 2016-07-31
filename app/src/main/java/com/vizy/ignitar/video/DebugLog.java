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

import android.util.Log;

/** DebugLog is a support class for the QCAR samples applications.
 *
 *  Exposes functionality for logging.
 *
 * */

public class DebugLog
{
    private static final String LOGTAG = "QCAR";

    /** Logging functions to generate ADB logcat messages. */

    public static final void LOGE(String nMessage)
    {
        Log.e(LOGTAG, nMessage);
    }

    public static final void LOGW(String nMessage)
    {
        Log.w(LOGTAG, nMessage);
    }

    public static final void LOGD(String nMessage)
    {
        Log.e(LOGTAG, nMessage);
    }

    public static final void LOGI(String nMessage)
    {
        Log.i(LOGTAG, nMessage);
    }
}
