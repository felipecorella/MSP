

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String user;

    public Client(Socket socket, String user) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.user = user;
        } catch (IOException e) {
            closeConnections(socket, reader, writer);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("User: ");
        String user = sc.nextLine();
        Socket socket = new Socket("localhost", 2121);
        Client client = new Client(socket, user);
        client.getMessages();
        client.globalMessage();
    }

    private void globalMessage() {
        // TODO add your handling code here:
        try {
            writer.write(user);
            writer.newLine();
            writer.flush();

            while (socket.isConnected()) {
                Scanner sc = new Scanner(System.in);
                String message = sc.nextLine();
                if (message.contains("DISCONNECT " + user)) {
                    closeConnections(socket, reader, writer);
                } else {
                    writer.write(user + ": " + message);
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e) {
            closeConnections(socket, reader, writer);
        }
    }

    public void getMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String global;

                while (socket.isConnected()) {
                    try {
                        global = reader.readLine();
                        System.out.println(global);
                    } catch (IOException e) {
                        closeConnections(socket, reader, writer);
                    }
                }
            }
        }).start();
    }

    public void closeConnections(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}