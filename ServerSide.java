    package com.co2monitoring;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerSide {
    //initialising constants
    private static final int MaxUsers = 4;
    private static final String CsvFile = "\"C:\\Users\\izeko\\OneDrive\\Documents\\CO2Readings\\co2readings.csv\"";

    public static void main(String[] args)
    {
        int port = 1234;

        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            //server socket that will listen on the specified port
            System.out.println("Server listening on port " + port);

            // Infinite loop to keep the server running till all spots are filled
            while (true){
                if (Thread.activeCount() <= MaxUsers)
                {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(" new user connected");
                    new Thread(new UserHandler(clientSocket)).start();
                }
            }
        }catch (IOException e)
        {
            System.out.println("Could not listen on port " + port);
            e.printStackTrace();
        }

    }

    private static class UserHandler implements Runnable
    {
        private Socket socket;

        public UserHandler (Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Send welcome message
                out.println("Welcome to the CO2 monitoring server!");

                // Collect data from the client
                out.println("Please enter your User ID:");
                String userId = in.readLine();

                out.println("Please enter your postcode:");
                String postcode = in.readLine();

                out.println("Please enter the CO2 concentration in ppm:");
                String co2Input = in.readLine();
                float co2 = Float.parseFloat(co2Input); // Convert input to float

                // Get the current timestamp
                String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

                // Save the collected data to the CSV file
                saveToCSV(timestamp, userId, postcode, co2);

                // Acknowledge to the client
                out.println("Thank you! Your data has been submitted.");

                // Close the connection with the client
                socket.close();

            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Method to save data to CSV file
        private synchronized void saveToCSV(String timestamp, String userId, String postcode, float co2) {
            try (FileWriter fw = new FileWriter("co2readings.csv", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {

                // Write data in CSV format
                out.println(timestamp + "," + userId + "," + postcode + "," + co2);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}


