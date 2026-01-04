import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class FullNode {

    private String nodeName;
    private String nodeAddress;
    private Map<String, String> keyValueStore;
    private Map<String, String> networkMap;

    public FullNode() {
    }

    public FullNode(String nodeName, String nodeAddress) {
        this.nodeName = nodeName;
        this.nodeAddress = nodeAddress;
        this.keyValueStore = new HashMap<>();
        this.networkMap = new HashMap<>();
    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            System.out.println("Full node server started at " + nodeAddress);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection established.");

                // Handle each client connection in a new thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error while starting the full node server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Receive START message
            String startMessage = in.readLine();
            if (startMessage != null && startMessage.startsWith("START")) {
                System.out.println("Received START message: " + startMessage);

                // Extract the node name from the START message
                String[] parts = startMessage.split("\\s+");
                if (parts.length == 3) {
                    String requesterNodeName = parts[2];

                    // Add the requester node to the network map
                    networkMap.put(requesterNodeName, clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
                    System.out.println("Added node " + requesterNodeName + " to the network map.");

                    // Respond with START message
                    out.println("START 1 FullNode");

                    // Handle requests
                    handleRequests(in, out);
                } else {
                    System.out.println("Invalid START message format.");
                    out.println("END Invalid START message");
                }
            } else {
                System.out.println("Invalid START message.");
                out.println("END Invalid START message");
            }

            // Close resources
            in.close();
            out.close();
            clientSocket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.err.println("Error while handling client connection: " + e.getMessage());
        }
    }

    private void handleRequests(BufferedReader in, PrintWriter out) throws IOException {
        String request;
        while ((request = in.readLine()) != null) {
            System.out.println("Received request: " + request);

            // Parse the request
            String[] parts = request.split("\\s+");
            if (parts.length > 0) {
                String command = parts[0];

                switch (command) {
                    case "PUT?":
                        handlePutRequest(parts, in, out);
                        break;
                    case "GET?":
                        handleGetRequest(parts, in, out);
                        break;
                    case "NOTIFY?":
                        handleNotifyRequest(parts, in, out);
                        break;
                    default:
                        System.out.println("Invalid request.");
                        out.println("END Invalid request");
                        break;
                }
            } else {
                System.out.println("Invalid request.");
                out.println("END Invalid request");
            }
        }
    }

    private void handlePutRequest(String[] parts, BufferedReader in, PrintWriter out) throws IOException {
        if (parts.length == 3) {
            int numKeyLines = Integer.parseInt(parts[1]);
            int numValueLines = Integer.parseInt(parts[2]);

            StringBuilder keyBuilder = new StringBuilder();
            StringBuilder valueBuilder = new StringBuilder();

            // Read key
            for (int i = 0; i < numKeyLines; i++) {
                String line = in.readLine();
                if (line != null) {
                    keyBuilder.append(line).append("\n");
                } else {
                    System.err.println("Unexpected end of input while reading key lines.");
                    out.println("END Unexpected end of input while reading key lines.");
                    return;
                }
            }

            // Read value
            for (int i = 0; i < numValueLines; i++) {
                String line = in.readLine();
                if (line != null) {
                    valueBuilder.append(line).append("\n");
                } else {
                    System.err.println("Unexpected end of input while reading value lines.");
                    out.println("END Unexpected end of input while reading value lines.");
                    return;
                }
            }

            String key = keyBuilder.toString().trim();
            String value = valueBuilder.toString().trim();

            // Store the key-value pair
            keyValueStore.put(key, value);
            System.out.println("Stored key-value pair: " + key + " -> " + value);
            out.println("SUCCESS");
        } else {
            System.out.println("Invalid PUT? request format.");
            out.println("END Invalid PUT? request format.");
        }
    }

    private void handleGetRequest(String[] parts, BufferedReader in, PrintWriter out) throws IOException {
        if (parts.length == 2) {
            int numKeyLines = Integer.parseInt(parts[1]);

            StringBuilder keyBuilder = new StringBuilder();

            // Read key
            for (int i = 0; i < numKeyLines; i++) {
                String line = in.readLine();
                if (line != null) {
                    keyBuilder.append(line).append("\n");
                } else {
                    System.err.println("Unexpected end of input while reading key lines.");
                    out.println("END Unexpected end of input while reading key lines.");
                    return;
                }
            }

            String key = keyBuilder.toString().trim();

            // Retrieve value for the key
            String value = keyValueStore.get(key);
            if (value != null) {
                // Send VALUE response
                out.println("VALUE 1");
                out.println(value);
            } else {
                // Send NOPE response
                out.println("NOPE");
            }
        } else {
            System.out.println("Invalid GET? request format.");
            out.println("END Invalid GET? request format.");
        }
    }

    private void handleNotifyRequest(String[] parts, BufferedReader in, PrintWriter out) throws IOException {
        if (parts.length == 1) {
            // Read node name and node address
            String nodeNameAndAddress = in.readLine();
            if (nodeNameAndAddress != null) {
                String[] addressParts = nodeNameAndAddress.split(":");
                if (addressParts.length == 2) {
                    String nodeName = addressParts[0];
                    String nodeAddress = addressParts[1];

                    // Add the node to the network map
                    networkMap.put(nodeName, nodeAddress);
                    System.out.println("Added node " + nodeName + " to the network map.");
                    out.println("NOTIFIED");
                } else {
                    System.out.println("Invalid NOTIFY? request format.");
                    out.println("END Invalid NOTIFY? request format.");
                }
            } else {
                System.out.println("Invalid NOTIFY? request format.");
                out.println("END Invalid NOTIFY? request format.");
            }
        } else {
            System.out.println("Invalid NOTIFY? request format.");
            out.println("END Invalid NOTIFY? request format.");
        }
    }

    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {
        int portNumber = 12345; // Choose a port number for your server

        try {
            // Create a ServerSocket that listens on the specified port
            ServerSocket serverSocket = new ServerSocket(portNumber);

            // Display a message indicating that the server is now listening
            System.out.println("Server is listening on port " + portNumber);

            // Infinite loop to accept incoming connections
            while (true) {
                // Accept incoming connections and create a new socket for each connection
                Socket clientSocket = serverSocket.accept();

                // Handle the incoming connection in a separate thread or method
                // For example, you can pass the socket to a new thread or method to process the connection
                handleConnection(clientSocket, startingNodeName, startingNodeAddress);
            }
        } catch (IOException e) {
            System.err.println("Error occurred while listening for connections: " + e.getMessage());
        }
    }

    private void handleConnection(Socket clientSocket, String startingNodeName, String startingNodeAddress) {
        // Implement your logic to handle the incoming connection
        // For example, you can read from or write to the socket

        // Close the socket when done processing the connection
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error occurred while closing the socket: " + e.getMessage());
        }
    }

    public boolean listen(String ipAddress, int portNumber) {
        try {
            // Create a ServerSocket bound to the specified IP address and port
            ServerSocket serverSocket = new ServerSocket(portNumber, 50, InetAddress.getByName(ipAddress));

            // Print a message indicating that the server is now listening
            System.out.println("Server is listening on " + ipAddress + ":" + portNumber);

            // Close the server socket
            serverSocket.close();

            return true; // Listening successfully
        } catch (IOException e) {
            // Error occurred while setting up the server socket
            System.err.println("Error occurred while setting up the server socket: " + e.getMessage());
            return false; // Listening failed
        }
    }
}