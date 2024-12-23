package algebre;
public class Domaine{
    String nom;
    Class type;
    public void setType(String n) {
        try {
            switch(n) {
                case "BOOLEAN" :
                    this.type=Class.forName("java.lang.Boolean");
                    break;
                case "INT" :
                    this.type=Class.forName("java.lang.Integer");
                    break;
                case "DOUBLE" :
                    this.type=Class.forName("java.lang.Double");
                    break;
                case "STRING" :
                    this.type=Class.forName("java.lang.String");
                    break;
                default: 
                    throw new Exception("No type : "+nom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Domaine(String name) throws Exception {
        name=name.toUpperCase();
        try{
            setType(name);
        } catch (Exception e) {
            throw new Exception (e.getMessage());
        }
        this.nom=name;
    }
    public String getNom() {
        return this.nom;
    }
    public Class getType() {
        return this.type;
    }
}