package test;
import java.io.*;
import java.util.Scanner;
import algebre.*;
public class MoteurSQL {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command="";
        String user="";
        System.out.println("Bienvenue dans le SGBD Java. Tapez 'exit' pour quitter.");
        while (true) {
            if(user!=""){
                System.out.print(user+"> ");
                command = scanner.nextLine().trim();
            } 
            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Fermeture du SGBD...");
                break;
            }
            try {
                executeCommand(command,user);
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
        scanner.close();
    }
    private static void executeCommand(String command,String user) throws Exception{
       
    }
}
