package client;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Scanner;
public class ClientSQL {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Addresse IP du serveur sql > ");
        String serverAddress = scanner.nextLine().trim();
        System.out.print("Le port du serveur sql > ");
        String serverPortString = scanner.nextLine().trim();
        int serverPort=Integer.parseInt(serverPortString);
        try(Socket socket = new Socket(serverAddress, serverPort)) {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                String response = input.readUTF();
                System.out.println(response);
                if(response.equals("Fermeture du serveur...")) {
                    break;
                }
                if(response.endsWith(">")) {
                    String query = clavier.readLine();
                    output.writeUTF(query);   
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}