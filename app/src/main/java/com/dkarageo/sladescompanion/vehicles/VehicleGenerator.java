package com.dkarageo.sladescompanion.vehicles;

import android.graphics.drawable.Drawable;

import com.dkarageo.sladescompanion.App;
import com.dkarageo.sladescompanion.R;


import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;


public class VehicleGenerator {

    private static String[] REGION_CODES = {
            "ΕΡΚ",
            "ΕΡΖ",
            "ΝΥΑ",
            "ΘΥΑ",
            "ΚΥΑ",
            "ΤΥΑ",
            "ΡΕΑ",
            "ΕΥΑ",
            "ΕΥΗ"
    };

    private static String[] MANUFACTURERS = {
            "Volkswagen",
            "Ferrari",
            "Tesla",
            "Porsche",
            "KIA",
            "Mazda",
            "Toyota",
            "Seat",
    };

    private static String[] MODELS = {
            "Golf GTI 1.6 Black",
            "F50 Italia Red",
            "Model S Silver",
            "Cayman S Black",
            "KIA Sportage 2018",
            "MX-5 RF",
            "Avalon 2018 Red",
            "Leon 1.8 Turbo 2018"
    };

    private static String[] AUTODRIVING_SYSTEMS = {
            "CheatPilot",
            "SmartFerrari",
            "AutoPilot",
            "PorscheDrive",
            "KIADrive",
            "MazdaSmartCar",
            "AwareDrive",
            "SPilot",
    };

    private static int[] IMAGES_RESOURCE_ID = {
        R.drawable.volkswagen,
        R.drawable.ferrari,
        R.drawable.tesla,
        R.drawable.porsche,
        R.drawable.kia,
        R.drawable.mazda,
        R.drawable.toyota,
        R.drawable.seat,
    };


    public static Vehicle generateVehicle() {
        Vehicle v = new Vehicle();

        String manufacturer = generateManufacturer();
        int autoDrivingSysVer = generateAutoDrivingSystemVersion();

        v.setLicenseNo(generateLicenseNo());
        v.setManufacturer(manufacturer);
        v.setModel(generateModel(manufacturer));
        v.setEngineSerialNo(generateEngineSerialNo());
        v.setLostTraction(generateLostTraction());
        v.setAutoDrivingSystemName(generateAutoDrivingSystemName(manufacturer));
        v.setAutoDrivingSystemVersion(autoDrivingSysVer);
        v.setAutonomyLevel(generateAutonomyLevel(autoDrivingSysVer));
        v.setVehicleImg(generateVehicleImg(manufacturer));

        return v;
    }

    private static String generateLicenseNo() {
        int num = ThreadLocalRandom.current().nextInt(1000, 10000);
        String rc = REGION_CODES[
                ThreadLocalRandom.current().nextInt(0, REGION_CODES.length)];

        return String.format("GR-%s-%4d", rc, num);
    }

    private static String generateManufacturer() {
        return MANUFACTURERS[ThreadLocalRandom.current().nextInt(0, MANUFACTURERS.length)];
    }

    private static String generateModel(String manufacturer) {
        String model = null;

        for (int i = 0; i < MANUFACTURERS.length; ++i) {
            if (MANUFACTURERS[i].equals(manufacturer)) {
                model = MODELS[i];
                break;
            }
        }

        return model;
    }

    private static String generateEngineSerialNo() {
        byte[] rawData = new byte[64];
        ThreadLocalRandom.current().nextBytes(rawData);
        return new String(rawData, Charset.forName("UTF-8"));
    }

    private static boolean generateLostTraction() { return false; }

    private static String generateAutoDrivingSystemName(String manufacturer) {
        String autoDrivingSys = null;

        for (int i = 0; i < MANUFACTURERS.length; ++i) {
            if (MANUFACTURERS[i].equals(manufacturer)) {
                autoDrivingSys = AUTODRIVING_SYSTEMS[i];
                break;
            }
        }

        return autoDrivingSys;
    }

    private static int generateAutoDrivingSystemVersion() {
        return ThreadLocalRandom.current().nextInt(6, 16);
    }

    private static int generateAutonomyLevel(int autoDrivingSysVersion) {
        return autoDrivingSysVersion / 3;
    }

    private static Drawable generateVehicleImg(String manufacturer) {
        int imgRes = -1;

        for (int i = 0; i < MANUFACTURERS.length; ++i) {
            if (MANUFACTURERS[i].equals(manufacturer)) {
                imgRes = IMAGES_RESOURCE_ID[i];
                break;
            }
        }

        if (imgRes > -1) return App.getContext().getDrawable(imgRes);
        else return null;
    }
}
