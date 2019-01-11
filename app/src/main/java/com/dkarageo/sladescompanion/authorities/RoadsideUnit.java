package com.dkarageo.sladescompanion.authorities;

import java.util.Date;


public class RoadsideUnit {
    private long    mSensorId;
    private String  mSensorType;
    private String  mOperator;
    private Date    mLastServiceDate;
    private Long    mServiceInterval;
    private String  mManufacturer;
    private boolean mIsFunctioningProperly;
    private float   mAccuracy;

    public long getSensorId() { return mSensorId; }
    public String getSensorType() { return mSensorType; }
    public String getOperator() { return mOperator; }
    public Date getLastServiceDate() { return mLastServiceDate; }
    public Long serviceInterval() { return mServiceInterval; }
    public String getManufacturer() { return mManufacturer; }
    public boolean getIsFunctioningProperly() { return mIsFunctioningProperly; }
    public float getAccuracy() { return mAccuracy; }

    public void setSensorId(long sensorId) { mSensorId = sensorId; }
    public void setSensorType(String sensorType) { mSensorType = sensorType; }
    public void setOperator(String operator) { mOperator = operator; }
    public void setLastServiceDate(Date lastServiceDate) { mLastServiceDate = lastServiceDate; }
    public void setServiceInterval(Long serviceInterval) { mServiceInterval = serviceInterval; }
    public void setManufacturer(String manufacturer) { mManufacturer = manufacturer; }
    public void setAccuracy(float accuracy) { mAccuracy = accuracy; }

    public void setIsFunctioningProperly(boolean isFunctioningProperly) {
        mIsFunctioningProperly = isFunctioningProperly;
    }
}
