package algebre;
public class Attribut{
    String nom;
    Domaine domaine;
    public Attribut(String n,Domaine d) {
        this.nom=n;
        this.domaine=d;
    }
    public String getNom() {
        return this.nom;
    }
    public Domaine getDomaine() {
        return this.domaine;
    }
    public void setNom(String n){
        this.nom=n;
    }
    public void setDomaine(String d){
        try {
            Domaine nd=new Domaine(d);
            this.domaine=nd;
        } catch (Exception e) {
            e.getMessage();
        }
    }
}