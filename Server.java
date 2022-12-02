package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {
    private ServerSocket serverSocket;
    
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
   public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(2121);
        Server server = new Server(serverSocket);
        server.openServer();
    }
    
    public void openServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("Nueva conección");
                ServerInput serverInput = new ServerInput(socket);
                Thread logs = new Thread(serverInput);
                logs.start();
            }
        } catch(IOException e){
            
        }
    }
    
    public void closeServer(){
        try{
            if (serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
     
    
}