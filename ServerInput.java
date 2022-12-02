package server;

import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ServerInput implements Runnable {

    public static ArrayList<ServerInput> usersConnected = new ArrayList<>();
    public ArrayList<String> users = new ArrayList<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String user;

    public ServerInput(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.user = reader.readLine();
            usersConnected.add(this);
            users.add(user);
            globalMessage("Server: " + user + " se ha conectado.");
        } catch (IOException e) {
            closeConnections(socket, reader, writer);
        }
    }

    public void run() {
        String message;
        String command;
        while (socket.isConnected()) {
            try {
                message = reader.readLine();
                command = getCommand(message);
                System.out.println("->" + command);
                if (message.contains("SEND")) {
                    String[] send = message.split("#");
                    System.out.println("-->SEND" + send.toString());
                    String[] to = send[1].split("@");
                    System.out.println("-->SEND" + to.toString());
                    for (String name : users) {
                        if (name.equals(to[1])) {
                            writer.write(to[0]);
                            writer.newLine();
                            writer.flush();
                        }
                    }
                } else if (message.contains("LIST")) {
                    System.out.println("-->LIST");
                    writer.write(users.toString());
                    /*for (ServerInput serverInput : usersConnected){
                        System.out.println(serverInput);
                        writer.write(serverInput.toString());
                        writer.newLine();
                        writer.flush();
                    }*/
                } else {
                    globalMessage(message);
                }

            } catch (IOException e) {
                closeConnections(socket, reader, writer);
                break;
            }
        }
    }

    public void globalMessage(String message) {
        for (ServerInput serverInput : usersConnected) {
            try {
                if (!serverInput.user.equals(user)) {
                    serverInput.writer.write(message);
                    serverInput.writer.newLine();
                    serverInput.writer.flush();
                }
            } catch (IOException e) {
                closeConnections(socket, reader, writer);
            }
        }
    }

    public void exitUser() {
        usersConnected.remove(this);
        globalMessage("Server: " + user + " se ha desconectado.");
    }

    public void closeConnections(Socket socket, BufferedReader reader, BufferedWriter writer) {
        exitUser();
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

    public String getCommand(String message) {
        String command = null;
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) != ' ') {
                command = command + message.charAt(i);
            }
        }
        return command;
    }
}