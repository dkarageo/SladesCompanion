package com.dkarageo.sladescompanion.units;

import java.util.Date;

public class Location {

    int    mLocationId;
    Date   mTimestamp;
    Date   mValidFrom;
    Date   mValidTo;
    double mLongitude;
    double mLatitude;
    double mAltitude;
    float  mConfidence;


    public int getLocationId() { return mLocationId; }
    public Date getTimestamp() { return mTimestamp; }
    public Date getValidFrom() { return mValidFrom; }
    public Date getValidTo() { return mValidTo; }
    public double getLongitude() { return mLongitude; }
    public double getLatitude() { return mLatitude; }
    public double getAltitude() { return mAltitude; }
    public float getConfidence() { return mConfidence; }

    public void setLocationId(int locationId) { mLocationId = locationId; }
    public void setTimestamp(Date timestamp) { mTimestamp = timestamp; }
    public void setValidFrom(Date validFrom) { mValidFrom = validFrom; }
    public void setValidTo(Date validTo) { mValidTo = validTo; }
    public void setLongitude(double longitude) { mLongitude = longitude; }
    public void setLatitude(double latitude) { mLatitude = latitude; }
    public void setAltitude(double altitude) { mAltitude = altitude; }
    public void setConfidence(float confidence) { mConfidence = confidence; }
}
