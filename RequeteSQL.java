package serveur;
import java.io.*;
import java.util.*;

import org.w3c.dom.Attr;

import java.lang.*;
import java.text.AttributedCharacterIterator.Attribute;

import algebre.*;
public class RequeteSQL {
    public static String createUser(String name,String password) throws Exception {
        File directory = new File("Data/"+name);
        if (!directory.exists()) {
            directory.mkdir();
            try {
                File file=new File("Data/"+name+"/"+name+".txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.write(password+"\n");
                bw.close();
                File adminFile=new File("Data/Admin/"+name+".txt");
                BufferedWriter bwa = new BufferedWriter(new FileWriter(adminFile, true));
                bwa.write("address:STRING,type:STRING,requete:STRING,date:STRING\n");
                bwa.close();
                return "Utilisateur cree";
            } catch (IOException e) {
                throw new Exception(e.getMessage());
            }
        } else{
            throw new Exception("Un utilisateur porte deja ce nom");
        }   
    }
    
    public static String connectUser(String name,String password) throws Exception {
        File file=new File("Data/"+name+"/"+name+".txt");
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine(); // Lire la première ligne du fichier
                br.close(); 
                if (line.equals(password)) {
                    return "Connexion avec succes";
                } else {
                    throw new Exception("Connexion en echec");
                }
            } catch (IOException e) {
                throw new Exception(e.getMessage());
            }
        } else {
            throw new Exception("Utilisateur inexistant");
        }
    }

    private static Relation getRelation (String table,String attribut) throws Exception {
        Vector<Attribut> attributs=new Vector<>();
        Vector<Uplet> uplets=new Vector<>();
        String[] colonnes=attribut.split(",");
        for(String colonne : colonnes) {
            String[] parts=colonne.split(":");
            try {
                Domaine domaine=new Domaine(parts[1]);
                Attribut a=new Attribut(parts[0],domaine);
                attributs.add(a);
            } catch (Exception e) {
                throw new Exception("Mauvais type de colonne");
            }
        } 
        return new Relation(table,attributs,uplets);
    }

    public static String createTable(String name,String table,String attribut) throws Exception {
        File file=new File("Data/"+name+"/"+table+".txt");
        if(!file.exists()) {
            Relation relation=null;
            try{
                relation=getRelation(table,attribut);
            } catch (Exception e) {
                throw new Exception("Colonnes non adaptes pour un table");
            }
            try {
                BufferedWriter bw= new BufferedWriter(new FileWriter(file,true));
                for(Attribut colonne : relation.getColonnes()) {
                    bw.write(colonne.getNom()+":"+colonne.getDomaine().getNom()+",");
                }
                bw.close();
            } catch (IOException e) {
                throw new Exception(e.getMessage());
            }
        } else {
            throw new Exception("Table existant");
        }
        return "Table cree"; 
    }   

    public static String showTables(String name) throws Exception {
        File directory=new File("Data/"+name);
        File[] files=directory.listFiles();
        if(files.length==1) {
            throw new Exception("Pas de tables");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("+--------------------+\n");
        sb.append("| Tables             |\n");
        sb.append("+--------------------+\n");
        for (File file : files) {
            String table=file.getName().substring(0,file.getName().length()-4);;
            if(!table.equals(name)) {
                sb.append(String.format("| %-18s |\n", table));
            } 
        }
        sb.append("+--------------------+\n");
        String result=sb.toString();
        return result;
    }

    public static String describeTable(String name,String table) throws Exception {
        File file=new File("Data/"+name+"/"+table+".txt");
        if(file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line=br.readLine();
                br.close();
                String[] attributs=line.split(",");
                StringBuilder sb = new StringBuilder();
                sb.append("+------------------+--------------+\n");
                sb.append("| Colonne          | Type         |\n");
                sb.append("+------------------+--------------+\n");
                for (String attribut : attributs) {
                    String[] parts=attribut.split(":");
                    sb.append(String.format("| %-16s | %-12s |\n", parts[0], parts[1]));
                    sb.append("+------------------+--------------+\n");
                }
                String result=sb.toString(); 
                return result;
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        } else {
            throw new Exception("Table inexistant");
        }
    }

    public static String dropTable(String name,String table) throws Exception {
        File file=new File("Data/"+name+"/"+table+".txt");
        if(file.exists()) {
            if(file.delete()) {
                return "Suppression avec succes";
            } else {
                throw new Exception("Suppression en echec");
            }
        } else {
            throw new Exception("Table inexistant");
        }
    }

    public static String truncateTable(String name,String table) throws Exception {
        File file=new File("Data/"+name+"/"+table+".txt");
        if(file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line=br.readLine();
                br.close();
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(line+"\n");
                bw.close();
                return "Truncate avec succes";
            } catch(IOException e) {
                throw new Exception(e.getMessage());
            }
        } else {
            throw new Exception("Table inexistant");
        }
    }

    private static Object realType(String object,String type) throws Exception {
        if(object.equals("null")) {
            return null;
        }
        if(type.equals("BOOLEAN")) {
            if("true".equalsIgnoreCase(object) || "false".equalsIgnoreCase(object)) {
                return Boolean.parseBoolean(object);
            } else {
                throw new Exception("Mauvais type de colonne");
            }
        } else if(type.equals("INT")) {
            try {
                return Integer.parseInt(object);
            } catch(NumberFormatException e) {
                throw new Exception("Mauvais type de colonne");
            }
        } else if(type.equals("DOUBLE")) {
            try {
                return Double.parseDouble(object);
            } catch(NumberFormatException e) {
                throw new Exception("Mauvais type de colonne");
            }
        } else {
            return object;
        }
    }

    private static void getDonnees(String name,Relation relation) throws Exception {
        File file=new File("Data/"+name+"/"+relation.getNom()+".txt");
        if(file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                Vector<String>uplets=new Vector<>();
                String line=br.readLine();
                while((line=br.readLine())!=null) {
                    uplets.add(line);
                }
                br.close();
                for(String uplet : uplets) {
                    Vector<Object> valeur=new Vector<>();
                    String[] parts=uplet.split(",");
                    for(int i=0;i<parts.length;i++) {
                        try {
                            Object object=realType(parts[i],relation.getColonnes().get(i).getDomaine().getNom());
                            valeur.add(object);
                        } catch (Exception e) {
                            throw new Exception("REALLY A NEW EXCEPTION");
                        }
                    }
                    Uplet ligne=new Uplet(valeur);
                    relation.addUplet(ligne);
                }
            } catch(IOException e) {
                throw new Exception(e.getMessage());
            }
        } else {
            throw new Exception("Table inexistant");
        }
    }

    public static String insertInto(String name,String table,String uplet) throws Exception {
        File file=new File("Data/"+name+"/"+table+".txt");
        if(file.exists()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String attribut=br.readLine();
                Vector<String>uplets=new Vector<>();
                String line="";
                while((line=br.readLine())!=null) {
                    uplets.add(line);
                }
                br.close();
                Relation relation=getRelation(table,attribut);
                getDonnees(name,relation);
                String[] lignes=uplet.split(";");
                for(int i=0;i<lignes.length;i++) {
                    lignes[i]=lignes[i].substring(1);
                    lignes[i]=lignes[i].substring(0,lignes[i].length()-1);
                }
                int count=0;
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                for(String ligne : lignes) {
                    String[] parts=ligne.split(",");
                    if(relation.getColonnes().size()==parts.length) {
                        for(int i=0;i<parts.length;i++) {
                            try {
                                Object object=realType(parts[i],relation.getColonnes().get(i).getDomaine().getNom());
                            } catch (Exception e) {
                                count++;
                                break;
                            }
                            if(i==parts.length-1) {
                                int equals=0;
                                for(String u : uplets) {
                                    if(u.equals(ligne)) {
                                        equals++;
                                        break;
                                    }
                                }
                                if(equals==0) {
                                    bw.write(ligne+"\n");
                                } else {
                                    count++;
                                }
                            }
                        }
                    } else {
                        count++;
                    }
                }
                bw.close();
                return count+" lignes non inserees";
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        } else {
            throw new Exception("Table inexistant");
        }
    }

    public static String selectFrom(String name,String table, String condition,String projection) throws Exception{
        File file=new File("Data/"+name+"/"+table+".txt");
        if(file.exists()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String attribut=br.readLine();
                br.close();
                Relation relation=getRelation(table,attribut);
                getDonnees(name,relation);
                Relation selection=relation.selectFrom(condition);
                if(selection==null) {
                    throw new Exception("Aucunes lignes correspondantes");
                }
                String[] parts=projection.split(",");
                selection=selection.projection(parts);
                StringBuilder sb = new StringBuilder();
                for(int i=0;i<selection.getColonnes().size();i++) {
                    sb.append("+------------------");
                }
                sb.append("+\n");
                for(Attribut colonne : selection.getColonnes()) {
                    sb.append("| %-16s "+colonne.getNom());
                }
                sb.append("|\n");
                for(int i=0;i<selection.getColonnes().size();i++) {
                    sb.append("+------------------");
                }
                sb.append("+\n");
                for (Uplet ligne : selection.getLignes()) {
                    for(Object object : ligne.getValeur()) {
                        sb.append(String.format("| %-16s ", object));
                    }
                    sb.append("|\n");
                    for(int i=0;i<ligne.getValeur().size();i++) {
                        sb.append("+------------------");
                    }
                    sb.append("+\n");
                }
                String result=sb.toString(); 
                return result;
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        } else {
            throw new Exception("Table inexistant");
        }
    }

    public static void save(String name,String address,String type,String requete,String date) {
        try {
            String uplet=address+","+type+","+requete+","+date;
            String result=insertInto("Admin",name,uplet);
        } catch (Exception e) {
            
        }
    } 

    private static Relation getRelationFromFile(File tableFile) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(tableFile));
        String attributLine = br.readLine();
        String dataLine;
        Vector<Uplet> uplets = new Vector<>();
        if (attributLine == null || attributLine.isEmpty()) {
            throw new Exception("Le fichier de la table est vide ou mal formaté.");
        }
        String[] attributs = attributLine.split(",");
        Vector<Attribut> colonnes = new Vector<>();
        for (String attribut : attributs) {
            String[] parts = attribut.split(":");
            if (parts.length != 2) {
                throw new Exception("L'attribut est mal formaté : " + attribut);
            }
            Domaine domaine = new Domaine(parts[1].trim());
            colonnes.add(new Attribut(parts[0].trim(), domaine));
        }
        while ((dataLine = br.readLine()) != null) {
            if (!dataLine.isEmpty()) {
                String[] values = dataLine.split(",");
                if (values.length != colonnes.size()) {
                    throw new Exception("Le nombre de valeurs dans la ligne ne correspond pas au nombre d'attributs.");
                }
                Vector<Object> valuesVector = new Vector<>();
                for (int i = 0; i < values.length; i++) {
                    valuesVector.add(values[i].trim());
                }
                uplets.add(new Uplet(valuesVector));
            }
        }
        br.close();
        return new Relation(tableFile.getName(), colonnes, uplets);
    }

    private static void saveRelationToFile(Relation relation, File tableFile) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(tableFile, false));
        StringBuilder attributLine = new StringBuilder();
        for (Attribut attribut : relation.getColonnes()) {
            if (attributLine.length() > 0) {
                attributLine.append(",");
            }
            attributLine.append(attribut.getNom()).append(":").append(attribut.getDomaine().getNom());
        }
        bw.write(attributLine.toString() + "\n");
        for (Uplet uplet : relation.getLignes()) {
            StringBuilder dataLine = new StringBuilder();
            for (Object value : uplet.getValeur()) {
                if (dataLine.length() > 0) {
                    dataLine.append(",");
                }
                dataLine.append(value);
            }
            bw.write(dataLine.toString() + "\n");
        }
        bw.close();
    }

    public static String alterAdd(String user, String table, String nom, String domaineStr) throws Exception {
        File tableFile = new File("Data/" + user + "/" + table + ".txt");
        if (!tableFile.exists()) {
            throw new Exception("Table inexistant");
        }
        Relation relation = getRelationFromFile(tableFile);
        for (Attribut attribut : relation.getColonnes()) {
            if (attribut.getNom().equals(nom)) {
                throw new Exception("L'attribut " + nom + " existe déjà dans la table.");
            }
        }
        Domaine domaine = new Domaine(domaineStr);
        Attribut nouvelAttribut = new Attribut(nom, domaine);
        relation.getColonnes().add(nouvelAttribut);
        for (Uplet uplet : relation.getLignes()) {
            uplet.addElement(null);
        }
        for (Uplet uplet : relation.getLignes()) {
            if (uplet.getValeur().size() != relation.getColonnes().size()) {
                throw new Exception("Le nombre d'éléments dans un uplet est incorrect.");
            }
        }
        saveRelationToFile(relation, tableFile);
        return "Attribut " + nom + " ajouté à la table " + table + ".";
    }

    public static String alterChangeName(String user, String table, String oldNom, String newNom) throws Exception {
        File tableFile = new File("Data/" + user + "/" + table + ".txt");
            if (!tableFile.exists()) {
                throw new Exception("Table inexistant");
            }
            Relation relation = getRelationFromFile(tableFile);
            Attribut attributToRename = null;
            for (Attribut attribut : relation.getColonnes()) {
                if (attribut.getNom().equals(oldNom)) {
                    attributToRename = attribut;
                    break;
                }
            }
        if (attributToRename == null) {
            throw new Exception("L'attribut " + oldNom + " n'existe pas dans la table.");
        }
        for (Attribut attribut : relation.getColonnes()) {
            if (attribut.getNom().equals(newNom)) {
                throw new Exception("L'attribut " + newNom + " existe déjà dans la table.");
            }
        }
        attributToRename.setNom(newNom);
        saveRelationToFile(relation, tableFile);
        return "L'attribut " + oldNom + " a été renommé en " + newNom + " dans la table " + table + ".";
    }

    public static String alterChangeType(String user, String table, String nom, String newType) throws Exception {
        File tableFile = new File("Data/" + user + "/" + table + ".txt");
        if (!tableFile.exists()) {
            throw new Exception("Table inexistant");
        }
        Relation relation = getRelationFromFile(tableFile);
        Attribut attributToModify = null;
        for (Attribut attribut : relation.getColonnes()) {
            if (attribut.getNom().equals(nom)) {
                attributToModify = attribut;
                break;
            }
        }
        if (attributToModify == null) {
            throw new Exception("L'attribut " + nom + " n'existe pas dans la table.");
        }
        String currentType = attributToModify.getDomaine().getType().getSimpleName();
        if (currentType.equals("Double")) {
            throw new Exception("L'attribut " + nom + " est déjà de type DOUBLE.");
        }
        if (!currentType.equals("Integer")) {
            throw new Exception("Seuls les attributs de type INT peuvent être modifiés en DOUBLE. L'attribut " + nom + " est de type " + currentType + ".");
        }
        if (newType.equals("DOUBLE")) {
            throw new Exception("Le type peut uniquement être changé en DOUBLE.");
        }
        attributToModify.setDomaine("DOUBLE");
        saveRelationToFile(relation, tableFile);
        return "L'attribut " + nom + " a vu son type changé en " + newType + " dans la table " + table + ".";
    }

    public static String updateSet(String user, String tableName, String colonne, String nouvelleValeur, String whereClause) throws Exception {
  
    File tableFile = new File("Data/" + user + "/" + tableName + ".txt");
    if (!tableFile.exists()) {
        throw new Exception("La table " + tableName + " n'existe pas");
    }
    Relation relation = getRelationFromFile(tableFile);
    Vector<Attribut> colonnes = relation.getColonnes();
    int colonneIndex = -1;
    for (int i = 0; i < colonnes.size(); i++) {
        if (colonnes.get(i).getNom().equals(colonne)) {
            colonneIndex = i;
            break;
        }
    }
    if (colonneIndex == -1) {
        throw new Exception("La colonne " + colonne + " n'existe pas");
    }
    String[] conditions = whereClause.split(" AND ");
    int updatedCount = 0;
    Vector<Uplet> lignes = relation.getLignes();
    for (Uplet uplet : lignes) {
        boolean satisfaitConditions = true;
        for (String condition : conditions) {
            String[] parts = condition.trim().split("=");
            if (parts.length != 2) {
                throw new Exception("Condition mal formatée : " + condition);
            }
            String nomColonne = parts[0].trim();
            String valeurRecherchee = parts[1].trim();
            int conditionColonneIndex = -1;
            for (int i = 0; i < colonnes.size(); i++) {
                if (colonnes.get(i).getNom().equals(nomColonne)) {
                    conditionColonneIndex = i;
                    break;
                }
            }
            if (conditionColonneIndex == -1) {
                throw new Exception("Colonne inconnue dans la condition : " + nomColonne);
            }
            Object valeurActuelle = uplet.getValeur().get(conditionColonneIndex);
            if (valeurActuelle == null) {
                if (!valeurRecherchee.equalsIgnoreCase("null")) {
                    satisfaitConditions = false;
                    break;
                }
            } else if (!valeurActuelle.toString().equals(valeurRecherchee)) {
                satisfaitConditions = false;
                break;
            }
        }
        if (satisfaitConditions) {
            Object nouvelleValeurObj = nouvelleValeur.equalsIgnoreCase("null") ? null : nouvelleValeur;
            uplet.getValeur().set(colonneIndex, nouvelleValeurObj);
            updatedCount++;
        }
    }
    saveRelationToFile(relation, tableFile);
    
    return updatedCount + " ligne(s) mise(s) à jour";
}

    public static String deleteFrom(String user, String table, String whereClause) throws Exception {
        File tableFile = new File("Data/" + user + "/" + table + ".txt");
        if (!tableFile.exists()) {
            throw new Exception("Table inexistante.");
        }
        Relation relation = getRelationFromFile(tableFile);
        Vector<Attribut> colonnes = relation.getColonnes();
        String[] conditions = whereClause.split(" AND ");
        int initialSize = relation.getLignes().size();
        Vector<String[]> parsedConditions = new Vector<>();
        for (String condition : conditions) {
            String[] item = condition.split("=");
            if (item.length != 2) {
                throw new Exception("Condition mal formatée : " + condition);
            }
            parsedConditions.add(new String[]{item[0].trim(), item[1].trim()});
        }
        Iterator<Uplet> iterator = relation.getLignes().iterator();
        while (iterator.hasNext()) {
            Uplet uplet = iterator.next();
            boolean match = true;
            for (String[] condition : parsedConditions) {
                String columnName = condition[0];
                String columnValue = condition[1];
                int columnIndex = -1;
                for (int i = 0; i < colonnes.size(); i++) {
                    if (colonnes.get(i).getNom().equalsIgnoreCase(columnName)) {
                        columnIndex = i;
                        break;
                    }
                }
                if (columnIndex == -1) {
                    throw new Exception("L'attribut " + columnName + " n'existe pas dans la table.");
                }
                Object valueInUplet = uplet.getValeur().get(columnIndex);
                if (valueInUplet == null) {
                    if (!columnValue.equalsIgnoreCase("null")) {
                        match = false;
                        break;
                    }
                } else if (!valueInUplet.toString().equals(columnValue)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                iterator.remove();
            }
        }
        int rowsDeleted = initialSize - relation.getLignes().size();
        if (rowsDeleted == 0) {
            return "Aucune ligne ne correspond aux conditions spécifiées.";
        }
        saveRelationToFile(relation, tableFile);
        return rowsDeleted + " ligne(s) supprimée(s) de la table " + table + ".";
    } 
}
