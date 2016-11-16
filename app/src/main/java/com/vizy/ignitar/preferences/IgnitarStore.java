package com.vizy.ignitar.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.vizy.ignitar.constants.IgnitarConstants;
import com.vizy.ignitar.models.User;

public class IgnitarStore {

    private static final String PREF_NAME = "IgnitarStore";
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final int PRIVATE_MODE = 0;

    public IgnitarStore(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public User getUserDetails() {
        User user = new User();
        user.setDeviceId(sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.deviceId, IgnitarConstants.EMPTY_STRING));
        user.setImeiNumber(sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.imeiNumber, IgnitarConstants.EMPTY_STRING));
        user.setDeviceName(sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.deviceName, IgnitarConstants.EMPTY_STRING));
        user.setAuthToken(sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.authToken, IgnitarConstants.EMPTY_STRING));
        user.setTourTaken(sharedPreferences.getBoolean(IgnitarConstants.IgnitarStoreConstants.isTourTaken, IgnitarConstants.EMPTY_BOOLEAN));
        user.setUserName(sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.userName, IgnitarConstants.EMPTY_STRING));
        user.setUserEmail(sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.userEmail, IgnitarConstants.EMPTY_STRING));
        user.setUserMobile(sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.userMobile, IgnitarConstants.EMPTY_STRING));
        return user;
    }

    public void saveDeviceId(@NonNull String deviceId) {
        editor = sharedPreferences.edit();
        editor.putString(IgnitarConstants.IgnitarStoreConstants.deviceId, deviceId);
        editor.apply();
    }

    public void saveImeiNumber(@NonNull String imeiNumber) {
        editor = sharedPreferences.edit();
        editor.putString(IgnitarConstants.IgnitarStoreConstants.imeiNumber, imeiNumber);
        editor.apply();
    }

    public void saveDeviceName(@NonNull String deviceName) {
        editor = sharedPreferences.edit();
        editor.putString(IgnitarConstants.IgnitarStoreConstants.deviceName, deviceName);
        editor.apply();
    }

    public void saveAuthToken(@NonNull String authToken) {
        editor = sharedPreferences.edit();
        editor.putString(IgnitarConstants.IgnitarStoreConstants.authToken, authToken);
        editor.apply();
    }

    public void saveTourTaken(@NonNull boolean isTrue) {
        editor = sharedPreferences.edit();
        editor.putBoolean(IgnitarConstants.IgnitarStoreConstants.isTourTaken, isTrue);
        editor.apply();
    }


    public void getDeviceId(@NonNull String deviceId) {
        editor = sharedPreferences.edit();
        editor.putString(IgnitarConstants.IgnitarStoreConstants.deviceId, deviceId);
        editor.apply();
    }

    public String getImeiNumber(@NonNull String imeiNumber) {
        editor = sharedPreferences.edit();
        return sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.imeiNumber, imeiNumber);
    }

    public String getDeviceName() {
        return sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.deviceName, IgnitarConstants.EMPTY_STRING);
    }

    public String getAuthToken() {
        return sharedPreferences.getString(IgnitarConstants.IgnitarStoreConstants.authToken, IgnitarConstants.EMPTY_STRING);
    }

    public boolean getTourTaken() {
        return sharedPreferences.getBoolean(IgnitarConstants.IgnitarStoreConstants.isTourTaken, IgnitarConstants.EMPTY_BOOLEAN);
    }

    public void clearIgnitarStore() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
