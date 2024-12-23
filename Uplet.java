package algebre;
import java.util.Vector;
public class Uplet{
    Vector<Object> valeur=new Vector<>();
    public Uplet(Vector<Object> v){
        this.valeur=v;
    }
    public Vector<Object> getValeur(){
        return this.valeur;
    }
    public void setValeur(Vector<Object> v){
        this.valeur=v;
    }
    public void addElement(Object value){
        valeur.add(value);
    }
    // public Vector<Object> getAttributValue(Relation relation,String attribut){
    //     Vector<Attribut> attributs = relation.getColonnes();
    //     Vector<Object> uplet=new Vector<>();
    //     for (int i = 0; i < attributs.size(); i++) {
    //         if (attributs.get(i).getNom().equals(attribut)) {
    //             uplet.add(valeur.get(i)); // Retourne la valeur associée à cet attribut
    //         }
    //     }
    //     return uplet;
    // }
}
