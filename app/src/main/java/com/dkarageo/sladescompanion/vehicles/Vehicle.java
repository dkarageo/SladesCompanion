package com.dkarageo.sladescompanion.vehicles;

import android.graphics.drawable.Drawable;


public class Vehicle {

    private long     mVehicleId;
    private String   mLicenseNo;
    private String   mManufacturer;
    private String   mModel;
    private String   mEngineSerialNo;
    private boolean  mLostTraction;
    private String   mAutoDrivingSystemName;
    private int      mAutoDrivingSystemVersion;
    private int      mAutonomyLevel;
    private Drawable mVehicleImg;

    public long getVehicleId() { return mVehicleId; }
    public String getLicenseNo() { return mLicenseNo; }
    public String getManufacturer() { return mManufacturer; }
    public String getModel() { return mModel; }
    public String getEngineSerialNo() { return mEngineSerialNo; }
    public String getAutoDrivingSystemName() { return mAutoDrivingSystemName; }
    public int getAutoDrivingSystemVersion() { return mAutoDrivingSystemVersion; }
    public int getAutonomyLevel() { return mAutonomyLevel; }
    public boolean lostTraction() { return mLostTraction; }
    public Drawable getVehicleImg() { return mVehicleImg; }

    public void setVehicleId(long vehicleId) { mVehicleId = vehicleId; }
    public void setLicenseNo(String licenseNo) { mLicenseNo = licenseNo; }
    public void setManufacturer(String manufacturer) { mManufacturer = manufacturer; }
    public void setModel(String model) { mModel = model; }
    public void setEngineSerialNo(String engineSerialNo) { mEngineSerialNo = engineSerialNo; }
    public void setLostTraction(boolean lostTraction) { mLostTraction = lostTraction; }
    public void setAutonomyLevel(int autonomyLevel) { mAutonomyLevel = autonomyLevel; }
    public void setVehicleImg(Drawable vehicleImg) { mVehicleImg = vehicleImg; }

    public void setAutoDrivingSystemName(String autoDrivingSystemName) {
        mAutoDrivingSystemName = autoDrivingSystemName;
    }

    public void setAutoDrivingSystemVersion(int autoDrivingSystemVersion) {
        mAutoDrivingSystemVersion = autoDrivingSystemVersion;
    }
}
