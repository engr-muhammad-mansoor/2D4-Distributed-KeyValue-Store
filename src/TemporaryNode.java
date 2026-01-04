import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

interface TemporaryNodeInterface {
    boolean start(String startingNodeName, String startingNodeAddress);

    boolean store(String key, String value);

    String get(String key);
}

public class TemporaryNode implements TemporaryNodeInterface {
    private  String startingNodeName;
    private  String startingNodeAddress;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public TemporaryNode() {
    }

    public TemporaryNode(String startingNodeName, String startingNodeAddress) {
        this.startingNodeName = startingNodeName;
        this.startingNodeAddress = startingNodeAddress;
    }

    @Override
    public boolean start(String startingNodeName, String startingNodeAddress) {
        try {
            socket = new Socket();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send START message
            out.println("START 1 TemporaryNode");

            // Receive response
            String response = in.readLine();
            if (response != null && response.startsWith("START")) {
                System.out.println("Connected to the network.");
                return true;
            } else {
                System.out.println("Failed to connect to the network.");
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error while connecting to the network: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean store(String key, String value) {
        try {
            // Send PUT? request
            out.println("PUT? 1 1");
            out.println(key);
            out.println(value);

            // Receive response
            String response = in.readLine();
            if (response != null && response.equals("SUCCESS")) {
                System.out.println("Key-Value pair stored successfully.");
                return true;
            } else {
                System.out.println("Failed to store Key-Value pair.");
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error while storing Key-Value pair: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String get(String key) {
        try {
            // Send GET? request
            out.println("GET? 1");
            out.println(key);

            // Receive response
            String response = in.readLine();
            if (response != null && response.startsWith("VALUE")) {
                int numValueLines = Integer.parseInt(response.split("\\s+")[1]);
                StringBuilder valueBuilder = new StringBuilder();
                for (int i = 0; i < numValueLines; i++) {
                    String line = in.readLine();
                    if (line != null) {
                        valueBuilder.append(line).append("\n");
                    } else {
                        System.err.println("Unexpected end of input while reading value lines.");
                        return null;
                    }
                }
                return valueBuilder.toString().trim();
            } else if (response != null && response.equals("NOPE")) {
                System.out.println("Key not found in the network.");
                return null;
            } else {
                System.out.println("Failed to retrieve value for the key.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error while retrieving value for the key: " + e.getMessage());
            return null;
        }
    }
}
