import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 12345;
    private static final String STORAGE_DIRECTORY = "server_storage/";

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            try {
                String clientRequest;
                while ((clientRequest = in.readLine()) != null) {
                    processRequest(clientRequest);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processRequest(String request) {
            String[] tokens = request.split(" ");
            String command = tokens[0];
            String fileName = tokens[1];

            switch (command) {
                case "UPLOAD":
                    uploadFile(fileName);
                    break;
                case "DOWNLOAD":
                    sendFile(fileName);
                    break;
                case "DELETE":
                    deleteFile(fileName);
                    break;
                default:
                    out.println("Invalid command.");
            }
        }

        private void uploadFile(String fileName) {
            try {
                InputStream fileInputStream = clientSocket.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(STORAGE_DIRECTORY + fileName);


            } catch (IOException e) {
                e.printStackTrace();
                out.println("Error uploading file.");
            }
        }

        private void sendFile(String fileName) {
            try {
                File file = new File(STORAGE_DIRECTORY + fileName);
                if (file.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    OutputStream outputStream = clientSocket.getOutputStream();


                } else {
                    out.println("File not found.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                out.println("Error sending file.");
            }
        }

        private void deleteFile(String fileName) {
            File file = new File(STORAGE_DIRECTORY + fileName);

        }
    }
}

