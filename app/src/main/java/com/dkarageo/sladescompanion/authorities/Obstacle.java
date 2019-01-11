package com.dkarageo.sladescompanion.authorities;

import java.util.Date;


public class Obstacle {

    private long    mObstacleId;
    private String  mObstacleType;
    private Date    mFirstlySpottedOn;
    private Date    mLastlySpottedOn;
    private float   mIsAliveConfidence;
    private boolean mRequiresService;

    public Obstacle(long obstacleId) {
        setObstacleId(obstacleId);
    }

    public long getObstacleId() { return mObstacleId; }
    public String getObstacleType() { return mObstacleType; }
    public Date getFirstlySpottedOn() { return mFirstlySpottedOn; }
    public Date getLastlySpottedOn() { return mLastlySpottedOn; }
    public float getIsAliveConfidence() { return mIsAliveConfidence; }
    public boolean requiresService() { return mRequiresService; }

    public void setObstacleId(long obstacleId) { mObstacleId = obstacleId; }
    public void setObstacleType(String obstacleTpye) { mObstacleType = obstacleTpye; }
    public void setFirstlySpottedOn(Date firstlySpottedOn) { mFirstlySpottedOn = firstlySpottedOn;}
    public void setLastlySpottedOn(Date lastlySpottedOn) { mLastlySpottedOn = lastlySpottedOn; }
    public void setRequiresService(boolean requiresService) { mRequiresService = requiresService; }

    public void setIsAliveConfidence(float isAliveConfidence) {
        mIsAliveConfidence = isAliveConfidence;
    }
}
