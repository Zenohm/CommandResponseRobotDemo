package com.example.fabio.commandresponserobotdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Robot {

    final static String ceaseURL = "http://192.168.1.100/command.json?command=drivestop";
    final static String infoURL = "http://192.168.1.100/full_status.json";
    final static int DELAY = 300;

    public static void send(String command, boolean singleCommand, Double duration) {

        // Do NOT use http://irobot2.local/, this has to go through Bonjour which
        // is slower than frozen ****. Fu Steve Jobs, even if you are Magneto.
        String commandURL = "http://192.168.1.100/" + command;
        URL obj;
        URL stopObj = null;
        HttpURLConnection con;
        try {
            obj = new URL(commandURL);
            stopObj = new URL(ceaseURL);
        } catch (MalformedURLException e2) {
        }
        long endTime = DELAY + 50; // Rough estimate for the amount of time the operation will take
        long startTime = System.nanoTime();

        if (!singleCommand) {
            duration *= 1000; // Convert from seconds to milliseconds

            while (duration - (endTime - startTime) / 1e6 > 0) { // This will probably overshoot any time you provide

                startTime = System.nanoTime();

                try {
                    obj = new URL(commandURL);
                    con = (HttpURLConnection) obj.openConnection();
                    con.connect();
                    con.getInputStream().close();
                    Thread.sleep(DELAY);
                } catch (InterruptedException | IOException e1) {
                }

                endTime = System.nanoTime(); // Roughly startTime + DELAY + 50
                duration -= (endTime - startTime) / 1e6;
                System.out.println("Time remaining: " + duration / 1000);

            }

            // Signal shutdown
            try {
                con = (HttpURLConnection) stopObj.openConnection();
                con.connect();
                con.getInputStream().close();
                Thread.sleep(DELAY);
            } catch (InterruptedException | IOException e1) {
            }

        } else {

            try {
                obj = new URL(commandURL);
                con = (HttpURLConnection) obj.openConnection();
                con.connect();
                con.getInputStream().close();
                Thread.sleep(DELAY);
            } catch (InterruptedException | IOException e1) {
            }

        }
    }

    private static void generalSend(String genericCommand) {

        String commandURL = "http://192.168.1.100/command.json?command=" + genericCommand;

        HttpURLConnection con;
        URL obj;

        try {
            obj = new URL(commandURL);
            con = (HttpURLConnection) obj.openConnection();
            con.connect();
            con.getInputStream().close();
            Thread.sleep(DELAY);
        } catch (InterruptedException | IOException e) {
        }

    }

    public static void quest(String command, String condition) throws IOException {

        String commandURL = "http://192.168.1.100/" + command;

        URL quest = new URL(commandURL);
        URL holyGrail = new URL(commandURL);
        boolean obtainedTheHolyGrail = false;
        // command.json?command=drive_only&degrees=0&speed=100
        while (!obtainedTheHolyGrail) {

            HttpURLConnection con = (HttpURLConnection) quest.openConnection();
            con.connect();
            con.disconnect();

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.getMessage();
            }

            HttpURLConnection info = (HttpURLConnection) holyGrail.openConnection();
            info.connect();

            // We need to parse the JSON and check the return value based on condition.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(info.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            info.disconnect();

        }
    }


    public static void test() {

        int degree = 180;
        String command = "command.json?command=drive_only&degrees=" + degree + "&speed=100";
        String commandURL = "http://192.168.1.100/" + command;
        URL obj = null;
        URL stopObj = null;
        try {
            obj = new URL(commandURL);
            stopObj = new URL(ceaseURL);
        } catch (MalformedURLException e4) {
        }
        long endTime = DELAY + 50; // Rough estimate for the amount of time the operation will take
        long startTime = System.nanoTime();

        Double duration = Double.POSITIVE_INFINITY;

        while (duration - (endTime - startTime) / 1e6 > 0) { // This will probably overshoot any time you provide

            degree += 10;
            degree %= 360;
            System.out.println(degree);
            HttpURLConnection con;
            commandURL = "http://192.168.1.100/command.json?command=drive_only&degrees=" + degree + "&speed=100";
            try {
                obj = new URL(commandURL);
                con = (HttpURLConnection) obj.openConnection();
                con.connect();
                con.getInputStream().close();
                Thread.sleep(DELAY);
            } catch (InterruptedException | IOException e1) {
            }

            endTime = System.nanoTime(); // Roughly startTime + DELAY + 50
            duration -= (endTime - startTime) / 1e6;
            System.out.println("Time remaining: " + duration / 1000);

        }

        // Signal shutdown
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) stopObj.openConnection();
            con.connect();
            con.disconnect();
            con.getInputStream().close();
        } catch (IOException e1) {
        }

    }


    public static void forward() {

        String command = "forward";
        generalSend(command);

    }

    // Stuff

    public static void forward(int speed, int degrees) {

        String commandURL = "http://192.168.1.100/command.json?command=drive_only&degrees=" + degrees + "&speed=" + speed;
        HttpURLConnection con;
        URL obj;
        try {
            obj = new URL(commandURL);
            con = (HttpURLConnection) obj.openConnection();
            con.connect();
            Thread.sleep(DELAY);
            con.getInputStream().close();
        } catch (IOException | InterruptedException e) {
        }

    }


    public static void backward() {

        String commandURL = "http://192.168.1.100/command.json?command=drive_only&degrees=180&speed=-200";
        HttpURLConnection con;
        URL obj;
        try {
            obj = new URL(commandURL);
            con = (HttpURLConnection) obj.openConnection();
            con.connect();
            Thread.sleep(DELAY);
            con.getInputStream().close();
        } catch (IOException | InterruptedException e) {
        }

    }


    public static void left() {

        String command = "spinleft";
        generalSend(command);

    }


    public static void right() {

        String command = "spinright";
        generalSend(command);

    }


    public static void stop() {

        String command = "drivestop";
        generalSend(command);

    }


    public static void dock() {

        String command = "dock";
        generalSend(command);

    }


    public static void leaveHome() {

        String command = "leavehomebase";
        generalSend(command);

    }


    public static void beep() {

        String command = "find_me";
        generalSend(command);

    }
}