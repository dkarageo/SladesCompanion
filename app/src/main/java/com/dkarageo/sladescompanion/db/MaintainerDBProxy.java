package com.dkarageo.sladescompanion.db;

import android.util.Log;

import com.dkarageo.sladescompanion.authorities.Obstacle;
import com.dkarageo.sladescompanion.authorities.RoadsideUnit;
import com.dkarageo.sladescompanion.preferences.PreferencesController;
import com.dkarageo.sladescompanion.units.Location;
import com.dkarageo.sladescompanion.vehicles.Vehicle;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;


public class MaintainerDBProxy {

    private static final String TAG_SQL_ERROR    = "SQLError";
    private static final String TAG_DRIVER_ERROR = "SQLDriverError";

    private static MaintainerDBProxy dbProxy = null;

    private Connection mConn = null;


    public static MaintainerDBProxy getMaintainerDBProxy() {
        if (dbProxy == null) dbProxy = new MaintainerDBProxy();
        return dbProxy;
    }

    private static Connection openConnection()
            throws SQLException {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            Log.e(TAG_DRIVER_ERROR, Log.getStackTraceString(ex));
            return null;
        }

        Connection conn = null;

        String dbHostname = PreferencesController.getDBHostname();
        int dbPort        = PreferencesController.getDBPort();
        String dbName     = PreferencesController.getDBName();
        String dbUsername = PreferencesController.getDBUsername();
        String dbPassword = PreferencesController.getDBPassword();

        conn = DriverManager.getConnection(
                "jdbc:mysql://" +
                        dbHostname +
                        (dbPort > 0 ? String.format(":%d", dbPort) : "") +
                        "/" +
                        dbName,
                dbUsername, dbPassword);

        return conn;
    }

    private MaintainerDBProxy() {}

    public List<RoadsideUnit> getRoadsideUnits(boolean sortByIsFunctioningProperly) {
        if (!isConnectionValid()) return null;

        ArrayList<RoadsideUnit> units = new ArrayList<>();

        String query  = String.format("SELECT * FROM %s.roadsidesensor ",
                                      PreferencesController.getDBName());
        String orderBy = "ORDER BY isFunctioningProperly ASC";
        if (sortByIsFunctioningProperly) query = query + orderBy;

        try {
            Statement s = mConn.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                RoadsideUnit u = new RoadsideUnit();

                u.setSensorId(rs.getLong("sensorId"));
                u.setSensorType(rs.getString("type"));
                u.setOperator(rs.getString("operator"));
                u.setLastServiceDate(rs.getDate("lastServiceDate"));
                u.setIsFunctioningProperly(rs.getBoolean("isFunctioningProperly"));
                u.setManufacturer(rs.getString("manufacturer"));
                u.setAccuracy(rs.getFloat("accuracy"));

                units.add(u);
            }

            s.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        return units;
    }

    public boolean updateRoadsideUnitBrokenState(RoadsideUnit ru, boolean newState) {
        if (!isConnectionValid()) return false;

        String query = String.format(
                "UPDATE %s.roadsidesensor SET isFunctioningProperly = ? WHERE sensorId = ?;",
                PreferencesController.getDBName()
        );

        try {
            PreparedStatement ps = mConn.prepareStatement(query);

            ps.setBoolean(1, newState);
            ps.setLong(2, ru.getSensorId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
            return false;
        }

        return true;
    }

    public List<Obstacle> getObstacles(boolean filterByRequiringService) {

        ArrayList<Obstacle> obstacles = new ArrayList<>();

        if (mConn == null) {
            try {
                mConn = openConnection();
            } catch (SQLException ex) {
                Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
            } finally {
                // When no connection to db, return an empty list.
                if (mConn == null) return obstacles;
            }
        }

        Statement s = null;
        String where = "WHERE requiresService = 1";
        String query = String.format("SELECT * FROM %s.obstacle %s;",
                                     PreferencesController.getDBName(),
                                     filterByRequiringService ? where : "");

        try {
            s = mConn.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                Obstacle o = new Obstacle(rs.getLong("obstacleId"));

                o.setObstacleType(rs.getString("type"));
                o.setFirstlySpottedOn(rs.getDate("firstlySpottedOn"));
                o.setLastlySpottedOn(rs.getDate("lastlySpottedOn"));
                o.setIsAliveConfidence(rs.getFloat("isAliveConfidence"));
                o.setRequiresService(rs.getBoolean("requiresService"));

                obstacles.add(o);
            }

            s.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        return obstacles;
    }

    public boolean updateObstacleRequiresMaintanceState(Obstacle o, boolean newState) {
        if (!isConnectionValid()) return false;

        String query = String.format(
                "UPDATE %s.obstacle SET requiresService = ? WHERE obstacleId = ?;",
                PreferencesController.getDBName()
        );

        try {
            PreparedStatement ps = mConn.prepareStatement(query);

            ps.setBoolean(1, newState);
            ps.setLong(2, o.getObstacleId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
            return false;
        }

        return true;
    }

    public long putObstacle(Obstacle obstacle) {
        if (!isConnectionValid()) return -2;

        final String query = String.format(
                "INSERT INTO %s.Obstacle (obstacleId, type, firstlySpottedOn, lastlySpottedOn, isAliveConfidence, requiresService) VALUES(?, ?, ?, ?, ?, ?)",
                PreferencesController.getDBName()
        );

        // Acquire GUID corresponding to current obstacle unit.
        long obstacleId = putUnit();
        if (obstacleId < 0) return -2;

        try {
            PreparedStatement ps = mConn.prepareStatement(query);
            ps.setLong(1, obstacleId);
            ps.setString(2, obstacle.getObstacleType());
            ps.setTimestamp(3, new Timestamp(obstacle.getFirstlySpottedOn().getTime()));
            ps.setTimestamp(4, new Timestamp(obstacle.getLastlySpottedOn().getTime()));
            ps.setFloat(5, obstacle.getIsAliveConfidence());
            ps.setBoolean(6, obstacle.requiresService());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        obstacle.setObstacleId(obstacleId);

        return obstacleId;
    }

    public boolean deleteObstacle(Obstacle obstacle) {
        if (!isConnectionValid()) return false;

        final String query = String.format("DELETE FROM %s.Obstacle WHERE obstacleId=?;",
                                           PreferencesController.getDBName());

        long obstacleId = obstacle.getObstacleId();
        if (!deleteAllLocations(obstacleId)) return false;

        try {
            PreparedStatement ps = mConn.prepareStatement(query);
            ps.setLong(1, obstacleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
            return false;
        }

        if (!deleteUnit(obstacleId)) return false;

        return true;
    }

    public long putUnit() {
        if (!isConnectionValid()) return -1;

        final String query = String.format(
                "INSERT INTO %s.Unit (updateInterval, isStructured, isDynamic, providerConfidence, type) VALUES(?, ?, ?, ?, ?)",
                PreferencesController.getDBName());
        long unitId = -1;

        try {
            PreparedStatement ps = mConn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, 1000);
            ps.setBoolean(2, false);
            ps.setBoolean(3, false);
            ps.setFloat(4, 1.0f);
            ps.setString(5, "Vehicle");
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) unitId = rs.getLong(1);

            ps.close();

        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        return unitId;
    }

    public boolean deleteUnit(long unitId) {
        if (!isConnectionValid()) return false;

        final String query = String.format("DELETE FROM %s.unit WHERE unitId=?;",
                                           PreferencesController.getDBName());

        try {
            PreparedStatement ps = mConn.prepareStatement(query);
            ps.setLong(1, unitId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        return true;
    }

    public boolean deleteVehicle(Vehicle v) {
        if (!isConnectionValid()) return false;

        final String query = String.format("DELETE FROM %s.vehicle WHERE vehicleId=?;",
                                           PreferencesController.getDBName());

        long vehicleId = v.getVehicleId();
        if (!deleteAllLocations(vehicleId)) return false;

        try {
            PreparedStatement ps = mConn.prepareStatement(query);
            ps.setLong(1, vehicleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
            return false;
        }

        if (!deleteUnit(vehicleId)) return false;

        return true;
    }

    public int putLocation(long unitId, Location location) {
        if (!isConnectionValid()) return -1;

        final String query = String.format(
                "INSERT INTO %s.Location (unitId, timestamp, validFrom, validTo, longitude, latitude, altitude, confidence, providerId) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                PreferencesController.getDBName()
        );
        int locationId = -1;

        try {
            PreparedStatement ps = mConn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setLong(1, unitId);
            ps.setTimestamp(2, new java.sql.Timestamp(location.getTimestamp().getTime()));
            ps.setTimestamp(3, new java.sql.Timestamp(location.getValidFrom().getTime()));
            ps.setTimestamp(4, new java.sql.Timestamp(location.getValidTo().getTime()));
            ps.setDouble(5, location.getLongitude());
            ps.setDouble(6, location.getLatitude());
            ps.setDouble(7, location.getAltitude());
            ps.setFloat(8, location.getConfidence());
            ps.setLong(9, unitId);  // TODO: It only works for vehicle locations. Do something better.

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) locationId = rs.getInt(1);

            ps.close();

        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        return locationId;
    }

    public boolean deleteAllLocations(long unitId) {
        if (!isConnectionValid()) return false;

        final String query = String.format("DELETE FROM %s.location WHERE unitId=?;",
                                           PreferencesController.getDBName());

        try {
            PreparedStatement ps = mConn.prepareStatement(query);
            ps.setLong(1, unitId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        return true;
    }

    public long putVehicle(Vehicle v) {
        if (!isConnectionValid()) return -2;

        final String query = String.format(
                "INSERT INTO %s.Vehicle (vehicleId, licenseNo, manufacturer, model, engineSerialNo, lostTraction, autoDriveSysId) VALUES(?, ?, ?, ?, ?, ?, ?)",
                PreferencesController.getDBName());

        // Acquire GUID corresponding to current vehicle unit.
        long vehicleId = putUnit();
        if (vehicleId < 0) return -2;

        // Acquire auto driving system id to register vehicle with.
        long autoDriveSysId = getAutoDrivingSystemId(v.getAutoDrivingSystemName(),
                                                     v.getManufacturer(),
                                                     v.getAutoDrivingSystemVersion());
        if (autoDriveSysId == -1) {
            autoDriveSysId = putAutoDrivingSystem(v.getAutoDrivingSystemName(),
                                                  v.getManufacturer(),
                                                  v.getAutoDrivingSystemVersion(),
                                                  v.getAutonomyLevel());
        }
        if (autoDriveSysId < 0) return -2;

        try {
            PreparedStatement ps = mConn.prepareStatement(query);
            ps.setLong(1, vehicleId);
            ps.setString(2, v.getLicenseNo());
            ps.setString(3, v.getManufacturer());
            ps.setString(4, v.getModel());
            ps.setString(5, v.getEngineSerialNo());
            ps.setBoolean(6, v.lostTraction());
            ps.setLong(7, autoDriveSysId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        v.setVehicleId(vehicleId);

        return vehicleId;
    }

    public int getVehiclesCount() {
        if (!isConnectionValid()) return -2;

        final String query = String.format("SELECT COUNT(vehicleId) FROM %s.Vehicle;",
                                           PreferencesController.getDBName());
        int count = -1;

        try {
            Statement s = mConn.createStatement();
            ResultSet rs = s.executeQuery(query);

            if(rs.next()) count = rs.getInt(1);

            s.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        return count;
    }

    public int getObstaclesCount() {
        if (!isConnectionValid()) return -2;

        final String query = String.format("SELECT COUNT(obstacleId) FROM %s.Obstacle;",
                                           PreferencesController.getDBName());
        int count = -1;

        try {
            Statement s = mConn.createStatement();
            ResultSet rs = s.executeQuery(query);

            if(rs.next()) count = rs.getInt(1);

            s.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        return count;
    }

    public long putAutoDrivingSystem(String name, String manufacturer,
                                     int version, int autonomyLevel) {
        if (!isConnectionValid()) return -1;

        final String query = String.format(
                "INSERT INTO %s.AutoDrivingSystem (manufacturer, version, autonomyLevel, name) VALUES(?, ?, ?, ?)",
                PreferencesController.getDBName()
        );
        long drivingSystemId = -1;

        try {
            PreparedStatement ps = mConn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, manufacturer);
            ps.setInt(2, version);
            ps.setInt(3, autonomyLevel);
            ps.setString(4, name);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) drivingSystemId = rs.getLong(1);

            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
        }

        return drivingSystemId;
    }

    public long getAutoDrivingSystemId(String name, String manufacturer, int version) {
        if (!isConnectionValid()) return -2;

        final String query = String.format(
                "SELECT drivingSystemId FROM %s.AutoDrivingSystem WHERE manufacturer = ? AND version = ? AND name = ?",
                PreferencesController.getDBName()
        );
        long drivingSystemId = -1;

        try {
            PreparedStatement ps = mConn.prepareStatement(query);
            ps.setString(1, manufacturer);
            ps.setInt(2, version);
            ps.setString(3, name);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) drivingSystemId = rs.getLong(1);

            ps.close();
        } catch (SQLException ex) {
            Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
            return -2;
        }

        return drivingSystemId;
    }

    public synchronized boolean isConnectionValid() {
        if (mConn == null) {
            try {
                mConn = openConnection();
            } catch(SQLException ex){
                Log.e(TAG_SQL_ERROR,Log.getStackTraceString(ex));
                return false;
            }
        }

        return true;
    }
}
