package com.garmin.fit;

import org.apache.commons.io.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tom
 * Date: 9/18/14
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfCreator {

    private final java.io.File conf = new java.io.File(System.getProperty("user.home") + System.getProperty("file.separator") + ".workoutCreator");
    private final List<String> lines = new ArrayList<String>();


    public ConfCreator(String filename){
        Decode decode = new Decode();
        MesgBroadcaster mesgBroadcaster = new MesgBroadcaster(decode);
        Listener listener = new Listener();
        FileInputStream in;


        try {
            in = new FileInputStream(filename);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error opening file " + filename + " [1]");
        }

        try {
            if (!Decode.checkIntegrity((InputStream) in))
                throw new RuntimeException("FIT file integrity failed.");
        } finally {
            try {
                in.close();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            in = new FileInputStream(filename);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error opening file " + filename + " [2]");
        }

        mesgBroadcaster.addListener((FileIdMesgListener) listener);

        try {
            mesgBroadcaster.run(in);
        } catch (FitRuntimeException e) {
            System.err.print("Exception decoding file: ");
            System.err.println(e.getMessage());

            try {
                in.close();
            } catch (java.io.IOException f) {
                throw new RuntimeException(f);
            }

            return;
        }


        try {
            FileUtils.writeLines(conf,lines);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            in.close();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class Listener implements FileIdMesgListener {
        public void onMesg(FileIdMesg mesg) {

            if ((mesg.getManufacturer() != null) && (!mesg.getManufacturer().equals(Manufacturer.INVALID))) {
                System.out.print("   Manufacturer: ");
                System.out.println(mesg.getManufacturer());
                lines.add("" + mesg.getType().getValue());
            }

            if ((mesg.getProduct() != null) && (!mesg.getProduct().equals(Fit.UINT16_INVALID))) {
                System.out.print("   Product: ");
                System.out.println(mesg.getProduct());
                lines.add(""+mesg.getType().getValue());
            }

            if ((mesg.getSerialNumber() != null) && (!mesg.getSerialNumber().equals(Fit.UINT32Z_INVALID))) {
                System.out.print("   Serial Number: ");
                System.out.println(mesg.getSerialNumber());
                lines.add(""+mesg.getType().getValue());
            }

        }

    }


}
