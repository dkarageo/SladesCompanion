package com.dkarageo.sladescompanion.db;

import android.util.Log;

import com.dkarageo.sladescompanion.authorities.Obstacle;
import com.dkarageo.sladescompanion.authorities.RoadsideUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;


public class MaintainerDBProxy {

    private static final String TAG_SQL_ERROR    = "SQLError";
    private static final String TAG_DRIVER_ERROR = "SQLDriverError";

    private static final String USERNAME = "sladesdb";
    private static final String PASSWORD = "Em6B14Negt_!";
    private static final String DBHOST   = "den1.mysql1.gear.host";
    private static final int PORT        = 3306;
    private static final String DBNAME   = "sladesdb";

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
        Properties connProps = new Properties();
        connProps.put("user", USERNAME);
        connProps.put("password", PASSWORD);

        conn = DriverManager.getConnection(
                "jdbc:mysql://" + DBHOST + "/" + DBNAME, USERNAME, PASSWORD);

        return conn;
    }

    private MaintainerDBProxy() {}

    public List<RoadsideUnit> getRoadsideUnits() {

        ArrayList<RoadsideUnit> units = new ArrayList<>();

        if (mConn == null) {
            try {
                mConn = openConnection();
            } catch (SQLException ex) {
                Log.e(TAG_SQL_ERROR, Log.getStackTraceString(ex));
            } finally {
                if (mConn == null) return units;  // When no connection to db, return an empty list.
            }
        }

        Statement s = null;
        String query = "SELECT * FROM sladesdb.roadsidesensor";

        try {
            s = mConn.createStatement();
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
        String query = String.format("SELECT * FROM %s.obstacle %s;", DBNAME,
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

//    private List<RoadsideUnit> testingRUs() {
//        ArrayList<RoadsideUnit> units = new ArrayList<>();
//
//        for (int i = 0; i < 10; ++i) {
//            RoadsideUnit u = new RoadsideUnit();
//            u.setSensorId(i * 500 + i);
//            u.setSensorType("Speed Camera");
//            u.setOperator("Road Authorities");
//            u.setLastServiceDate(GregorianCalendar.getInstance().getTime());
//            u.setServiceInterval((long) 10000);
//            u.setManufacturer("Siemens");
//            u.setAccuracy((float) 0.99);
//
//            units.add(u);
//        }
//
//        return units;
//    }
}
