package algebre;
import java.util.Vector;
public class Relation{
    String nom;
    Vector<Attribut> colonnes;
    Vector<Uplet> lignes;
    public String getNom() {
        return this.nom;
    }
    public Vector<Attribut> getColonnes() {
        return this.colonnes;
    }
    public Vector<Uplet> getLignes(){
        return this.lignes;
    }
    public int cardinal(){
        return this.lignes.size();
    }
    public boolean appartenance(Uplet u){
        return lignes.contains(u);
    }
    public void sansDoublon(Vector<Uplet>l){
        for(int i=0;i<l.size();i++){
            for(int j=i+1;j<l.size();j++){
                Uplet u1=l.get(i);
                Uplet u2=l.get(j);
                int equals=0;
                for(int k=0;i<u1.getValeur().size();k++){
                    Object o1=u1.getValeur().get(k);
                    Object o2=u2.getValeur().get(k);
                    if(o1.equals(o2)){
                        equals++;
                        if(equals==u1.getValeur().size()){
                            l.remove(j);
                        }
                    }
                }
            }
        }
    }
    public Relation(String n, Vector<Attribut> c, Vector<Uplet> l) {
        this.nom=n;
        this.colonnes=c;
        this.lignes=l;
    }
    public void addUplet(Uplet u) throws Exception {
        lignes.add(u);
    }
    Vector<Attribut> assemblerColonnes(Relation r){
        Vector<Attribut> result=new Vector<>();
        for(int i=0;i<this.cardinal();i++){
            Attribut colonne1=this.colonnes.get(i);
            Attribut colonne2=r.getColonnes().get(i);
            Domaine domaine=null;
            if(colonne1.getDomaine().getNom()==colonne2.getDomaine().getNom()) {
                try {
                    domaine=new Domaine(colonne1.getDomaine().getNom());
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                try{
                    Class[]hierarchy={Class.forName("java.lang.Boolean"),Class.forName("java.lang.Integer"),Class.forName("java.lang.Double"),Class.forName("java.lang.String")};
                    int index1=-1;
                    int index2=-1;
                    for(int j=0;j<hierarchy.length;j++){
                        if(hierarchy[j].isAssignableFrom(colonne1.getDomaine().getType())){
                            index1=j;
                            break;
                        }
                    }
                    for(int j=0;j<hierarchy.length;j++){
                        if(hierarchy[j].isAssignableFrom(colonne2.getDomaine().getType())){
                            index2=j;
                            break;
                        }
                    }
                    try {
                        if(index1<index2){
                            domaine=new Domaine(colonne1.getDomaine().getNom());
                        }else{
                            domaine=new Domaine(colonne1.getDomaine().getNom());
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }catch(ClassNotFoundException e){
                    e.getMessage();
                }
            }
            Attribut colonne=new Attribut(colonne1.getNom()+"_"+colonne2.getNom(),domaine);
            result.add(colonne);
        }
        return result;
    }
    Vector<Uplet> assemblerLignes (Relation r){
        Vector<Uplet> result=new Vector<>();
        result.addAll(this.lignes);
        result.addAll(r.getLignes());
        Vector<Attribut> attribut=assemblerColonnes(r);
        for(Uplet ligne : result){
            for(int i=0;i<attribut.size();i++){
                Object valeur=ligne.getValeur().get(i);
                if(!attribut.get(i).getDomaine().getType().isInstance(valeur)){
                    if(valeur=="true"){
                        valeur=0;
                    }else if(valeur=="false"){
                        valeur=1;
                    }
                    if(!attribut.get(i).getDomaine().getType().isInstance(valeur)){
                        valeur=(double)valeur;
                        if(!attribut.get(i).getDomaine().getType().isInstance(valeur)){
                            try{
                                valeur=String.valueOf(valeur);
                            }catch(Exception e){
                                e.getMessage();
                            }
                        }
                    }
                }
            }
        }
        return result;    
    }
    public Relation union(Relation r) throws Exception{
        if(this.getColonnes().size()==r.getColonnes().size()){
            String name="Union_"+this.nom+"_"+r.getNom();
            Vector<Attribut> attributs=this.assemblerColonnes(r);
            Vector<Uplet> uplets=this.assemblerLignes(r);
            Relation result=new Relation(name,attributs,uplets);
            return result;
        }else{
            Exception e=new Exception("Nombre de colonnes incompatibles");
            throw e;
        }  
    }
    public Relation intersection(Relation r) throws Exception{
        if(this.getColonnes().size()==r.getColonnes().size()){
            String name="Intersection_"+this.nom+"_"+r.getNom();
            Vector<Attribut> attributs=this.assemblerColonnes(r);
            Vector<Uplet> uplets=this.assemblerLignes(r);
            Vector<Uplet> inter=new Vector<>();
            for(int i=0;i<uplets.size();i++){
                Uplet u1=uplets.get(i);
                for(int j=i+1;j<uplets.size();j++){
                    Uplet u2=uplets.get(j);
                    for(int k=0;i<attributs.size();k++){
                        Object o1=u1.getValeur().get(k);
                        Object o2=u2.getValeur().get(k);
                        if(!o1.equals(o2)) break;
                        if(k+1==attributs.size()) {
                            inter.add(uplets.get(i));
                            uplets.remove(j);
                        }
                    }
                }
            }
            Relation result=new Relation(name,attributs,inter);
            return result;
        }else{
            Exception e=new Exception("Nombre de colonnes incompatibles");
            throw e;
        }  
    }
    public Relation difference(Relation r) throws Exception{
        if(this.colonnes.size()==r.getColonnes().size()){
            String name="Difference_"+this.nom+"_"+r.getNom();
            Vector<Uplet> uplets=new Vector<>();
            for(int i=0;i<this.cardinal();i++){
                Uplet u1=this.lignes.get(i);
                for(int j=0;j<r.cardinal();j++){
                    Uplet u2=r.getLignes().get(j);
                    int equals=0;
                    for(int k=0;k<this.colonnes.size();k++){
                        Object o1=u1.getValeur().get(k);
                        Object o2=u2.getValeur().get(k);
                        if(o1.equals(o2)) equals++;
                        if(k+1==this.colonnes.size() && equals!=this.colonnes.size()) {
                            uplets.addElement(this.lignes.get(i));
                        }
                    }
                }
            }
            Relation result=new Relation(name,this.colonnes,uplets);
            return result;
        }else{
            Exception e=new Exception("Nombre de colonnes incompatibles");
            throw e;
        }  
    }
    public Relation projection(String[] nomColonnes){
        String name="Projection_"+nom+"_sur_les_colonnes_";
        for(int i=0;i<nomColonnes.length;i++){
            name=name+nomColonnes[i]+"_";
        }
        Vector<Attribut> attributs=new Vector<>();
        int[] index=new int[nomColonnes.length];
        int compt=0;
        for(int i=0;i<this.colonnes.size();i++){
            Attribut a=this.colonnes.get(i);
            for(String nom : nomColonnes){
                if(a.getNom().equals(nom)){
                    attributs.add(a);
                    index[compt]=i;
                    break;
                }   
            }          
        }
        Vector<Uplet> uplets=new Vector<>();
        for(Uplet u : this.lignes){
            Vector valeur=new Vector();
            for(int i=0;i<u.getValeur().size();i++){
                for(int j=0;j<index.length;j++){
                    if(i==index[j]){
                        valeur.add(u.getValeur().get(i));
                        break;
                    }
                }     
            }
            uplets.add(new Uplet(valeur));
        }
        Relation result=new Relation(name,attributs,uplets);
        return result;
    }
    Relation produitCartesien(Relation r){
        String name="ProduitCartesien_"+nom+"_"+r.getNom();
        Vector<Attribut> attributs=new Vector<>();
        attributs.addAll(this.colonnes);
        attributs.addAll(r.getColonnes());
        Vector<Uplet> uplets=new Vector<>();
        for(Uplet u1 : this.lignes){
            Vector valeur=new Vector();
            valeur.addAll(u1.getValeur());
            for(Uplet u2 : r.getLignes()){
                valeur.addAll(u2.getValeur());
                Uplet u3=new Uplet(valeur);
                uplets.add(u3);
            }
        }
        Relation result=new Relation(name,attributs,uplets);
        return result;
    }
    Relation selection(String colonne,Object valeur,String teta) throws Exception {
        String name="Selection"+nom+"_"+colonne+"="+valeur;
        Vector<Uplet> uplets=new Vector<>();
        int index=-1;
        for(int i=0;i<this.colonnes.size();i++){
            if(this.colonnes.get(i).getNom().equals(colonne)){
                index=i;
                break;
            }
        }
        if(index==-1) {
            throw new Exception("Colonne inexistante");
        }
        if(!teta.equals("=") && !teta.equals("!=") && !this.colonnes.get(index).getDomaine().getNom().equals("INT") && !this.colonnes.get(index).getDomaine().getNom().equals("DOUBLE")) {
            throw new Exception("Mauvaise condition");
        }
        if(teta.equals("=")) {
            for(Uplet u : lignes){
                if(u.getValeur().get(index)==valeur){
                    uplets.add(u);
                }
            }
        } else if(teta.equals("!=")) {
            for(Uplet u : lignes){
                if(u.getValeur().get(index)!=valeur){
                    uplets.add(u);
                }
            }
        } else if(teta.equals("<")) {
            for(Uplet u : lignes){
                double number1=(double)u.getValeur().get(index);
                double number2=(double)valeur;
                if(number1<number2){
                    uplets.add(u);
                }
            }
        } else if(teta.equals("<=")) {
            for(Uplet u : lignes){
                double number1=(double)u.getValeur().get(index);
                double number2=(double)valeur;
                if(number1<=number2){
                    uplets.add(u);
                }
            }
        } else if(teta.equals(">")) {
            for(Uplet u : lignes){
                double number1=(double)u.getValeur().get(index);
                double number2=(double)valeur;
                if(number1>number2){
                    uplets.add(u);
                }
            }
        } else if(teta.equals(">=")) {
            for(Uplet u : lignes){
                double number1=(double)u.getValeur().get(index);
                double number2=(double)valeur;
                if(number1>=number2){
                    uplets.add(u);
                }
            }
        }
        Relation result=new Relation(name,this.colonnes,uplets);
        return result;
    }
    Relation selection(String colonne1,String colonne2,String teta) throws Exception{
        String name="Selection"+nom+"_"+colonne1+"="+colonne2;
        Vector<Uplet> uplets=new Vector<>();
        int index1=-1;
        int index2=-1;
        for(int i=0;i<this.colonnes.size();i++){
            if(this.colonnes.get(i).getNom().equals(colonne1)){
                index1=i;
            }
            if(this.colonnes.get(i).getNom().equals(colonne2)){
                index2=i;
            }
        }
        if(index1==-1 || index2==-1) {
            throw new Exception("Colonne inexistante");
        }
        if(!teta.equals("=") && !teta.equals("!=") && !this.colonnes.get(index1).getDomaine().getNom().equals("INT") && !this.colonnes.get(index1).getDomaine().getNom().equals("DOUBLE") && !this.colonnes.get(index2).getDomaine().getNom().equals("INT") && !this.colonnes.get(index2).getDomaine().getNom().equals("DOUBLE")) {
            throw new Exception("Mauvaise condition");
        }
        if(teta.equals("=")) {
            for(Uplet u : lignes){
                if(u.getValeur().get(index1)==u.getValeur().get(index2)){
                    uplets.add(u);
                }
            }
        }else if(teta.equals("!=")) {
            for(Uplet u : lignes){
                if(u.getValeur().get(index1)!=u.getValeur().get(index2)){
                    uplets.add(u);
                }
            }
        } else if(teta.equals("<")) {
            for(Uplet u : lignes){
                double number1=(double)u.getValeur().get(index1);
                double number2=(double)u.getValeur().get(index2);
                if(number1<number2){
                    uplets.add(u);
                }
            }
        } else if(teta.equals("<=")) {
            for(Uplet u : lignes){
                double number1=(double)u.getValeur().get(index1);
                double number2=(double)u.getValeur().get(index2);
                if(number1<=number2){
                    uplets.add(u);
                }
            }
        } else if(teta.equals(">")) {
            for(Uplet u : lignes){
                double number1=(double)u.getValeur().get(index1);
                double number2=(double)u.getValeur().get(index2);
                if(number1>number2){
                    uplets.add(u);
                }
            }
        } else if(teta.equals(">=")) {
            for(Uplet u : lignes){
                double number1=(double)u.getValeur().get(index1);
                double number2=(double)u.getValeur().get(index2);
                if(number1>=number2){
                    uplets.add(u);
                }
            }
        }
        Relation result=new Relation(name,this.colonnes,uplets);
        return result;
    }
    public Relation tetaJointure(Relation r,String colonne,Object valeur,String teta) throws Exception{
        String name="tetaJointure"+nom+"_"+r.getNom()+"_"+colonne+"="+valeur;
        Relation produit=this.produitCartesien(r);
        try{
            Relation result=produit.selection(colonne,valeur,teta);
            return result;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    public Relation tetaJointure(Relation r,String colonne1,String colonne2,String teta) throws Exception{
        String name="tetaJointure"+nom+"_"+r.getNom()+"_"+colonne1+"="+colonne2;
        Relation produit=this.produitCartesien(r);
        try{
            Relation result=produit.selection(colonne1,colonne2,teta);
            return result;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    void condition(String[] parts,String[][]conditions,String[] conjonction) throws Exception{
        int count1=0;
        int count2=0;
        for(int i=0;i<parts.length;i++) {
            if(i%2==0) {
                String[] part=parts[i].split("!=");
                if(part.length!=2) {
                    part=parts[i].split(">=");
                    if(part.length!=2) {
                        part=parts[i].split("<=");
                        if(part.length!=2) {
                            part=parts[i].split("=");
                            if(part.length!=2) {
                                part=parts[i].split("<");
                                if(part.length!=2) {
                                    part=parts[i].split(">");
                                    if(part.length!=2) {
                                        throw new Exception("Mauvaise condition");
                                    } else {
                                        conditions[count1][0]=part[0];
                                        conditions[count1][1]=">";
                                        conditions[count1][2]=part[1];
                                    }
                                } else {
                                    conditions[count1][0]=part[0];
                                    conditions[count1][1]="<";
                                    conditions[count1][2]=part[1];
                                }
                            } else {
                                conditions[count1][0]=part[0];
                                conditions[count1][1]="=";
                                conditions[count1][2]=part[1];
                            }
                        } else {
                            conditions[count1][0]=part[0];
                            conditions[count1][1]="<=";
                            conditions[count1][2]=part[1];
                        }
                    } else {
                        conditions[count1][0]=part[0];
                        conditions[count1][1]=">=";
                        conditions[count1][2]=part[1];
                    }
                } else {
                    conditions[count1][0]=part[0];
                    conditions[count1][1]="!=";
                    conditions[count1][2]=part[1];
                }
                count1++;
            } else {
                conjonction[count2]=parts[i];
                count2++;
            }
        }
    }
    static Object realType(String object) {
        if("true".equalsIgnoreCase(object) || "false".equalsIgnoreCase(object)) {
                return Boolean.parseBoolean(object);
        } else {
            try {
                return Integer.parseInt(object);
            } catch(NumberFormatException e) {
                try {
                    return Double.parseDouble(object);
                } catch(NumberFormatException ee) {
                    return object;
                }
            }
        }
    }
    public Relation selectFrom(String condition) throws Exception {
        try {
            if(condition==null) {
                return this;
            }
            String[] parts=condition.split(" ");
            if(!(parts.length%2==1)) {
                throw new Exception("Condition insuffisante");
            }
            String[][] conditions=new String[(parts.length+1)/2][3];
            String[] conjonction=new String[(parts.length-1)/2];
            try{
                this.condition(parts,conditions,conjonction);
            } catch(Exception e) {
                throw new Exception(e.getMessage());
            }
            Relation[] relation=new Relation[(parts.length+1)/2];
            for(int i=0;i<relation.length;i++) {
                try{
                    relation[i]=this.selection(conditions[i][0],conditions[i][2],conditions[i][1]);
                } catch(Exception ee) {
                    try{
                        Object object=realType(conditions[i][2]);
                        relation[i]=this.selection(conditions[i][0],object,conditions[i][1]);
                    } catch(Exception e) {
                        throw new Exception(e.getMessage());
                    }
                }
            }
            Relation result=null;
            if(conjonction.length==1) {
                if(conjonction[0].equals("et")) {
                    result=relation[0].intersection(relation[1]);
                } else if(conjonction[0].equals("ou")) {
                    result=relation[0].union(relation[1]);
                }
            }else if(conjonction.length==2) {
                Relation relation1=null;
                if(conjonction[0].equals("et")) {
                    relation1=relation[0].intersection(relation[1]);
                } else if(conjonction[0].equals("ou")) {
                    relation1=relation[0].union(relation[1]);
                }
                if(conjonction[1].equals("et")) {
                    result=relation1.intersection(relation[2]);
                } else if(conjonction[1].equals("ou")) {
                    result=relation1.union(relation[2]);
                }
            }else if(conjonction.length>=3) {
                Relation relation1=null;
                Relation relation2=null;
                if(conjonction[0].equals("et")) {
                    relation1=relation[0].intersection(relation[1]);
                } else if(conjonction[0].equals("ou")) {
                    relation1=relation[0].union(relation[1]);
                }
                if(conjonction[2].equals("et")) {
                    relation2=relation[2].intersection(relation[3]);
                } else if(conjonction[2].equals("ou")) {
                    relation2=relation[2].union(relation[3]);
                }
                if(conjonction[1].equals("et")) {
                    result=relation1.intersection(relation2);
                } else if(conjonction[1].equals("ou")) {
                    result=relation1.union(relation2);
                }
            }
            return result;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    public Relation updateSet(String colonne,String valeur,String condition) throws Exception{
        try{
            Relation relation=this.selectFrom(condition);
            Relation result=this.difference(relation);
            int index=-1;
            for(int i=0;i<relation.getColonnes().size();i++){
                if(relation.getColonnes().get(i).getNom().equals(colonne)){
                    index=i;
                    break;
                }
            }
            Object object=realType(valeur);
            for(int i=0;i<relation.getLignes().size();i++) {
                relation.getLignes().get(i).getValeur().set(index,object);
            }
            result=result.union(relation);
            return result;
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    public Relation deleteFrom(String condition) throws Exception{
        try{
            Relation relation=this.selectFrom(condition);
            Relation result=this.difference(relation);
            return result;
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}