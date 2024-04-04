package serverTest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleClient {

    public static void main(String[] args) {
        final String SERVER_ADDRESS = "localhost"; // Server address
        final int PORT = 12345; // Server port number

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            System.out.println("Connected to server.");

            // Create input and output streams
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read a number from the user
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter a number: ");
            double number = Double.parseDouble(reader.readLine());
            System.out.println("Sending number to server...");

            // Send the number to the server
            output.println(number);

            // Receive and print the result from the server
            System.out.println("Waiting for response from server...");
            String result = input.readLine();
            System.out.println("Result from server: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
