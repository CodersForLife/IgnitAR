package com.vizy.ignitar.models;

public class User {

    private String deviceId;
    private String imeiNumber;
    private String deviceName;
    private String authToken;
    private boolean isTourTaken;
    private String userEmail;
    private String userName;
    private String userMobile;
    private float productScan;
    private int couponEarned;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isTourTaken() {
        return isTourTaken;
    }

    public void setTourTaken(boolean tourTaken) {
        isTourTaken = tourTaken;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public float getProductScan() {
        return productScan;
    }

    public void setProductScan(float productScan) {
        this.productScan = productScan;
    }

    public int getCouponEarned() {
        return couponEarned;
    }

    public void setCouponEarned(int couponEarned) {
        this.couponEarned = couponEarned;
    }
}
