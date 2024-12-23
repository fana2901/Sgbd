package serveur;
import java.io.*;
import java.net.*;
import java.util.*;
import algebre.*;
public class ServeurSQL {
    static int port() throws Exception{
        try{
            Properties config=new Properties();
            FileInputStream configFile=new FileInputStream("fichier.conf");
            config.load(configFile);
            configFile.close();
            int port=Integer.parseInt(config.getProperty("server.port"));
            return port;
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }
    public static void main(String[] args) {
        try {
            int port=port();
            ServerSocket serverSocket=new ServerSocket(port);
            System.out.println("Serveur SQL démarré, écoute sur le port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start(); // Créer un thread pour chaque client
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class ClientHandler extends Thread {
    private Socket clientSocket;
    private String IPAddress;
    private DataInputStream input;
    private DataOutputStream output;
    private String user="";
    private String command="";
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    @Override
    public void run() {
        try {

            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
            while(true) {
                output.writeUTF("SGBD veuillez vous connecter en tant que user>");
                command=input.readUTF();
                if(command.toUpperCase().startsWith("CREATE USER ")){
                    String userName=command.split(" ")[2];
                    output.writeUTF("Password>");
                    String password = input.readUTF();
                    try {
                        String response= RequeteSQL.createUser(userName,password); 
                        user=userName;
                        output.writeUTF(response);
                        break;
                    } catch (Exception ee) {
                        output.writeUTF(ee.getMessage());
                    }
                } else if(command.toUpperCase().startsWith("CONNECT USER ")){
                    String userName = command.split(" ")[2];
                    output.writeUTF("Password>");
                    String password = input.readUTF();
                    try{
                        String response= RequeteSQL.connectUser(userName,password); 
                        user=userName;
                        output.writeUTF(response);
                        break;
                    } catch (Exception ee) {
                        output.writeUTF(ee.getMessage());
                    }
                }
            }
            IPAddress=this.clientSocket.getInetAddress().getHostAddress();
            while (true) {
                output.writeUTF(user+">");
                command=input.readUTF();
                if (command.equalsIgnoreCase("exit")) {
                    output.writeUTF("Fermeture du serveur...");
                }
                try {
                    executeCommand(user);
                } catch (Exception e) {
                    output.writeUTF("Erreur : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur : "+e.getMessage());
        }
    }
    private void executeCommand(String user) throws IOException {
        String date=new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        if (command.toUpperCase().startsWith("CREATE TABLE ")) {
            String [] parts=command.split(" "); 
            if(parts.length==4) {
                String name=parts[2];
                String attribut=parts[3];
                try {
                    String response=RequeteSQL.createTable(user,name,attribut);
                    RequeteSQL.save(user,IPAddress,"CREATE TABLE",command,date); 
                    output.writeUTF(response);

                } catch (Exception e) {
                    output.writeUTF(e.getMessage());
                }
            }
        }
        if (command.toUpperCase().equals("SHOW TABLES")) {
            try {
                String response = RequeteSQL.showTables(user);
                RequeteSQL.save(user,IPAddress,"SHOW TABLES",command,date);
                output.writeUTF(response);
            } catch (Exception e) {
                output.writeUTF(e.getMessage());
            }
        } 
        if (command.toUpperCase().startsWith("DESCRIBE TABLE ")) {
            String[] parts = command.split(" ");
            if(parts.length==3) {
                String table = parts[2];
                try {
                    String response = RequeteSQL.describeTable(user,table);
                    RequeteSQL.save(user,IPAddress,"DESCRIBE TABLE",command,date);
                    output.writeUTF(response);            
                } catch (Exception e) {
                    output.writeUTF(e.getMessage());
                }
            } 
        }
        if (command.toUpperCase().startsWith("DROP TABLE ")) {
            String [] parts=command.split(" "); 
            if(parts.length==3) {
                String table=parts[2];
                try{
                    String response=RequeteSQL.dropTable(user,table);
                    RequeteSQL.save(user,IPAddress,"DROP TABLE",command,date); 
                    output.writeUTF(response);              
                } catch (Exception e) {
                    output.writeUTF(e.getMessage());
                }
            }
        }
        if (command.toUpperCase().startsWith("TRUNCATE TABLE ")) {
            String [] parts=command.split(" ");
            if(parts.length==3) {
                String table=parts[2];
                try{
                    String response=RequeteSQL.truncateTable(user,table);
                    RequeteSQL.save(user,IPAddress,"TRUNCATE TABLE",command,date); 
                    output.writeUTF(response);         
                } catch (Exception e) {
                    output.writeUTF(e.getMessage());
                }
            } 
        }
        if (command.toUpperCase().startsWith("ALTER TABLE ")) {
            String [] parts = command.split(" ");
            if(parts[3].equals("ADD")){
                String table=parts[2];
                String[] caract=parts[4].split(":");
                    try{
                        String nom=caract[0];
                        String domaine=caract[1];
                        String response=RequeteSQL.alterAdd(user,table,nom,domaine);
                        RequeteSQL.save(user,IPAddress,"ALTER TABLE ADD",command,date); 
                        output.writeUTF(response);              
                    }
                    catch(Exception e){
                        output.writeUTF(e.getMessage());
                    }
                
            }else if(parts[3].equals("RENAME") && parts[4].equals("COLUMN") ){
                String table=parts[2];
                String old=parts[5];
                String nouv=parts[7];
                    try{    
                        String response=RequeteSQL.alterChangeName(user,table,old,nouv);
                        RequeteSQL.save(user,IPAddress,"ALTER TABLE CHANGE NAME",command,date); 
                        output.writeUTF(response);              
                    }
                    catch(Exception e){
                        output.writeUTF(e.getMessage());
                    }
            } else if(parts[3].equals("SET") && parts[4].equals("TYPE") ){
                String table=parts[2];
                String attribut=parts[5];
                String nouv=parts[7];
                    try{    
                        String response=RequeteSQL.alterChangeType(user,table,attribut,nouv);
                        RequeteSQL.save(user,IPAddress,"ALTER TABLE CHANGE TYPE",command,date); 
                        output.writeUTF(response);          
                    }
                    catch(Exception e){
                         output.writeUTF(e.getMessage());
                    }
            }
        }
        if (command.toUpperCase().startsWith("INSERT INTO ")) {
            String [] parts=command.split(" "); 
            if(parts.length==4) {
                String name=parts[2];
                String uplet=parts[3];
                try {
                    String response=RequeteSQL.insertInto(user,name,uplet);
                    RequeteSQL.save(user,IPAddress,"INSERT INTO",command,date);
                    output.writeUTF(response);
                } catch (Exception e) {
                        output.writeUTF(e.getMessage());         
                }
            }
        }
        if (command.toUpperCase().startsWith("SELECT ")) {
            String[] projection=command.split(" these ");
            if(projection.length==2) {
                String[] condition=projection[0].split(" where ");
                if(condition.length==1 || condition.length==2) {
                    String[] table=condition[0].split(" from ");
                    if(table.length==2) {
                        try {
                            if(condition.length==1) {
                               String response=RequeteSQL.selectFrom(user,table[1],null,projection[1]);
                                RequeteSQL.save(user,IPAddress,"SELECT",command,date);
                                output.writeUTF(response);
                            } else {
                                String response=RequeteSQL.selectFrom(user,table[1],condition[1],projection[1]);
                                RequeteSQL.save(user,IPAddress,"SELECT",command,date);
                                output.writeUTF(response);
                            }
                        } catch (Exception e) {
                                output.writeUTF(e.getMessage());
                        }
                    }
                }
            }
        }

        if (command.toUpperCase().startsWith("UPDATE ")) {
            String[] parts = command.split(" WHERE ");
            if (parts.length != 2) {
                output.writeUTF("Erreur: La clause WHERE est requise");
                return;
            }
            String[] setParts = parts[0].split(" SET ");
            if (setParts.length != 2) {
                output.writeUTF("Erreur de la clause SET");
                return;
            }
            String[] tableParts = setParts[0].split(" ");
            if (tableParts.length != 2) {
                output.writeUTF("Erreur:commande UPDATE invalide");
                return;
            }
            String tableName = tableParts[1].trim();
            String[] assignments = setParts[1].split(",");
            try {
                int totalUpdated = 0;
                String response = "";
                for (String assignment : assignments) {
                    String[] keyValue = assignment.trim().split("=");
                    if (keyValue.length != 2) {
                        output.writeUTF("Erreur: Format d'assignation invalide: " + assignment);
                        return;
                    }
                    String colonne = keyValue[0].trim();
                    String nouvelleValeur = keyValue[1].trim();
                    String partialResponse = RequeteSQL.updateSet(user, tableName, colonne, nouvelleValeur, parts[1].trim());
                    try {
                        String[] responseParts = partialResponse.split(" ");
                        totalUpdated = Integer.parseInt(responseParts[0]);
                    } catch (NumberFormatException e) {
                        response = partialResponse;
                    }
                }
                if (response.isEmpty()) {
                    response = totalUpdated + " ligne(s) mise(s) à jour";
                }
                RequeteSQL.save(user, IPAddress, "UPDATE", command, date);
                output.writeUTF(response);
            } catch (Exception e) {
                output.writeUTF("Erreur lors de la mise à jour: " + e.getMessage());
            }
            
        }
    
        if (command.toUpperCase().startsWith("DELETE ")) {
            String[] condition = command.split("WHERE", 2);
            if (condition.length == 2) {
                String table = condition[0].split(" ")[2].trim(); 
                String whereClause = condition[1].trim(); 
                try {
                    String response = RequeteSQL.deleteFrom(user, table, whereClause);
                    RequeteSQL.save(user, IPAddress, "DELETE", command, date);
                    output.writeUTF(response);
                } catch (Exception e) {
                    output.writeUTF(e.getMessage());
                }
            } else {
                output.writeUTF("Erreur: La clause WHERE est requise.");
            }
        }
        
        }
    }
