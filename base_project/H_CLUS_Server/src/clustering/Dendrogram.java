package clustering;

import data.Data;
import java.io.Serializable;

/**
 * Classe che rappresenta un oggetto dendrogramma
 * Implementa l'interfaccia Serializable per poter serializzare gli oggetti istanza della classe.
 */
class Dendrogram implements Serializable {
  private ClusterSet tree[];

  /**
   * Costruttore della classe Dendrogram
   * @param depth profondita' del dendrogramma
   */
  Dendrogram(Integer depth) {
    tree = new ClusterSet[depth];
  }

   /**
   * Imposta il clusterSet all'interno del dendrogramma
   * @param c clusterSet da impostare
   * @param level livello del dendrogramma in cui inserire il clusterSet
   */
  void setClusterSet(ClusterSet c, Integer level) {
    tree[level] = c;
  }

  /**
   * Restituisce il clusterSet che contiene i cluster del livello passato come parametro del dendrogramma
   * @param level livello del dendrogramma del quale ottenere i cluster contenuti
   * @return clusterSet presenti nel dendrogramma al livello passato come parametro
   */
  ClusterSet getClusterSet(Integer level) {
    return tree[level];
  }

   /**
   * Restituisce la profondita' del dendrogramma
   * @return profondita' del dendrogramma
   */
  Integer getDepth() {
    return tree.length;
  }

   /**
   * Restituisce una stringa che rappresenta il dendrogramma
   * @return stringa che rappresenta il dendrogramma
   */
  public String toString() {
    String v = "";
    for (Integer i = 0; i < getDepth(); i++) {
      v += ("level" + i + ":\n" + getClusterSet(i) + "\n");
    }
    return v;
  }

  /**
   * Restituisce una stringa che rappresenta il dendrogramma, completa degli esempi presenti nei clusterSet di ogni suo livello
   * @param data daset degli esempi presenti nel dendrogramma
   * @return stringa che rappresente il dendrogramma completo degli esempi presenti nei clusterSet di ogni suo livello
   */
  public String toString(Data data) { 
    String v = "";
    for (Integer i = 0; i < getDepth(); i++) {
      v += ("level" + i + ":\n" + getClusterSet(i).toString(data) + "\n");
    }
    return v;
  }

}