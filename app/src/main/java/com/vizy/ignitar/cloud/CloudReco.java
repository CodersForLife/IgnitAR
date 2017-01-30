package com.vizy.ignitar.cloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.vizy.ignitar.R;
import com.vizy.ignitar.activities.CompanyPageActivity;
import com.vizy.ignitar.activities.branding.CompanyBrand;
import com.vizy.ignitar.appsession.base.SampleApplicationControl;
import com.vizy.ignitar.appsession.base.SampleApplicationException;
import com.vizy.ignitar.appsession.base.SampleApplicationSession;
import com.vizy.ignitar.appsession.services.LoadingDialogHandler;
import com.vizy.ignitar.appsession.services.SampleApplicationGLView;
import com.vizy.ignitar.appsession.services.video.VideoPlayerHelper;
import com.vizy.ignitar.constants.IgnitarConstants;
import com.vizy.ignitar.preferences.IgnitarStore;
import com.vizy.ignitar.utils.StringUtils;
import com.vuforia.CameraDevice;
import com.vuforia.ObjectTracker;
import com.vuforia.State;
import com.vuforia.TargetFinder;
import com.vuforia.TargetSearchResult;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;

import org.json.JSONException;
import org.json.JSONObject;

public class CloudReco extends Activity implements SampleApplicationControl {

    private static final String TAG = "CloudReco";
    private IgnitarStore ignitarStore;
    private SampleApplicationSession vuforiaAppSession;
    // These codes match the ones defined in TargetFinder in Vuforia.jar
    static final int INIT_SUCCESS = 2;
    static final int INIT_ERROR_NO_NETWORK_CONNECTION = -1;
    static final int INIT_ERROR_SERVICE_NOT_AVAILABLE = -2;
    static final int UPDATE_ERROR_AUTHORIZATION_FAILED = -1;
    static final int UPDATE_ERROR_PROJECT_SUSPENDED = -2;
    static final int UPDATE_ERROR_NO_NETWORK_CONNECTION = -3;
    static final int UPDATE_ERROR_SERVICE_NOT_AVAILABLE = -4;
    static final int UPDATE_ERROR_BAD_FRAME_QUALITY = -5;
    static final int UPDATE_ERROR_UPDATE_SDK = -6;
    static final int UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE = -7;
    static final int UPDATE_ERROR_REQUEST_TIMEOUT = -8;
    static final int HIDE_LOADING_DIALOG = 0;
    static final int SHOW_LOADING_DIALOG = 1;
    // Our OpenGL view:
    private SampleApplicationGLView mGlView;
    // Our renderer:
    private CloudRecoRenderer mRenderer;
    private boolean mExtendedTracking = false;
    boolean mFinderStarted = false;
    boolean mStopFinderIfStarted = false;
    public VideoPlayerHelper mVideoPlayerHelper = new VideoPlayerHelper();
    private int mSeekPosition = 0;
    private static final String kAccessKey = "bd576667f8fce7e18e90e314ae4ea05d7e348d1d";
    private static final String kSecretKey = "10cf3e480d8679a2d63709483bf230a286172af4";
    // View overlays to be displayed in the Augmented View
    private RelativeLayout mUILayout;
    // Error message handling:
    private int mlastErrorCode = 0;
    private int mInitErrorCode = 0;
    private boolean mFinishActivityOnError;
    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;
    private GestureDetector mGestureDetector;
    private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    private double mLastErrorTime;
    boolean mIsDroidDevice = false;

    // Called when the activity first starts or needs to be recreated after
    // resuming the application or a configuration change.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        ignitarStore = new IgnitarStore(CloudReco.this);
        //   setupWindowAnimations();
        vuforiaAppSession = new SampleApplicationSession(this);
        startLoadingAnimation();
        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Creates the GestureDetector listener for processing double tap
        mGestureDetector = new GestureDetector(this, new GestureListener());
        mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith("droid");
        mVideoPlayerHelper.init();
        mVideoPlayerHelper.setActivity(this);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Generates a Handler to trigger autofocus
            // after 1 second
            autofocusHandler.postDelayed(new Runnable() {
                public void run() {
                    boolean result = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
                    if (!result)
                        Log.e("SingleTapUp", "Unable to trigger focus");
                }
            }, 1000L);
            return true;
        }
    }

    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        try {
            vuforiaAppSession.resumeAR();
        } catch (SampleApplicationException e) {
            Log.e(TAG, e.getString());
        }
        // Resume the GL view:
        if (mGlView != null) {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
    }

    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(Configuration config) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);
        vuforiaAppSession.onConfigurationChanged();
    }

    // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        try {
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e) {
            Log.e(TAG, e.getString());
        }
        // Pauses the OpenGLView
        if (mGlView != null) {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
    }

    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        try {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e) {
            Log.e(TAG, e.getString());
        }
        System.gc();
    }

    public void deinitCloudReco() {
        // Get the object tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
        if (objectTracker == null) {
            Log.e(TAG, "Failed to destroy the tracking data set because the ObjectTracker has not"
                    + " been initialized.");
            return;
        }
        // Deinitialize target finder:
        TargetFinder finder = objectTracker.getTargetFinder();
        finder.deinit();
    }

    private void startLoadingAnimation() {
        // Inflates the Overlay Layout to be displayed above the Camera View
        LayoutInflater inflater = LayoutInflater.from(this);
        mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay, null, false);
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);
        // By default
        loadingDialogHandler.mLoadingDialogContainer = mUILayout.findViewById(R.id.loading_indicator);
        loadingDialogHandler.mLoadingDialogContainer.setVisibility(View.VISIBLE);
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    // Initializes AR application components.
    private void initApplicationAR() {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();
        // Initialize the GLView with proper flags
        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);
        // Setups the Renderer of the GLView
        mRenderer = new CloudRecoRenderer(vuforiaAppSession, this);
        mGlView.setRenderer(mRenderer);
    }

    // Returns the error message for each error code
    private String getStatusDescString(int code) {
        if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
            return getString(R.string.UPDATE_ERROR_AUTHORIZATION_FAILED_DESC);
        if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
            return getString(R.string.UPDATE_ERROR_PROJECT_SUSPENDED_DESC);
        if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
            return getString(R.string.UPDATE_ERROR_NO_NETWORK_CONNECTION_DESC);
        if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
            return getString(R.string.UPDATE_ERROR_SERVICE_NOT_AVAILABLE_DESC);
        if (code == UPDATE_ERROR_UPDATE_SDK)
            return getString(R.string.UPDATE_ERROR_UPDATE_SDK_DESC);
        if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
            return getString(R.string.UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE_DESC);
        if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
            return getString(R.string.UPDATE_ERROR_REQUEST_TIMEOUT_DESC);
        if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
            return getString(R.string.UPDATE_ERROR_BAD_FRAME_QUALITY_DESC);
        else {
            return getString(R.string.UPDATE_ERROR_UNKNOWN_DESC);
        }
    }

    // Returns the error message for each error code
    private String getStatusTitleString(int code) {
        if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
            return getString(R.string.UPDATE_ERROR_AUTHORIZATION_FAILED_TITLE);
        if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
            return getString(R.string.UPDATE_ERROR_PROJECT_SUSPENDED_TITLE);
        if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
            return getString(R.string.UPDATE_ERROR_NO_NETWORK_CONNECTION_TITLE);
        if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
            return getString(R.string.UPDATE_ERROR_SERVICE_NOT_AVAILABLE_TITLE);
        if (code == UPDATE_ERROR_UPDATE_SDK)
            return getString(R.string.UPDATE_ERROR_UPDATE_SDK_TITLE);
        if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
            return getString(R.string.UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE_TITLE);
        if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
            return getString(R.string.UPDATE_ERROR_REQUEST_TIMEOUT_TITLE);
        if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
            return getString(R.string.UPDATE_ERROR_BAD_FRAME_QUALITY_TITLE);
        else {
            return getString(R.string.UPDATE_ERROR_UNKNOWN_TITLE);
        }
    }

    // Shows error messages as System dialogs
    public void showErrorMessage(int errorCode, double errorTime, boolean finishActivityOnError) {
        if (errorTime < (mLastErrorTime + 5.0) || errorCode == mlastErrorCode)
            return;
        mlastErrorCode = errorCode;
        mFinishActivityOnError = finishActivityOnError;
        runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }
                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(CloudReco.this);
                builder.setMessage(getStatusDescString(CloudReco.this.mlastErrorCode))
                        .setTitle(getStatusTitleString(CloudReco.this.mlastErrorCode))
                        .setCancelable(false).setIcon(0).setPositiveButton(getString(R.string.button_OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mFinishActivityOnError) {
                            finish();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }

    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message) {
        final String errorMessage = message;
        runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }
                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(CloudReco.this);
                builder.setMessage(errorMessage).setTitle(getString(R.string.INIT_ERROR))
                        .setCancelable(false).setIcon(0).setPositiveButton(getString(R.string.button_OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }

    public void startFinderIfStopped() {
        if (!mFinderStarted) {
            mFinderStarted = true;
            // Get the object tracker:
            TrackerManager trackerManager = TrackerManager.getInstance();
            ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
            // Initialize target finder:
            TargetFinder targetFinder = objectTracker.getTargetFinder();
            targetFinder.clearTrackables();
            targetFinder.startRecognition();
        }
    }

    public void stopFinderIfStarted() {
        if (mFinderStarted) {
            mFinderStarted = false;
            // Get the object tracker:
            TrackerManager trackerManager = TrackerManager.getInstance();
            ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
            // Initialize target finder:
            TargetFinder targetFinder = objectTracker.getTargetFinder();
            targetFinder.stop();
            targetFinder.stop();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Process the Gestures
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean doLoadTrackersData() {
        Log.d(TAG, "initCloudReco");
        // Get the object tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
        // Initialize target finder:
        TargetFinder targetFinder = objectTracker.getTargetFinder();
        // Start initialization:
        if (targetFinder.startInit(kAccessKey, kSecretKey)) {
            targetFinder.waitUntilInitFinished();
        }
        int resultCode = targetFinder.getInitState();
        if (resultCode != TargetFinder.INIT_SUCCESS) {
            if (resultCode == TargetFinder.INIT_ERROR_NO_NETWORK_CONNECTION) {
                mInitErrorCode = UPDATE_ERROR_NO_NETWORK_CONNECTION;
            } else {
                mInitErrorCode = UPDATE_ERROR_SERVICE_NOT_AVAILABLE;
            }
            Log.e(TAG, "Failed to initialize target finder.");
            return false;
        }
        return true;
    }

    @Override
    public boolean doUnloadTrackersData() {
        return true;
    }

    @Override
    public void onInitARDone(SampleApplicationException exception) {
        if (exception == null) {
            initApplicationAR();
            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            // Start the camera:
            try {
                vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
            } catch (SampleApplicationException e) {
                Log.d(TAG, StringUtils.isNullOrEmpty(e.getMessage()) ? IgnitarConstants.Exceptions.SAMPLE_APPLICATION_EXCEPTION : e.getMessage());
            }
            boolean result = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
            if (!result)
                Log.e(TAG, "Unable to enable continuous autofocus");
            mUILayout.bringToFront();
            // Hides the Loading Dialog
            loadingDialogHandler.sendEmptyMessage(HIDE_LOADING_DIALOG);
            mUILayout.setBackgroundColor(Color.TRANSPARENT);
        } else {
            Log.e(TAG, exception.getString());
            if (mInitErrorCode != 0) {
                showErrorMessage(mInitErrorCode, 10, true);
            } else {
                showInitializationErrorMessage(exception.getString());
            }
        }
    }


    @Override
    public void onVuforiaUpdate(@NonNull State state) {
        // Get the tracker manager:
        TrackerManager trackerManager = TrackerManager.getInstance();
        // Get the object tracker:
        ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
        // Get the target finder:
        TargetFinder finder = objectTracker.getTargetFinder();
        // Check if there are new results available:
        final int statusCode = finder.updateSearchResults();
        // Show a message if we encountered an error:
        if (statusCode < 0) {
            boolean closeAppAfterError = (statusCode == UPDATE_ERROR_NO_NETWORK_CONNECTION ||
                    statusCode == UPDATE_ERROR_SERVICE_NOT_AVAILABLE);
            showErrorMessage(statusCode, state.getFrame().getTimeStamp(), closeAppAfterError);
        } else if (statusCode == TargetFinder.UPDATE_RESULTS_AVAILABLE) {
            // Process new search results
            if (finder.getResultCount() > 0) {
                TargetSearchResult result = finder.getResult(0);
                String data = result.getMetaData(), type = new String(), link = new String();
                try {
                    JSONObject metaData = new JSONObject(data);
                    type = metaData.getString("type");
                    link = metaData.getString("link");
                } catch (JSONException e) {
                    Log.d(TAG, StringUtils.isNullOrEmpty(e.getMessage()) ? IgnitarConstants.Exceptions.JSON_EXCEPTION :
                            e.getMessage());
                }
                switch (result.getTargetName()) {
                    case IgnitarConstants.CloudTargets.CHAI_THELA:
                        if (ignitarStore.getProductScan() == 1.0f) {
                            ignitarStore.saveCouponCount(ignitarStore.getCouponCount() + 1);
                            ignitarStore.saveProductScan(IgnitarConstants.EMPTY_FLOAT);
                        } else {
                            ignitarStore.saveProductScan(ignitarStore.getProductScan() + 0.25f);
                        }
//                        startActivity(new Intent(CloudReco.this, CompanyPageActivity.class));
                        startActivity(new Intent(CloudReco.this, CompanyBrand.class));
                        break;
                    case IgnitarConstants.CloudTargets.PAMPLET:
                        doAction(type, link);
                        break;
                    default:
                        doAction(type, link);
                        break;
                }
            }
        }
    }

    @Override
    public boolean doInitTrackers() {
        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;
        // Indicate if the trackers were initialized correctly
        boolean result = true;
        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null) {
            Log.d(TAG, "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else {
            Log.d(TAG, "Tracker successfully initialized");
        }
        return result;
    }

    @Override
    public boolean doStartTrackers() {
        // Indicate if the trackers were started correctly
        boolean result = true;
        // Start the tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
        objectTracker.start();
        // Start cloud based recognition if we are in scanning mode:
        TargetFinder targetFinder = objectTracker.getTargetFinder();
        targetFinder.startRecognition();
        mFinderStarted = true;
        return result;
    }

    @Override
    public boolean doStopTrackers() {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
        if (objectTracker != null) {
            objectTracker.stop();
            // Stop cloud based recognition:
            TargetFinder targetFinder = objectTracker.getTargetFinder();
            targetFinder.stop();
            mFinderStarted = false;
            // Clears the trackables
            targetFinder.clearTrackables();
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public boolean doDeinitTrackers() {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        return result;
    }

    public void doAction(@NonNull String type, @NonNull String link) {
        if (((!StringUtils.isNullOrEmpty(type))) && (!StringUtils.isNullOrEmpty(link))) {
            switch (type) {
                case IgnitarConstants.CloudAction.BROWSER:
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                case IgnitarConstants.CloudAction.VIDEO:
                    String videoName = "video_name_1";
                    mVideoPlayerHelper.load(link, videoName, VideoPlayerHelper.MEDIA_TYPE.FULLSCREEN, true, -1);
                    mVideoPlayerHelper.play(true, -1);
                    break;
            }
        }
    }
}
